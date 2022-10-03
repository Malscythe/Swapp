package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kofigyan.stateprogressbar.StateProgressBar;

import Swapp.R;
import Swapp.databinding.ActivityPostItemS2Binding;
import Swapp.databinding.ActivityPostItemS3Binding;

public class PostItem_S3 extends AppCompatActivity {

    String[] descriptionData = {"Details", "Description", "Location", "Images"};
    Button nextBtn, getLocationBtn;
    String currentState, street, city, postal_code, state, country, barangay, house_no;
    LinearLayout preLocation, postLocation;
    ActivityPostItemS3Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostItemS3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StateProgressBar stateProgressBar = findViewById(R.id.stateProgress);
        stateProgressBar.setStateDescriptionData(descriptionData);

        currentState = getIntent().getStringExtra("currentState");
        street = getIntent().getStringExtra("street");
        city = getIntent().getStringExtra("city");
        postal_code = getIntent().getStringExtra("postalCode");
        state = getIntent().getStringExtra("state");
        country = getIntent().getStringExtra("country");
        barangay = getIntent().getStringExtra("brgy");
        house_no = getIntent().getStringExtra("houseNo");

        String houseStreet = house_no + " " + street;

        if (currentState.equals("postLocation")) {
            preLocation.setVisibility(View.GONE);
            postLocation.setVisibility(View.VISIBLE);

            binding.barangay.setText(barangay);
            binding.street.setText(houseStreet);
            binding.city.setText(city);
            binding.postalCode.setText(postal_code);
            binding.state.setText(state);
            binding.country.setText(country);
        } else if (currentState.equals("preLocation")) {
            preLocation.setVisibility(View.VISIBLE);
            postLocation.setVisibility(View.GONE);
        }

        nextBtn = findViewById(R.id.nextBtn);
        getLocationBtn = findViewById(R.id.getLocation);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostItem_S3.this, currentLoc.class);
                intent.putExtra("from", "postitem");
                intent.putExtra("category", "postitem");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}