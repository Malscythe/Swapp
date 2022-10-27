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

public class Transactions extends AppCompatActivity {

    RecyclerView transactionRV;

    List<TransactionFetch> transactionFetchList;
    TransactionAdapter transactionAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FloatingActionButton exportData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        transactionRV = findViewById(R.id.transactionRV);

        transactionRV.setLayoutManager(new LinearLayoutManager(this));

        transactionFetchList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        exportData = findViewById(R.id.exportData);

        databaseReference = FirebaseDatabase.getInstance().getReference("trade-transactions");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (!ds.child("Transaction_Status").getValue(String.class).equals("Waiting for review")) {
                        TransactionFetch transactionFetch = new TransactionFetch();
                        transactionFetch.setPoster_UID(ds.child("Posted_Item").child("Poster_UID").getValue(String.class));
                        transactionFetch.setOfferer_UID(ds.child("Offered_Item").child("Poster_UID").getValue(String.class));
                        transactionFetch.setPosted_ItemName(ds.child("Posted_Item").child("Item_Name").getValue(String.class));
                        transactionFetch.setOffered_ItemName(ds.child("Offered_Item").child("Item_Name").getValue(String.class));
                        transactionFetch.setTransactionKey(ds.getKey());
                        transactionFetch.setTransactionStatus(ds.child("Transaction_Status").getValue(String.class));
                        transactionFetchList.add(transactionFetch);
                    }
                }

                transactionAdapter = new TransactionAdapter(transactionFetchList);
                transactionRV.setAdapter(transactionAdapter);
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
                            String strDate = new SimpleDateFormat("MMMM dd, yyyy hh:mm aa", Locale.getDefault()).format(new Date());
                            File root = new File(Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "FileExcel");
                            if (!root.exists()) {
                                root.mkdirs();
                            }

                            File path = new File(root, "/Transactions " + strCurrentDate + ".xlsx");

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

                            XSSFSheet sheet = workbook.createSheet("Transactions - " + strDate);
                            XSSFRow row = sheet.createRow(0);

                            XSSFCell cell1 = row.createCell(0);
                            cell1.setCellValue("Transaction Key");
                            cell1.setCellStyle(headerStyle);

                            XSSFCell cell2 = row.createCell(1);
                            cell2.setCellValue("Posted Item");
                            cell2.setCellStyle(headerStyle);

                            XSSFCell cell3 = row.createCell(2);
                            cell3.setCellValue("Posted By");
                            cell3.setCellStyle(headerStyle);

                            XSSFCell cell4 = row.createCell(3);
                            cell4.setCellValue("Offered Item");
                            cell4.setCellStyle(headerStyle);

                            XSSFCell cell5 = row.createCell(4);
                            cell5.setCellValue("Offered By");
                            cell5.setCellStyle(headerStyle);

                            XSSFCell cell6 = row.createCell(5);
                            cell6.setCellValue("Status");
                            cell6.setCellStyle(headerStyle);

                            int counter = 0;

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                row = sheet.createRow(counter + 1);

                                cell1 = row.createCell(0);
                                cell1.setCellValue(dataSnapshot.getKey());
                                sheet.setColumnWidth(0, (dataSnapshot.getKey().length() + 30) * 256);

                                cell2 = row.createCell(1);
                                cell2.setCellValue(dataSnapshot.child("Posted_Item").child("Item_Name").getValue(String.class));
                                sheet.setColumnWidth(1, (dataSnapshot.child("Posted_Item").child("Item_Name").getValue(String.class).length() + 30) * 256);

                                cell3 = row.createCell(2);
                                cell3.setCellValue(dataSnapshot.child("Posted_Item").child("Poster_UID").getValue(String.class));
                                sheet.setColumnWidth(2, (dataSnapshot.child("Posted_Item").child("Poster_UID").getValue(String.class).length() + 30) * 256);

                                cell4 = row.createCell(3);
                                cell4.setCellValue(dataSnapshot.child("Offered_Item").child("Item_Name").getValue(String.class));
                                sheet.setColumnWidth(3, (dataSnapshot.child("Offered_Item").child("Item_Name").getValue(String.class).length() + 30) * 256);

                                cell5 = row.createCell(4);
                                cell5.setCellValue(dataSnapshot.child("Offered_Item").child("Poster_UID").getValue(String.class));
                                sheet.setColumnWidth(4, (dataSnapshot.child("Offered_Item").child("Poster_UID").getValue(String.class).length() + 30) * 256);

                                cell6 = row.createCell(5);
                                cell6.setCellValue(dataSnapshot.child("Transaction_Status").getValue(String.class));
                                sheet.setColumnWidth(5, (dataSnapshot.child("Transaction_Status").getValue(String.class).length() + 30) * 256);

                                counter++;
                            }

                            workbook.write(outputStream);
                            outputStream.close();
                            new SweetAlertDialog(Transactions.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Create excel report")
                                    .setContentText("Report for Transactions has been created!")
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

        Intent intent = new Intent(Transactions.this, AdminHomepage.class);
        startActivity(intent);
        CustomIntent.customType(Transactions.this, "right-to-left");
    }
}