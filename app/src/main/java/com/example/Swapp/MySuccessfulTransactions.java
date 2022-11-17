    package com.example.Swapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Swapp.R;

public class MySuccessfulTransactions extends AppCompatActivity {

    private static final String TAG = "TAG";
    RecyclerView offerRV3;

    List<CompleteTransactionFetch> completeTransactionFetches;
    MySuccessfulTransactionAdapter mySuccessfulTransactionAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;
    DatabaseReference databaseReferenceUrl = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");
    TabLayout transactionTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_successful_transactions);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        offerRV3 = findViewById(R.id.offerRV_3);
        offerRV3.setLayoutManager(new LinearLayoutManager(this));
        completeTransactionFetches = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        String itemid = getIntent().getStringExtra("itemid");
        String itemname = getIntent().getStringExtra("itemname");

        transactionTabLayout = findViewById(R.id.tabTop);

        transactionTabLayout.selectTab(transactionTabLayout.getTabAt(2));
        transactionTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "Accepted offers":
                        Intent intent = new Intent(MySuccessfulTransactions.this, MyItemCurrentTransaction.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case "Offers sent":
                        Intent intent1 = new Intent(MySuccessfulTransactions.this, MyOffersSentTransaction.class);
                        startActivity(intent1);
                        overridePendingTransition(0, 0);
                        break;
                    case "Successful":
                        break;
                    case "Unsuccessful":
                        Intent intent2 = new Intent(MySuccessfulTransactions.this, MyUnsuccessfulTransactions.class);
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
                                Intent intent = new Intent(MySuccessfulTransactions.this, Messages.class);
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
                        Intent intent = new Intent(MySuccessfulTransactions.this, UserHomepage.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.offers:
                        Intent intent2 = new Intent(MySuccessfulTransactions.this, OfferMainAcitvity.class);
                        startActivity(intent2);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.transactions:
                        return true;
                }
                return true;
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("trade-transactions");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("Transaction_Status").getValue(String.class).equals("Successful") &&
                       (ds.child("Offered_Item").child("Poster_UID").getValue(String.class).equals(uid) || ds.child("Posted_Item").child("Poster_UID").getValue(String.class).equals(uid))) {

                        CompleteTransactionFetch completeTransactionFetch = new CompleteTransactionFetch();

                        completeTransactionFetch.setTransaction_Key(ds.getKey());

                        completeTransactionFetch.setOffered_ItemName(ds.child("Offered_Item").child("Item_Name").getValue(String.class));
                        completeTransactionFetch.setOfferer_Name(ds.child("Offered_Item").child("Poster_Name").getValue(String.class));
                        completeTransactionFetch.setOfferer_UID(ds.child("Offered_Item").child("Poster_UID").getValue(String.class));
                        completeTransactionFetch.setOfferer_Image(ds.child("Offered_Item").child("Images").child("1").getValue(String.class));

                        completeTransactionFetch.setPosted_ItemName(ds.child("Posted_Item").child("Item_Name").getValue(String.class));
                        completeTransactionFetch.setPoster_Name(ds.child("Posted_Item").child("Poster_Name").getValue(String.class));
                        completeTransactionFetch.setPoster_UID(ds.child("Posted_Item").child("Poster_UID").getValue(String.class));
                        completeTransactionFetch.setPosted_Image(ds.child("Posted_Item").child("Images").child("1").getValue(String.class));

                        completeTransactionFetches.add(completeTransactionFetch);

                        mySuccessfulTransactionAdapter = new MySuccessfulTransactionAdapter(completeTransactionFetches);
                        offerRV3.setAdapter(mySuccessfulTransactionAdapter);
                    }
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
        Intent intent = new Intent(MySuccessfulTransactions.this, UserHomepage.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}