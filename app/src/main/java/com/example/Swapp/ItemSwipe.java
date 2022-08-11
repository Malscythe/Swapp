package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import Swapp.R;

public class ItemSwipe extends AppCompatActivity {

    private static final String TAG = "Debug";
    private cards cards_data[];
    private arrayAdapter arrayAdapter;
    private int i;
    String chosenCategory;
    String allCategories;

    private FirebaseDatabase fDatabase;

    ListView listView;
    ArrayList<cards> rowItems;

    TextView itemName;
    String currentItem, poster_uid, parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_swipe);
        Intent intent = getIntent();

        if(intent != null){
            if (getIntent().getStringExtra("category").equals("all")) {
                chosenCategory = "all";
            } else {
                chosenCategory = getIntent().getStringExtra("category");
            }
        }

        getItems();

        rowItems = new ArrayList<cards>();

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                currentItem = rowItems.get(0).getItem_Name();
                poster_uid = rowItems.get(0).getPoster_UID();
                parent = currentItem + "-" + poster_uid;
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(ItemSwipe.this, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                DatabaseReference infoToPass;

                infoToPass = FirebaseDatabase.getInstance().getReference("items");
                infoToPass.child(parent).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot dataSnapshot = task.getResult();
                        Intent intent = new Intent(ItemSwipe.this, MoreInfo.class);
                        intent.putExtra("url", dataSnapshot.child("Image_Url").getValue().toString());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(ItemSwipe.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getItems(){
        DatabaseReference items = FirebaseDatabase.getInstance().getReference().child("items");
        items.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists() && snapshot.child("Item_Category").getValue().toString().equals(chosenCategory)) {

                    cards item = new cards(snapshot.child("Image_Url").getValue().toString(),
                            snapshot.child("Item_Category").getValue().toString(),
                            snapshot.child("Item_Description").getValue().toString(),
                            snapshot.child("Item_Location").getValue().toString(),
                            snapshot.child("Item_Name").getValue().toString(),
                            snapshot.child("Item_Preferred").getValue().toString(),
                            snapshot.child("Poster_Name").getValue().toString(),
                            snapshot.child("Poster_UID").getValue().toString());

                    rowItems.add(item);
                    arrayAdapter.notifyDataSetChanged();
                } else if (snapshot.exists() && chosenCategory.equals("all")) {
                    cards item = new cards(snapshot.child("Image_Url").getValue().toString(),
                            snapshot.child("Item_Category").getValue().toString(),
                            snapshot.child("Item_Description").getValue().toString(),
                            snapshot.child("Item_Location").getValue().toString(),
                            snapshot.child("Item_Name").getValue().toString(),
                            snapshot.child("Item_Preferred").getValue().toString(),
                            snapshot.child("Poster_Name").getValue().toString(),
                            snapshot.child("Poster_UID").getValue().toString());
                    rowItems.add(item);
                    arrayAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent(ItemSwipe.this, Categories.class);
        startActivity(intent);
    }
}