package com.example.Swapp;

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

import Swapp.R;

public class AdminHomepage extends AppCompatActivity {

    ImageView usermanage;

    private PieChart chart;
    private int i1 = 15;
    private int i2 = 25;
    private int i3 = 35;
    private int i4 = 45;

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

        chart = findViewById(R.id.pie_chart);
        addToPieChart();
    }

    private void addToPieChart() {

        chart.addPieSlice(new PieModel("Integer 1", i1, Color.parseColor("#FFA726")));
        chart.addPieSlice(new PieModel("Integer 2", i2, Color.parseColor("#66BB6A")));
        chart.addPieSlice(new PieModel("Integer 3", i3, Color.parseColor("#EF5350")));
        chart.addPieSlice(new PieModel("Integer 4", i4, Color.parseColor("#2986F6")));

        chart.startAnimation();
    }
}