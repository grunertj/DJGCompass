package com.jwg.grunert.djgcompass;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * http://stackoverflow.com/questions/19353255/how-to-put-google-maps-v2-on-a-fragment-using-viewpager
 */
public class GoogleMapFragment extends Fragment implements OnMapReadyCallback {
    static GoogleMap mMap;
    static SupportMapFragment mapFragment;
    static double zoom = 0;

    public GoogleMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_google_map, container, false);
        setRetainInstance(true);
/*
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
*/
        return view;
    }
    // http://stackoverflow.com/questions/32168567/googlemap-in-fragment-goes-blank-at-orientation-change

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.setRetainInstance(true);
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double LatitudeDestination = MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_DOUBLE);
        double LongitudeDestination = MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_DOUBLE);
        LatLng destination = new LatLng(LatitudeDestination, LongitudeDestination);
        // mMap.clear();

        // mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 10.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
        if (zoom == 0 ) {
            mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.0f/2 ) );
            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 10.0f));
        } else {
            mMap.animateCamera(CameraUpdateFactory.zoomTo((float) zoom/2));
            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, (float)zoom));
        }


        Toast.makeText(getActivity().getApplicationContext(), "Google Maps ready, LatitudeDestination: " + zoom + " " +LatitudeDestination+" LongitudeDestination "+LongitudeDestination, Toast.LENGTH_LONG).show();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getActivity().getApplicationContext(), latLng.latitude + " " + latLng.longitude, Toast.LENGTH_SHORT).show();
                LatLng destination = new LatLng(MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_DOUBLE),
                        MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_DOUBLE));
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
                if (zoom == 0) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
                } else {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo((float) zoom));
                }
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                LatLng destination = new LatLng(latLng.latitude, latLng.longitude);
                MainActivity.preferences.setDestination(latLng.latitude, latLng.longitude);
                MainActivity.preferences.setPreference_provider("Google Map");

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));

                Toast.makeText(getActivity().getApplicationContext(),
                        "Current: " + latLng.latitude + " " + latLng.longitude +
                                "\nPrevious: " + MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_DOUBLE) + " " + MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_DOUBLE), Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLng destination = new LatLng(MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_DOUBLE),
                        MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_DOUBLE));
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
                if (zoom == 0 ) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
                } else {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo((float)zoom));
                }
                Toast.makeText(getActivity().getApplicationContext(), "Google Maps loaded, " +
                        "LatitudeDestination: " + zoom + " " +destination.latitude+" LongitudeDestination "
                        +destination.longitude, Toast.LENGTH_LONG).show();

            }
        });
    }



        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser && isResumed()) {
                LatLng destination = new LatLng(MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_DOUBLE),
                        MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_DOUBLE));
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
                if (zoom == 0 ) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
                } else {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo((float)zoom));
                }
            } else if (isResumed()) {
                // MainActivity.preferences.setDestination(LatitudeDestination,LongitudeDestination);
                if (mMap != null) {
                    zoom = mMap.getCameraPosition().zoom;
                }
            }
        }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMap != null) {
            zoom = mMap.getCameraPosition().zoom;
        }
    }
}
