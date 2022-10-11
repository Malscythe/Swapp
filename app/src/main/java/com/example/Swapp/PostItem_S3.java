package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.ArrayList;

import Swapp.R;
import Swapp.databinding.ActivityPostItemS2Binding;
import Swapp.databinding.ActivityPostItemS3Binding;
import maes.tech.intentanim.CustomIntent;

public class PostItem_S3 extends AppCompatActivity {

    private static final String TAG = "TAG";
    String[] descriptionData = {"Details", "Description", "Location", "Images"};
    Button nextBtn, getLocationBtn;
    String currentState, street, city, longitude, latitude, state, country, barangay, house_no;
    LinearLayout preLocation, postLocation;
    ActivityPostItemS3Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostItemS3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.w(TAG, "post item s3");

        StateProgressBar stateProgressBar = findViewById(R.id.stateProgress);
        stateProgressBar.setStateDescriptionData(descriptionData);

        currentState = getIntent().getStringExtra("currentState");
        street = getIntent().getStringExtra("street");
        city = getIntent().getStringExtra("city");
        state = getIntent().getStringExtra("state");
        country = getIntent().getStringExtra("country");
        barangay = getIntent().getStringExtra("brgy");
        house_no = getIntent().getStringExtra("houseNo");
        longitude = getIntent().getStringExtra("longitude");
        latitude = getIntent().getStringExtra("latitude");

        preLocation = findViewById(R.id.preLocation);
        postLocation = findViewById(R.id.postLocation);

        String houseStreet = house_no + " " + street;

        if (currentState.equals("postLocation")) {
            preLocation.setVisibility(View.GONE);
            postLocation.setVisibility(View.VISIBLE);

            binding.barangay.setText(barangay);
            binding.street.setText(houseStreet);
            binding.city.setText(city);
            binding.state.setText(state);
            binding.country.setText(country);
        } else if (currentState.equals("preLocation")) {
            preLocation.setVisibility(View.VISIBLE);
            postLocation.setVisibility(View.GONE);
        }

        nextBtn = findViewById(R.id.nextBtn);
        getLocationBtn = findViewById(R.id.getLocation);

