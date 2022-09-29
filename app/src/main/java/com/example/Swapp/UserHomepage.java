package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Swapp.chat.Chat;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class UserHomepage extends AppCompatActivity {

    private static final String TAG = "TAG";

    TextView pendingCounts, successfulCounts, unsuccessfulCounts, currentCounts, name;
    Dialog chartDialog;
    ImageView viewChart;
    CircleImageView accsetting;
    Button dlvrbtn, inbbtn;
    CardView tradeButton;
    Long pendingTrades = 0L;
    Long currentTrades = 0L;
    Long successfulTrades = 0L;
    Long unsuccessfulTrades = 0L;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    BottomNavigationView bottomNavigationView;
    DatabaseReference databaseReferenceUrl = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        inbbtn = findViewById(R.id.inboxBtn);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewChart = findViewById(R.id.viewBtn);
        chartDialog = new Dialog(this);
//        logoutBtn = findViewById(R.id.logoutBtn);
        tradeButton = findViewById(R.id.tradeButton);
        accsetting = findViewById(R.id.accountSetting);
        pendingCounts = findViewById(R.id.pendingText);
        successfulCounts = findViewById(R.id.successfulText);
        unsuccessfulCounts = findViewById(R.id.unsuccessfulText);
        currentCounts = findViewById(R.id.currentText);
        firebaseAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.userFullName);

        String uid = firebaseAuth.getCurrentUser().getUid();

        bottomNavigationView.setSelectedItemId(R.id.homepage);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.inbox:
                        databaseReferenceUrl.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name = snapshot.child("First_Name").getValue(String.class).concat(" " + snapshot.child("Last_Name").getValue(String.class));
                                Intent intent = new Intent(UserHomepage.this, Messages.class);
                                intent.putExtra("mobile", snapshot.child("Phone").getValue(String.class));
                                intent.putExtra("email", snapshot.child("Email").getValue(String.class));
                                intent.putExtra("name", name);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        return true;
                    case R.id.home:
                        return true;
                    case  R.id.offers:
                        Intent intent = new Intent(UserHomepage.this, OfferMainAcitvity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                }
                return true;
            }
        });

        name.setText(MemoryData.getFirstName(UserHomepage.this) + "!");

        MemoryData.saveUid(uid, UserHomepage.this);

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
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("pending", pendingCounts.getText().toString());
                intent.putExtra("current", currentCounts.getText().toString());
                intent.putExtra("successful", successfulCounts.getText().toString());
                intent.putExtra("unsuccessful", unsuccessfulCounts.getText().toString());
                startActivity(intent);
                CustomIntent.customType(UserHomepage.this, "fadein-to-fadeout");
            }
        });

//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MemoryData.saveUid("", UserHomepage.this);
//                MemoryData.saveData("", UserHomepage.this);
//                MemoryData.saveName("", UserHomepage.this);
//                MemoryData.saveState(false, UserHomepage.this);
//
//                DatabaseReference df = FirebaseDatabase.getInstance().getReference();
//                df.child("users-status").child(uid).child("Status").setValue("Offline");
//
//                Intent intent = new Intent(UserHomepage.this, login.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });
        tradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomepage.this, Categories.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                CustomIntent.customType(UserHomepage.this, "left-to-right");
            }
        });
        dlvrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomepage.this, deliverytrack.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                CustomIntent.customType(UserHomepage.this, "left-to-right");
            }
        });

        accsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemoryData.saveUid("", UserHomepage.this);
                MemoryData.saveData("", UserHomepage.this);
                MemoryData.saveName("", UserHomepage.this);
                MemoryData.saveFirstName("", UserHomepage.this);
                MemoryData.saveState(false, UserHomepage.this);

                DatabaseReference df = FirebaseDatabase.getInstance().getReference();
                df.child("users-status").child(uid).child("Status").setValue("Offline");

                Intent intent = new Intent(UserHomepage.this, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }
}