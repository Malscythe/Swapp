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
import maes.tech.intentanim.CustomIntent;

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
        String itemid = getIntent().getStringExtra("itemid");
        String itemname = getIntent().getStringExtra("itemname");

        Log.d(TAG, "items/" + itemid + "/" + itemname +"/Offers");
        databaseReference = FirebaseDatabase.getInstance().getReference("items/" + itemid + "/" + itemname +"/Offers");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    Log.d(TAG, ds.toString());

                    String address = ds.child("Address").child("City").getValue(String.class).concat(", " + ds.child("Address").child("State").getValue(String.class));

                    OfferFetch offerFetch = new OfferFetch();
                    offerFetch.setItem_Name(ds.child("Item_Name").getValue(String.class));
                    offerFetch.setItem_Location(address);
                    offerFetch.setPoster_UID(ds.child("Poster_UID").getValue(String.class));
                    offerFetch.setParentKey(itemname);
                    offerFetch.setOfferCount(ds.child("Offers").getChildrenCount());
                    offerFetch.setImage_Url(ds.child("Images").child(String.valueOf(1)).getValue(String.class));
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
        CustomIntent.customType(OfferSecondActivity.this, "right-to-left");
    }
}
