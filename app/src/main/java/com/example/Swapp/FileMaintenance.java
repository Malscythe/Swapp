package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
import java.util.Locale;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class FileMaintenance extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<FileMaintenanceModel> userArrayList;
    maintenanceAdapter myAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton exportData;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_maintenance);

        recyclerView = findViewById(R.id.userRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        exportData = findViewById(R.id.exportData);

        FirebaseRecyclerOptions<FileMaintenanceModel> options =
                new FirebaseRecyclerOptions.Builder<FileMaintenanceModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").orderByChild("isAdmin").startAt("0").endAt("0"), FileMaintenanceModel.class)
                        .build();

        myAdapter = new maintenanceAdapter(options);
        recyclerView.setAdapter(myAdapter);

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    exportData.setVisibility(View.VISIBLE);
                } else {
                    exportData.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            String strCurrentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
                            String strDate = new SimpleDateFormat("MMMM dd, yyyy HH:mm aa", Locale.getDefault()).format(new Date());
                            File root = new File(Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "FileExcel");
                            if (!root.exists())
                                root.mkdirs();
                            File path = new File(root, "/List of users " + strCurrentDate + ".xlsx");

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

                            XSSFSheet sheet = workbook.createSheet("List of users - " + strDate);
                            XSSFRow row = sheet.createRow(0);

                            XSSFCell cell1 = row.createCell(0);
                            cell1.setCellValue("User ID");
                            cell1.setCellStyle(headerStyle);

                            XSSFCell cell2 = row.createCell(1);
                            cell2.setCellValue("First Name");
                            cell2.setCellStyle(headerStyle);

                            XSSFCell cell3 = row.createCell(2);
                            cell3.setCellValue("Last Name");
                            cell3.setCellStyle(headerStyle);

                            XSSFCell cell4 = row.createCell(3);
                            cell4.setCellValue("Email");
                            cell4.setCellStyle(headerStyle);

                            XSSFCell cell5 = row.createCell(4);
                            cell5.setCellValue("Phone");
                            cell5.setCellStyle(headerStyle);

                            XSSFCell cell6 = row.createCell(5);
                            cell6.setCellValue("Birth Date");
                            cell6.setCellStyle(headerStyle);

                            XSSFCell cell7 = row.createCell(6);
                            cell7.setCellValue("Gender");
                            cell7.setCellStyle(headerStyle);

                            int counter = 0;

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                row = sheet.createRow(counter + 1);

                                cell1 = row.createCell(0);
                                cell1.setCellValue(dataSnapshot.getKey());
                                sheet.setColumnWidth(0, (dataSnapshot.getKey().length() + 30) * 256);

                                cell2 = row.createCell(1);
                                cell2.setCellValue(dataSnapshot.child("First_Name").getValue(String.class));
                                sheet.setColumnWidth(1, (dataSnapshot.child("First_Name").getValue(String.class).length() + 30) * 256);

                                cell3 = row.createCell(2);
                                cell3.setCellValue(dataSnapshot.child("Last_Name").getValue(String.class));
                                sheet.setColumnWidth(2, (dataSnapshot.child("Last_Name").getValue(String.class).length() + 30) * 256);

                                cell4 = row.createCell(3);
                                cell4.setCellValue(dataSnapshot.child("Email").getValue(String.class));
                                sheet.setColumnWidth(3, (dataSnapshot.child("Email").getValue(String.class).length() + 30) * 256);

                                cell5 = row.createCell(4);
                                cell5.setCellValue(dataSnapshot.child("Phone").getValue(String.class));
                                sheet.setColumnWidth(4, (dataSnapshot.child("Phone").getValue(String.class).length() + 30) * 256);

                                cell6 = row.createCell(5);
                                cell6.setCellValue(dataSnapshot.child("Birth_Date").getValue(String.class));
                                sheet.setColumnWidth(5, (dataSnapshot.child("Birth_Date").getValue(String.class).length() + 30) * 256);

                                cell7 = row.createCell(6);
                                cell7.setCellValue(dataSnapshot.child("Gender").getValue(String.class));
                                sheet.setColumnWidth(6, (dataSnapshot.child("Gender").getValue(String.class).length() + 30) * 256);

                                counter++;
                            }

                            workbook.write(outputStream);
                            outputStream.close();
                            Toast.makeText(FileMaintenance.this, "Data Exported", Toast.LENGTH_SHORT).show();
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

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                db = FirebaseFirestore.getInstance();
                userArrayList = new ArrayList<FileMaintenanceModel>();
                //myAdapter = new maintenanceAdapter(FileMaintenance.this,userArrayList);

                recyclerView.setAdapter(myAdapter);

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        myAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myAdapter.startListening();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fmainsearch, menu);
        MenuItem item = menu.findItem(R.id.fmainSearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchData(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchData(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void searchData(String s) {

        FirebaseRecyclerOptions<FileMaintenanceModel> options =
                new FirebaseRecyclerOptions.Builder<FileMaintenanceModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").orderByChild("Email").startAt(s).endAt(s + "~"), FileMaintenanceModel.class)
                        .build();

        myAdapter = new maintenanceAdapter(options);
        myAdapter.startListening();
        recyclerView.setAdapter(myAdapter);
    }

    //
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.fmainSearch) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(FileMaintenance.this, AdminHomepage.class));
        CustomIntent.customType(FileMaintenance.this, "right-to-left");
    }
}