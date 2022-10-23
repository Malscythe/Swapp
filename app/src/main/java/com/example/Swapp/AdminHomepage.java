package com.example.Swapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

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

    CardView manageUsers, goToActivityLogs;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    TextView registered, online, transactions, posted;
    ImageView logout;

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
        logout = findViewById(R.id.logoutBtn);

        if (ActivityCompat.checkSelfPermission(AdminHomepage.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(AdminHomepage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AdminHomepage.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(AdminHomepage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomepage.this, login.class));
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                registered.setText(String.valueOf(snapshot.child("users").getChildrenCount()));
                transactions.setText(String.valueOf(snapshot.child("trade-transactions").getChildrenCount()));

                long newCount = 0;
                for (DataSnapshot dataSnapshot : snapshot.child("items").getChildren()) {
                    newCount = newCount + dataSnapshot.getChildrenCount();
                }

                posted.setText(String.valueOf(newCount));

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

        goToActivityLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> strings = new ArrayList<>();
                strings.add("WTF");
                strings.add("WTF");
                strings.add("WTF");
                strings.add("WTF");
                strings.add("WTF");
                strings.add("WTF");
                strings.add("WTF");

                createXlxs(strings);
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

    private void createXlxs(ArrayList<String> strings) {
        try {
            String strDate = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.getDefault()).format(new Date());
            File root = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "FileExcel");
            if (!root.exists())
                root.mkdirs();
            File path = new File(root, "/Activity Logs - " + strDate + ".xlsx");

            XSSFWorkbook workbook = new XSSFWorkbook();
            FileOutputStream outputStream = new FileOutputStream(path);

            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.MEDIUM);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerStyle.setBorderRight(BorderStyle.MEDIUM);
            headerStyle.setBorderLeft(BorderStyle.MEDIUM);

            XSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headerStyle.setFont(font);

            XSSFSheet sheet = workbook.createSheet("Activity Logs - " + strDate);
            XSSFRow row = sheet.createRow(0);

            XSSFCell cell = row.createCell(0);
            cell.setCellValue("C1");
            cell.setCellStyle(headerStyle);

            for (int i = 0; i < strings.size(); i++) {
                row = sheet.createRow(i + 1);

                cell = row.createCell(0);
                cell.setCellValue(strings.get(i));
                sheet.setColumnWidth(0, (strings.get(i).length() + 30) * 256);

            }

            workbook.write(outputStream);
            outputStream.close();
            Toast.makeText(AdminHomepage.this, "File created!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
