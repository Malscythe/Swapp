package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Swapp.chat.Chat;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class UserHomepage extends AppCompatActivity {

    private static final String TAG = "TAG";

    TextView viewChart, pendingCounts, successfulCounts, unsuccessfulCounts, currentCounts, name;
    Dialog chartDialog;
    ImageView logoutBtn, imgbtn;
    Button trdbtn, dlvrbtn, accsetting, inbbtn;
    Long pendingTrades = 0L;
    Long currentTrades = 0L;
    Long successfulTrades = 0L;
    Long unsuccessfulTrades = 0L;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceUrl = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        imgbtn = findViewById(R.id.locatebtn);

        inbbtn = findViewById(R.id.inboxBtn);
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
        name = findViewById(R.id.userFullName);

        String uid = firebaseAuth.getCurrentUser().getUid();
        name.setText(MemoryData.getName(UserHomepage.this));

        MemoryData.saveUid(uid, UserHomepage.this);

        Log.w(TAG, MemoryData.getLastMsgTS(UserHomepage.this, "1", "09275201847"));

        databaseReference = FirebaseDatabase.getInstance().getReference("items/");
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("Poster_UID").getValue(String.class).equals(uid)) {
                        String itemid = ds.getKey();
                        databaseReference = FirebaseDatabase.getInstance().getReference("items/" + itemid + "/Offers");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                pendingTrades = pendingTrades + snapshot.getChildrenCount();
                                pendingCounts.setText(pendingTrades.toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                pendingTrades = 0L;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("Poster_UID").getValue(String.class).equals(uid)) {
                        String itemid = ds.getKey();
                        databaseReference = FirebaseDatabase.getInstance().getReference("items/" + itemid + "/Accepted_Offers");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                currentTrades = currentTrades + snapshot.getChildrenCount();
                                currentCounts.setText(currentTrades.toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if (ds.child("Accepted_Offers").child(uid).exists()) {
                        currentTrades = Long.parseLong(currentCounts.getText().toString()) + 1L;
                        currentCounts.setText(currentTrades.toString());
                    }
                }
                currentTrades = 0L;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        inbbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReferenceUrl.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("First_Name").getValue(String.class).concat(" " + snapshot.child("Last_Name").getValue(String.class));
                        Intent intent = new Intent(UserHomepage.this, Messages.class);
                        intent.putExtra("mobile", snapshot.child("Phone").getValue(String.class));
                        intent.putExtra("email", snapshot.child("Email").getValue(String.class));
                        intent.putExtra("name", name);
                        startActivity(intent);
                        CustomIntent.customType(UserHomepage.this, "left-to-right");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        dlvrbtn = findViewById(R.id.deliverBtn);

        successfulCounts.setText(successfulTrades.toString());
        unsuccessfulCounts.setText(unsuccessfulTrades.toString());
        currentCounts.setText(currentTrades.toString());

        viewChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomepage.this, popup.class);
                intent.putExtra("pending", pendingCounts.getText().toString());
                intent.putExtra("current", currentCounts.getText().toString());
                intent.putExtra("successful", successfulCounts.getText().toString());
                intent.putExtra("unsuccessful", unsuccessfulCounts.getText().toString());
                startActivity(intent);
                CustomIntent.customType(UserHomepage.this, "fadein-to-fadeout");
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemoryData.saveUid("", UserHomepage.this);
                MemoryData.saveData("", UserHomepage.this);
                MemoryData.saveName("", UserHomepage.this);
                MemoryData.saveState(false, UserHomepage.this);

                DatabaseReference df = FirebaseDatabase.getInstance().getReference();
                df.child("users-status").child(uid).child("Status").setValue("Offline");

                startActivity(new Intent(UserHomepage.this, login.class));
            }
        });
        trdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomepage.this, Categories.class));
                CustomIntent.customType(UserHomepage.this, "left-to-right");
            }
        });
        dlvrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomepage.this, deliverytrack.class));
                CustomIntent.customType(UserHomepage.this, "left-to-right");
            }
        });

        accsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomepage.this, OfferMainAcitvity.class));
                CustomIntent.customType(UserHomepage.this, "left-to-right");
            }
        });

        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserHomepage.this, currentLoc.class));
                CustomIntent.customType(UserHomepage.this, "left-to-right");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MemoryData.saveUid("", UserHomepage.this);
        MemoryData.saveData("", UserHomepage.this);
        MemoryData.saveName("", UserHomepage.this);
        MemoryData.saveState(false, UserHomepage.this);

        String uid = firebaseAuth.getCurrentUser().getUid();
        Log.w(TAG, "DESTROYED : " + uid);
        DatabaseReference df = FirebaseDatabase.getInstance().getReference();
        df.child("users-status").child(uid).child("Status").setValue("Offline");
        startActivity(new Intent(UserHomepage.this, login.class));
    }
}