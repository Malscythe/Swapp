package com.example.Swapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Swapp.R;

public class OfferSecondActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    RecyclerView offerRV2;

    List<OfferFetch> offerFetchList;
    OfferAdapter2 offerAdapter2;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offersecondactivity);

        offerRV2 = findViewById(R.id.offerRV_2);
        offerRV2.setLayoutManager(new LinearLayoutManager(this));
        offerFetchList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        String itemid = getIntent().getStringExtra("itemid");
        databaseReference = FirebaseDatabase.getInstance().getReference("items/" + itemid + "/Offers");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    OfferFetch offerFetch = ds.getValue(OfferFetch.class);
                    offerFetchList.add(offerFetch);
                }

                offerAdapter2 = new OfferAdapter2(offerFetchList);
                offerRV2.setAdapter(offerAdapter2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OfferSecondActivity.this, OfferMainAcitvity.class);
        startActivity(intent);
    }
}
