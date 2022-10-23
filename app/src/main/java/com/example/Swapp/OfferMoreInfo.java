package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.Swapp.chat.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.ArrayList;
import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class OfferMoreInfo extends AppCompatActivity {

    private static final String TAG = "Debug";
    private DatabaseReference mDatabase;

    FirebaseAuth firebaseAuth;

    LoadingDialog loadingDialog = new LoadingDialog(OfferMoreInfo.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_more_info);

        String item_Name = getIntent().getStringExtra("itemName");
        String uid = getIntent().getStringExtra("userID");
        String poster_itemName = getIntent().getStringExtra("poster_itemName");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        String currentId = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference items = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName).child("Offers").child(uid);
        items.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String category = snapshot.child("Item_Category").getValue(String.class);

                RatingBar userRating = findViewById(R.id.rating);
                TextView userRatingNum = findViewById(R.id.ratingNum);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("user-rating").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userRating.setRating(Float.parseFloat(snapshot.child(uid).child("Average_Rating").getValue(String.class)));
                        userRatingNum.setText(snapshot.child(uid).child("Average_Rating").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                switch (category) {
                    case "Men's Apparel":
                        List<SlideModel> mSlideModels = new ArrayList<>();

                        RelativeLayout mLayout = findViewById(R.id.mensLayout);
                        TextView mItemName = findViewById(R.id.Item_NameM);
                        ImageSlider mItemImage = findViewById(R.id.image_sliderM);
                        TextView mPosterName = findViewById(R.id.Item_Poster);
                        TextView mMaterial = findViewById(R.id.materialM);
                        TextView mBrand = findViewById(R.id.brandM);
                        TextView mColor = findViewById(R.id.colorM);
                        TextView mType = findViewById(R.id.typeM);
                        TextView mCategory = findViewById(R.id.categoryM);
                        TextView mSizes = findViewById(R.id.sizesM);
                        TextView mUsage = findViewById(R.id.usageM);
                        TextView mLocation = findViewById(R.id.Item_LocationM);
                        TextView mDescription = findViewById(R.id.adM);
                        Button mAcceptBtn = findViewById(R.id.acceptBtn);
                        Button mDeclineBtn = findViewById(R.id.declineBtn);
                        ImageView mSizeChart = findViewById(R.id.sizeChartImageM);
                        CardView mSizeChartLayout = findViewById(R.id.sizeChartM);

                        mLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            mSlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String mAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        mItemImage.setImageList(mSlideModels, null);
                        mItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        mPosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        mMaterial.setText(snapshot.child("Item_Material").getValue(String.class));
                        mBrand.setText(snapshot.child("Item_Brand").getValue(String.class));
                        mColor.setText(snapshot.child("Item_Color").getValue(String.class));
                        mType.setText(snapshot.child("Item_Type").getValue(String.class));
                        mCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        mSizes.setText(snapshot.child("Item_Sizes").getValue(String.class));
                        mUsage.setText(snapshot.child("Item_Usage").getValue(String.class));
                        mLocation.setText(mAddress);
                        mDescription.setText(snapshot.child("Item_Description").getValue(String.class));

                        if (snapshot.hasChild("Item_SizeChart")) {
                            mSizeChartLayout.setVisibility(View.VISIBLE);

                            Glide.with(OfferMoreInfo.this).load(snapshot.child("Item_SizeChart").getValue(String.class)).into(mSizeChart);
                        } else {

                            mSizeChartLayout.setVisibility(View.GONE);
                        }

                        mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                mLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Type").setValue(snapshot.child("Offers").child(uid).child("Item_Type").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Brand").setValue(snapshot.child("Offers").child(uid).child("Item_Brand").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Color").setValue(snapshot.child("Offers").child(uid).child("Item_Color").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Material").setValue(snapshot.child("Offers").child(uid).child("Item_Material").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Usage").setValue(snapshot.child("Offers").child(uid).child("Item_Usage").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Sizes").setValue(snapshot.child("Offers").child(uid).child("Item_Sizes").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Description").setValue(snapshot.child("Offers").child(uid).child("Item_Description").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);

                                                if (snapshot.child("chat").exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                        String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                        String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                        if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", dataSnapshot.getKey());
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        } else {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", "");
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        }
                                                    }
                                                } else {
                                                    loadingDialog.DismissDialog();

                                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                    intent.putExtra("mobile", mobile);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("userID", uid);
                                                    intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                    startActivity(intent);
                                                    CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        mDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.DismissDialog();

                                mLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        loadingDialog.DismissDialog();

                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        break;
                    case "Women's Apparel":
                        List<SlideModel> wSlideModels = new ArrayList<>();

                        RelativeLayout wLayout = findViewById(R.id.womensLayout);
                        TextView wItemName = findViewById(R.id.Item_NameW);
                        ImageSlider wItemImage = findViewById(R.id.image_sliderW);
                        TextView wPosterName = findViewById(R.id.Item_Poster);
                        TextView wMaterial = findViewById(R.id.materialW);
                        TextView wBrand = findViewById(R.id.brandW);
                        TextView wColor = findViewById(R.id.colorW);
                        TextView wType = findViewById(R.id.typeW);
                        TextView wCategory = findViewById(R.id.categoryW);
                        TextView wSizes = findViewById(R.id.sizesW);
                        TextView wUsage = findViewById(R.id.usageW);
                        TextView wLocation = findViewById(R.id.Item_LocationW);
                        TextView wDescription = findViewById(R.id.adW);
                        Button wAcceptBtn = findViewById(R.id.acceptBtn);
                        Button wDeclineBtn = findViewById(R.id.declineBtn);
                        ImageView wSizeChart = findViewById(R.id.sizeChartImageW);
                        CardView wSizeChartLayout = findViewById(R.id.sizeChartW);

                        wLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            wSlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String wAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        wItemImage.setImageList(wSlideModels, null);
                        wItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        wPosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        wMaterial.setText(snapshot.child("Item_Material").getValue(String.class));
                        wBrand.setText(snapshot.child("Item_Brand").getValue(String.class));
                        wColor.setText(snapshot.child("Item_Color").getValue(String.class));
                        wType.setText(snapshot.child("Item_Type").getValue(String.class));
                        wCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        wSizes.setText(snapshot.child("Item_Sizes").getValue(String.class));
                        wUsage.setText(snapshot.child("Item_Usage").getValue(String.class));
                        wLocation.setText(wAddress);
                        wDescription.setText(snapshot.child("Item_Description").getValue(String.class));

                        if (snapshot.hasChild("Item_SizeChart")) {
                            wSizeChartLayout.setVisibility(View.VISIBLE);

                            Glide.with(OfferMoreInfo.this).load(snapshot.child("Item_SizeChart").getValue(String.class)).into(wSizeChart);
                        } else {

                            wSizeChartLayout.setVisibility(View.GONE);
                        }

                        wAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                wLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Type").setValue(snapshot.child("Offers").child(uid).child("Item_Type").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Brand").setValue(snapshot.child("Offers").child(uid).child("Item_Brand").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Color").setValue(snapshot.child("Offers").child(uid).child("Item_Color").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Material").setValue(snapshot.child("Offers").child(uid).child("Item_Material").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Usage").setValue(snapshot.child("Offers").child(uid).child("Item_Usage").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Sizes").setValue(snapshot.child("Offers").child(uid).child("Item_Sizes").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Description").setValue(snapshot.child("Offers").child(uid).child("Item_Description").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);

                                                if (snapshot.child("chat").exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                        String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                        String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                        if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", dataSnapshot.getKey());
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        } else {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", "");
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        }
                                                    }
                                                } else {
                                                    loadingDialog.DismissDialog();

                                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                    intent.putExtra("mobile", mobile);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("userID", uid);
                                                    intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                    startActivity(intent);
                                                    CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        wDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                wLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        loadingDialog.DismissDialog();

                                        snapshot.child("Offers").child(uid).getRef().removeValue();
                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        break;
                    case "Gadgets":
                        List<SlideModel> gadgetsSlideModels = new ArrayList<>();

                        RelativeLayout gadgetsLayout = findViewById(R.id.gadgetsLayout);
                        TextView gadgetsItemName = findViewById(R.id.Item_NameGadget);
                        ImageSlider gadgetsItemImage = findViewById(R.id.image_sliderGadget);
                        TextView gadgetsPosterName = findViewById(R.id.Item_Poster);
                        TextView gadgetsBrand = findViewById(R.id.brandGadget);
                        TextView gadgetsColor = findViewById(R.id.colorGadget);
                        TextView gadgetsType = findViewById(R.id.typeGadget);
                        TextView gadgetsCategory = findViewById(R.id.categoryGadget);
                        TextView gadgetsUsage = findViewById(R.id.usageGadget);
                        TextView gadgetsLocation = findViewById(R.id.Item_LocationGadget);
                        TextView gadgetsDescription = findViewById(R.id.adGadget);
                        Button gadgetAcceptBtn = findViewById(R.id.acceptBtn);
                        Button gadgetDeclineBtn = findViewById(R.id.declineBtn);

                        gadgetsLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            gadgetsSlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String gadgetsAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        gadgetsItemImage.setImageList(gadgetsSlideModels, null);
                        gadgetsItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        gadgetsPosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        gadgetsBrand.setText(snapshot.child("Item_Brand").getValue(String.class));
                        gadgetsColor.setText(snapshot.child("Item_Color").getValue(String.class));
                        gadgetsType.setText(snapshot.child("Item_Type").getValue(String.class));
                        gadgetsCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        gadgetsUsage.setText(snapshot.child("Item_Usage").getValue(String.class));
                        gadgetsLocation.setText(gadgetsAddress);
                        gadgetsDescription.setText(snapshot.child("Item_Description").getValue(String.class));

                        gadgetAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                gadgetsLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Type").setValue(snapshot.child("Offers").child(uid).child("Item_Type").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Brand").setValue(snapshot.child("Offers").child(uid).child("Item_Brand").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Color").setValue(snapshot.child("Offers").child(uid).child("Item_Color").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Usage").setValue(snapshot.child("Offers").child(uid).child("Item_Usage").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Description").setValue(snapshot.child("Offers").child(uid).child("Item_Description").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);

                                                if (snapshot.child("chat").exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                        String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                        String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                        if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", dataSnapshot.getKey());
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        } else {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", "");
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        }
                                                    }
                                                } else {
                                                    loadingDialog.DismissDialog();

                                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                    intent.putExtra("mobile", mobile);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("userID", uid);
                                                    intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                    startActivity(intent);
                                                    CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        gadgetDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                gadgetsLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        loadingDialog.DismissDialog();

                                        snapshot.child("Offers").child(uid).getRef().removeValue();
                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        break;
                    case "Game":
                        List<SlideModel> gameSlideModels = new ArrayList<>();

                        RelativeLayout gameLayout = findViewById(R.id.gameLayout);
                        TextView gameItemName = findViewById(R.id.Item_NameGame);
                        ImageSlider gameItemImage = findViewById(R.id.image_sliderGame);
                        TextView gamePosterName = findViewById(R.id.Item_Poster);
                        TextView gameBrand = findViewById(R.id.brandGame);
                        TextView gameType = findViewById(R.id.typeGame);
                        TextView gameCategory = findViewById(R.id.categoryGame);
                        TextView gameUsage = findViewById(R.id.usageGame);
                        TextView gameLocation = findViewById(R.id.Item_LocationGame);
                        TextView gameDescription = findViewById(R.id.adGame);
                        Button gameAcceptBtn = findViewById(R.id.acceptBtn);
                        Button gameDeclineBtn = findViewById(R.id.declineBtn);

                        gameLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            gameSlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String gameAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        gameItemImage.setImageList(gameSlideModels, null);
                        gameItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        gamePosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        gameBrand.setText(snapshot.child("Item_Brand").getValue(String.class));
                        gameType.setText(snapshot.child("Item_Type").getValue(String.class));
                        gameCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        gameUsage.setText(snapshot.child("Item_Usage").getValue(String.class));
                        gameLocation.setText(gameAddress);
                        gameDescription.setText(snapshot.child("Item_Description").getValue(String.class));

                        gameAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                gameLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Type").setValue(snapshot.child("Offers").child(uid).child("Item_Type").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Brand").setValue(snapshot.child("Offers").child(uid).child("Item_Brand").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Usage").setValue(snapshot.child("Offers").child(uid).child("Item_Usage").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Description").setValue(snapshot.child("Offers").child(uid).child("Item_Description").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);

                                                if (snapshot.child("chat").exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                        String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                        String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                        if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", dataSnapshot.getKey());
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        } else {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", "");
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        }
                                                    }
                                                } else {
                                                    loadingDialog.DismissDialog();

                                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                    intent.putExtra("mobile", mobile);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("userID", uid);
                                                    intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                    startActivity(intent);
                                                    CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        gameDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        loadingDialog.DismissDialog();

                                        gameLayout.setVisibility(View.GONE);
                                        snapshot.child("Offers").child(uid).getRef().removeValue();
                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        break;
                    case "Bags":
                        List<SlideModel> bagSlideModels = new ArrayList<>();

                        RelativeLayout bagLayout = findViewById(R.id.bagLayout);
                        TextView bagItemName = findViewById(R.id.Item_NameBag);
                        ImageSlider bagItemImage = findViewById(R.id.image_sliderBag);
                        TextView bagPosterName = findViewById(R.id.Item_Poster);
                        TextView bagBrand = findViewById(R.id.brandBag);
                        TextView bagType = findViewById(R.id.typeBag);
                        TextView bagColor = findViewById(R.id.colorBag);
                        TextView bagCategory = findViewById(R.id.categoryBag);
                        TextView bagUsage = findViewById(R.id.usageBag);
                        TextView bagLocation = findViewById(R.id.Item_LocationBag);
                        TextView bagDescription = findViewById(R.id.adBag);
                        Button bagAcceptBtn = findViewById(R.id.acceptBtn);
                        Button bagDeclineBtn = findViewById(R.id.declineBtn);

                        bagLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            bagSlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String bagAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        bagItemImage.setImageList(bagSlideModels, null);
                        bagItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        bagPosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        bagBrand.setText(snapshot.child("Item_Brand").getValue(String.class));
                        bagColor.setText(snapshot.child("Item_Color").getValue(String.class));
                        bagType.setText(snapshot.child("Item_Type").getValue(String.class));
                        bagCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        bagUsage.setText(snapshot.child("Item_Usage").getValue(String.class));
                        bagLocation.setText(bagAddress);
                        bagDescription.setText(snapshot.child("Item_Description").getValue(String.class));

                        bagAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                bagLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Type").setValue(snapshot.child("Offers").child(uid).child("Item_Type").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Brand").setValue(snapshot.child("Offers").child(uid).child("Item_Brand").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Usage").setValue(snapshot.child("Offers").child(uid).child("Item_Usage").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Color").setValue(snapshot.child("Offers").child(uid).child("Item_Color").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Description").setValue(snapshot.child("Offers").child(uid).child("Item_Description").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);
                                                loadingDialog.DismissDialog();

                                                for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                    String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                    String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                    if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                        Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                        intent.putExtra("mobile", mobile);
                                                        intent.putExtra("name", userName);
                                                        intent.putExtra("chat_key", dataSnapshot.getKey());
                                                        intent.putExtra("userID", uid);
                                                        intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                        startActivity(intent);
                                                        CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                    } else {
                                                        Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                        intent.putExtra("mobile", mobile);
                                                        intent.putExtra("name", userName);
                                                        intent.putExtra("chat_key", "");
                                                        intent.putExtra("userID", uid);
                                                        intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                        startActivity(intent);
                                                        CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        bagDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                bagLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        loadingDialog.DismissDialog();

                                        snapshot.child("Offers").child(uid).getRef().removeValue();
                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        break;
                    case "Groceries":
                        List<SlideModel> grocerySlideModels = new ArrayList<>();

                        RelativeLayout groceryLayout = findViewById(R.id.groceryLayout);
                        TextView groceryItemName = findViewById(R.id.Item_NameGrocery);
                        ImageSlider groceryItemImage = findViewById(R.id.image_sliderGrocery);
                        TextView groceryPosterName = findViewById(R.id.Item_Poster);
                        TextView groceryList = findViewById(R.id.adGrocery);
                        TextView groceryCategory = findViewById(R.id.categoryGrocery);
                        TextView groceryLocation = findViewById(R.id.Item_LocationGrocery);
                        Button groceryAcceptBtn = findViewById(R.id.acceptBtn);
                        Button groceryDeclineBtn = findViewById(R.id.declineBtn);

                        groceryLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            grocerySlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String groceryAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        groceryItemImage.setImageList(grocerySlideModels, null);
                        groceryItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        groceryPosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        groceryList.setText(snapshot.child("Item_Type").getValue(String.class));
                        groceryCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        groceryLocation.setText(groceryAddress);

                        groceryAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                groceryLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Type").setValue(snapshot.child("Offers").child(uid).child("Item_Type").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);

                                                if (snapshot.child("chat").exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                        String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                        String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                        if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", dataSnapshot.getKey());
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        } else {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", "");
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        }
                                                    }
                                                } else {
                                                    loadingDialog.DismissDialog();

                                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                    intent.putExtra("mobile", mobile);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("userID", uid);
                                                    intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                    startActivity(intent);
                                                    CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        groceryDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                groceryLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        loadingDialog.DismissDialog();

                                        snapshot.child("Offers").child(uid).getRef().removeValue();
                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        break;
                    case "Furniture":
                        List<SlideModel> furnitureSlideModels = new ArrayList<>();

                        RelativeLayout furnitureLayout = findViewById(R.id.furnitureLayout);
                        TextView furnitureItemName = findViewById(R.id.Item_NameFurniture);
                        ImageSlider furnitureItemImage = findViewById(R.id.image_sliderFurniture);
                        TextView furniturePosterName = findViewById(R.id.Item_Poster);
                        TextView furnitureBrand = findViewById(R.id.brandFurniture);
                        TextView furnitureColor = findViewById(R.id.colorFurniture);
                        TextView furnitureWidth = findViewById(R.id.widthFurniture);
                        TextView furnitureHeight = findViewById(R.id.heightFurniture);
                        TextView furnitureLength = findViewById(R.id.lengthFurniture);
                        TextView furnitureCategory = findViewById(R.id.categoryFurniture);
                        TextView furnitureUsage = findViewById(R.id.usageFurniture);
                        TextView furnitureLocation = findViewById(R.id.Item_LocationFurniture);
                        TextView furnitureDescription = findViewById(R.id.adFurniture);
                        Button furnitureAcceptBtn = findViewById(R.id.acceptBtn);
                        Button furnitureDeclineBtn = findViewById(R.id.declineBtn);

                        furnitureLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            furnitureSlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String furnitureAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        furnitureItemImage.setImageList(furnitureSlideModels, null);
                        furnitureItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        furniturePosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        furnitureBrand.setText(snapshot.child("Item_Brand").getValue(String.class));
                        furnitureColor.setText(snapshot.child("Item_Color").getValue(String.class));
                        furnitureWidth.setText(snapshot.child("Item_Width").getValue(String.class));
                        furnitureHeight.setText(snapshot.child("Item_Height").getValue(String.class));
                        furnitureLength.setText(snapshot.child("Item_Length").getValue(String.class));
                        furnitureCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        furnitureUsage.setText(snapshot.child("Item_Usage").getValue(String.class));
                        furnitureLocation.setText(furnitureAddress);
                        furnitureDescription.setText(snapshot.child("Item_Description").getValue(String.class));

                        furnitureAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                furnitureLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Height").setValue(snapshot.child("Offers").child(uid).child("Item_Height").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Width").setValue(snapshot.child("Offers").child(uid).child("Item_Width").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Length").setValue(snapshot.child("Offers").child(uid).child("Item_Length").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Brand").setValue(snapshot.child("Offers").child(uid).child("Item_Brand").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Usage").setValue(snapshot.child("Offers").child(uid).child("Item_Usage").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Color").setValue(snapshot.child("Offers").child(uid).child("Item_Color").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Description").setValue(snapshot.child("Offers").child(uid).child("Item_Description").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);

                                                if (snapshot.child("chat").exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                        String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                        String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                        if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", dataSnapshot.getKey());
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        } else {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", "");
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        }
                                                    }
                                                } else {
                                                    loadingDialog.DismissDialog();

                                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                    intent.putExtra("mobile", mobile);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("userID", uid);
                                                    intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                    startActivity(intent);
                                                    CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        furnitureDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                furnitureLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        loadingDialog.DismissDialog();

                                        snapshot.child("Offers").child(uid).getRef().removeValue();
                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        break;
                    case "Babies & Kids":
                        List<SlideModel> bnkSlideModels = new ArrayList<>();

                        RelativeLayout bnkLayout = findViewById(R.id.bnkLayout);
                        TextView bnkItemName = findViewById(R.id.Item_NameBnk);
                        ImageSlider bnkItemImage = findViewById(R.id.image_sliderBnk);
                        TextView bnkPosterName = findViewById(R.id.Item_Poster);
                        TextView bnkBrand = findViewById(R.id.brandBnk);
                        TextView bnkAge = findViewById(R.id.ageBnk);
                        TextView bnkType = findViewById(R.id.typeBnk);
                        TextView bnkCategory = findViewById(R.id.categoryBnk);
                        TextView bnkUsage = findViewById(R.id.usageBnk);
                        TextView bnkLocation = findViewById(R.id.Item_LocationBnk);
                        TextView bnkDescription = findViewById(R.id.adBnk);
                        Button bnkAcceptBtn = findViewById(R.id.acceptBtn);
                        Button bnkDeclineBtn = findViewById(R.id.declineBtn);

                        bnkLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            bnkSlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String bnkAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        bnkItemImage.setImageList(bnkSlideModels, null);
                        bnkItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        bnkPosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        bnkBrand.setText(snapshot.child("Item_Brand").getValue(String.class));
                        bnkAge.setText(snapshot.child("Item_Age").getValue(String.class));
                        bnkType.setText(snapshot.child("Item_Type").getValue(String.class));
                        bnkCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        bnkUsage.setText(snapshot.child("Item_Usage").getValue(String.class));
                        bnkLocation.setText(bnkAddress);
                        bnkDescription.setText(snapshot.child("Item_Description").getValue(String.class));

                        bnkAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                bnkLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Type").setValue(snapshot.child("Offers").child(uid).child("Item_Type").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Brand").setValue(snapshot.child("Offers").child(uid).child("Item_Brand").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Usage").setValue(snapshot.child("Offers").child(uid).child("Item_Usage").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Age").setValue(snapshot.child("Offers").child(uid).child("Item_Age").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Description").setValue(snapshot.child("Offers").child(uid).child("Item_Description").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);

                                                if (snapshot.child("chat").exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                        String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                        String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                        if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", dataSnapshot.getKey());
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        } else {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", "");
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        }
                                                    }
                                                } else {
                                                    loadingDialog.DismissDialog();

                                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                    intent.putExtra("mobile", mobile);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("userID", uid);
                                                    intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                    startActivity(intent);
                                                    CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        bnkDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        loadingDialog.DismissDialog();

                                        bnkLayout.setVisibility(View.GONE);
                                        snapshot.child("Offers").child(uid).getRef().removeValue();
                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        break;
                    case "Appliances":
                        List<SlideModel> appliancesSlideModels = new ArrayList<>();

                        RelativeLayout appliancesLayout = findViewById(R.id.appliancesLayout);
                        TextView appliancesItemName = findViewById(R.id.Item_NameAppliances);
                        ImageSlider appliancesItemImage = findViewById(R.id.image_sliderAppliances);
                        TextView appliancesPosterName = findViewById(R.id.Item_Poster);
                        TextView appliancesBrand = findViewById(R.id.brandAppliances);
                        TextView appliancesType = findViewById(R.id.typeAppliances);
                        TextView appliancesColor = findViewById(R.id.colorAppliances);
                        TextView appliancesCategory = findViewById(R.id.categoryAppliances);
                        TextView appliancesUsage = findViewById(R.id.usageAppliances);
                        TextView appliancesLocation = findViewById(R.id.Item_LocationAppliances);
                        TextView appliancesDescription = findViewById(R.id.adAppliances);
                        Button appliancesAcceptBtn = findViewById(R.id.acceptBtn);
                        Button appliancesDeclineBtn = findViewById(R.id.declineBtn);

                        appliancesLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            appliancesSlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String appliancesAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        appliancesItemImage.setImageList(appliancesSlideModels, null);
                        appliancesItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        appliancesPosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        appliancesBrand.setText(snapshot.child("Item_Brand").getValue(String.class));
                        appliancesColor.setText(snapshot.child("Item_Color").getValue(String.class));
                        appliancesType.setText(snapshot.child("Item_Type").getValue(String.class));
                        appliancesCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        appliancesUsage.setText(snapshot.child("Item_Usage").getValue(String.class));
                        appliancesLocation.setText(appliancesAddress);
                        appliancesDescription.setText(snapshot.child("Item_Description").getValue(String.class));

                        appliancesAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                appliancesLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Type").setValue(snapshot.child("Offers").child(uid).child("Item_Type").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Brand").setValue(snapshot.child("Offers").child(uid).child("Item_Brand").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Usage").setValue(snapshot.child("Offers").child(uid).child("Item_Usage").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Color").setValue(snapshot.child("Offers").child(uid).child("Item_Color").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Description").setValue(snapshot.child("Offers").child(uid).child("Item_Description").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);

                                                if (snapshot.child("chat").exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                        String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                        String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                        if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", dataSnapshot.getKey());
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        } else {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", "");
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        }
                                                    }
                                                } else {
                                                    loadingDialog.DismissDialog();

                                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                    intent.putExtra("mobile", mobile);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("userID", uid);
                                                    intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                    startActivity(intent);
                                                    CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        appliancesDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                appliancesLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        loadingDialog.DismissDialog();

                                        snapshot.child("Offers").child(uid).getRef().removeValue();
                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        break;
                    case "Motors":
                        List<SlideModel> motorSlideModels = new ArrayList<>();

                        RelativeLayout motorLayout = findViewById(R.id.motorLayout);
                        TextView motorItemName = findViewById(R.id.Item_NameMotor);
                        ImageSlider motorItemImage = findViewById(R.id.image_sliderMotor);
                        TextView motorPosterName = findViewById(R.id.Item_Poster);
                        TextView motorBrand = findViewById(R.id.brandMotor);
                        TextView motorModel = findViewById(R.id.modelMotor);
                        TextView motorColor = findViewById(R.id.colorMotor);
                        TextView motorCategory = findViewById(R.id.categoryMotor);
                        TextView motorUsage = findViewById(R.id.usageMotor);
                        TextView motorLocation = findViewById(R.id.Item_LocationMotor);
                        TextView motorDescription = findViewById(R.id.adMotor);
                        Button motorAcceptBtn = findViewById(R.id.acceptBtn);
                        Button motorDeclineBtn = findViewById(R.id.declineBtn);

                        motorLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            motorSlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String motorAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        motorItemImage.setImageList(motorSlideModels, null);
                        motorItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        motorPosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        motorBrand.setText(snapshot.child("Item_Brand").getValue(String.class));
                        motorColor.setText(snapshot.child("Item_Color").getValue(String.class));
                        motorModel.setText(snapshot.child("Item_Model").getValue(String.class));
                        motorCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        motorUsage.setText(snapshot.child("Item_Usage").getValue(String.class));
                        motorLocation.setText(motorAddress);
                        motorDescription.setText(snapshot.child("Item_Description").getValue(String.class));

                        motorAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                motorLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Model").setValue(snapshot.child("Offers").child(uid).child("Item_Model").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Brand").setValue(snapshot.child("Offers").child(uid).child("Item_Brand").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Usage").setValue(snapshot.child("Offers").child(uid).child("Item_Usage").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Color").setValue(snapshot.child("Offers").child(uid).child("Item_Color").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Description").setValue(snapshot.child("Offers").child(uid).child("Item_Description").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);

                                                if (snapshot.child("chat").exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                        String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                        String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                        if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", dataSnapshot.getKey());
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        } else {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", "");
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        }
                                                    }
                                                } else {
                                                    loadingDialog.DismissDialog();

                                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                    intent.putExtra("mobile", mobile);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("userID", uid);
                                                    intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                    startActivity(intent);
                                                    CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        motorDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                motorLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        loadingDialog.DismissDialog();

                                        snapshot.child("Offers").child(uid).getRef().removeValue();
                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        break;
                    case "Audio":
                        List<SlideModel> audioSlideModels = new ArrayList<>();

                        RelativeLayout audioLayout = findViewById(R.id.audioLayout);
                        TextView audioItemName = findViewById(R.id.Item_NameAudio);
                        ImageSlider audioItemImage = findViewById(R.id.image_sliderAudio);
                        TextView audioPosterName = findViewById(R.id.Item_Poster);
                        TextView audioArtist = findViewById(R.id.artistAudio);
                        TextView audioReleaseDate = findViewById(R.id.releaseDateAudio);
                        TextView audioCategory = findViewById(R.id.categoryAudio);
                        TextView audioUsage = findViewById(R.id.usageAudio);
                        TextView audioLocation = findViewById(R.id.Item_LocationAudio);
                        TextView audioDescription = findViewById(R.id.adAudio);
                        Button audioAcceptBtn = findViewById(R.id.acceptBtn);
                        Button audioDeclineBtn = findViewById(R.id.declineBtn);

                        audioLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            audioSlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String audioAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        audioItemImage.setImageList(audioSlideModels, null);
                        audioItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        audioPosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        audioArtist.setText(snapshot.child("Item_Artist").getValue(String.class));
                        audioReleaseDate.setText(snapshot.child("Item_ReleaseDate").getValue(String.class));
                        audioCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        audioUsage.setText(snapshot.child("Item_Usage").getValue(String.class));
                        audioLocation.setText(audioAddress);
                        audioDescription.setText(snapshot.child("Item_Description").getValue(String.class));

                        audioAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                audioLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Artist").setValue(snapshot.child("Offers").child(uid).child("Item_Artist").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Usage").setValue(snapshot.child("Offers").child(uid).child("Item_Usage").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_ReleaseDate").setValue(snapshot.child("Offers").child(uid).child("Item_ReleaseDate").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Description").setValue(snapshot.child("Offers").child(uid).child("Item_Description").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);

                                                if (snapshot.child("chat").exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                        String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                        String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                        if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", dataSnapshot.getKey());
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        } else {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", "");
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        }
                                                    }
                                                } else {
                                                    loadingDialog.DismissDialog();

                                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                    intent.putExtra("mobile", mobile);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("userID", uid);
                                                    intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                    startActivity(intent);
                                                    CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        audioDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                audioLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        loadingDialog.DismissDialog();

                                        snapshot.child("Offers").child(uid).getRef().removeValue();
                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        break;
                    case "School":
                        List<SlideModel> schoolSlideModels = new ArrayList<>();

                        RelativeLayout schoolLayout = findViewById(R.id.schoolLayout);
                        TextView schoolItemName = findViewById(R.id.Item_NameSchool);
                        ImageSlider schoolItemImage = findViewById(R.id.image_sliderSchool);
                        TextView schoolPosterName = findViewById(R.id.Item_Poster);
                        TextView schoolBrand = findViewById(R.id.brandSchool);
                        TextView schoolType = findViewById(R.id.typeSchool);
                        TextView schoolColor = findViewById(R.id.colorSchool);
                        TextView schoolCategory = findViewById(R.id.categorySchool);
                        TextView schoolUsage = findViewById(R.id.usageSchool);
                        TextView schoolLocation = findViewById(R.id.Item_LocationSchool);
                        TextView schoolDescription = findViewById(R.id.adSchool);
                        Button schoolAcceptBtn = findViewById(R.id.acceptBtn);
                        Button schoolDeclineBtn = findViewById(R.id.declineBtn);

                        schoolLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            schoolSlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String schoolAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        schoolItemImage.setImageList(schoolSlideModels, null);
                        schoolItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        schoolPosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        schoolBrand.setText(snapshot.child("Item_Brand").getValue(String.class));
                        schoolColor.setText(snapshot.child("Item_Color").getValue(String.class));
                        schoolType.setText(snapshot.child("Item_Type").getValue(String.class));
                        schoolCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        schoolUsage.setText(snapshot.child("Item_Usage").getValue(String.class));
                        schoolLocation.setText(schoolAddress);
                        schoolDescription.setText(snapshot.child("Item_Description").getValue(String.class));

                        schoolAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                schoolLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Type").setValue(snapshot.child("Offers").child(uid).child("Item_Type").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Brand").setValue(snapshot.child("Offers").child(uid).child("Item_Brand").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Usage").setValue(snapshot.child("Offers").child(uid).child("Item_Usage").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Color").setValue(snapshot.child("Offers").child(uid).child("Item_Color").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Description").setValue(snapshot.child("Offers").child(uid).child("Item_Description").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);

                                                if (snapshot.child("chat").exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                        String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                        String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                        if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", dataSnapshot.getKey());
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        } else {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", "");
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        }
                                                    }
                                                } else {
                                                    loadingDialog.DismissDialog();

                                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                    intent.putExtra("mobile", mobile);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("userID", uid);
                                                    intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                    startActivity(intent);
                                                    CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        schoolDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                schoolLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        loadingDialog.DismissDialog();

                                        snapshot.child("Offers").child(uid).getRef().removeValue();
                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        break;
                    case "Others":
                        List<SlideModel> otherSlideModels = new ArrayList<>();

                        RelativeLayout otherLayout = findViewById(R.id.otherLayout);
                        TextView otherItemName = findViewById(R.id.Item_NameOther);
                        ImageSlider otherItemImage = findViewById(R.id.image_sliderOther);
                        TextView otherPosterName = findViewById(R.id.Item_Poster);
                        TextView otherType = findViewById(R.id.typeOther);
                        TextView otherCategory = findViewById(R.id.categoryOther);
                        TextView otherLocation = findViewById(R.id.Item_LocationOther);
                        TextView otherDescription = findViewById(R.id.adOther);
                        Button otherAcceptBtn = findViewById(R.id.acceptBtn);
                        Button otherDeclineBtn = findViewById(R.id.declineBtn);

                        otherLayout.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                            otherSlideModels.add(new SlideModel(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class), null));
                        }

                        String otherAddress = snapshot.child("Address").child("City").getValue(String.class).concat(", " + snapshot.child("Address").child("State").getValue(String.class));

                        otherItemImage.setImageList(otherSlideModels, null);
                        otherItemName.setText(snapshot.child("Item_Name").getValue(String.class));
                        otherPosterName.setText(snapshot.child("Poster_Name").getValue(String.class));
                        otherType.setText(snapshot.child("Item_Type").getValue(String.class));
                        otherCategory.setText(snapshot.child("Item_Category").getValue(String.class));
                        otherLocation.setText(otherAddress);
                        otherDescription.setText(snapshot.child("Item_Description").getValue(String.class));

                        otherAcceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                otherLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_Name").setValue(snapshot.child("Offers").child(uid).child("Poster_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Poster_UID").setValue(uid);
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Type").setValue(snapshot.child("Offers").child(uid).child("Item_Type").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Description").setValue(snapshot.child("Offers").child(uid).child("Item_Description").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Name").setValue(snapshot.child("Offers").child(uid).child("Item_Name").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Item_Category").setValue(snapshot.child("Offers").child(uid).child("Item_Category").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Street").setValue(snapshot.child("Offers").child(uid).child("Address").child("Street").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Barangay").setValue(snapshot.child("Offers").child(uid).child("Address").child("Barangay").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("City").setValue(snapshot.child("Offers").child(uid).child("Address").child("City").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("State").setValue(snapshot.child("Offers").child(uid).child("Address").child("State").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Country").setValue(snapshot.child("Offers").child(uid).child("Address").child("Country").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Latitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Latitude").getValue(String.class));
                                        databaseReference.child("Accepted_Offers").child(uid).child("Address").child("Longitude").setValue(snapshot.child("Offers").child(uid).child("Address").child("Longitude").getValue(String.class));

                                        for (int i = 1; i <= snapshot.child("Offers").child(uid).child("Images").getChildrenCount(); i++) {
                                            databaseReference.child("Accepted_Offers").child(uid).child("Images").child(String.valueOf(i)).setValue(snapshot.child("Offers").child(uid).child("Images").child(String.valueOf(i)).getValue(String.class));
                                        }

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                int newPendingParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Pending").getValue(String.class));
                                                newPendingParent = newPendingParent - 1;

                                                int newPendingOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Pending").getValue(String.class));
                                                newPendingOfferer = newPendingOfferer - 1;

                                                databaseReference.child("user-transactions").child(currentId).child("Pending").setValue(String.valueOf(newPendingParent));
                                                databaseReference.child("user-transactions").child(uid).child("Pending").setValue(String.valueOf(newPendingOfferer));

                                                if (snapshot.child("user-transactions").child(currentId).child("Current").exists()) {
                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(currentId).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent + 1;

                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue(String.valueOf(newCurrentParent));
                                                } else {
                                                    databaseReference.child("user-transactions").child(currentId).child("Current").setValue("1");
                                                }

                                                if (snapshot.child("user-transactions").child(uid).child("Current").exists()) {
                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(uid).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer + 1;

                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue(String.valueOf(newCurrentOfferer));
                                                } else {
                                                    databaseReference.child("user-transactions").child(uid).child("Current").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        snapshot.child("Offers").child(uid).getRef().removeValue();

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String mobile = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                String userName = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
                                                String currentMobile = snapshot.child("users").child(currentId).child("Phone").getValue(String.class);

                                                if (snapshot.child("chat").exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {
                                                        String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                                        String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                                        if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", dataSnapshot.getKey());
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        } else {
                                                            loadingDialog.DismissDialog();

                                                            Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                            intent.putExtra("mobile", mobile);
                                                            intent.putExtra("name", userName);
                                                            intent.putExtra("chat_key", "");
                                                            intent.putExtra("userID", uid);
                                                            intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                            startActivity(intent);
                                                            CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                        }
                                                    }
                                                } else {
                                                    loadingDialog.DismissDialog();

                                                    Intent intent = new Intent(OfferMoreInfo.this, Chat.class);
                                                    intent.putExtra("mobile", mobile);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("userID", uid);
                                                    intent.putExtra("userStatus", snapshot.child("users-status").child(uid).child("Status").getValue(String.class));

                                                    startActivity(intent);
                                                    CustomIntent.customType(OfferMoreInfo.this, "left-to-right");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        otherDeclineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                loadingDialog.startLoadingDialog();

                                otherLayout.setVisibility(View.GONE);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(currentId).child(poster_itemName);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        loadingDialog.DismissDialog();

                                        snapshot.child("Offers").child(uid).getRef().removeValue();
                                        Intent intent = new Intent(OfferMoreInfo.this, OfferSecondActivity.class);
                                        intent.putExtra("itemid", currentId);
                                        intent.putExtra("itemname", poster_itemName);
                                        startActivity(intent);
                                        CustomIntent.customType(OfferMoreInfo.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        break;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}