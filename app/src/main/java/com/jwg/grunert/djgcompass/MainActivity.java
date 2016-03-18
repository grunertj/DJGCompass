package com.jwg.grunert.djgcompass;

/*
Error on Windows
E/b: Authentication failed on the server.
E/Google Maps Android API: Authorization failure.  Please see https://developers.google.com/maps/documentation/android/start for how to correctly set up the map.
E/Google Maps Android API: In the Google Developer Console (https://console.developers.google.com)
  Ensure that the "Google Maps Android API v2" is enabled.
  Ensure that the following Android Key exists:
  API Key: AIzaSyB2VEWA5xof5xDsTDzzukTPDZoCgm-QtZQ
  Android Application (<cert_fingerprint>;<package_name>): 01:F1:90:18:7D:DF:4B:D2:E0:51:0A:8D:E8:56:50:70:39:0D:18:4D;com.jwg.grunert.djgcompass
*/

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    static final int USER_PERMISSION_REQUEST = 10;
    static boolean jens = false;
    static boolean internet = false;
    static boolean connected = false;
    static boolean play_services = false;
    static Preferences preferences;

    DestinationFragment destinationFragment = null;
    DirectionFragment directionFragment = null;
    DebugFragment debugFragment = null;
    GoogleMapFragment googleMapFragment = null;
    OpenStreetMapFragment openStreetMapFragment = null;

    static ViewPager viewPager;
    TabLayout tabLayout;
    String global_fragments[];

// http://stackoverflow.com/questions/9570237/android-check-internet-connection
    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        DebugInfo.network_provider = "unknown";

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            // Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();
            DebugInfo.network_provider = activeNetworkInfo.getTypeName();

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }

    public static boolean checkGoogleApi(Context context) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        if (api.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // http://developer.android.com/intl/ko/training/system-ui/navigation.html
        // View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        /*
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        */

        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        preferences = new Preferences(this);

        if (jens == false) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET},
                        USER_PERMISSION_REQUEST);
                //return;
                // jens = true;
            } else {
                jens = true;
            }
        }

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        internet = false;
        play_services = false;

        internet = checkConnection(this);

        if (internet) {
            play_services = checkGoogleApi(this);
        }

        DebugInfo.internet = internet;
        DebugInfo.play_services = play_services;

        if (internet && play_services) {
            global_fragments = new String[5];
            global_fragments[0] = "Destination";
            global_fragments[1] = "Direction";
            global_fragments[2] = "Debug";
            global_fragments[3] = "GMap";
            global_fragments[4] = "Osm";
        } else {
            global_fragments = new String[3];
            global_fragments[0] = "Destination";
            global_fragments[1] = "Direction";
            global_fragments[2] = "Debug";
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        String data = intent.getDataString();

        if (action.equals("android.intent.action.VIEW") && data.matches("geo:.*")) {
            Preferences preferences = new Preferences(this);
            preferences.setDestination(data);
            MainActivity.preferences.setPreference_provider("External Intent");
        }

        if (destinationFragment == null) {
            destinationFragment = new DestinationFragment();
            destinationFragment.setRetainInstance(true);
        }

        if (directionFragment == null) {
            directionFragment = new DirectionFragment();
            directionFragment.setRetainInstance(true);
        }

        if (debugFragment == null) {
            debugFragment = new DebugFragment();
            debugFragment.setRetainInstance(true);
        }

        if (internet && play_services) {
            if (googleMapFragment == null) {
                googleMapFragment = new GoogleMapFragment();
                googleMapFragment.setRetainInstance(true);
            }
        }

        if (internet && play_services) {
            if (openStreetMapFragment == null) {
                openStreetMapFragment = new OpenStreetMapFragment();
                openStreetMapFragment.setRetainInstance(true);
            }
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(), getApplicationContext()));
        viewPager.setOffscreenPageLimit(100);

        /*
        up to here it's already working with swipes
         */
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });
    }

    private class CustomAdapter extends FragmentPagerAdapter {
        private String fragments [] = global_fragments;
        public CustomAdapter(FragmentManager fm, Context applicationContext) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return destinationFragment;
                case 1:
                    return directionFragment;
                case 2:
                    return debugFragment;
                case 3:
                    return googleMapFragment;
                case 4:
                    return openStreetMapFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case USER_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Status.append("Permission granted");
                    jens = true;
                } else {
                    //Status.append("Permission denied");
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
