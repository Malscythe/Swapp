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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.util.ArrayList;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class FileMaintenance extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<FileMaintenanceModel> userArrayList;
    maintenanceAdapter myAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_maintenance);

        recyclerView = findViewById(R.id.userRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<FileMaintenanceModel> options =
                new FirebaseRecyclerOptions.Builder<FileMaintenanceModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").orderByChild("isAdmin").startAt("0").endAt("0"), FileMaintenanceModel.class)
                        .build();

        myAdapter = new maintenanceAdapter(options);
        recyclerView.setAdapter(myAdapter);

        // swipe down refresh
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
        getMenuInflater().inflate(R.menu.fmainsearch,menu);
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
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").orderByChild("Email").startAt(s).endAt(s+"~"), FileMaintenanceModel.class)
                        .build();

        myAdapter = new maintenanceAdapter(options);
        myAdapter.startListening();
        recyclerView.setAdapter(myAdapter);
    }
//
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.fmainSearch)
        {
            Toast.makeText(this,"Settings", Toast.LENGTH_SHORT).show();
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