package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class MoreInfo extends AppCompatActivity {

    private static final String TAG = "Debug";
    private DatabaseReference mDatabase;

    Context context;

    TextView itemName, posterName, itemLocation, itemPref, itemDesc;
    ImageView itemImage;
    Button offerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        String url = getIntent().getStringExtra("url");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Log.w(TAG, url);

        itemName = findViewById(R.id.Item_Name);
        itemImage = findViewById(R.id.moreInfoImage);
        posterName = findViewById(R.id.Item_Poster);
        itemLocation = findViewById(R.id.Item_Location);
        itemPref = findViewById(R.id.Item_Pref);
        itemDesc = findViewById(R.id.Item_Desc);
        offerBtn = findViewById(R.id.offerbtn);

        DatabaseReference items = FirebaseDatabase.getInstance().getReference().child("items");
        items.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists() && snapshot.child("Image_Url").getValue().equals(url)) {
                    itemName.setText(snapshot.child("Item_Name").getValue().toString());
                    posterName.setText(snapshot.child("Poster_Name").getValue().toString());
                    itemLocation.setText(snapshot.child("Item_Location").getValue().toString());
                    itemPref.setText(snapshot.child("Item_Preferred").getValue().toString());
                    itemDesc.setText(snapshot.child("Item_Description").getValue().toString());
                    Glide.with(MoreInfo.this).load(snapshot.child("Image_Url").getValue().toString()).into(itemImage);

                    offerBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MoreInfo.this, offerItem_S1.class);
                            intent.putExtra("itemid", snapshot.child("Item_Name").getValue().toString().concat("-" + snapshot.child("Poster_UID").getValue().toString()));
                            startActivity(intent);
                            CustomIntent.customType(MoreInfo.this, "left-to-right");
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        String url = getIntent().getStringExtra("url");
        String passedLocation = getIntent().getStringExtra("location");

        DatabaseReference items = FirebaseDatabase.getInstance().getReference().child("items");
        items.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists() && snapshot.child("Image_Url").getValue().equals(url)) {
                    Intent intent = new Intent(MoreInfo.this, ItemSwipe.class);
                    intent.putExtra("category", snapshot.child("Item_Category").getValue().toString());
                    intent.putExtra("keys", getIntent().getSerializableExtra("keys"));
                    startActivity(intent);
                    CustomIntent.customType(MoreInfo.this, "right-to-left");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}