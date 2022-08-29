package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import Swapp.R;

public class FileMaintenance extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<FileMaintenanceModel> userArrayList;
    maintenanceAdapter myAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_maintenance);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();

        recyclerView = findViewById(R.id.userRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        userArrayList = new ArrayList<FileMaintenanceModel>();
        myAdapter = new maintenanceAdapter(FileMaintenance.this,userArrayList);

        recyclerView.setAdapter(myAdapter);

        EventChangeListener();

        // swipe down refresh
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                db = FirebaseFirestore.getInstance();
                userArrayList = new ArrayList<FileMaintenanceModel>();
                myAdapter = new maintenanceAdapter(FileMaintenance.this,userArrayList);

                recyclerView.setAdapter(myAdapter);

                EventChangeListener();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    private void EventChangeListener() {

        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null)
                {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    Log.e("FireStore Database Error",error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges())
                {
                    if (dc.getType() == DocumentChange.Type.ADDED) {

                        userArrayList.add(dc.getDocument().toObject(FileMaintenanceModel.class));
                    }

                    myAdapter.notifyDataSetChanged();
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                    }
                });
    }

    @Override
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

        db = FirebaseFirestore.getInstance();
        userArrayList = new ArrayList<FileMaintenanceModel>();
        myAdapter = new maintenanceAdapter(FileMaintenance.this,userArrayList);

        recyclerView.setAdapter(myAdapter);

        db.collection("users").orderBy("First_Name").startAt(s).endAt(s+"\uf8ff")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null)
                        {
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("FireStore Database Error",error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges())
                        {
                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                userArrayList.add(dc.getDocument().toObject(FileMaintenanceModel.class));
                            }

                            myAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });

   // db.collection("users")
         //   .orderBy("First_Name")
         //   .startAt(s)
       //     .endAt(s+"\uf8ff"),FileMaintenanceModel.class


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.fmainSearch)
        {
            Toast.makeText(this,"Settings", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}