        String category = getIntent().getStringExtra("category");

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PostItem_S3.this, PostItem_S4.class);

                if (TextUtils.isEmpty(binding.street.getText().toString())) {
                    binding.streetL.setError("This cannot be empty.");
                    binding.streetL.setErrorIconDrawable(null);
                    return;
                } else {
                    binding.streetL.setError(null);
                    binding.streetL.setErrorIconDrawable(null);
                }

                if (TextUtils.isEmpty(binding.barangay.getText().toString())) {
                    binding.barangayL.setError("This cannot be empty.");
                    binding.barangayL.setErrorIconDrawable(null);
                    return;
                } else {
                    binding.barangayL.setError(null);
                    binding.barangayL.setErrorIconDrawable(null);
                }

                if (TextUtils.isEmpty(binding.city.getText().toString())) {
                    binding.cityL.setError("This cannot be empty.");
                    binding.cityL.setErrorIconDrawable(null);
                    return;
                } else {
                    binding.cityL.setError(null);
                    binding.cityL.setErrorIconDrawable(null);
                }

                if (TextUtils.isEmpty(binding.state.getText().toString())) {
                    binding.stateL.setError("This cannot be empty.");
                    binding.stateL.setErrorIconDrawable(null);
                    return;
                } else {
                    binding.stateL.setError(null);
                    binding.stateL.setErrorIconDrawable(null);
                }

                if (TextUtils.isEmpty(binding.country.getText().toString())) {
                    binding.countryL.setError("This cannot be empty.");
                    binding.countryL.setErrorIconDrawable(null);
                    return;
                } else {
                    binding.countryL.setError(null);
                    binding.countryL.setErrorIconDrawable(null);
                }

                switch (category) {
                    case "Men's Apparel":

                        intent.putExtra("mensClothingType", getIntent().getStringExtra("mensClothingType"));
                        intent.putExtra("mensBrand", getIntent().getStringExtra("mensBrand"));
                        intent.putExtra("mensColor", getIntent().getStringExtra("mensColor"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("mensMaterial", getIntent().getStringExtra("mensMaterial"));
                        intent.putExtra("mensUsage", getIntent().getStringExtra("mensUsage"));
                        intent.putExtra("mensSizes", getIntent().getStringExtra("mensSizes"));
                        intent.putExtra("mensDescription", getIntent().getStringExtra("mensDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Gadgets":

                        intent.putExtra("gadgetType", getIntent().getStringExtra("gadgetType"));
                        intent.putExtra("gadgetBrand", getIntent().getStringExtra("gadgetBrand"));
                        intent.putExtra("gadgetColor", getIntent().getStringExtra("gadgetColor"));
                        intent.putExtra("gadgetUsage", getIntent().getStringExtra("gadgetUsage"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("gadgetDescription", getIntent().getStringExtra("gadgetDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Game":

                        intent.putExtra("gameType", getIntent().getStringExtra("gameType"));
                        intent.putExtra("gameBrand", getIntent().getStringExtra("gameBrand"));
                        intent.putExtra("gameUsage", getIntent().getStringExtra("gameUsage"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("gameDescription", getIntent().getStringExtra("gameDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Bags":

                        intent.putExtra("bagType", getIntent().getStringExtra("bagType"));
                        intent.putExtra("bagBrand", getIntent().getStringExtra("bagBrand"));
                        intent.putExtra("bagColor", getIntent().getStringExtra("bagColor"));
                        intent.putExtra("bagUsage", getIntent().getStringExtra("bagUsage"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("bagDescription", getIntent().getStringExtra("bagDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Groceries":

                        intent.putExtra("groceryList", getIntent().getStringExtra("groceryList"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Furniture":

                        intent.putExtra("furnitureBrand", getIntent().getStringExtra("furnitureBrand"));
                        intent.putExtra("furnitureColor", getIntent().getStringExtra("furnitureColor"));
                        intent.putExtra("furnitureUsage", getIntent().getStringExtra("furnitureUsage"));
                        intent.putExtra("furnitureHeight", getIntent().getStringExtra("furnitureHeight"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("furnitureWidth", getIntent().getStringExtra("furnitureWidth"));
                        intent.putExtra("furnitureLength", getIntent().getStringExtra("furnitureLength"));
                        intent.putExtra("furnitureDescription", getIntent().getStringExtra("furnitureDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Babies & Kids":

                        intent.putExtra("bnkAge", getIntent().getStringExtra("bnkAge"));
                        intent.putExtra("bnkBrand", getIntent().getStringExtra("bnkBrand"));
                        intent.putExtra("bnkType", getIntent().getStringExtra("bnkType"));
                        intent.putExtra("bnkUsage", getIntent().getStringExtra("bnkUsage"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("bnkDescription", getIntent().getStringExtra("bnkDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Appliances":

                        intent.putExtra("appliancesType", getIntent().getStringExtra("appliancesType"));
                        intent.putExtra("appliancesBrand", getIntent().getStringExtra("appliancesBrand"));
                        intent.putExtra("appliancesColor", getIntent().getStringExtra("appliancesColor"));
                        intent.putExtra("appliancesUsage", getIntent().getStringExtra("appliancesUsage"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("appliancesDescription", getIntent().getStringExtra("appliancesDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Motors":

                        intent.putExtra("motorModel", getIntent().getStringExtra("motorModel"));
                        intent.putExtra("motorBrand", getIntent().getStringExtra("motorBrand"));
                        intent.putExtra("motorColor", getIntent().getStringExtra("motorColor"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("motorUsage", getIntent().getStringExtra("motorUsage"));
                        intent.putExtra("motorDescription", getIntent().getStringExtra("motorDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Audio":

                        intent.putExtra("audioArtist", getIntent().getStringExtra("audioArtist"));
                        intent.putExtra("audioReleaseDate", getIntent().getStringExtra("audioReleaseDate"));
                        intent.putExtra("audioUsage", getIntent().getStringExtra("audioUsage"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("audioDescription", getIntent().getStringExtra("audioDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "School":

                        intent.putExtra("schoolType", getIntent().getStringExtra("schoolType"));
                        intent.putExtra("schoolBrand", getIntent().getStringExtra("schoolBrand"));
                        intent.putExtra("schoolColor", getIntent().getStringExtra("schoolColor"));
                        intent.putExtra("schoolUsage", getIntent().getStringExtra("schoolUsage"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("schoolDescription", getIntent().getStringExtra("schoolDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Women's Apparel":

                        intent.putExtra("womensClothingType", getIntent().getStringExtra("womensClothingType"));
                        intent.putExtra("womensBrand", getIntent().getStringExtra("womensBrand"));
                        intent.putExtra("womensColor", getIntent().getStringExtra("womensColor"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("womensMaterial", getIntent().getStringExtra("womensMaterial"));
                        intent.putExtra("womensUsage", getIntent().getStringExtra("womensUsage"));
                        intent.putExtra("womensSizes", getIntent().getStringExtra("womensSizes"));
                        intent.putExtra("womensDescription", getIntent().getStringExtra("womensDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Others":

                        intent.putExtra("otherType", getIntent().getStringExtra("otherType"));
                        intent.putExtra("otherDescription", getIntent().getStringExtra("otherDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("category", getIntent().getStringExtra("category"));
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.putExtra("street", binding.street.getText().toString());
                        intent.putExtra("barangay", binding.barangay.getText().toString());
                        intent.putExtra("city", binding.city.getText().toString());
                        intent.putExtra("state", binding.state.getText().toString());
                        intent.putExtra("country", binding.country.getText().toString());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                }
            }
        });

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PostItem_S3.this, currentLoc.class);

                switch (category) {
                    case "Men's Apparel":

                        intent.putExtra("mensClothingType", getIntent().getStringExtra("mensClothingType"));
                        intent.putExtra("mensBrand", getIntent().getStringExtra("mensBrand"));
                        intent.putExtra("mensColor", getIntent().getStringExtra("mensColor"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("mensMaterial", getIntent().getStringExtra("mensMaterial"));
                        intent.putExtra("mensUsage", getIntent().getStringExtra("mensUsage"));
                        intent.putExtra("mensSizes", getIntent().getStringExtra("mensSizes"));
                        intent.putExtra("mensDescription", getIntent().getStringExtra("mensDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("from", "postitem");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Gadgets":

                        intent.putExtra("gadgetType", getIntent().getStringExtra("gadgetType"));
                        intent.putExtra("gadgetBrand", getIntent().getStringExtra("gadgetBrand"));
                        intent.putExtra("gadgetColor", getIntent().getStringExtra("gadgetColor"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("gadgetUsage", getIntent().getStringExtra("gadgetUsage"));
                        intent.putExtra("gadgetDescription", getIntent().getStringExtra("gadgetDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("from", "postitem");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Game":

                        intent.putExtra("gameType", getIntent().getStringExtra("gameType"));
                        intent.putExtra("gameBrand", getIntent().getStringExtra("gameBrand"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("gameUsage", getIntent().getStringExtra("gameUsage"));
                        intent.putExtra("gameDescription", getIntent().getStringExtra("gameDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("from", "postitem");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Bags":

                        intent.putExtra("bagType", getIntent().getStringExtra("bagType"));
                        intent.putExtra("bagBrand", getIntent().getStringExtra("bagBrand"));
                        intent.putExtra("bagColor", getIntent().getStringExtra("bagColor"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("bagUsage", getIntent().getStringExtra("bagUsage"));
                        intent.putExtra("bagDescription", getIntent().getStringExtra("bagDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("from", "postitem");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Groceries":

                        intent.putExtra("groceryList", getIntent().getStringExtra("groceryList"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("from", "postitem");
                        intent.putExtra("Offers", "false");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Furniture":

                        intent.putExtra("furnitureBrand", getIntent().getStringExtra("furnitureBrand"));
                        intent.putExtra("furnitureColor", getIntent().getStringExtra("furnitureColor"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("furnitureUsage", getIntent().getStringExtra("furnitureUsage"));
                        intent.putExtra("furnitureHeight", getIntent().getStringExtra("furnitureHeight"));
                        intent.putExtra("furnitureWidth", getIntent().getStringExtra("furnitureWidth"));
                        intent.putExtra("furnitureLength", getIntent().getStringExtra("furnitureLength"));
                        intent.putExtra("furnitureDescription", getIntent().getStringExtra("furnitureDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("from", "postitem");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Babies & Kids":

                        intent.putExtra("bnkAge", getIntent().getStringExtra("bnkAge"));
                        intent.putExtra("bnkBrand", getIntent().getStringExtra("bnkBrand"));
                        intent.putExtra("bnkType", getIntent().getStringExtra("bnkType"));
                        intent.putExtra("bnkUsage", getIntent().getStringExtra("bnkUsage"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("bnkDescription", getIntent().getStringExtra("bnkDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("from", "postitem");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Appliances":

                        intent.putExtra("appliancesType", getIntent().getStringExtra("appliancesType"));
                        intent.putExtra("appliancesBrand", getIntent().getStringExtra("appliancesBrand"));
                        intent.putExtra("appliancesColor", getIntent().getStringExtra("appliancesColor"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("appliancesUsage", getIntent().getStringExtra("appliancesUsage"));
                        intent.putExtra("appliancesDescription", getIntent().getStringExtra("appliancesDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("from", "postitem");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Motors":

                        intent.putExtra("motorModel", getIntent().getStringExtra("motorModel"));
                        intent.putExtra("motorBrand", getIntent().getStringExtra("motorBrand"));
                        intent.putExtra("motorColor", getIntent().getStringExtra("motorColor"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("motorUsage", getIntent().getStringExtra("motorUsage"));
                        intent.putExtra("motorDescription", getIntent().getStringExtra("motorDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("from", "postitem");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Audio":

                        intent.putExtra("audioArtist", getIntent().getStringExtra("audioArtist"));
                        intent.putExtra("audioReleaseDate", getIntent().getStringExtra("audioReleaseDate"));
                        intent.putExtra("audioUsage", getIntent().getStringExtra("audioUsage"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("audioDescription", getIntent().getStringExtra("audioDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("from", "postitem");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "School":

                        intent.putExtra("schoolType", getIntent().getStringExtra("schoolType"));
                        intent.putExtra("schoolBrand", getIntent().getStringExtra("schoolBrand"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("schoolColor", getIntent().getStringExtra("schoolColor"));
                        intent.putExtra("schoolUsage", getIntent().getStringExtra("schoolUsage"));
                        intent.putExtra("schoolDescription", getIntent().getStringExtra("schoolDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("from", "postitem");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Women's Apparel":

                        intent.putExtra("womensClothingType", getIntent().getStringExtra("womensClothingType"));
                        intent.putExtra("womensBrand", getIntent().getStringExtra("womensBrand"));
                        intent.putExtra("womensColor", getIntent().getStringExtra("womensColor"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("womensMaterial", getIntent().getStringExtra("womensMaterial"));
                        intent.putExtra("womensUsage", getIntent().getStringExtra("womensUsage"));
                        intent.putExtra("womensSizes", getIntent().getStringExtra("womensSizes"));
                        intent.putExtra("womensDescription", getIntent().getStringExtra("womensDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("from", "postitem");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                        break;
                    case "Others":

                        intent.putExtra("otherType", getIntent().getStringExtra("otherType"));
                        intent.putExtra("otherDescription", getIntent().getStringExtra("otherDescription"));
                        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
                        intent.putExtra("rft", getIntent().getStringExtra("rft"));
                        intent.putExtra("Offers", "false");
                        intent.putExtra("from", "postitem");
                        intent.putExtra("category", "postitem");
                        intent.putExtra("category1", category);
                        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S3.this, "left-to-right");

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PostItem_S3.this, PostItem_S2.class);
        intent.putExtra("category", getIntent().getStringExtra("category"));
        intent.putExtra("item_name", getIntent().getStringExtra("item_name"));
        intent.putExtra("rft", getIntent().getStringExtra("rft"));
        intent.putExtra("pref_item", getIntent().getStringExtra("pref_item"));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        CustomIntent.customType(PostItem_S3.this, "right-to-left");
    }
}