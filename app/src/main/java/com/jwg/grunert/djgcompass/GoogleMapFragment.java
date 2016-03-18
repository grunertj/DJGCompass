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

    public GoogleMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_google_map, container, false);
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
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
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.0f ) );

        Toast.makeText(getActivity().getApplicationContext(), "Google Maps ready, LatitudeDestination: "+LatitudeDestination+" LongitudeDestination "+LongitudeDestination, Toast.LENGTH_SHORT).show();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getActivity().getApplicationContext(), latLng.latitude + " " + latLng.longitude, Toast.LENGTH_SHORT).show();
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
            mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.0f ) );
        } else if (isResumed()) {
            // MainActivity.preferences.setDestination(LatitudeDestination,LongitudeDestination);
        }
    }

}
