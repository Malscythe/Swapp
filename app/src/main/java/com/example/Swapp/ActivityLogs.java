package com.example.Swapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
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

public class ActivityLogs extends AppCompatActivity {

    RecyclerView activityLogsRV;

    List<ActivityLogsFetch> activityLogsFetches;
    ActivityLogsAdapter activityLogsAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FloatingActionButton exportData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        activityLogsRV = findViewById(R.id.activityLogsRV);

        activityLogsRV.setLayoutManager(new LinearLayoutManager(this));

        activityLogsFetches = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        exportData = findViewById(R.id.exportData);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.child("activity-logs").getChildren())
                {
                    if (snapshot.child("users").hasChild(ds.child("User_ID").getValue(String.class))) {
                        String userName = snapshot.child("users").child(ds.child("User_ID").getValue(String.class)).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(ds.child("User_ID").getValue(String.class)).child("Last_Name").getValue(String.class));

                        ActivityLogsFetch activityLogsFetch = new ActivityLogsFetch();
                        activityLogsFetch.setUser_Name(userName);
                        activityLogsFetch.setUser_ID(ds.child("User_ID").getValue(String.class));
                        activityLogsFetch.setActivity(ds.child("Activity").getValue(String.class));
                        activityLogsFetch.setDate(ds.child("Date").getValue(String.class));
                        activityLogsFetches.add(activityLogsFetch);
                    }
                }

                activityLogsAdapter = new ActivityLogsAdapter(activityLogsFetches);
                activityLogsRV.setAdapter(activityLogsAdapter);

                activityLogsRV.scrollToPosition(activityLogsRV.getAdapter().getItemCount() - 1);
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
                            File root = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"Exported Reports");

                            if (!root.exists()) {
                                root.mkdirs();
                            }

                            File path = new File(root, "/Activity logs " + strCurrentDate + ".xlsx");

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

                            XSSFCell cell1 = row.createCell(0);
                            cell1.setCellValue("Date");
                            cell1.setCellStyle(headerStyle);

                            XSSFCell cell2 = row.createCell(1);
                            cell2.setCellValue("User ID");
                            cell2.setCellStyle(headerStyle);

                            XSSFCell cell3 = row.createCell(2);
                            cell3.setCellValue("User Name");
                            cell3.setCellStyle(headerStyle);

                            XSSFCell cell4 = row.createCell(3);
                            cell4.setCellValue("Activity");
                            cell4.setCellStyle(headerStyle);

                            int counter = 0;

                            for (DataSnapshot dataSnapshot : snapshot.child("activity-logs").getChildren()) {

                                if (snapshot.child("users").hasChild(dataSnapshot.child("User_ID").getValue(String.class))) {
                                    String userName = snapshot.child("users").child(dataSnapshot.child("User_ID").getValue(String.class)).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(dataSnapshot.child("User_ID").getValue(String.class)).child("Last_Name").getValue(String.class));

                                    row = sheet.createRow(counter + 1);

                                    cell1 = row.createCell(0);
                                    cell1.setCellValue(dataSnapshot.child("Date").getValue(String.class));
                                    sheet.setColumnWidth(0, (dataSnapshot.child("Date").getValue(String.class).length() + 30) * 256);

                                    cell2 = row.createCell(1);
                                    cell2.setCellValue(dataSnapshot.child("User_ID").getValue(String.class));
                                    sheet.setColumnWidth(1, (dataSnapshot.child("User_ID").getValue(String.class).length() + 30) * 256);

                                    cell3 = row.createCell(2);
                                    cell3.setCellValue(userName);
                                    sheet.setColumnWidth(2, (userName.length() + 30) * 256);

                                    cell4 = row.createCell(3);
                                    cell4.setCellValue(dataSnapshot.child("Activity").getValue(String.class));
                                    sheet.setColumnWidth(3, (dataSnapshot.child("Activity").getValue(String.class).length() + 30) * 256);

                                    counter++;
                                }
                            }

                            workbook.write(outputStream);
                            outputStream.close();
                            new SweetAlertDialog(ActivityLogs.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Create excel report")
                                    .setContentText("Report for Activity Logs has been created!")
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

        Intent intent = new Intent(ActivityLogs.this, AdminHomepage.class);
        startActivity(intent);
        CustomIntent.customType(ActivityLogs.this, "right-to-left");
    }
}