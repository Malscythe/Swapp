package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class ItemSwipe extends AppCompatActivity {

    private static final String TAG = "Debug";
    private cards cards_data[];
    private arrayAdapter arrayAdapter;

    RelativeLayout noMoreItemBanner;

    ArrayList<cards> rowItems;

    Button noMoreItemsBtn;

    String currentItem, poster_uid, parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_swipe);
        ArrayList<String> keysList = (ArrayList<String>) getIntent().getSerializableExtra("keys");

        noMoreItemBanner = findViewById(R.id.noMoreItemBanner);
        noMoreItemsBtn = findViewById(R.id.returnToCategories);

        noMoreItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemSwipe.this, Categories.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                CustomIntent.customType(ItemSwipe.this, "right-to-left");
                finish();
            }
        });

        getItems();

        rowItems = new ArrayList<cards>();

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                currentItem = rowItems.get(0).getItem_Name();
                poster_uid = rowItems.get(0).getPoster_UID();
                parent = currentItem + "-" + poster_uid;
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();

                if (rowItems.size() == 0) {
                    noMoreItemBanner.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

            }

            @Override
            public void onRightCardExit(Object dataObject) {

                Intent intent = new Intent(ItemSwipe.this, MoreInfo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("poster_uid", poster_uid);
                intent.putExtra("item_name", currentItem);
                intent.putExtra("keys", keysList);
                startActivity(intent);
                CustomIntent.customType(ItemSwipe.this, "right-to-left");
                finish();

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

            }
        });
    }

    public void getItems() {
        ArrayList<String> keysList = (ArrayList<String>) getIntent().getSerializableExtra("keys");

        if (keysList.isEmpty()) {
            noMoreItemBanner.setVisibility(View.VISIBLE);
        }

        DatabaseReference items = FirebaseDatabase.getInstance().getReference().child("items");
        items.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < keysList.size(); i++) {

                    String concatinated = keysList.get(i);
                    String uid = concatinated.substring(0, concatinated.indexOf('-'));
                    String itemName = concatinated.substring((concatinated.indexOf("-") + 1), concatinated.length());

                    if ((snapshot.child(uid).getKey().equals(uid)) && (snapshot.child(uid).child(itemName).getKey().equals(itemName))) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("user-rating").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                cards item = new cards(snapshot.child(uid).child(itemName).child("Images").child("1").getValue(String.class),
                                        snapshot.child(uid).child(itemName).child("Item_RFT").getValue(String.class),
                                        snapshot.child(uid).child(itemName).child("Item_Category").getValue(String.class),
                                        snapshot.child(uid).child(itemName).child("Item_Description").getValue(String.class),
                                        snapshot.child(uid).child(itemName).child("Address").child("City").getValue(String.class),
                                        snapshot.child(uid).child(itemName).child("Address").child("State").getValue(String.class),
                                        snapshot.child(uid).child(itemName).child("Item_Name").getValue(String.class),
                                        snapshot.child(uid).child(itemName).child("Item_PrefItem").getValue(String.class),
                                        snapshot.child(uid).child(itemName).child("Poster_Name").getValue(String.class),
                                        snapshot.child(uid).child(itemName).child("Poster_UID").getValue(String.class),
                                        keysList,
                                        snapshot1.child(uid).child("Average_Rating").getValue(String.class)
                                );
                                rowItems.add(item);
                                arrayAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
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
        Intent intent = new Intent(ItemSwipe.this, Categories.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        CustomIntent.customType(ItemSwipe.this, "right-to-left");
        finish();
    }
}