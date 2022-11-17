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

public class popup_fnr extends AppCompatActivity {

    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_fnr);

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

        Float one = (float) getIntent().getIntExtra("one", 0);
        Float two = (float) getIntent().getIntExtra("two", 0);
        Float three = (float) getIntent().getIntExtra("three", 0);
        Float four = (float) getIntent().getIntExtra("four", 0);
        Float five = (float) getIntent().getIntExtra("five", 0);

        if (one != 0f) {
            entries.add(new PieEntry(one / 100, "1 Rating"));
        } else {
            entries.remove(new PieEntry(one, "1 Rating"));
        }

        if (two != 0f) {
            entries.add(new PieEntry(two / 100, "2 Rating"));
        } else {
            entries.remove(new PieEntry(two, "2 Rating"));
        }

        if (three != 0f) {
            entries.add(new PieEntry(three / 100, "3 Rating"));
        } else {
            entries.remove(new PieEntry(three, "3 Rating"));
        }

        if (four != 0f) {
            entries.add(new PieEntry(four / 100, "4 Rating"));
        } else {
            entries.remove(new PieEntry(four, "4 Rating"));
        }

        if (five != 0f) {
            entries.add(new PieEntry(five / 100, "5 Rating"));
        } else {
            entries.remove(new PieEntry(five, "5 Rating"));
        }

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FFFFBB33"));
        colors.add(Color.parseColor("#FF33B5E5"));
        colors.add(Color.parseColor("#FF99CC00"));
        colors.add(Color.parseColor("#FFFF4444"));
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