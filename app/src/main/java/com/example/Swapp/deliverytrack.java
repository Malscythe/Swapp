package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;

import Swapp.R;

public class deliverytrack extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverytrack);

        AutoCompleteTextView courierDropDown = findViewById(R.id.courierList);
        AutoCompleteTextView couriers = findViewById(R.id.courierList);
        Button trackBtn = findViewById(R.id.trackBtn);


        String[] courierArr = getResources().getStringArray(R.array.courierList);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item, courierArr);
        couriers.setAdapter(arrayAdapter);

        WebView trackDisplay = findViewById(R.id.webView);
        WebSettings webSettings = trackDisplay.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);

        trackDisplay.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                return false;
            }
        });

        trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (courierDropDown.getText().toString().equals("Entrego")) {
                    trackDisplay.loadUrl("https://track.entrego.org/search");
                }  else if (courierDropDown.getText().toString().equals("JRMT Express")) {
                    trackDisplay.loadUrl("https://100parcels.com/en/jrmt-express");
                } else if (courierDropDown.getText().toString().equals("Ninja Van")) {
                    trackDisplay.loadUrl("https://www.ninjavan.co/en-ph/international/tracking");
                } else if (courierDropDown.getText().toString().equals("JT Express")) {
                    trackDisplay.loadUrl("https://www.jtexpress.ph/index/query/gzquery.html");
                } else if (courierDropDown.getText().toString().equals("2GO Express")) {
                    trackDisplay.loadUrl("https://100parcels.com/en/2go");
                } else if (courierDropDown.getText().toString().equals("LBC Express")) {
                    trackDisplay.loadUrl("https://www.lbcexpress.com/track/");
                } else if (courierDropDown.getText().toString().equals("Others")) {
                    trackDisplay.loadUrl("https://100parcels.com/");
                }
            }
        });
    }
}