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
    FloatingActionButton exportData, openChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fnr);

        fnrRV = findViewById(R.id.fnrRV);

        fnrRV.setLayoutManager(new LinearLayoutManager(this));

        feedbacksRatingFetchList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        exportData = findViewById(R.id.exportData);
        openChart = findViewById(R.id.openChart);

        databaseReference = FirebaseDatabase.getInstance().getReference("user-rating");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    for (DataSnapshot dataSnapshot : ds.child("transactions").getChildren()) {

                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users");
                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                FeedbacksRatingFetch feedbacksRatingFetch = new FeedbacksRatingFetch();

                                String userToRate = snapshot.child(ds.getKey()).child("First_Name").getValue(String.class).concat(" " + snapshot.child(ds.getKey()).child("Last_Name").getValue(String.class));
                                String ratedBy = snapshot.child(dataSnapshot.child("Rated_By").getValue(String.class)).child("First_Name").getValue(String.class).concat(" " + snapshot.child(dataSnapshot.child("Rated_By").getValue(String.class)).child("Last_Name").getValue(String.class));

                                feedbacksRatingFetch.setUser_To_Rate(userToRate);
                                feedbacksRatingFetch.setFeedback(dataSnapshot.child("Feedback").getValue(String.class));
                                feedbacksRatingFetch.setDate_Rated(dataSnapshot.child("Date_Rated").getValue(String.class));
                                feedbacksRatingFetch.setRated_By(ratedBy);
                                feedbacksRatingFetch.setRate(String.valueOf(dataSnapshot.child("Rate").getValue(Float.class)));
                                feedbacksRatingFetch.setTransaction_Key(dataSnapshot.getKey());
                                feedbacksRatingFetchList.add(feedbacksRatingFetch);

                                feedbacksRatingAdapter = new FeedbacksRatingAdapter(feedbacksRatingFetchList);
                                fnrRV.setAdapter(feedbacksRatingAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        openChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = FirebaseDatabase.getInstance().getReference("user-rating");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        int five = 0;
                        int four = 0;
                        int three = 0;
                        int two = 0;
                        int one = 0;

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.hasChild("transactions")) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("transactions").getChildren()) {
                                    switch (dataSnapshot1.child("Rate").getValue(Integer.class)) {
                                        case 1:
                                            one++;
                                            break;
                                        case 2:
                                            two++;
                                            break;
                                        case 3:
                                            three++;
                                            break;
                                        case 4:
                                            four++;
                                            break;
                                        case 5:
                                            five++;
                                            break;
                                    }
                                }
                            }
                        }

                        Intent intent = new Intent(FeedbacksRating.this, popup_fnr.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("one", one);
                        intent.putExtra("two", two);
                        intent.putExtra("three", three);
                        intent.putExtra("four", four);
                        intent.putExtra("five", five);
                        startActivity(intent);
                        CustomIntent.customType(FeedbacksRating.this, "fadein-to-fadeout");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String strCurrentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
                            String strDate = new SimpleDateFormat("MMMM dd, yyyy HH:mm aa", Locale.getDefault()).format(new Date());
                            File root = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"Exported Reports");

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
                            cell1.setCellValue("Transaction Key");
                            cell1.setCellStyle(headerStyle);

                            XSSFCell cell2 = row.createCell(1);
                            cell2.setCellValue("Date Rated");
                            cell2.setCellStyle(headerStyle);

                            XSSFCell cell3 = row.createCell(2);
                            cell3.setCellValue("User Rated");
                            cell3.setCellStyle(headerStyle);

                            XSSFCell cell4 = row.createCell(3);
                            cell4.setCellValue("Rated By");
                            cell4.setCellStyle(headerStyle);

                            XSSFCell cell5 = row.createCell(4);
                            cell5.setCellValue("Rating");
                            cell5.setCellStyle(headerStyle);

                            XSSFCell cell6 = row.createCell(5);
                            cell6.setCellValue("Feedback");
                            cell6.setCellStyle(headerStyle);

                            int counter = 0;

                            for (DataSnapshot dataSnapshot : snapshot.child("user-rating").getChildren()) {

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("transactions").getChildren()) {

                                    String userToRate = snapshot.child("users").child(dataSnapshot.getKey()).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(dataSnapshot.getKey()).child("Last_Name").getValue(String.class));
                                    String ratedBy = snapshot.child("users").child(dataSnapshot1.child("Rated_By").getValue(String.class)).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(dataSnapshot1.child("Rated_By").getValue(String.class)).child("Last_Name").getValue(String.class));

                                    row = sheet.createRow(counter + 1);

                                    cell1 = row.createCell(0);
                                    cell1.setCellValue(dataSnapshot1.getKey());
                                    sheet.setColumnWidth(0, (dataSnapshot1.getKey().length() + 30) * 256);

                                    cell2 = row.createCell(1);
                                    cell2.setCellValue(dataSnapshot1.child("Date_Rated").getValue(String.class));
                                    sheet.setColumnWidth(1, (dataSnapshot1.child("Date_Rated").getValue(String.class).length() + 30) * 256);

                                    cell3 = row.createCell(2);
                                    cell3.setCellValue(userToRate);
                                    sheet.setColumnWidth(2, (userToRate.length() + 30) * 256);

                                    cell4 = row.createCell(3);
                                    cell4.setCellValue(ratedBy);
                                    sheet.setColumnWidth(3, (ratedBy.length() + 30) * 256);

                                    cell5 = row.createCell(4);
                                    cell5.setCellValue(String.valueOf(dataSnapshot1.child("Rate").getValue(Float.class)));
                                    sheet.setColumnWidth(4, (String.valueOf(dataSnapshot1.child("Rate").getValue(Float.class)).length() + 30) * 256);

                                    cell6 = row.createCell(5);
                                    cell6.setCellValue(dataSnapshot1.child("Feedback").getValue(String.class));
                                    sheet.setColumnWidth(5, (dataSnapshot1.child("Feedback").getValue(String.class).length() + 30) * 256);

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