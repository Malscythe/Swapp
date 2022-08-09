package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Swapp.R;

public class Categories extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        ExtendedFloatingActionButton addItem;

        Button all, mens, gadget, games, bags, groceries, furniture, babieskids, appliances, motors, audio, school, womens, others;

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

        mens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Men's Apparel");
                startActivity(intent);
            }
        });

        gadget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Gadgets");
                startActivity(intent);
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "all");
                startActivity(intent);
            }
        });

        games.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Game");
                startActivity(intent);
            }
        });

        bags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Bags");
                startActivity(intent);
            }
        });

        groceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Groceries");
                startActivity(intent);
            }
        });

        furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Furniture");
                startActivity(intent);
            }
        });

        babieskids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Babies & Kids");
                startActivity(intent);
            }
        });
        appliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Appliances");
                startActivity(intent);
            }
        });

        motors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Motors");
                startActivity(intent);
            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Audio");
                startActivity(intent);
            }
        });

        school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "School");
                startActivity(intent);
            }
        });
        womens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Women's Apparel");
                startActivity(intent);
            }
        });

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, ItemSwipe.class);
                intent.putExtra("category", "Others");
                startActivity(intent);
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories.this, PostItem.class);
                startActivity(intent);
            }
        });
    }
}