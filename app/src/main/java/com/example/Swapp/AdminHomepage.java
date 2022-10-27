package com.example.Swapp;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class AdminHomepage extends AppCompatActivity {

    CardView manageUsers, goToActivityLogs, goToFNR, goToPosted, goToTransaction, goToApproval;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    TextView registered, online, transactions, posted, approval;
    ImageView logout;
    long newCount = 0;
    long newApprovalCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);

        manageUsers = findViewById(R.id.manageUsers);
        goToActivityLogs = findViewById(R.id.goToActivityLogs);
        registered = findViewById(R.id.registeredCounter);
        online = findViewById(R.id.onlineCounter);
        transactions = findViewById(R.id.transactionsCounter);
        posted = findViewById(R.id.postedCounter);
        approval = findViewById(R.id.approvalCounter);
        logout = findViewById(R.id.logoutBtn);
        goToFNR = findViewById(R.id.feedbacksRating);
        goToPosted = findViewById(R.id.goToPostedItems);
        goToTransaction = findViewById(R.id.goToTransactions);
        goToApproval = findViewById(R.id.goToApproval);

        if ((ActivityCompat.checkSelfPermission(AdminHomepage.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(AdminHomepage.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(AdminHomepage.this, MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(AdminHomepage.this, new String[]{READ_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(AdminHomepage.this, new String[]{WRITE_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(AdminHomepage.this, new String[]{MANAGE_EXTERNAL_STORAGE}, 0);
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomepage.this, login.class);
                intent.putExtra("logoutFrom", "Admin");
                startActivity(intent);
                CustomIntent.customType(AdminHomepage.this, "right-to-left");
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long newRegisteredCount = 0;
                for (DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {
                    if (dataSnapshot.child("isAdmin").getValue(String.class).equals("0")) {
                        newRegisteredCount = newRegisteredCount + 1;
                    }
                }
                registered.setText(String.valueOf(newRegisteredCount));
                transactions.setText(String.valueOf(snapshot.child("trade-transactions").getChildrenCount()));

                newCount = 0;
                newApprovalCount = 0;
                databaseReference.child("items").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                if (dataSnapshot1.child("Status").getValue(String.class).equals("Validated")) {
                                    newCount = newCount + 1;
                                }

                                if (dataSnapshot1.child("Status").getValue(String.class).equals("Validating")) {
                                    newApprovalCount = newApprovalCount + 1;
                                }
                            }
                        }
                        posted.setText(String.valueOf(newCount));
                        approval.setText(String.valueOf(newApprovalCount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                databaseReference.child("users-status").orderByChild("Status").equalTo("Online").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        online.setText(String.valueOf(snapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        goToApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomepage.this, ItemApproval.class));
                CustomIntent.customType(AdminHomepage.this, "left-to-right");
            }
        });

        goToTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomepage.this, Transactions.class));
                CustomIntent.customType(AdminHomepage.this, "left-to-right");
            }
        });

        goToFNR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomepage.this, FeedbacksRating.class));
                CustomIntent.customType(AdminHomepage.this, "left-to-right");
            }
        });

        goToPosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomepage.this, PostedItems.class));
                CustomIntent.customType(AdminHomepage.this, "left-to-right");
            }
        });

        goToActivityLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomepage.this, ActivityLogs.class));
                CustomIntent.customType(AdminHomepage.this, "left-to-right");
            }
        });

        manageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FileMaintenance = new Intent(AdminHomepage.this, FileMaintenance.class);
                startActivity(FileMaintenance);
                CustomIntent.customType(AdminHomepage.this, "left-to-right");
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }
}
