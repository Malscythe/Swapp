package com.example.Swapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Swapp.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import maes.tech.intentanim.CustomIntent;

public class FeedbacksRating extends AppCompatActivity {

    RecyclerView fnrRV;

    List<FeedbacksRatingFetch> feedbacksRatingFetchList;
    FeedbacksRatingAdapter feedbacksRatingAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FloatingActionButton exportData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fnr);

        fnrRV = findViewById(R.id.fnrRV);

        fnrRV.setLayoutManager(new LinearLayoutManager(this));

        feedbacksRatingFetchList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        exportData = findViewById(R.id.exportData);

        databaseReference = FirebaseDatabase.getInstance().getReference("user-rating");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    for (DataSnapshot dataSnapshot : ds.child("transactions").getChildren()) {

                        FeedbacksRatingFetch feedbacksRatingFetch = new FeedbacksRatingFetch();

                        feedbacksRatingFetch.setUser_ID(ds.getKey());
                        feedbacksRatingFetch.setFeedback(dataSnapshot.child("Feedback").getValue(String.class));
                        feedbacksRatingFetch.setRated_By(dataSnapshot.child("Rated_By").getValue(String.class));
                        feedbacksRatingFetch.setRate(String.valueOf(dataSnapshot.child("Rate").getValue(Float.class)));
                        feedbacksRatingFetch.setTransaction_Key(dataSnapshot.getKey());
                        feedbacksRatingFetchList.add(feedbacksRatingFetch);
                    }

                }

                feedbacksRatingAdapter = new FeedbacksRatingAdapter(feedbacksRatingFetchList);
                fnrRV.setAdapter(feedbacksRatingAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String strCurrentDate = new SimpleDateFormat("MMMM dd, yyyy hh mm aa", Locale.getDefault()).format(new Date());
                            String strDate = new SimpleDateFormat("MMMM dd, yyyy HH:mm aa", Locale.getDefault()).format(new Date());
                            File root = new File(Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "FileExcel");
                            if (!root.exists()) {
                                root.mkdirs();
                            }
                            File path = new File(root, "/Feedbacks and Rating " + strCurrentDate + ".xlsx");

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

                            XSSFSheet sheet = workbook.createSheet("Feedbacks and Rating - " + strDate);
                            XSSFRow row = sheet.createRow(0);

                            XSSFCell cell1 = row.createCell(0);
                            cell1.setCellValue("User ID");
                            cell1.setCellStyle(headerStyle);

                            XSSFCell cell2 = row.createCell(1);
                            cell2.setCellValue("Transaction Key");
                            cell2.setCellStyle(headerStyle);

                            XSSFCell cell3 = row.createCell(2);
                            cell3.setCellValue("Rated By");
                            cell3.setCellStyle(headerStyle);

                            XSSFCell cell4 = row.createCell(3);
                            cell4.setCellValue("Rating");
                            cell4.setCellStyle(headerStyle);

                            XSSFCell cell5 = row.createCell(4);
                            cell5.setCellValue("Feedback");
                            cell5.setCellStyle(headerStyle);

                            int counter = 0;

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("transactions").getChildren()) {

                                    row = sheet.createRow(counter + 1);

                                    cell1 = row.createCell(0);
                                    cell1.setCellValue(dataSnapshot.getKey());
                                    sheet.setColumnWidth(0, (dataSnapshot.getKey().length() + 30) * 256);

                                    cell2 = row.createCell(1);
                                    cell2.setCellValue(dataSnapshot1.getKey());
                                    sheet.setColumnWidth(1, (dataSnapshot1.getKey().length() + 30) * 256);

                                    cell3 = row.createCell(2);
                                    cell3.setCellValue(dataSnapshot1.child("Rated_By").getValue(String.class));
                                    sheet.setColumnWidth(2, (dataSnapshot1.child("Rated_By").getValue(String.class).length() + 30) * 256);

                                    cell4 = row.createCell(3);
                                    cell4.setCellValue(String.valueOf(dataSnapshot1.child("Rate").getValue(Float.class)));
                                    sheet.setColumnWidth(3, (String.valueOf(dataSnapshot1.child("Rate").getValue(Float.class)).length() + 30) * 256);

                                    cell5 = row.createCell(4);
                                    cell5.setCellValue(dataSnapshot1.child("Feedback").getValue(String.class));
                                    sheet.setColumnWidth(4, (dataSnapshot1.child("Feedback").getValue(String.class).length() + 30) * 256);

                                    counter++;
                                }
                            }

                            workbook.write(outputStream);
                            outputStream.close();
                            new SweetAlertDialog(FeedbacksRating.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Create excel report")
                                    .setContentText("Report for Feedbacks and Rating has been created!")
                                    .show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(FeedbacksRating.this, AdminHomepage.class);
        startActivity(intent);
        CustomIntent.customType(FeedbacksRating.this, "right-to-left");
    }
}