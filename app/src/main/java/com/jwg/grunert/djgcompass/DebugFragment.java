package com.jwg.grunert.djgcompass;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class DebugFragment extends Fragment {
    TextView textViewDebug1;
    DebugInfo debugInfo;


    public DebugFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_debug, container, false);

        textViewDebug1 = (TextView) view.findViewById(R.id.textViewDebug1);

        debugInfo = new DebugInfo();

        textViewDebug1.setText("Location Enabled: " + DebugInfo.jens
                + "\nInternet: "+ DebugInfo.internet
                + "\nPlay Services: "+ DebugInfo.play_services
                + "\nNetwork Provider: "+DebugInfo.network_provider
                + "\nReset Location: " + MainActivity.preferences.getReset_location()
                + "\nDestination Location: " + MainActivity.preferences.getPreference_provider()
                + "\nLocation Provider: "+DebugInfo.provider
                + "\nLocation Accuracy: "+DebugInfo.accuracy);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            textViewDebug1.setText("Location Enabled: " + DebugInfo.jens
                    + "\nInternet: "+ DebugInfo.internet
                    + "\nPlay Services: "+ DebugInfo.play_services
                    + "\nNetwork Provider: "+DebugInfo.network_provider
                    + "\nReset Location: " + MainActivity.preferences.getReset_location()
                    + "\nDestination Location: " + MainActivity.preferences.getPreference_provider()
                    + "\nLocation Provider: "+DebugInfo.provider
                    + "\nLocation Accuracy: "+DebugInfo.accuracy);
        } else if (isResumed()) {

        }
    }
}
