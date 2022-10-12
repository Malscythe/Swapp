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
                    } else {
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

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (category.equals("postitem")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            final MapsFragment mapsFragment = new MapsFragment();

            Bundle b = new Bundle();

            String category1 = getIntent().getStringExtra("category1");
            String isOffer = getIntent().getStringExtra("Offers");

            if (isOffer.equals("true")) {
                switch (category1) {
                    case "Men's Apparel":

                        b.putString("mensClothingType", getIntent().getStringExtra("mensClothingType"));
                        b.putString("mensBrand", getIntent().getStringExtra("mensBrand"));
                        b.putString("mensColor", getIntent().getStringExtra("mensColor"));
                        b.putString("Offers", isOffer);
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("mensMaterial", getIntent().getStringExtra("mensMaterial"));
                        b.putString("mensUsage", getIntent().getStringExtra("mensUsage"));
                        b.putString("mensSizes", getIntent().getStringExtra("mensSizes"));
                        b.putString("mensDescription", getIntent().getStringExtra("mensDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Gadgets":

                        b.putString("gadgetType", getIntent().getStringExtra("gadgetType"));
                        b.putString("gadgetBrand", getIntent().getStringExtra("gadgetBrand"));
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("Offers", isOffer);
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("gadgetColor", getIntent().getStringExtra("gadgetColor"));
                        b.putString("gadgetUsage", getIntent().getStringExtra("gadgetUsage"));
                        b.putString("gadgetDescription", getIntent().getStringExtra("gadgetDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Game":

                        b.putString("gameType", getIntent().getStringExtra("gameType"));
                        b.putString("gameBrand", getIntent().getStringExtra("gameBrand"));
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("Offers", isOffer);
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("gameUsage", getIntent().getStringExtra("gameUsage"));
                        b.putString("gameDescription", getIntent().getStringExtra("gameDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));

                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Bags":

                        b.putString("bagType", getIntent().getStringExtra("bagType"));
                        b.putString("bagBrand", getIntent().getStringExtra("bagBrand"));
                        b.putString("bagColor", getIntent().getStringExtra("bagColor"));
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("Offers", isOffer);
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("bagUsage", getIntent().getStringExtra("bagUsage"));
                        b.putString("bagDescription", getIntent().getStringExtra("bagDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));

                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Groceries":

                        b.putString("groceryList", getIntent().getStringExtra("groceryList"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("Offers", isOffer);
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));

                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Furniture":

                        b.putString("furnitureBrand", getIntent().getStringExtra("furnitureBrand"));
                        b.putString("furnitureColor", getIntent().getStringExtra("furnitureColor"));
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("Offers", isOffer);
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("furnitureUsage", getIntent().getStringExtra("furnitureUsage"));
                        b.putString("furnitureHeight", getIntent().getStringExtra("furnitureHeight"));
                        b.putString("furnitureWidth", getIntent().getStringExtra("furnitureWidth"));
                        b.putString("furnitureLength", getIntent().getStringExtra("furnitureLength"));
                        b.putString("furnitureDescription", getIntent().getStringExtra("furnitureDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));

                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Babies & Kids":

                        b.putString("bnkAge", getIntent().getStringExtra("bnkAge"));
                        b.putString("bnkBrand", getIntent().getStringExtra("bnkBrand"));
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("Offers", isOffer);
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("bnkType", getIntent().getStringExtra("bnkType"));
                        b.putString("bnkUsage", getIntent().getStringExtra("bnkUsage"));
                        b.putString("bnkDescription", getIntent().getStringExtra("bnkDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));

                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Appliances":

                        b.putString("appliancesType", getIntent().getStringExtra("appliancesType"));
                        b.putString("appliancesBrand", getIntent().getStringExtra("appliancesBrand"));
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("Offers", isOffer);
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("appliancesColor", getIntent().getStringExtra("appliancesColor"));
                        b.putString("appliancesUsage", getIntent().getStringExtra("appliancesUsage"));
                        b.putString("appliancesDescription", getIntent().getStringExtra("appliancesDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));

                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Motors":

                        b.putString("motorModel", getIntent().getStringExtra("motorModel"));
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("Offers", isOffer);
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("motorBrand", getIntent().getStringExtra("motorBrand"));
                        b.putString("motorColor", getIntent().getStringExtra("motorColor"));
                        b.putString("motorUsage", getIntent().getStringExtra("motorUsage"));
                        b.putString("motorDescription", getIntent().getStringExtra("motorDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));

                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Audio":

                        b.putString("audioArtist", getIntent().getStringExtra("audioArtist"));
                        b.putString("audioReleaseDate", getIntent().getStringExtra("audioReleaseDate"));
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("Offers", isOffer);
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("audioUsage", getIntent().getStringExtra("audioUsage"));
                        b.putString("audioDescription", getIntent().getStringExtra("audioDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));

                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "School":

                        b.putString("schoolType", getIntent().getStringExtra("schoolType"));
                        b.putString("schoolBrand", getIntent().getStringExtra("schoolBrand"));
                        b.putString("schoolColor", getIntent().getStringExtra("schoolColor"));
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("Offers", isOffer);
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("schoolUsage", getIntent().getStringExtra("schoolUsage"));
                        b.putString("schoolDescription", getIntent().getStringExtra("schoolDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));

                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Women's Apparel":

                        b.putString("womensClothingType", getIntent().getStringExtra("womensClothingType"));
                        b.putString("womensBrand", getIntent().getStringExtra("womensBrand"));
                        b.putString("womensColor", getIntent().getStringExtra("womensColor"));
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("Offers", isOffer);
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("womensMaterial", getIntent().getStringExtra("womensMaterial"));
                        b.putString("womensUsage", getIntent().getStringExtra("womensUsage"));
                        b.putString("womensSizes", getIntent().getStringExtra("womensSizes"));
                        b.putString("womensDescription", getIntent().getStringExtra("womensDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));

                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Others":

                        b.putString("otherType", getIntent().getStringExtra("otherType"));
                        b.putString("otherDescription", getIntent().getStringExtra("otherDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("uid", getIntent().getStringExtra("uid"));
                        b.putString("Offers", isOffer);
                        b.putString("itemname", getIntent().getStringExtra("itemname"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));

                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                }
            } else {

                switch (category1) {
                    case "Men's Apparel":

                        b.putString("Offers", isOffer);
                        b.putString("mensClothingType", getIntent().getStringExtra("mensClothingType"));
                        b.putString("mensBrand", getIntent().getStringExtra("mensBrand"));
                        b.putString("mensColor", getIntent().getStringExtra("mensColor"));
                        b.putString("mensMaterial", getIntent().getStringExtra("mensMaterial"));
                        b.putString("mensUsage", getIntent().getStringExtra("mensUsage"));
                        b.putString("mensSizes", getIntent().getStringExtra("mensSizes"));
                        b.putString("mensDescription", getIntent().getStringExtra("mensDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Gadgets":

                        b.putString("Offers", isOffer);
                        b.putString("gadgetType", getIntent().getStringExtra("gadgetType"));
                        b.putString("gadgetBrand", getIntent().getStringExtra("gadgetBrand"));
                        b.putString("gadgetColor", getIntent().getStringExtra("gadgetColor"));
                        b.putString("gadgetUsage", getIntent().getStringExtra("gadgetUsage"));
                        b.putString("gadgetDescription", getIntent().getStringExtra("gadgetDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Game":
                        b.putString("Offers", isOffer);
                        b.putString("gameType", getIntent().getStringExtra("gameType"));
                        b.putString("gameBrand", getIntent().getStringExtra("gameBrand"));
                        b.putString("gameUsage", getIntent().getStringExtra("gameUsage"));
                        b.putString("gameDescription", getIntent().getStringExtra("gameDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Bags":
                        b.putString("Offers", isOffer);
                        b.putString("bagType", getIntent().getStringExtra("bagType"));
                        b.putString("bagBrand", getIntent().getStringExtra("bagBrand"));
                        b.putString("bagColor", getIntent().getStringExtra("bagColor"));
                        b.putString("bagUsage", getIntent().getStringExtra("bagUsage"));
                        b.putString("bagDescription", getIntent().getStringExtra("bagDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Groceries":
                        b.putString("Offers", isOffer);
                        b.putString("groceryList", getIntent().getStringExtra("groceryList"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Furniture":
                        b.putString("Offers", isOffer);
                        b.putString("furnitureBrand", getIntent().getStringExtra("furnitureBrand"));
                        b.putString("furnitureColor", getIntent().getStringExtra("furnitureColor"));
                        b.putString("furnitureUsage", getIntent().getStringExtra("furnitureUsage"));
                        b.putString("furnitureHeight", getIntent().getStringExtra("furnitureHeight"));
                        b.putString("furnitureWidth", getIntent().getStringExtra("furnitureWidth"));
                        b.putString("furnitureLength", getIntent().getStringExtra("furnitureLength"));
                        b.putString("furnitureDescription", getIntent().getStringExtra("furnitureDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Babies & Kids":
                        b.putString("Offers", isOffer);
                        b.putString("bnkAge", getIntent().getStringExtra("bnkAge"));
                        b.putString("bnkBrand", getIntent().getStringExtra("bnkBrand"));
                        b.putString("bnkType", getIntent().getStringExtra("bnkType"));
                        b.putString("bnkUsage", getIntent().getStringExtra("bnkUsage"));
                        b.putString("bnkDescription", getIntent().getStringExtra("bnkDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Appliances":
                        b.putString("Offers", isOffer);
                        b.putString("appliancesType", getIntent().getStringExtra("appliancesType"));
                        b.putString("appliancesBrand", getIntent().getStringExtra("appliancesBrand"));
                        b.putString("appliancesColor", getIntent().getStringExtra("appliancesColor"));
                        b.putString("appliancesUsage", getIntent().getStringExtra("appliancesUsage"));
                        b.putString("appliancesDescription", getIntent().getStringExtra("appliancesDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Motors":

                        b.putString("motorModel", getIntent().getStringExtra("motorModel"));
                        b.putString("Offers", isOffer);
                        b.putString("motorBrand", getIntent().getStringExtra("motorBrand"));
                        b.putString("motorColor", getIntent().getStringExtra("motorColor"));
                        b.putString("motorUsage", getIntent().getStringExtra("motorUsage"));
                        b.putString("motorDescription", getIntent().getStringExtra("motorDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Audio":
                        b.putString("Offers", isOffer);
                        b.putString("audioArtist", getIntent().getStringExtra("audioArtist"));
                        b.putString("audioReleaseDate", getIntent().getStringExtra("audioReleaseDate"));
                        b.putString("audioUsage", getIntent().getStringExtra("audioUsage"));
                        b.putString("audioDescription", getIntent().getStringExtra("audioDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "School":
                        b.putString("Offers", isOffer);
                        b.putString("schoolType", getIntent().getStringExtra("schoolType"));
                        b.putString("schoolBrand", getIntent().getStringExtra("schoolBrand"));
                        b.putString("schoolColor", getIntent().getStringExtra("schoolColor"));
                        b.putString("schoolUsage", getIntent().getStringExtra("schoolUsage"));
                        b.putString("schoolDescription", getIntent().getStringExtra("schoolDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Women's Apparel":
                        b.putString("Offers", isOffer);
                        b.putString("womensClothingType", getIntent().getStringExtra("womensClothingType"));
                        b.putString("womensBrand", getIntent().getStringExtra("womensBrand"));
                        b.putString("womensColor", getIntent().getStringExtra("womensColor"));
                        b.putString("womensMaterial", getIntent().getStringExtra("womensMaterial"));
                        b.putString("womensUsage", getIntent().getStringExtra("womensUsage"));
                        b.putString("womensSizes", getIntent().getStringExtra("womensSizes"));
                        b.putString("womensDescription", getIntent().getStringExtra("womensDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                    case "Others":
                        b.putString("Offers", isOffer);
                        b.putString("otherType", getIntent().getStringExtra("otherType"));
                        b.putString("otherDescription", getIntent().getStringExtra("otherDescription"));
                        b.putString("item_name", getIntent().getStringExtra("item_name"));
                        b.putString("from", getIntent().getStringExtra("from"));
                        b.putString("category", getIntent().getStringExtra("category"));
                        b.putString("category1", getIntent().getStringExtra("category1"));
                        b.putString("rft", getIntent().getStringExtra("rft"));
                        b.putString("pref_item", getIntent().getStringExtra("pref_item"));
                        mapsFragment.setArguments(b);
                        fragmentTransaction.add(R.id.container, mapsFragment).commit();

                        break;
                }
            }
        } else {
            databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                            databaseReference1.child("items").child(dataSnapshot.getKey()).child(dataSnapshot1.getKey()).orderByChild("Item_Category").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    if (!snapshot1.hasChildren()) {
                                        Intent intent = new Intent(currentLoc.this, alert_dialog_noitem.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        CustomIntent.customType(currentLoc.this, "fadein-to-fadeout");
                                    } else {
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

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}