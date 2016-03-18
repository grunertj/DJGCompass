package com.jwg.grunert.djgcompass;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;


public class DirectionFragment extends Fragment implements LocationListener {
    LocationManager locationManager;
    DebugInfo debugInfo;
    SquareScaleImageView needle;
    Preferences preferences;
    TextView textViewSpeed, textViewDestinationDistance, textViewDrivenDistance;
    Location previousLocation;
    static boolean new_driving_distance = false;
    static boolean was_visible = false;
    float DrivenDistance;

    static final long MIN_TIME_IN_MILLISECONDS = 1000;
    static final float MIN_DISTANCE_IN_METERS = 10;

    float [] Distance = new float[10];

    double LatitudeDestination;
    double LongitudeDestination;

    public DirectionFragment() {
        // Required empty public constructor
    }

    int getDifferentialBearing(int cb, int db) {
        return ((((cb - db) % 360) + 540) % 360) - 180;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_direction, container, false);
        needle = (SquareScaleImageView) view.findViewById(R.id.needle);
        textViewSpeed = (TextView) view.findViewById(R.id.textViewSpeed);
        textViewDestinationDistance = (TextView) view.findViewById(R.id.textViewDestinationDistance);
        textViewDrivenDistance = (TextView) view.findViewById(R.id.textViewDrivenDistance);

        debugInfo = new DebugInfo();
        preferences = new Preferences(getActivity());
        Location previousLocation = new Location("previous");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onLocationChanged(Location location) {
        int DestinationBearing;
        int CurrentBearing;
        int DifferentialBearing;
        float DestinationDistance;

        int Speed;

        DebugInfo.provider = "onLocationChanged: "+location.getProvider();

        Location.distanceBetween(location.getLatitude(), location.getLongitude(), LatitudeDestination, LongitudeDestination, Distance);
        DestinationDistance = Distance[0];

        textViewDestinationDistance.setText(String.format(Locale.getDefault(),"Distance: %.2f km",DestinationDistance / 1000.0f));

        if (new_driving_distance) {
            new_driving_distance = false;
            previousLocation = location;
            DrivenDistance = 0.0f;
        } else if (location.hasSpeed() && location.hasBearing() && location.hasAccuracy() && location.getAccuracy() < 20.0f) {
            DrivenDistance = DrivenDistance + Math.abs(location.distanceTo(previousLocation));
            previousLocation = location;
        }

        textViewDrivenDistance.setText(String.format(Locale.getDefault(),"Trip: %.2f km",DrivenDistance / 1000.0f));

        DestinationBearing = Math.round(Distance[1]);

        if (location.hasAccuracy()) {
            DebugInfo.accuracy = location.getAccuracy();
        }

        if (DestinationBearing < 0 ) {
            DestinationBearing = 360 + DestinationBearing;
        }

        if (location.hasSpeed()) {
            Speed = (int) Math.round(location.getSpeed()*3.6);
            textViewSpeed.setText(String.format(Locale.getDefault(),"Speed: %d km/h",Speed));
        }

        if(location.hasBearing()) {

            CurrentBearing = Math.round(location.getBearing());
            if (CurrentBearing < 0 ) {
                CurrentBearing = 360 + CurrentBearing;
            }
            DifferentialBearing = getDifferentialBearing(DestinationBearing, CurrentBearing);
            rotateNeedle(needle,DifferentialBearing);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        DebugInfo.provider = "onStatusChanged: "+provider;
    }

    @Override
    public void onProviderEnabled(String provider) {
        DebugInfo.provider = "onProviderEnabled: "+provider;
    }

    @Override
    public void onProviderDisabled(String provider) {
        DebugInfo.provider = "onProviderDisabled: "+provider;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.jens == true && was_visible == true) {
            if (locationManager == null) {
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            }
            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET},
                        MainActivity.USER_PERMISSION_REQUEST);
            } else {
                MainActivity.jens = true;
            }
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_IN_MILLISECONDS, MIN_DISTANCE_IN_METERS, this);
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_IN_MILLISECONDS, MIN_DISTANCE_IN_METERS, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_IN_MILLISECONDS, MIN_DISTANCE_IN_METERS, this);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {
            was_visible = true;
            LatitudeDestination = preferences.getLatitudeDestination(Preferences.RETURN_AS_DOUBLE);
            LongitudeDestination = preferences.getLongitudeDestination(Preferences.RETURN_AS_DOUBLE);
            new_driving_distance = true;

            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET},
                        MainActivity.USER_PERMISSION_REQUEST);
            } else {
                MainActivity.jens = true;
            }
            if (MainActivity.jens == true) {
                if (locationManager == null) {
                    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                }
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_IN_MILLISECONDS, MIN_DISTANCE_IN_METERS, this);
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_IN_MILLISECONDS, MIN_DISTANCE_IN_METERS, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_IN_MILLISECONDS, MIN_DISTANCE_IN_METERS, this);
            }
        } else if (isResumed()) {
            was_visible = false;

            if (locationManager != null) {
                locationManager.removeUpdates(this);
            }
        }
        DebugInfo.jens = MainActivity.jens;
    }

    @Override
    public void onDestroy() {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    MainActivity.USER_PERMISSION_REQUEST);
        } else {
            MainActivity.jens = true;
        }
        if (MainActivity.jens == true) {
            if (locationManager != null) {
                locationManager.removeUpdates(this);
                locationManager = null;
            }
        }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    MainActivity.USER_PERMISSION_REQUEST);
        } else {
            MainActivity.jens = true;
        }
        if (MainActivity.jens == true) {
            if (locationManager != null) {
                locationManager.removeUpdates(this);
                locationManager = null;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    MainActivity.USER_PERMISSION_REQUEST);
        } else {
            MainActivity.jens = true;
        }
        if (MainActivity.jens == true) {
            if (locationManager != null) {
                locationManager.removeUpdates(this);
                locationManager = null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MainActivity.USER_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Status.append("Permission granted");
                    MainActivity.jens = true;
                } else {
                    //Status.append("Permission denied");
                }
                return;
            }
        }
        DebugInfo.jens = MainActivity.jens;
    }

    private void rotateNeedle(ImageView iv, int differential_angel){
        int angel;

        iv.setRotation(differential_angel);
        angel = Math.abs(differential_angel);

       /*
        * 5x4 matrix for transforming the color+alpha components of a Bitmap.
        * The matrix is stored in a single array, and its treated as follows:
        * [  a, b, c, d, e,
        *   f, g, h, i, j,
        *   k, l, m, n, o,
        *   p, q, r, s, t ]
        *
        * When applied to a color [r, g, b, a], the resulting color is computed
        * as (after clamping)
        * R' = a*R + b*G + c*B + d*A + e;
        * G' = f*R + g*G + h*B + i*A + j;
        * B' = k*R + l*G + m*B + n*A + o;
        * A' = p*R + q*G + r*B + s*A + t;
        */

        float redValue = ((float)angel*255f/180f)/255;
        float greenValue = ((float)angel*-255f/180f+255f)/255;
        float blueValue = 0;

        float[] colorMatrix = {
                redValue, 0, 0, 0, 0,  //red
                0, greenValue, 0, 0, 0, //green
                0, 0, blueValue, 0, 0,  //blue
                0, 0, 0, 1, 0    //alpha
        };

        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        iv.setColorFilter(colorFilter);
    }
}
