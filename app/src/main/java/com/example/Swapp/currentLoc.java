package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class currentLoc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_loc);

        String category = getIntent().getStringExtra("category");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            if (category.equals("all")) {
                databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChildren()) {
                            Intent intent = new Intent(currentLoc.this, alert_dialog_noitem.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            CustomIntent.customType(currentLoc.this, "fadein-to-fadeout");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                databaseReference.child("items").orderByChild("Item_Category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChildren()) {
                            Intent intent = new Intent(currentLoc.this, alert_dialog_noitem.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            CustomIntent.customType(currentLoc.this, "fadein-to-fadeout");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            final MapsFragment mapsFragment = new MapsFragment();

            Bundle b = new Bundle();
            b.putString("from", getIntent().getStringExtra("from"));
            b.putString("category", getIntent().getStringExtra("category"));
            mapsFragment.setArguments(b);
            fragmentTransaction.add(R.id.container, mapsFragment).commit();
        }

}