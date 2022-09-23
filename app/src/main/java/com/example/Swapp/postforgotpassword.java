package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class postforgotpassword extends AppCompatActivity {

    Button returnToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postforgotpassword);

        returnToLogin = findViewById(R.id.returnLogin);

        returnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), login.class));
                CustomIntent.customType(postforgotpassword.this, "right-to-left");
            }
        });
    }
}