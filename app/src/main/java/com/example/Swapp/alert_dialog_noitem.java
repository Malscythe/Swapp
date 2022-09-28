package com.example.Swapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class alert_dialog_noitem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog_noitems);

        Button returnToCategories;

        returnToCategories = findViewById(R.id.returnToCategories);

        returnToCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(alert_dialog_noitem.this, Categories.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                CustomIntent.customType(alert_dialog_noitem.this, "right-to-left");
                finish();
            }
        });

    }
}
