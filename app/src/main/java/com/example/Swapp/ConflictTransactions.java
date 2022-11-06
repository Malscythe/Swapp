package com.example.Swapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ConflictTransactions extends AppCompatActivity {

    RecyclerView conflictRV;

    List<ConflictTransactionsFetch> conflictTransactionsFetchList;
    ConflictTransactionsAdapter conflictTransactionsAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_validate);

        conflictRV = findViewById(R.id.conflictRV);

        conflictRV.setLayoutManager(new LinearLayoutManager(this));

        conflictTransactionsFetchList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("trade-transactions");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    if (ds.child("Transaction_Status").getValue(String.class).equals("On hold")) {

                        ConflictTransactionsFetch conflictTransactionsFetch = new ConflictTransactionsFetch();

                        conflictTransactionsFetch.setTransaction_Key(ds.getKey());
                        conflictTransactionsFetch.setTransaction_Mode(ds.child("Mode_Transaction").getValue(String.class));
                        conflictTransactionsFetch.setPoster_UID(ds.child("Posted_Item").child("Poster_UID").getValue(String.class));
                        conflictTransactionsFetch.setOfferer_UID(ds.child("Offered_Item").child("Poster_UID").getValue(String.class));
                        conflictTransactionsFetch.setPoster_Response(ds.child("Poster_Response").getValue(String.class));
                        conflictTransactionsFetch.setOfferer_Response(ds.child("Offerer_Response").getValue(String.class));

                        conflictTransactionsFetchList.add(conflictTransactionsFetch);
                    }
                }

                conflictTransactionsAdapter = new ConflictTransactionsAdapter(conflictTransactionsFetchList);
                conflictRV.setAdapter(conflictTransactionsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(ConflictTransactions.this, AdminHomepage.class);
        startActivity(intent);
        CustomIntent.customType(ConflictTransactions.this, "right-to-left");
    }
}