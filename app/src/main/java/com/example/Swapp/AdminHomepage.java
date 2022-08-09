package com.example.Swapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import Swapp.R;

public class AdminHomepage extends AppCompatActivity {

    ImageView usermanage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);

        usermanage = (ImageView) findViewById(R.id.usermanage);
        usermanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FileMaintenance = new Intent(AdminHomepage.this, FileMaintenance.class);
                startActivity(FileMaintenance);
            }
        });


    }


}
