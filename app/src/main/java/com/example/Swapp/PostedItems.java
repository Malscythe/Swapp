package com.example.Swapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

public class PostedItems extends AppCompatActivity {

    RecyclerView postedRV;

    List<PostedItemsFetch> postedItemsFetches;
    PostedItemsAdapter postedItemsAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FloatingActionButton exportData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posted_items);

        postedRV = findViewById(R.id.postedRV);

        postedRV.setLayoutManager(new LinearLayoutManager(this));

        postedItemsFetches = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        exportData = findViewById(R.id.exportData);

        databaseReference = FirebaseDatabase.getInstance().getReference("items");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    for (DataSnapshot dataSnapshot : ds.getChildren()) {
                        if (dataSnapshot.child("Status").getValue(String.class).equals("Validated")) {
                            PostedItemsFetch postedItemsFetch = new PostedItemsFetch();
                            postedItemsFetch.setPoster_UID(dataSnapshot.child("Poster_UID").getValue(String.class));
                            postedItemsFetch.setItem_Image(dataSnapshot.child("Images").child("1").getValue(String.class));
                            postedItemsFetch.setItem_Location(dataSnapshot.child("Address").child("City").getValue(String.class).concat(", " + dataSnapshot.child("Address").child("State").getValue(String.class)));
                            postedItemsFetch.setItem_PosterName(dataSnapshot.child("Poster_Name").getValue(String.class));
                            postedItemsFetch.setItem_Name(dataSnapshot.child("Item_Name").getValue(String.class));
                            postedItemsFetches.add(postedItemsFetch);
                        }
                    }
                }

                postedItemsAdapter = new PostedItemsAdapter(postedItemsFetches);
                postedRV.setAdapter(postedItemsAdapter);
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
                            String strCurrentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
                            String strDate = new SimpleDateFormat("MMMM dd, yyyy hh:mm aa", Locale.getDefault()).format(new Date());
                            File root = new File(Environment
                                    .getExternalStorageDirectory(), "Exported Reports");
                            if (!root.exists()) {
                                root.mkdirs();
                            }

                            File path = new File(root, "/Posted items " + strCurrentDate + ".xlsx");

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

                            XSSFSheet sheet = workbook.createSheet("Posted items - " + strDate);
                            XSSFRow row = sheet.createRow(0);

                            XSSFCell cell1 = row.createCell(0);
                            cell1.setCellValue("User ID");
                            cell1.setCellStyle(headerStyle);

                            XSSFCell cell2 = row.createCell(1);
                            cell2.setCellValue("Posted By");
                            cell2.setCellStyle(headerStyle);

                            XSSFCell cell3 = row.createCell(2);
                            cell3.setCellValue("Item Name");
                            cell3.setCellStyle(headerStyle);

                            XSSFCell cell4 = row.createCell(3);
                            cell4.setCellValue("Category");
                            cell4.setCellStyle(headerStyle);

                            XSSFCell cell5 = row.createCell(4);
                            cell5.setCellValue("Location");
                            cell5.setCellStyle(headerStyle);

                            XSSFCell cell6 = row.createCell(5);
                            cell6.setCellValue("Status");
                            cell6.setCellStyle(headerStyle);

                            int counter = 0;

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    row = sheet.createRow(counter + 1);

                                    cell1 = row.createCell(0);
                                    cell1.setCellValue(dataSnapshot1.child("Poster_UID").getValue(String.class));
                                    sheet.setColumnWidth(0, (dataSnapshot1.child("Poster_UID").getValue(String.class).length() + 30) * 256);

                                    cell2 = row.createCell(1);
                                    cell2.setCellValue(dataSnapshot1.child("Poster_Name").getValue(String.class));
                                    sheet.setColumnWidth(1, (dataSnapshot1.child("Poster_Name").getValue(String.class).length() + 30) * 256);

                                    cell3 = row.createCell(2);
                                    cell3.setCellValue(dataSnapshot1.child("Item_Name").getValue(String.class));
                                    sheet.setColumnWidth(2, (dataSnapshot1.child("Item_Name").getValue(String.class).length() + 30) * 256);

                                    cell4 = row.createCell(3);
                                    cell4.setCellValue(dataSnapshot1.child("Item_Category").getValue(String.class));
                                    sheet.setColumnWidth(3, (dataSnapshot1.child("Item_Category").getValue(String.class).length() + 30) * 256);

                                    cell5 = row.createCell(4);
                                    cell5.setCellValue(dataSnapshot1.child("Address").child("City").getValue(String.class).concat(", " + dataSnapshot1.child("Address").child("State").getValue(String.class)));
                                    sheet.setColumnWidth(4, ((dataSnapshot1.child("Address").child("City").getValue(String.class).concat(", " + dataSnapshot1.child("Address").child("State").getValue(String.class))).length() + 30) * 256);

                                    cell6 = row.createCell(5);
                                    cell6.setCellValue(dataSnapshot1.child("Status").getValue(String.class));
                                    sheet.setColumnWidth(5, (dataSnapshot1.child("Status").getValue(String.class).length() + 30) * 256);

                                    counter++;
                                }
                            }

                            workbook.write(outputStream);
                            outputStream.close();
                            new SweetAlertDialog(PostedItems.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Create excel report")
                                    .setContentText("Report for Posted Items has been created!")
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

        Intent intent = new Intent(PostedItems.this, AdminHomepage.class);
        startActivity(intent);
        CustomIntent.customType(PostedItems.this, "right-to-left");
    }
}