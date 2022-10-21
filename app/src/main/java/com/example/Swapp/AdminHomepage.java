package com.example.Swapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class AdminHomepage extends AppCompatActivity {

    CardView manageUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);

        manageUsers = findViewById(R.id.manageUsers);
        manageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FileMaintenance = new Intent(AdminHomepage.this, FileMaintenance.class);
                startActivity(FileMaintenance);
                CustomIntent.customType(AdminHomepage.this, "left-to-right");
            }
        });


    }


}
