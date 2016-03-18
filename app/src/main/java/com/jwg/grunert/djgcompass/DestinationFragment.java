package com.jwg.grunert.djgcompass;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.Locale;

public class DestinationFragment extends Fragment {
    EditText editTextDestinationLatitude, editTextDestinationLongitude;
    Button buttonDestinationManual, buttonDestinationOsmand, buttonDestinationReset, buttonHelp;
    LocationManager locationManager = null;
    Location lastknownlocation = null;

    public DestinationFragment() {
        // Required empty public constructor
    }

    private void buildAlertMessageResetLocation() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format(Locale.getDefault(),"You want to use last known location or Rome as reset location?"))
                .setCancelable(true)
                .setNegativeButton("Last known", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
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
                                lastknownlocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            }
                        }

                        if (lastknownlocation != null) {
                            MainActivity.preferences.clear(lastknownlocation);
                        } else {
                            MainActivity.preferences.clear();
                        }

                        locationManager = null;
                        editTextDestinationLatitude.setText(MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_STRING));
                        editTextDestinationLongitude.setText(MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_STRING));
                        editTextDestinationLatitude.clearFocus();
                        editTextDestinationLongitude.clearFocus();
                        // dialog.dismiss();
                    }
                })
                .setPositiveButton("Rome", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        MainActivity.preferences.clear();

                        editTextDestinationLatitude.setText(MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_STRING));
                        editTextDestinationLongitude.setText(MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_STRING));
                        editTextDestinationLatitude.clearFocus();
                        editTextDestinationLongitude.clearFocus();
                        // dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageNoOsmand() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Please install OSMAND or use tabs Google Maps or Open Street Map for direction.");

        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageHelp() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Compass points towards direction, shows current speed, direct distance to direction and driven distance." +
                "\n\nDestination can be set in 4 different ways: manually, osmand, google maps and open street maps. " +
                "Google maps and open street maps are only enabled if internet is on. Osmand works also offline. Reset sets direction to last known location or center of Rome." +
                "\n\nmanually: enter coordinates and switch to DIRECTION" +
                "\n\nosmand: intercept geo location shared by osmand (if installed on device)" +
                "\n\ngoogle maps: long press destination and switch to DIRECTION (google maps work only if enabled on device)" +
                "\n\nopen street map: center map to destination, click on share button, click on Geo URI (e.g. geo:39.5621,8.9410?z=12), accept and switch to DIRECTION");

        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    protected void hideSoftKeyboard(EditText top, EditText bottom) {
        top.setInputType(1);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(top.getWindowToken(), 0);
        bottom.setInputType(1);
        imm.hideSoftInputFromWindow(bottom.getWindowToken(), 0);
    }

    public void manual_coordinates() {
        MainActivity.preferences.setDestination(editTextDestinationLatitude.getText().toString(), editTextDestinationLongitude.getText().toString());
        hideSoftKeyboard(editTextDestinationLatitude, editTextDestinationLongitude);
        editTextDestinationLatitude.clearFocus();
        editTextDestinationLongitude.clearFocus();
    }

    public void reset_coordinates() {
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
                lastknownlocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }

        if (lastknownlocation != null) {
            MainActivity.preferences.clear(lastknownlocation);
        } else {
            MainActivity.preferences.clear();
        }

        locationManager = null;
        lastknownlocation = null;

        editTextDestinationLatitude.setText(MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_STRING));
        editTextDestinationLongitude.setText(MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_STRING));
        editTextDestinationLatitude.clearFocus();
        editTextDestinationLongitude.clearFocus();
    }

    public boolean isPackageExisted(String targetPackage) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public void osmand_coordinates() {
        if (isPackageExisted("net.osmand.plus")) {
            Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("net.osmand.plus");
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(launchIntent);
            getActivity().finish();
        } else if (isPackageExisted("net.osmand")) {
            Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("net.osmand");
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(launchIntent);
            getActivity().finish();
        } else {
            buildAlertMessageNoOsmand();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_destination, container, false);

        editTextDestinationLatitude = (EditText) view.findViewById(R.id.editTextDestinationLatitude);
        editTextDestinationLongitude = (EditText) view.findViewById(R.id.editTextDestinationLongitude);
        // buttonDestinationManual = (Button) view.findViewById(R.id.buttonDestinationManual);
        buttonDestinationOsmand = (Button) view.findViewById(R.id.buttonDestinationOsmand);
        buttonDestinationReset = (Button) view.findViewById(R.id.buttonDestinationReset);
        buttonHelp = (Button) view.findViewById(R.id.buttonHelp);

        editTextDestinationLatitude.setText(MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_STRING));
        editTextDestinationLongitude.setText(MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_STRING));
        editTextDestinationLatitude.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        editTextDestinationLongitude.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        /*
        buttonDestinationManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manual_coordinates();
            }
        });
        */

        buttonDestinationReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAlertMessageResetLocation();
                // reset_coordinates();
                // reset_coordinates();
            }
        });

        buttonDestinationOsmand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                osmand_coordinates();
            }
        });

        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAlertMessageHelp();
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {
            editTextDestinationLatitude.setText(MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_STRING));
            editTextDestinationLongitude.setText(MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_STRING));
        } else if (isResumed()) {
            MainActivity.preferences.setDestination(editTextDestinationLatitude.getText().toString(), editTextDestinationLongitude.getText().toString());
            MainActivity.preferences.setPreference_provider("Manual Entry");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        editTextDestinationLatitude.setText(MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_STRING));
        editTextDestinationLongitude.setText(MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_STRING));
    }

    @Override
    public void onPause() {
        super.onPause();
        // MainActivity.preferences.setDestination(editTextDestinationLatitude.getText().toString(), editTextDestinationLongitude.getText().toString());
    }
}
