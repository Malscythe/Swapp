package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;

import Swapp.R;

public class UserHomepage extends AppCompatActivity {

    private static final String TAG = "TAG";

    TextView viewChart, pendingCounts, successfulCounts, unsuccessfulCounts, currentCounts;
    Dialog chartDialog;
    ImageView logoutBtn;
    Button trdbtn, dlvrbtn, accsetting;
    Long pendingTrades = 0L;
    Long currentTrades = 0L;
    Long successfulTrades = 0L;
    Long unsuccessfulTrades = 0L;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        viewChart = findViewById(R.id.viewBtn);
        chartDialog = new Dialog(this);
        logoutBtn = findViewById(R.id.logoutBtn);
        trdbtn = findViewById(R.id.tradeButton);
        accsetting = findViewById(R.id.accSettings);
        pendingCounts = findViewById(R.id.pendingText);
        successfulCounts = findViewById(R.id.successfulText);
        unsuccessfulCounts = findViewById(R.id.unsuccessfulText);
        currentCounts = findViewById(R.id.currentText);
        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();

        dlvrbtn = findViewById(R.id.deliverBtn);

        databaseReference = FirebaseDatabase.getInstance().getReference("items/");
        databaseReference.orderByChild("Poster_UID").equalTo(uid).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String itemid = ds.getKey();
                    databaseReference = FirebaseDatabase.getInstance().getReference("items/" + itemid + "/Offers");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                pendingTrades = pendingTrades + snapshot.getChildrenCount();
                                Log.w(TAG, String.valueOf(pendingTrades));

                                pendingCounts.setText(String.valueOf(pendingTrades));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        successfulCounts.setText(String.valueOf(successfulTrades));
        unsuccessfulCounts.setText(String.valueOf(unsuccessfulTrades));
        currentCounts.setText(String.valueOf(currentTrades));

        viewChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomepage.this, popup.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomepage.this, login.class));
            }
        });
        trdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomepage.this, Categories.class));
            }
        });
        dlvrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomepage.this, deliverytrack.class));
            }
        });

        accsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomepage.this, OfferMainAcitvity.class));
            }
        });

    }
}