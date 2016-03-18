package com.jwg.grunert.djgcompass;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class OpenStreetMapFragment extends Fragment {
    static WebView myWebView;
    static TextView textView;
    static double LatitudeDestination, LongitudeDestination;

    ProgressDialog progressDialog;
    ProgressBar progressBar;

    static String myAddr;

    String ua = "Mozilla/5.0 (Android; Tablet; rv:20.0) Gecko/20.0 Firefox/20.0";

    public OpenStreetMapFragment() {
        // Required empty public constructor
    }

    private void buildAlertMessageUseCoordinates() {
        myAddr = String.format("http://www.openstreetmap.org/?mlat=%.6f&mlon=%.6f&zoom=12", LatitudeDestination, LongitudeDestination);
        myAddr = myAddr.replaceAll(",",".");
        textView.setText(myAddr);
        myWebView.loadUrl(myAddr);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format(Locale.getDefault(),"You want to use these coordinates?\n%.6f:\n%.6f:", LatitudeDestination, LongitudeDestination))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                        MainActivity.preferences.setDestination(LatitudeDestination, LongitudeDestination);
                        MainActivity.preferences.setPreference_provider("Open Street Map");
                        // MainActivity.viewPager.setCurrentItem(1);
                        /*
                        myAddr = String.format("http://www.openstreetmap.org/?mlat=%.6f&mlon=%.6f&zoom=12", LatitudeDestination, LongitudeDestination);
                        myAddr = myAddr.replaceAll(",",".");
                        textView.setText(myAddr);
                        myWebView.loadUrl(myAddr);
                        */
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_open_street_map, container, false);

        myWebView = (WebView) view.findViewById(R.id.webview);
        textView = (TextView) view.findViewById(R.id.textView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        myWebView.getSettings().setUserAgentString(ua);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        if ( !isNetworkAvailable() ) { // loading offline
            myWebView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }

        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                textView.setText(url);
                if (url.matches("geo:.*")) {
                    String array[] = url.split(":|,|\\?");
                    LatitudeDestination = Float.valueOf(array[1]);
                    LongitudeDestination = Float.valueOf(array[2]);
                    buildAlertMessageUseCoordinates();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
/*
                if (progressDialog!=null) {
                    progressDialog.dismiss();
                }
                */
            }


        });

        LatitudeDestination = MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_DOUBLE);
        LongitudeDestination = MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_DOUBLE);

        myAddr = String.format("http://www.openstreetmap.org/?mlat=%.6f&mlon=%.6f&zoom=12", LatitudeDestination, LongitudeDestination);
        myAddr = myAddr.replaceAll(",", ".");

        // progressDialog = ProgressDialog.show(getActivity(), "Aguarde...", "Processando sua reques.", true);

        myWebView.loadUrl(myAddr);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            LatitudeDestination = MainActivity.preferences.getLatitudeDestination(Preferences.RETURN_AS_DOUBLE);
            LongitudeDestination = MainActivity.preferences.getLongitudeDestination(Preferences.RETURN_AS_DOUBLE);
            myAddr = String.format("http://www.openstreetmap.org/?mlat=%.6f&mlon=%.6f&zoom=12", LatitudeDestination, LongitudeDestination);
            myAddr = myAddr.replaceAll(",", ".");
            myWebView.loadUrl(myAddr);
            textView.setText(myAddr);

        } else if (isResumed()) {

        }
    }
}
