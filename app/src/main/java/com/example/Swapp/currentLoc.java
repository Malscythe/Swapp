package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.auth.User;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class currentLoc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_loc);

        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final MapsFragment mapsFragment = new MapsFragment();

        Bundle b = new Bundle();
        b.putString("fromUserHomepage", getIntent().getStringExtra("fromUserHomepage"));
        mapsFragment.setArguments(b);
        fragmentTransaction.add(R.id.container, mapsFragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getIntent().getStringExtra("fromUserHomepage").equals("true")) {
            startActivity(new Intent(currentLoc.this, UserHomepage.class));
            CustomIntent.customType(currentLoc.this, "right-to-left");
        } else {
            Intent intent = new Intent(currentLoc.this, PostItem.class);
            intent.putExtra("address", "");
            startActivity(intent);
            CustomIntent.customType(currentLoc.this, "right-to-left");
        }

    }
}