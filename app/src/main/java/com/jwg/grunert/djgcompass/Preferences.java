package com.jwg.grunert.djgcompass;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import java.text.NumberFormat;
import java.util.Locale;
import java.text.ParseException;


/**
 * Created by Werner-Jens Grunert on 3/9/2016.
 */
public class Preferences {
    static final int RETURN_AS_STRING = 10;
    static final double RETURN_AS_DOUBLE = 1.0f;
    // static final double DEFAULT_DESTINATION_LATITUDE = 45.463890f; // Milano
    // static final double DEFAULT_DESTINATION_LONGITUDE = 9.189277f; // Milano
    static final double DEFAULT_DESTINATION_LATITUDE = 41.90269452f; // Rome
    static final double DEFAULT_DESTINATION_LONGITUDE = 12.49623042f; // Rome



    static String preference_provider = "default";
    static String reset_location = "default";

    SharedPreferences sharedPreferences;

    double LatitudeDestination = DEFAULT_DESTINATION_LATITUDE; // Center Milan
    double LongitudeDestination = DEFAULT_DESTINATION_LONGITUDE;

    Context context;

    public Preferences(double latitudeDestination, double longitudeDestination, Context context) {
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);
        LatitudeDestination = latitudeDestination;
        LongitudeDestination = longitudeDestination;

        if (sharedPreferences.contains("Latitude") == false) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Latitude", String.format(Locale.getDefault(),"%.8f", latitudeDestination));
            editor.commit();
        }

        if (sharedPreferences.contains("Longitude") == false) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Longitude", String.format(Locale.getDefault(),"%.8f", longitudeDestination));
            editor.commit();
        }

        this.context = context;
    }

    public Preferences(Context context) {
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);
        double latitudeDestination = DEFAULT_DESTINATION_LATITUDE;
        double longitudeDestination = DEFAULT_DESTINATION_LONGITUDE;

        if (sharedPreferences.contains("Latitude") == false) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Latitude", String.format(Locale.getDefault(),"%.8f", latitudeDestination));
            editor.commit();
        }

        if (sharedPreferences.contains("Longitude") == false) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Longitude", String.format(Locale.getDefault(),"%.8f", longitudeDestination));
            editor.commit();
        }

        this.context = context;
    }

    public String getPreference_provider() {
        return preference_provider;
    }

    public void setPreference_provider(String preference_provider) {
        this.preference_provider = preference_provider;
    }

    public String getReset_location() {
        return reset_location;
    }

    public void setReset_location(String reset_location) {
        this.reset_location = reset_location;
    }

    public double getLatitudeDestination(double i) {
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);
        String latitude;
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        Number number;

        if (sharedPreferences.contains("Latitude")) {
            latitude = (sharedPreferences.getString("Latitude", "Empty"));
            try {
                number = format.parse(latitude);
                LatitudeDestination = number.floatValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return LatitudeDestination;
    }

    public String getLatitudeDestination(int i) {
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);


        if (sharedPreferences.contains("Latitude")) {
            return (sharedPreferences.getString("Latitude", "Empty"));
        } else {
            return String.format(Locale.getDefault(),"%.6f",LatitudeDestination);
        }
    }

    public void setLatitudeDestination(double latitudeDestination) {
        LatitudeDestination = latitudeDestination;
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Latitude", String.format(Locale.getDefault(),"%.8f", latitudeDestination));
        editor.commit();
    }

    public double getLongitudeDestination(double i) {
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);
        String longitude;
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        Number number;

        if (sharedPreferences.contains("Longitude")) {
            longitude = (sharedPreferences.getString("Longitude", "Empty"));
            try {
                number = format.parse(longitude);
                LongitudeDestination = number.floatValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return LongitudeDestination;
    }

    public String getLongitudeDestination(int i) {
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Longitude")) {
            return (sharedPreferences.getString("Longitude", "Empty"));
        } else {
            return String.format(Locale.getDefault(),"%.6f",LongitudeDestination);
        }
    }

    public void setLongitudeDestination(double longitudeDestination) {
        LongitudeDestination = longitudeDestination;
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Longitude", String.format(Locale.getDefault(),"%.8f", longitudeDestination));
        editor.commit();
    }

    public void setDestination(double latitudeDestination, double longitudeDestination) {
        LatitudeDestination = latitudeDestination;
        LongitudeDestination = longitudeDestination;
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Latitude", String.format(Locale.getDefault(),"%.8f", LatitudeDestination));
        editor.putString("Longitude", String.format(Locale.getDefault(),"%.8f", LongitudeDestination));
        editor.commit();
    }

    public void setDestination(String data) {
        String array[] = data.split(":|,|\\?");
        LatitudeDestination = Float.valueOf(array[1]);
        LongitudeDestination = Float.valueOf(array[2]);
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Latitude", String.format(Locale.getDefault(),"%.8f", LatitudeDestination));
        editor.putString("Longitude", String.format(Locale.getDefault(),"%.8f", LongitudeDestination));
        editor.commit();
    }

    public void setDestination(String latitude, String longitude) {
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        Number number;
        try {
            number = format.parse(latitude);
            LatitudeDestination = number.floatValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            number = format.parse(longitude);
            LongitudeDestination = number.floatValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Latitude", String.format(Locale.getDefault(),"%.8f", LatitudeDestination));
        editor.putString("Longitude", String.format(Locale.getDefault(),"%.8f", LongitudeDestination));
        editor.commit();
    }

    public void clear() {
        reset_location = "Default location (Rome center)";
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear().commit();
        editor.putString("Latitude", String.format(Locale.getDefault(),"%.8f", DEFAULT_DESTINATION_LATITUDE));
        editor.putString("Longitude", String.format(Locale.getDefault(),"%.8f", DEFAULT_DESTINATION_LONGITUDE));
        editor.commit();
    }

    public void clear(Location location) {
        reset_location = "Last known";
        sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
        editor.putString("Latitude", String.format(Locale.getDefault(),"%.8f", location.getLatitude()));
        editor.putString("Longitude", String.format(Locale.getDefault(),"%.8f", location.getLongitude()));
        editor.commit();
    }
}
