package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.rpc.Help;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class OfferMainAcitvity extends AppCompatActivity {

    RecyclerView offerRV;

    List<OfferFetch> offerFetchList;
    OfferAdapter offerAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_main_acitvity);

        offerRV = findViewById(R.id.offerRV);
        offerRV.setLayoutManager(new LinearLayoutManager(this));
        offerFetchList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.offers);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.inbox:
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name = snapshot.child("First_Name").getValue(String.class).concat(" " + snapshot.child("Last_Name").getValue(String.class));
                                Intent intent = new Intent(OfferMainAcitvity.this, Messages.class);
                                intent.putExtra("mobile", snapshot.child("Phone").getValue(String.class));
                                intent.putExtra("email", snapshot.child("Email").getValue(String.class));
                                intent.putExtra("name", name);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        return true;
                    case R.id.home:
                        startActivity(new Intent(OfferMainAcitvity.this, UserHomepage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case  R.id.offers:
                        return true;
                    case R.id.transactions:
                        Intent intent2 = new Intent(OfferMainAcitvity.this, CurrentTransactions.class);
                        startActivity(intent2);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return true;
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("items/" + uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    String address = ds.child("Address").child("City").getValue(String.class).concat(", " + ds.child("Address").child("State").getValue(String.class));

                    OfferFetch offerFetch = new OfferFetch();
                    offerFetch.setItem_Name(ds.child("Item_Name").getValue(String.class));
                    offerFetch.setItem_Location(address);
                    offerFetch.setPoster_UID(ds.child("Poster_UID").getValue(String.class));
                    offerFetch.setOfferCount(ds.child("Offers").getChildrenCount());
                    offerFetch.setImage_Url(ds.child("Images").child(String.valueOf(1)).getValue(String.class));
                    offerFetchList.add(offerFetch);
                }

                offerAdapter = new OfferAdapter(offerFetchList);
                offerRV.setAdapter(offerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(OfferMainAcitvity.this, UserHomepage.class);
        startActivity(intent);
        CustomIntent.customType(OfferMainAcitvity.this, "right-to-left");
    }
}