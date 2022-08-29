package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import Swapp.R;

public class deliverytrack extends AppCompatActivity {

    ImageView entrego, jrmt, gogo, lbc, jtexp, ninjavan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverytrack);

        entrego = findViewById(R.id.btnEntrego);
        jrmt = findViewById(R.id.btnjrmt);
        gogo = findViewById(R.id.btngogo);
        jtexp = findViewById(R.id.btnjt);
        lbc = findViewById(R.id.btnlbc);
        ninjavan = findViewById(R.id.btnninja);


        entrego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://track.entrego.org/search");
            }
        });

        jrmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://parcelsapp.com/en/carriers/jrmt-express");
            }
        });

        gogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://app.gogoxpress.com/track");
            }
        });

        jtexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://www.jtexpress.ph/index/query/gzquery.html");
            }
        });

        lbc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://www.lbcexpress.com/track/");
            }
        });

        ninjavan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://www.ninjavan.co/en-ph/tracking");
            }
        });

    }

    private void gotoUrl(String s) {

        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));

    }
}