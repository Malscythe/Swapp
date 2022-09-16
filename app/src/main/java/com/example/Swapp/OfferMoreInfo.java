package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.Swapp.chat.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import Swapp.R;

public class OfferMoreInfo extends AppCompatActivity {

    private static final String TAG = "Debug";
    private DatabaseReference mDatabase;

    Uri imageUri;
    Context context;

    TextView itemName, offererName, itemLocation, itemDesc;
    ImageView itemImage;
    Button acceptBtn, declineBtn;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_more_info);

        String url = getIntent().getStringExtra("url");
        String parentKey = getIntent().getStringExtra("parentKey");
        String offererID = getIntent().getStringExtra("userID");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        itemName = findViewById(R.id.Item_Name);
        itemImage = findViewById(R.id.moreInfoImage);
        offererName = findViewById(R.id.Item_Offerer);
        itemLocation = findViewById(R.id.Item_Location);
        itemDesc = findViewById(R.id.Item_Desc);
        acceptBtn = findViewById(R.id.acceptBtn);
        declineBtn = findViewById(R.id.declineBtn);

        DatabaseReference items = FirebaseDatabase.getInstance().getReference().child("items/" + parentKey + "/Offers");
        items.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists() && snapshot.child("Image_Url").getValue().equals(url)) {
                    itemName.setText(snapshot.child("Item_Name").getValue().toString());
                    offererName.setText(snapshot.child("Poster_Name").getValue().toString());
                    itemLocation.setText(snapshot.child("Item_Location").getValue().toString());
                    itemDesc.setText(snapshot.child("Item_Description").getValue().toString());
                    Glide.with(OfferMoreInfo.this).load(snapshot.child("Image_Url").getValue().toString()).into(itemImage);
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

        if (imageUri == null){
            Uri noimageUri = (new Uri.Builder())
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(getResources().getResourcePackageName(R.drawable.noimage))
                    .appendPath(getResources().getResourcePackageName(R.drawable.noimage))
                    .appendPath(getResources().getResourcePackageName(R.drawable.noimage))
                    .build();
            imageUri = noimageUri;
        }

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                Query query = ref.child("items/" + parentKey + "/Offers").orderByChild("Image_Url").equalTo(url);

                databaseReference.child("users").child(offererID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String mobile = snapshot.child("Phone").getValue(String.class);
                        String userName = snapshot.child("First_Name").getValue(String.class).concat(" " + snapshot.child("Last_Name").getValue(String.class));

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                                    ref.child("items").child(parentKey).child("Accepted_Offers").child(offererID).child("Image_Url").setValue(url);
                                    ref.child("items").child(parentKey).child("Accepted_Offers").child(offererID).child("Item_Description").setValue(itemDesc.getText().toString());
                                    ref.child("items").child(parentKey).child("Accepted_Offers").child(offererID).child("Item_Location").setValue(itemLocation.getText().toString());
                                    ref.child("items").child(parentKey).child("Accepted_Offers").child(offererID).child("Item_Name").setValue(itemName.getText().toString());
                                    ref.child("items").child(parentKey).child("Accepted_Offers").child(offererID).child("Offerer_Name").setValue(userName);
                                    ref.child("items").child(parentKey).child("Accepted_Offers").child(offererID).child("Poster_UID").setValue(offererID);
                                    ref.child("items").child(parentKey).child("Accepted_Offers").child(offererID).child("ParentKey").setValue(parentKey);

                                    dataSnapshot.getRef().removeValue();
                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                    intent.putExtra("mobile", mobile);
                                    intent.putExtra("name", userName);
                                    intent.putExtra("chat_key", "");

                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query query = ref.child("items/" + parentKey + "/Offers").orderByChild("Image_Url").equalTo(url);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            dataSnapshot.getRef().removeValue();
                            Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                            intent.putExtra("itemid", parentKey);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });
            }
        });
    }
}