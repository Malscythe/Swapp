package com.example.Swapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.kroegerama.imgpicker.BottomSheetImagePicker;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import Swapp.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import maes.tech.intentanim.CustomIntent;

public class PostItem_S4 extends AppCompatActivity implements BottomSheetImagePicker.OnImagesSelectedListener {

    private static final String TAG = "TAG";
    Button uploadBtn, postItemBtn;
    private ViewPager2 viewPager2;
    private List<Image> imageList;
    private ImageAdapter adapter;
    private Handler sliderHandler = new Handler();
    String[] descriptionData = {"Details", "Description", "Location", "Images"};
    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String strDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item_s4);

        strDate = new SimpleDateFormat("MMMM dd, yyyy hh:mm aa", Locale.getDefault()).format(new Date());

        StateProgressBar stateProgressBar = findViewById(R.id.stateProgress);
        stateProgressBar.setStateDescriptionData(descriptionData);

        imageList = new ArrayList<>();

        viewPager2 = findViewById(R.id.viewPager2);

        viewPager2.setEnabled(false);
        uploadBtn = findViewById(R.id.insertImages);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                        .multiSelect(1, 4)
                        .multiSelectTitles(
                                R.plurals.pick_multi,
                                R.plurals.pick_multi_more,
                                R.string.pick_multi_limit
                        )
                        .peekHeight(R.dimen.peekHeight)
                        .columnSize(R.dimen.columnSize)
                        .requestTag("multi")
                        .show(getSupportFragmentManager(), null);
            }
        });
    }


    @Override
    public void onImagesSelected(@NonNull List<? extends Uri> list, @Nullable String s) {

        postItemBtn = findViewById(R.id.uploadToDB);
        uploadBtn = findViewById(R.id.insertImages);

        viewPager2 = findViewById(R.id.viewPager2);

        viewPager2.setEnabled(true);

        imageList = new ArrayList<>();

        for (Uri uri : list) {
            imageList.add(new Image(uri));
        }

        adapter = new ImageAdapter(imageList, viewPager2);
        viewPager2.setAdapter(adapter);

        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setClipChildren(false);
        viewPager2.setClipToPadding(false);

        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        if (imageList.size() == 0) {
            uploadBtn.setBackground(AppCompatResources.getDrawable(this, R.drawable.buttonborder));
            postItemBtn.setBackground(AppCompatResources.getDrawable(this, R.drawable.buttonborder_white));
            postItemBtn.setVisibility(View.GONE);
            uploadBtn.setText("Insert photo");
            uploadBtn.setTextColor(AppCompatResources.getColorStateList(this, R.color.white));
        } else {
            uploadBtn.setBackground(AppCompatResources.getDrawable(this, R.drawable.buttonborder_white));
            postItemBtn.setBackground(AppCompatResources.getDrawable(this, R.drawable.buttonborder));
            postItemBtn.setTextColor(AppCompatResources.getColorStateList(this, R.color.white));
            postItemBtn.setVisibility(View.VISIBLE);
            uploadBtn.setText("Update photo");
            uploadBtn.setTextColor(AppCompatResources.getColorStateList(this, R.color.primary));
        }

        String category = getIntent().getStringExtra("category");

        firebaseAuth = FirebaseAuth.getInstance();
        String currentId = firebaseAuth.getCurrentUser().getUid();

        postItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Set<Image> set = new HashSet<>(imageList);
                imageList.clear();
                imageList.addAll(set);

                SweetAlertDialog pDialog = new SweetAlertDialog(PostItem_S4.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Uploading item...");
                pDialog.setCancelable(false);
                pDialog.show();

                switch (category) {
                    case "Men's Apparel":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/MenApparel/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String mType = getIntent().getStringExtra("mensClothingType");
                                                    String mBrand = getIntent().getStringExtra("mensBrand");
                                                    String mColor = getIntent().getStringExtra("mensColor");
                                                    String mMaterial = getIntent().getStringExtra("mensMaterial");
                                                    String mUsage = getIntent().getStringExtra("mensUsage");
                                                    String mSizes = getIntent().getStringExtra("mensSizes");
                                                    String mDescription = getIntent().getStringExtra("mensDescription");
                                                    String mItemName = getIntent().getStringExtra("item_name");
                                                    String mReasonForTrading = getIntent().getStringExtra("rft");
                                                    String mCategory = getIntent().getStringExtra("category");
                                                    String mPrefItem = getIntent().getStringExtra("pref_item");
                                                    String mStreet = getIntent().getStringExtra("street");
                                                    String mBarangay = getIntent().getStringExtra("barangay");
                                                    String mCity = getIntent().getStringExtra("city");
                                                    String mState = getIntent().getStringExtra("state");
                                                    String mCountry = getIntent().getStringExtra("country");
                                                    String mLatitude = getIntent().getStringExtra("latitude");
                                                    String mLongitude = getIntent().getStringExtra("longitude");
                                                    String mSizeChart = getIntent().getStringExtra("mensSizeChart");
                                                    String mLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(mItemName)) {
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Item_Type").setValue(mType);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Item_Brand").setValue(mBrand);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Item_Color").setValue(mColor);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Item_Material").setValue(mMaterial);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Item_Usage").setValue(mUsage);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Item_Sizes").setValue(mSizes);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Item_Description").setValue(mDescription);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Item_Name").setValue(mItemName);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Item_RFT").setValue(mReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Item_Category").setValue(mCategory);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Item_PrefItem").setValue(mPrefItem);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Address").child("Street").setValue(mStreet);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Address").child("Barangay").setValue(mBarangay);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Address").child("City").setValue(mCity);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Address").child("State").setValue(mState);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Address").child("Country").setValue(mCountry);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Status").setValue("Validating");

                                                        if (!mLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(mItemName).child("Address").child("Landmark").setValue(mLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Address").child("Latitude").setValue(mLatitude);
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Address").child("Longitude").setValue(mLongitude);
                                                    }

                                                    if (snapshot.child("items").child(currentId).child(mItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(mItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(mItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }

                                                    StorageReference storageReference1 = FirebaseStorage.getInstance().getReference("images/items/MenApparel/" + getIntent().getStringExtra("item_name") + "/SizeChart");

                                                    Uri uri = Uri.parse(mSizeChart);

                                                    UploadTask uploadTask = storageReference1.putFile(uri);

                                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Uri> task) {
                                                                    databaseReference.child("items").child(currentId).child(mItemName).child("Item_SizeChart").setValue(task.getResult().toString());

                                                                }
                                                            });
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");


                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }

                        break;
                    case "Gadgets":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/" + "/" + category + "/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String gadgetType = getIntent().getStringExtra("gadgetType");
                                                    String gadgetBrand = getIntent().getStringExtra("gadgetBrand");
                                                    String gadgetColor = getIntent().getStringExtra("gadgetColor");
                                                    String gadgetUsage = getIntent().getStringExtra("gadgetUsage");
                                                    String gadgetDescription = getIntent().getStringExtra("gadgetDescription");
                                                    String gadgetItemName = getIntent().getStringExtra("item_name");
                                                    String gadgetReasonForTrading = getIntent().getStringExtra("rft");
                                                    String gadgetCategory = getIntent().getStringExtra("category");
                                                    String gadgetPrefItem = getIntent().getStringExtra("pref_item");
                                                    String gadgetStreet = getIntent().getStringExtra("street");
                                                    String gadgetBarangay = getIntent().getStringExtra("barangay");
                                                    String gadgetCity = getIntent().getStringExtra("city");
                                                    String gadgetState = getIntent().getStringExtra("state");
                                                    String gadgetCountry = getIntent().getStringExtra("country");
                                                    String gadgetLatitude = getIntent().getStringExtra("latitude");
                                                    String gadgetLongitude = getIntent().getStringExtra("longitude");
                                                    String gadgetLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(gadgetItemName)) {
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Item_Type").setValue(gadgetType);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Item_Brand").setValue(gadgetBrand);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Item_Color").setValue(gadgetColor);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Item_Usage").setValue(gadgetUsage);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Item_Description").setValue(gadgetDescription);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Item_Name").setValue(gadgetItemName);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Item_RFT").setValue(gadgetReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Item_Category").setValue(gadgetCategory);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Item_PrefItem").setValue(gadgetPrefItem);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Address").child("Street").setValue(gadgetStreet);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Address").child("Barangay").setValue(gadgetBarangay);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Address").child("City").setValue(gadgetCity);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Address").child("State").setValue(gadgetState);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Address").child("Country").setValue(gadgetCountry);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Status").setValue("Validating");

                                                        if (!gadgetLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(gadgetItemName).child("Address").child("Landmark").setValue(gadgetLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Address").child("Latitude").setValue(gadgetLatitude);
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Address").child("Longitude").setValue(gadgetLongitude);
                                                    }

                                                    if (snapshot.child("items").child(currentId).child(gadgetItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(gadgetItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(gadgetItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        break;
                    case "Game":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/" + "/" + category + "/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String gameType = getIntent().getStringExtra("gameType");
                                                    String gameBrand = getIntent().getStringExtra("gameBrand");
                                                    String gameUsage = getIntent().getStringExtra("gameUsage");
                                                    String gameDescription = getIntent().getStringExtra("gameDescription");
                                                    String gameItemName = getIntent().getStringExtra("item_name");
                                                    String gameReasonForTrading = getIntent().getStringExtra("rft");
                                                    String gameCategory = getIntent().getStringExtra("category");
                                                    String gamePrefItem = getIntent().getStringExtra("pref_item");
                                                    String gameStreet = getIntent().getStringExtra("street");
                                                    String gameBarangay = getIntent().getStringExtra("barangay");
                                                    String gameCity = getIntent().getStringExtra("city");
                                                    String gameState = getIntent().getStringExtra("state");
                                                    String gameCountry = getIntent().getStringExtra("country");
                                                    String gameLatitude = getIntent().getStringExtra("latitude");
                                                    String gameLongitude = getIntent().getStringExtra("longitude");
                                                    String gameLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(gameItemName)) {
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Item_Type").setValue(gameType);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Item_Brand").setValue(gameBrand);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Item_Usage").setValue(gameUsage);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Item_Description").setValue(gameDescription);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Item_Name").setValue(gameItemName);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Item_RFT").setValue(gameReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Item_Category").setValue(gameCategory);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Item_PrefItem").setValue(gamePrefItem);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Address").child("Street").setValue(gameStreet);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Address").child("Barangay").setValue(gameBarangay);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Address").child("City").setValue(gameCity);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Address").child("State").setValue(gameState);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Address").child("Country").setValue(gameCountry);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Status").setValue("Validating");

                                                        if (!gameLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(gameItemName).child("Address").child("Landmark").setValue(gameLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Address").child("Latitude").setValue(gameLatitude);
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Address").child("Longitude").setValue(gameLongitude);

                                                    }

                                                    if (snapshot.child("items").child(currentId).child(gameItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(gameItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(gameItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        break;
                    case "Bags":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/" + "/" + category + "/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String bagType = getIntent().getStringExtra("bagType");
                                                    String bagBrand = getIntent().getStringExtra("bagBrand");
                                                    String bagColor = getIntent().getStringExtra("bagColor");
                                                    String bagUsage = getIntent().getStringExtra("bagUsage");
                                                    String bagDescription = getIntent().getStringExtra("bagDescription");
                                                    String bagItemName = getIntent().getStringExtra("item_name");
                                                    String bagReasonForTrading = getIntent().getStringExtra("rft");
                                                    String bagCategory = getIntent().getStringExtra("category");
                                                    String bagPrefItem = getIntent().getStringExtra("pref_item");
                                                    String bagStreet = getIntent().getStringExtra("street");
                                                    String bagBarangay = getIntent().getStringExtra("barangay");
                                                    String bagCity = getIntent().getStringExtra("city");
                                                    String bagState = getIntent().getStringExtra("state");
                                                    String bagCountry = getIntent().getStringExtra("country");
                                                    String bagLatitude = getIntent().getStringExtra("latitude");
                                                    String bagLongitude = getIntent().getStringExtra("longitude");
                                                    String bagLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(bagItemName)) {
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Item_Type").setValue(bagType);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Item_Brand").setValue(bagBrand);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Item_Color").setValue(bagColor);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Item_Usage").setValue(bagUsage);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Item_Description").setValue(bagDescription);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Item_Name").setValue(bagItemName);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Item_RFT").setValue(bagReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Item_Category").setValue(bagCategory);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Item_PrefItem").setValue(bagPrefItem);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Address").child("Street").setValue(bagStreet);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Address").child("Barangay").setValue(bagBarangay);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Address").child("City").setValue(bagCity);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Address").child("State").setValue(bagState);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Address").child("Country").setValue(bagCountry);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Status").setValue("Validating");

                                                        if (!bagLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(bagItemName).child("Address").child("Landmark").setValue(bagLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Address").child("Latitude").setValue(bagLatitude);
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Address").child("Longitude").setValue(bagLongitude);

                                                    }

                                                    if (snapshot.child("items").child(currentId).child(bagItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(bagItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(bagItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        break;
                    case "Groceries":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/" + "/" + category + "/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String groceryList = getIntent().getStringExtra("groceryList");
                                                    String groceryItemName = getIntent().getStringExtra("item_name");
                                                    String groceryReasonForTrading = getIntent().getStringExtra("rft");
                                                    String groceryCategory = getIntent().getStringExtra("category");
                                                    String groceryPrefItem = getIntent().getStringExtra("pref_item");
                                                    String groceryStreet = getIntent().getStringExtra("street");
                                                    String groceryBarangay = getIntent().getStringExtra("barangay");
                                                    String groceryCity = getIntent().getStringExtra("city");
                                                    String groceryState = getIntent().getStringExtra("state");
                                                    String groceryCountry = getIntent().getStringExtra("country");
                                                    String groceryLatitude = getIntent().getStringExtra("latitude");
                                                    String groceryLongitude = getIntent().getStringExtra("longitude");
                                                    String groceryLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(groceryItemName)) {
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Item_Type").setValue(groceryList);
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Item_Name").setValue(groceryItemName);
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Item_RFT").setValue(groceryReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Item_Category").setValue(groceryCategory);
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Item_PrefItem").setValue(groceryPrefItem);
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Address").child("Street").setValue(groceryStreet);
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Address").child("Barangay").setValue(groceryBarangay);
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Address").child("City").setValue(groceryCity);
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Address").child("State").setValue(groceryState);
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Address").child("Country").setValue(groceryCountry);
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Status").setValue("Validating");

                                                        if (!groceryLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(groceryItemName).child("Address").child("Landmark").setValue(groceryLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Address").child("Latitude").setValue(groceryLatitude);
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Address").child("Longitude").setValue(groceryLongitude);

                                                    }

                                                    if (snapshot.child("items").child(currentId).child(groceryItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(groceryItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(groceryItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        break;
                    case "Furniture":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/" + "/" + category + "/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String furnitureBrand = getIntent().getStringExtra("furnitureBrand");
                                                    String furnitureColor = getIntent().getStringExtra("furnitureColor");
                                                    String furnitureUsage = getIntent().getStringExtra("furnitureUsage");
                                                    String furnitureHeight = getIntent().getStringExtra("furnitureHeight");
                                                    String furnitureWidth = getIntent().getStringExtra("furnitureWidth");
                                                    String furnitureLength = getIntent().getStringExtra("furnitureLength");
                                                    String furnitureDescription = getIntent().getStringExtra("furnitureDescription");
                                                    String furnitureItemName = getIntent().getStringExtra("item_name");
                                                    String furnitureReasonForTrading = getIntent().getStringExtra("rft");
                                                    String furnitureCategory = getIntent().getStringExtra("category");
                                                    String furniturePrefItem = getIntent().getStringExtra("pref_item");
                                                    String furnitureStreet = getIntent().getStringExtra("street");
                                                    String furnitureBarangay = getIntent().getStringExtra("barangay");
                                                    String furnitureCity = getIntent().getStringExtra("city");
                                                    String furnitureState = getIntent().getStringExtra("state");
                                                    String furnitureCountry = getIntent().getStringExtra("country");
                                                    String furnitureLatitude = getIntent().getStringExtra("latitude");
                                                    String furnitureLongitude = getIntent().getStringExtra("longitude");
                                                    String furnitureLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(furnitureItemName)) {
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Item_Height").setValue(furnitureHeight);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Item_Width").setValue(furnitureWidth);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Item_Length").setValue(furnitureLength);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Item_Brand").setValue(furnitureBrand);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Item_Color").setValue(furnitureColor);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Item_Usage").setValue(furnitureUsage);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Item_Description").setValue(furnitureDescription);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Item_Name").setValue(furnitureItemName);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Item_RFT").setValue(furnitureReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Item_Category").setValue(furnitureCategory);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Item_PrefItem").setValue(furniturePrefItem);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Address").child("Street").setValue(furnitureStreet);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Address").child("Barangay").setValue(furnitureBarangay);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Address").child("City").setValue(furnitureCity);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Address").child("State").setValue(furnitureState);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Address").child("Country").setValue(furnitureCountry);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Status").setValue("Validating");

                                                        if (!furnitureLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(furnitureItemName).child("Address").child("Landmark").setValue(furnitureLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Address").child("Latitude").setValue(furnitureLatitude);
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Address").child("Longitude").setValue(furnitureLongitude);

                                                    }

                                                    if (snapshot.child("items").child(currentId).child(furnitureItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(furnitureItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(furnitureItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        break;
                    case "Babies & Kids":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/" + "/" + category + "/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String bnkAge = getIntent().getStringExtra("bnkAge");
                                                    String bnkBrand = getIntent().getStringExtra("bnkBrand");
                                                    String bnkType = getIntent().getStringExtra("bnkType");
                                                    String bnkUsage = getIntent().getStringExtra("bnkUsage");
                                                    String bnkDescription = getIntent().getStringExtra("bnkDescription");
                                                    String bnkItemName = getIntent().getStringExtra("item_name");
                                                    String bnkReasonForTrading = getIntent().getStringExtra("rft");
                                                    String bnkCategory = getIntent().getStringExtra("category");
                                                    String bnkPrefItem = getIntent().getStringExtra("pref_item");
                                                    String bnkStreet = getIntent().getStringExtra("street");
                                                    String bnkBarangay = getIntent().getStringExtra("barangay");
                                                    String bnkCity = getIntent().getStringExtra("city");
                                                    String bnkState = getIntent().getStringExtra("state");
                                                    String bnkCountry = getIntent().getStringExtra("country");
                                                    String bnkLatitude = getIntent().getStringExtra("latitude");
                                                    String bnkLongitude = getIntent().getStringExtra("longitude");
                                                    String bnkLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(bnkItemName)) {
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Item_Type").setValue(bnkType);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Item_Brand").setValue(bnkBrand);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Item_Age").setValue(bnkAge);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Item_Usage").setValue(bnkUsage);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Item_Description").setValue(bnkDescription);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Item_Name").setValue(bnkItemName);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Item_RFT").setValue(bnkReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Item_Category").setValue(bnkCategory);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Item_PrefItem").setValue(bnkPrefItem);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Address").child("Street").setValue(bnkStreet);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Address").child("Barangay").setValue(bnkBarangay);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Address").child("City").setValue(bnkCity);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Address").child("State").setValue(bnkState);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Address").child("Country").setValue(bnkCountry);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Status").setValue("Validating");

                                                        if (!bnkLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(bnkItemName).child("Address").child("Landmark").setValue(bnkLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Address").child("Latitude").setValue(bnkLatitude);
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Address").child("Longitude").setValue(bnkLongitude);

                                                    }

                                                    if (snapshot.child("items").child(currentId).child(bnkItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(bnkItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(bnkItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        break;
                    case "Appliances":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/" + "/" + category + "/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String appliancesType = getIntent().getStringExtra("appliancesType");
                                                    String appliancesBrand = getIntent().getStringExtra("appliancesBrand");
                                                    String appliancesColor = getIntent().getStringExtra("appliancesColor");
                                                    String appliancesUsage = getIntent().getStringExtra("appliancesUsage");
                                                    String appliancesDescription = getIntent().getStringExtra("appliancesDescription");
                                                    String appliancesItemName = getIntent().getStringExtra("item_name");
                                                    String appliancesReasonForTrading = getIntent().getStringExtra("rft");
                                                    String appliancesCategory = getIntent().getStringExtra("category");
                                                    String appliancesPrefItem = getIntent().getStringExtra("pref_item");
                                                    String appliancesStreet = getIntent().getStringExtra("street");
                                                    String appliancesBarangay = getIntent().getStringExtra("barangay");
                                                    String appliancesCity = getIntent().getStringExtra("city");
                                                    String appliancesState = getIntent().getStringExtra("state");
                                                    String appliancesCountry = getIntent().getStringExtra("country");
                                                    String appliancesLatitude = getIntent().getStringExtra("latitude");
                                                    String appliancesLongitude = getIntent().getStringExtra("longitude");
                                                    String appliancesLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(appliancesItemName)) {
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Item_Type").setValue(appliancesType);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Item_Brand").setValue(appliancesBrand);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Item_Color").setValue(appliancesColor);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Item_Usage").setValue(appliancesUsage);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Item_Description").setValue(appliancesDescription);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Item_Name").setValue(appliancesItemName);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Item_RFT").setValue(appliancesReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Item_Category").setValue(appliancesCategory);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Item_PrefItem").setValue(appliancesPrefItem);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Address").child("Street").setValue(appliancesStreet);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Address").child("Barangay").setValue(appliancesBarangay);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Address").child("City").setValue(appliancesCity);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Address").child("State").setValue(appliancesState);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Address").child("Country").setValue(appliancesCountry);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Status").setValue("Validating");

                                                        if (!appliancesLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(appliancesItemName).child("Address").child("Landmark").setValue(appliancesLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Address").child("Latitude").setValue(appliancesLatitude);
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Address").child("Longitude").setValue(appliancesLongitude);

                                                    }

                                                    if (snapshot.child("items").child(currentId).child(appliancesItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(appliancesItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(appliancesItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        break;
                    case "Motors":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/" + "/" + category + "/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String motorModel = getIntent().getStringExtra("motorModel");
                                                    String motorBrand = getIntent().getStringExtra("motorBrand");
                                                    String motorColor = getIntent().getStringExtra("motorColor");
                                                    String motorUsage = getIntent().getStringExtra("motorUsage");
                                                    String motorDescription = getIntent().getStringExtra("motorDescription");
                                                    String motorItemName = getIntent().getStringExtra("item_name");
                                                    String motorReasonForTrading = getIntent().getStringExtra("rft");
                                                    String motorCategory = getIntent().getStringExtra("category");
                                                    String motorPrefItem = getIntent().getStringExtra("pref_item");
                                                    String motorStreet = getIntent().getStringExtra("street");
                                                    String motorBarangay = getIntent().getStringExtra("barangay");
                                                    String motorCity = getIntent().getStringExtra("city");
                                                    String motorState = getIntent().getStringExtra("state");
                                                    String motorCountry = getIntent().getStringExtra("country");
                                                    String motorLatitude = getIntent().getStringExtra("latitude");
                                                    String motorLongitude = getIntent().getStringExtra("longitude");
                                                    String motorLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(motorItemName)) {
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Item_Model").setValue(motorModel);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Item_Brand").setValue(motorBrand);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Item_Color").setValue(motorColor);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Item_Usage").setValue(motorUsage);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Item_Description").setValue(motorDescription);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Item_Name").setValue(motorItemName);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Item_RFT").setValue(motorReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Item_Category").setValue(motorCategory);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Item_PrefItem").setValue(motorPrefItem);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Address").child("Street").setValue(motorStreet);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Address").child("Barangay").setValue(motorBarangay);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Address").child("City").setValue(motorCity);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Address").child("State").setValue(motorState);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Address").child("Country").setValue(motorCountry);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Status").setValue("Validating");

                                                        if (!motorLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(motorItemName).child("Address").child("Landmark").setValue(motorLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Address").child("Latitude").setValue(motorLatitude);
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Address").child("Longitude").setValue(motorLongitude);

                                                    }

                                                    if (snapshot.child("items").child(currentId).child(motorItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(motorItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(motorItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        break;
                    case "Audio":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/" + "/" + category + "/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String audioArtist = getIntent().getStringExtra("audioArtist");
                                                    String audioReleaseDate = getIntent().getStringExtra("audioReleaseDate");
                                                    String audioUsage = getIntent().getStringExtra("audioUsage");
                                                    String audioDescription = getIntent().getStringExtra("audioDescription");
                                                    String audioItemName = getIntent().getStringExtra("item_name");
                                                    String audioReasonForTrading = getIntent().getStringExtra("rft");
                                                    String audioCategory = getIntent().getStringExtra("category");
                                                    String audioPrefItem = getIntent().getStringExtra("pref_item");
                                                    String audioStreet = getIntent().getStringExtra("street");
                                                    String audioBarangay = getIntent().getStringExtra("barangay");
                                                    String audioCity = getIntent().getStringExtra("city");
                                                    String audioState = getIntent().getStringExtra("state");
                                                    String audioCountry = getIntent().getStringExtra("country");
                                                    String audioLatitude = getIntent().getStringExtra("latitude");
                                                    String audioLongitude = getIntent().getStringExtra("longitude");
                                                    String audioLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(audioItemName)) {
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Item_Artist").setValue(audioArtist);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Item_ReleaseDate").setValue(audioReleaseDate);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Item_Usage").setValue(audioUsage);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Item_Description").setValue(audioDescription);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Item_Name").setValue(audioItemName);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Item_RFT").setValue(audioReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Item_Category").setValue(audioCategory);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Item_PrefItem").setValue(audioPrefItem);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Address").child("Street").setValue(audioStreet);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Address").child("Barangay").setValue(audioBarangay);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Address").child("City").setValue(audioCity);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Address").child("State").setValue(audioState);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Address").child("Country").setValue(audioCountry);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Status").setValue("Validating");

                                                        if (!audioLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(audioItemName).child("Address").child("Landmark").setValue(audioLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Address").child("Latitude").setValue(audioLatitude);
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Address").child("Longitude").setValue(audioLongitude);

                                                    }

                                                    if (snapshot.child("items").child(currentId).child(audioItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(audioItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(audioItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        break;
                    case "School":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/" + "/" + category + "/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String schoolType = getIntent().getStringExtra("schoolType");
                                                    String schoolBrand = getIntent().getStringExtra("schoolBrand");
                                                    String schoolColor = getIntent().getStringExtra("schoolColor");
                                                    String schoolUsage = getIntent().getStringExtra("schoolUsage");
                                                    String schoolDescription = getIntent().getStringExtra("schoolDescription");
                                                    String schoolItemName = getIntent().getStringExtra("item_name");
                                                    String schoolReasonForTrading = getIntent().getStringExtra("rft");
                                                    String schoolCategory = getIntent().getStringExtra("category");
                                                    String schoolPrefItem = getIntent().getStringExtra("pref_item");
                                                    String schoolStreet = getIntent().getStringExtra("street");
                                                    String schoolBarangay = getIntent().getStringExtra("barangay");
                                                    String schoolCity = getIntent().getStringExtra("city");
                                                    String schoolState = getIntent().getStringExtra("state");
                                                    String schoolCountry = getIntent().getStringExtra("country");
                                                    String schoolLatitude = getIntent().getStringExtra("latitude");
                                                    String schoolLongitude = getIntent().getStringExtra("longitude");
                                                    String schoolLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(schoolItemName)) {
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Item_Type").setValue(schoolType);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Item_Brand").setValue(schoolBrand);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Item_Color").setValue(schoolColor);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Item_Usage").setValue(schoolUsage);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Item_Description").setValue(schoolDescription);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Item_Name").setValue(schoolItemName);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Item_RFT").setValue(schoolReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Item_Category").setValue(schoolCategory);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Item_PrefItem").setValue(schoolPrefItem);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Address").child("Street").setValue(schoolStreet);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Address").child("Barangay").setValue(schoolBarangay);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Address").child("City").setValue(schoolCity);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Address").child("State").setValue(schoolState);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Address").child("Country").setValue(schoolCountry);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Status").setValue("Validating");

                                                        if (!schoolLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(schoolItemName).child("Address").child("Landmark").setValue(schoolLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Address").child("Latitude").setValue(schoolLatitude);
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Address").child("Longitude").setValue(schoolLongitude);

                                                    }

                                                    if (snapshot.child("items").child(currentId).child(schoolItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(schoolItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(schoolItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        break;
                    case "Women's Apparel":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/WomenApparel/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String wType = getIntent().getStringExtra("womensClothingType");
                                                    String wBrand = getIntent().getStringExtra("womensBrand");
                                                    String wColor = getIntent().getStringExtra("womensColor");
                                                    String wMaterial = getIntent().getStringExtra("womensMaterial");
                                                    String wUsage = getIntent().getStringExtra("womensUsage");
                                                    String wSizes = getIntent().getStringExtra("womensSizes");
                                                    String wDescription = getIntent().getStringExtra("womensDescription");
                                                    String wItemName = getIntent().getStringExtra("item_name");
                                                    String wReasonForTrading = getIntent().getStringExtra("rft");
                                                    String wCategory = getIntent().getStringExtra("category");
                                                    String wPrefItem = getIntent().getStringExtra("pref_item");
                                                    String wStreet = getIntent().getStringExtra("street");
                                                    String wBarangay = getIntent().getStringExtra("barangay");
                                                    String wCity = getIntent().getStringExtra("city");
                                                    String wState = getIntent().getStringExtra("state");
                                                    String wCountry = getIntent().getStringExtra("country");
                                                    String wLatitude = getIntent().getStringExtra("latitude");
                                                    String wLongitude = getIntent().getStringExtra("longitude");
                                                    String wSizeChart = getIntent().getStringExtra("womensSizeChart");
                                                    String wLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(wItemName)) {
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Item_Type").setValue(wType);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Item_Brand").setValue(wBrand);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Item_Color").setValue(wColor);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Item_Material").setValue(wMaterial);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Item_Usage").setValue(wUsage);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Item_Sizes").setValue(wSizes);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Item_Description").setValue(wDescription);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Item_Name").setValue(wItemName);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Item_RFT").setValue(wReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Item_Category").setValue(wCategory);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Item_PrefItem").setValue(wPrefItem);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Address").child("Street").setValue(wStreet);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Address").child("Barangay").setValue(wBarangay);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Address").child("City").setValue(wCity);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Address").child("State").setValue(wState);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Address").child("Country").setValue(wCountry);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Status").setValue("Validating");

                                                        if (!wLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(wItemName).child("Address").child("Landmark").setValue(wLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Address").child("Latitude").setValue(wLatitude);
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Address").child("Longitude").setValue(wLongitude);

                                                    }

                                                    if (snapshot.child("items").child(currentId).child(wItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(wItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(wItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }

                                                    StorageReference storageReference1 = FirebaseStorage.getInstance().getReference("images/items/WomenApparel/" + getIntent().getStringExtra("item_name") + "/SizeChart");

                                                    Uri uri = Uri.parse(wSizeChart);

                                                    UploadTask uploadTask = storageReference1.putFile(uri);

                                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Uri> task) {
                                                                    databaseReference.child("items").child(currentId).child(wItemName).child("Item_SizeChart").setValue(task.getResult().toString());
                                                                }
                                                            });
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        break;
                    case "Others":

                        for (int i = 0; i < imageList.size(); i++) {

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/items/" + "/" + category + "/" + getIntent().getStringExtra("item_name") + "/" + "Image-" + i);

                            UploadTask uploadTask = storageReference.putFile(imageList.get(i).getImage());

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String otherType = getIntent().getStringExtra("otherType");
                                                    String otherDescription = getIntent().getStringExtra("otherDescription");
                                                    String otherItemName = getIntent().getStringExtra("item_name");
                                                    String otherReasonForTrading = getIntent().getStringExtra("rft");
                                                    String otherCategory = getIntent().getStringExtra("category");
                                                    String otherPrefItem = getIntent().getStringExtra("pref_item");
                                                    String otherStreet = getIntent().getStringExtra("street");
                                                    String otherBarangay = getIntent().getStringExtra("barangay");
                                                    String otherCity = getIntent().getStringExtra("city");
                                                    String otherState = getIntent().getStringExtra("state");
                                                    String otherCountry = getIntent().getStringExtra("country");
                                                    String otherLatitude = getIntent().getStringExtra("latitude");
                                                    String otherLongitude = getIntent().getStringExtra("longitude");
                                                    String otherLandmark = getIntent().getStringExtra("landmark");

                                                    if (!snapshot.child("items").child(currentId).hasChild(otherItemName)) {
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Poster_Name").setValue(MemoryData.getName(PostItem_S4.this));
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Poster_UID").setValue(currentId);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Item_Type").setValue(otherType);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Item_Description").setValue(otherDescription);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Item_Name").setValue(otherItemName);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Item_RFT").setValue(otherReasonForTrading);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Item_Category").setValue(otherCategory);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Item_PrefItem").setValue(otherPrefItem);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Open_For_Offers").setValue("true");
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Address").child("Street").setValue(otherStreet);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Address").child("Barangay").setValue(otherBarangay);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Address").child("City").setValue(otherCity);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Address").child("State").setValue(otherState);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Address").child("Country").setValue(otherCountry);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Status").setValue("Validating");

                                                        if (!otherLandmark.equals("")) {
                                                            databaseReference.child("items").child(currentId).child(otherItemName).child("Address").child("Landmark").setValue(otherLandmark);
                                                        }

                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Address").child("Latitude").setValue(otherLatitude);
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Address").child("Longitude").setValue(otherLongitude);
                                                    }

                                                    if (snapshot.child("items").child(currentId).child(otherItemName).hasChild("Images")) {
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Images").child(String.valueOf(snapshot.child("items").child(currentId).child(otherItemName).child("Images").getChildrenCount() + 1)).setValue(task.getResult().toString());
                                                    } else {
                                                        databaseReference.child("items").child(currentId).child(otherItemName).child("Images").child("1").setValue(task.getResult().toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(currentId);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Posted an item " + getIntent().getStringExtra("item_name"));

                                                    pDialog.dismiss();

                                                    Intent intent = new Intent(PostItem_S4.this, UserHomepage.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.putExtra("Origin", "PostItem");
                                                    startActivity(intent);
                                                    CustomIntent.customType(PostItem_S4.this, "left-to-right");
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                        break;
                }
            }
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager2.isEnabled()) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        sliderHandler.postDelayed(sliderRunnable, 3500);
    }
}