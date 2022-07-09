package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Type;
import java.util.ArrayList;

import Swapp.R;

public class UserHomepage extends AppCompatActivity {

    SearchView searchView;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        pieChart = findViewById(R.id.reportSummaryChart);
        setupPieChart();
        loadPieChartData();


    }

    private void setupPieChart() {
        Typeface tf = getResources().getFont(R.font.poppins);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelTypeface(tf);
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextColor(Color.parseColor("#03989e"));
        l.setDrawInside(false);
        l.setTextSize(12);
        l.setTypeface(tf);
        l.setEnabled(true);
    }

    private void loadPieChartData() {
        Typeface tf = getResources().getFont(R.font.poppins);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(0.10f, "Current"));
        entries.add(new PieEntry(0.15f, "Successful"));
        entries.add(new PieEntry(0.10f, "Unsuccessful"));
        entries.add(new PieEntry(0.25f, "Pending"));

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTypeface(tf);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();

    }
}