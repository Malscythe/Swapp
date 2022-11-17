package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class MyOffersSentTransaction extends AppCompatActivity {

    private static final String TAG = "TAG";
    RecyclerView offerRV3;

    List<OfferFetch> offerFetchList;
    MyOfferSentTransactionAdapter myOfferSentTransactionAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;
    DatabaseReference databaseReferenceUrl = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");
    TabLayout transactionTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_offers_sent_transaction);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        offerRV3 = findViewById(R.id.offerRV_3);
        offerRV3.setLayoutManager(new LinearLayoutManager(this));
        offerFetchList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        String itemid = getIntent().getStringExtra("itemid");
        String itemname = getIntent().getStringExtra("itemname");

        transactionTabLayout = findViewById(R.id.tabTop);

        transactionTabLayout.selectTab(transactionTabLayout.getTabAt(1));
        transactionTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "Accepted offers":
                        Intent intent = new Intent(MyOffersSentTransaction.this, MyItemCurrentTransaction.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case "Offers sent":
                        break;
                    case "Successful":
                        Intent intent1 = new Intent(MyOffersSentTransaction.this, MySuccessfulTransactions.class);
                        startActivity(intent1);
                        overridePendingTransition(0, 0);
                        break;
                    case "Unsuccessful":
                        Intent intent2 = new Intent(MyOffersSentTransaction.this, MyUnsuccessfulTransactions.class);
                        startActivity(intent2);
                        overridePendingTransition(0, 0);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        bottomNavigationView.setSelectedItemId(R.id.transactions);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.inbox:
                        databaseReferenceUrl.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name = snapshot.child("First_Name").getValue(String.class).concat(" " + snapshot.child("Last_Name").getValue(String.class));
                                Intent intent = new Intent(MyOffersSentTransaction.this, Messages.class);
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
                        Intent intent = new Intent(MyOffersSentTransaction.this, UserHomepage.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.offers:
                        Intent intent2 = new Intent(MyOffersSentTransaction.this, OfferMainAcitvity.class);
                        startActivity(intent2);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.transactions:
                        return true;
                }
                return true;
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("items");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot : ds.getChildren()) {

                        if (dataSnapshot.child("Accepted_Offers").hasChild(uid)) {
                            String address = dataSnapshot.child("Accepted_Offers").child(uid).child("Address").child("City").getValue(String.class).concat(", " + dataSnapshot.child("Accepted_Offers").child(uid).child("Address").child("State").getValue(String.class));

                            OfferFetch offerFetch = new OfferFetch();
                            offerFetch.setItem_Name(dataSnapshot.child("Accepted_Offers").child(uid).child("Item_Name").getValue(String.class));
                            offerFetch.setItem_Location(address);
                            offerFetch.setPoster_UID(dataSnapshot.child("Accepted_Offers").child(uid).child("Poster_UID").getValue(String.class));
                            offerFetch.setParentKey(itemname);
                            offerFetch.setOfferCount(dataSnapshot.child("Accepted_Offers").child(uid).child("Offers").getChildrenCount());
                            offerFetch.setImage_Url(dataSnapshot.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(1)).getValue(String.class));
                            offerFetchList.add(offerFetch);
                        }
                    }

                    myOfferSentTransactionAdapter = new MyOfferSentTransactionAdapter(offerFetchList);
                    offerRV3.setAdapter(myOfferSentTransactionAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyOffersSentTransaction.this, UserHomepage.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}