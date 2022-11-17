package com.example.Swapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import Swapp.R;
import Swapp.databinding.ActivityCategoriesBinding;
import maes.tech.intentanim.CustomIntent;

public class Categories extends AppCompatActivity {

    private ActivityCategoriesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button backBtn;
        CardView all, mens, gadget, games, bags, groceries, furniture, babieskids, appliances, motors, audio, school, womens, others;

        backBtn = findViewById(R.id.backBtn);
        mens = findViewById(R.id.mens);
        gadget = findViewById(R.id.gadgets);
        all = findViewById(R.id.all);
        games = findViewById(R.id.games);
        bags = findViewById(R.id.bags);
        groceries = findViewById(R.id.groceries);
        furniture = findViewById(R.id.furniture);
        babieskids = findViewById(R.id.babies);
        appliances = findViewById(R.id.appliances);
        motors = findViewById(R.id.motors);
        audio = findViewById(R.id.audio);
        school = findViewById(R.id.school);
        womens = findViewById(R.id.womens);
        others = findViewById(R.id.other);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, UserHomepage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "right-to-left");
                finish();
            }
        });


        mens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                intent.putExtra("category", "Men's Apparel");
                intent.putExtra("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });


        gadget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category","Gadgets");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category", "all");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        games.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Game");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        bags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Bags");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        groceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Groceries");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Furniture");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        babieskids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Babies & Kids");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });
        appliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Appliances");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        motors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Motors");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Audio");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category", "School");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });
        womens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Women's Apparel");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, currentLoc.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Others");
                extras.putString("from", "categories");
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtras(extras);
                startActivity(intent);
                CustomIntent.customType(Categories.this, "left-to-right");
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Categories.this, UserHomepage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        CustomIntent.customType(Categories.this, "right-to-left");
        finish();
    }
}