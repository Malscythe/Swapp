package com.example.Swapp;

import android.content.Intent;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class ItemApproval extends AppCompatActivity {

    RecyclerView postedRV;

    List<ItemApprovalFetch> itemApprovalFetchList;
    ItemApprovalAdapter itemApprovalAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_approval);

        postedRV = findViewById(R.id.postedRV);

        postedRV.setLayoutManager(new LinearLayoutManager(this));

        itemApprovalFetchList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("items");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    for (DataSnapshot dataSnapshot : ds.getChildren()) {
                        if (dataSnapshot.child("Status").getValue(String.class).equals("Validating")) {
                            ItemApprovalFetch itemApprovalFetch = new ItemApprovalFetch();
                            itemApprovalFetch.setPoster_UID(dataSnapshot.child("Poster_UID").getValue(String.class));
                            itemApprovalFetch.setItem_Image(dataSnapshot.child("Images").child("1").getValue(String.class));
                            itemApprovalFetch.setItem_Location(dataSnapshot.child("Address").child("City").getValue(String.class).concat(", " + dataSnapshot.child("Address").child("State").getValue(String.class)));
                            itemApprovalFetch.setItem_PosterName(dataSnapshot.child("Poster_Name").getValue(String.class));
                            itemApprovalFetch.setItem_Name(dataSnapshot.child("Item_Name").getValue(String.class));
                            itemApprovalFetch.setPoster_Phone(dataSnapshot.child("Phone").getValue(String.class));
                            itemApprovalFetch.setPassedActivity(ItemApproval.this);
                            itemApprovalFetchList.add(itemApprovalFetch);
                        }
                    }
                }

                itemApprovalAdapter = new ItemApprovalAdapter(itemApprovalFetchList);
                postedRV.setAdapter(itemApprovalAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(ItemApproval.this, AdminHomepage.class);
        startActivity(intent);
        CustomIntent.customType(ItemApproval.this, "right-to-left");
    }
}