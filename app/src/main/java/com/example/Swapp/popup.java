package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import Swapp.R;

public class popup extends AppCompatActivity {

    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.55));

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
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTextColor(Color.parseColor("#03989e"));
        l.setDrawInside(false);
        l.setTextSize(10);
        l.setTypeface(tf);
        l.setEnabled(true);
    }

    private void loadPieChartData() {
        Typeface tf = getResources().getFont(R.font.poppins);
        ArrayList<PieEntry> entries = new ArrayList<>();

        Float current = Float.parseFloat(getIntent().getStringExtra("current"));
        Float pending = Float.parseFloat(getIntent().getStringExtra("pending"));
        Float successful = Float.parseFloat(getIntent().getStringExtra("successful"));
        Float unsuccessful = Float.parseFloat(getIntent().getStringExtra("unsuccessful"));

        if (current != 0f) {
            entries.add(new PieEntry(current / 100, "Current"));
        } else {
            entries.remove(new PieEntry(current, "Current"));
        }

        if (pending != 0f) {
            entries.add(new PieEntry(pending / 100, "Pending"));
        } else {
            entries.remove(new PieEntry(pending, "Pending"));
        }

        if (successful != 0f) {
            entries.add(new PieEntry(successful / 100, "Successful"));
        } else {
            entries.remove(new PieEntry(successful, "Successful"));
        }

        if (unsuccessful != 0f) {
            entries.add(new PieEntry(unsuccessful / 100, "Unsuccessful"));
        } else {
            entries.remove(new PieEntry(unsuccessful, "Unsuccessful"));
        }

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FFFFBB33"));
        colors.add(Color.parseColor("#FF33B5E5"));
        colors.add(Color.parseColor("#FF99CC00"));
        colors.add(Color.parseColor("#FFFF4444"));

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