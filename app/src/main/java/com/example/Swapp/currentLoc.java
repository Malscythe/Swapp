package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import Swapp.R;

public class currentLoc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_loc);

        getSupportFragmentManager().beginTransaction().add(R.id.container, new MapsFragment()).commit();
    }
}