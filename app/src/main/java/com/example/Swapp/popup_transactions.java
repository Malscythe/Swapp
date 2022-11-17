package com.example.Swapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;

import Swapp.R;

public class popup_transactions extends AppCompatActivity {

    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_transactions);

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

        Float success = (float) getIntent().getIntExtra("successful", 0);
        Float unsuccess = (float) getIntent().getIntExtra("unsuccessful", 0);
        Float onhold = (float) getIntent().getIntExtra("onhold", 0);

        if (success != 0f) {
            entries.add(new PieEntry(success / 100, "Successful"));
        } else {
            entries.remove(new PieEntry(success, "Successful"));
        }

        if (unsuccess != 0f) {
            entries.add(new PieEntry(unsuccess / 100, "Unsuccessful"));
        } else {
            entries.remove(new PieEntry(unsuccess, "Unsuccessful"));
        }

        if (onhold != 0f) {
            entries.add(new PieEntry(onhold / 100, "On hold"));
        } else {
            entries.remove(new PieEntry(onhold, "On hold"));
        }

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FFFFBB33"));
        colors.add(Color.parseColor("#FF33B5E5"));
        colors.add(Color.parseColor("#FF32FF21"));

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