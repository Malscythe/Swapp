package com.example.Swapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;

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

public class ViewItems extends AppCompatActivity {

    RecyclerView postedRV;

    List<ViewItemsFetch> viewItemsFetchList;
    ViewItemsAdapter viewItemsAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        postedRV = findViewById(R.id.postedRV);

        postedRV.setLayoutManager(new LinearLayoutManager(this));

        viewItemsFetchList = new ArrayList<>();

        String keyToDisplay = getIntent().getStringExtra("key");

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("items");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    for (DataSnapshot dataSnapshot : ds.getChildren()) {
                        if (getIntent().getStringExtra("category").equals("all")) {
                            if (dataSnapshot.child("Status").getValue(String.class).equals("Validated") && dataSnapshot.child("Poster_UID").getValue(String.class).equals(keyToDisplay)) {
                                ViewItemsFetch viewItemsFetch = new ViewItemsFetch();
                                viewItemsFetch.setItem_Image(dataSnapshot.child("Images").child("1").getValue(String.class));
                                viewItemsFetch.setItem_Location(dataSnapshot.child("Address").child("City").getValue(String.class).concat(", " + dataSnapshot.child("Address").child("State").getValue(String.class)));
                                viewItemsFetch.setItem_Name(dataSnapshot.child("Item_Name").getValue(String.class));
                                viewItemsFetchList.add(viewItemsFetch);
                            }
                        } else {
                            if (dataSnapshot.child("Status").getValue(String.class).equals("Validated") && dataSnapshot.child("Poster_UID").getValue(String.class).equals(keyToDisplay) && dataSnapshot.child("Item_Category").getValue(String.class).equals(getIntent().getStringExtra("category"))) {
                                ViewItemsFetch viewItemsFetch = new ViewItemsFetch();
                                viewItemsFetch.setItem_Image(dataSnapshot.child("Images").child("1").getValue(String.class));
                                viewItemsFetch.setItem_Location(dataSnapshot.child("Address").child("City").getValue(String.class).concat(", " + dataSnapshot.child("Address").child("State").getValue(String.class)));
                                viewItemsFetch.setItem_Name(dataSnapshot.child("Item_Name").getValue(String.class));
                                viewItemsFetchList.add(viewItemsFetch);
                            }
                        }

                    }
                }

                viewItemsAdapter = new ViewItemsAdapter(viewItemsFetchList);
                postedRV.setAdapter(viewItemsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ViewItems.this, currentLoc.class);
        intent.putExtra("from", getIntent().getStringExtra("from"));
        intent.putExtra("category", getIntent().getStringExtra("category"));
        startActivity(intent);
        CustomIntent.customType(ViewItems.this, "up-to-bottom");
    }
}