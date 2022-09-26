package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.auth.User;

import Swapp.R;
import Swapp.databinding.ActivityCategoriesBinding;
import Swapp.databinding.ActivityOfferItemBinding;
import maes.tech.intentanim.CustomIntent;

public class Categories extends AppCompatActivity {

    private ActivityCategoriesBinding binding;
    ImageButton homebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ExtendedFloatingActionButton addItem;

        Button all, mens, gadget, games, bags, groceries, furniture, babieskids, appliances, motors, audio, school, womens, others;

        homebtn = findViewById(R.id.homebtn);
        addItem = findViewById(R.id.floatingActionButton);
        mens = findViewById(R.id.mens);
        gadget = findViewById(R.id.gadgets);
        all = findViewById(R.id.all);
        games = findViewById(R.id.games);
        bags = findViewById(R.id.bags);
        groceries = findViewById(R.id.groceries);
        furniture = findViewById(R.id.furniture);
        babieskids = findViewById(R.id.babieskids);
        appliances = findViewById(R.id.appliances);
        motors = findViewById(R.id.motors);
        audio = findViewById(R.id.audio);
        school = findViewById(R.id.school);
        womens = findViewById(R.id.womens);
        others = findViewById(R.id.others);

        String[] locationsArr = getResources().getStringArray(R.array.locationFilter);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item, locationsArr);
        binding.itemLocation.setAdapter(arrayAdapter);
        binding.itemLocation.setText(arrayAdapter.getItem(0).toString(), false);

        mens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Men's Apparel");
                intent.putExtra("location", binding.itemLocation.getText().toString());
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Categories.this, UserHomepage.class));
                CustomIntent.customType(Categories.this, "right-to-left");
            }
        });

        gadget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category","Gadgets");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category", "all");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        games.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Game");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        bags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Bags");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        groceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Groceries");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Furniture");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        babieskids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Babies & Kids");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });
        appliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Appliances");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        motors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Motors");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Audio");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category", "School");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });
        womens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Women's Apparel");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Others");
                extras.putString("location", binding.itemLocation.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, PostItem.class);
                intent.putExtra("address", "");
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Categories.this, UserHomepage.class);
        startActivity(intent);
        CustomIntent.customType(Categories.this, "right-to-left");
    }
}