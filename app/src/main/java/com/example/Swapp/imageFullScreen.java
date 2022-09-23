package com.example.Swapp;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;

import Swapp.R;

public class imageFullScreen extends AppCompatActivity {

    ImageView fullImage;
    RelativeLayout imageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_full_screen);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        String imageUrl = getIntent().getStringExtra("imageUrl");

        fullImage = findViewById(R.id.imageFullView);
        imageLayout = findViewById(R.id.imageLayout);

        Glide.with(imageFullScreen.this).load(imageUrl).into(fullImage);

        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
