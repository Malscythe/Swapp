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

public class TransactionMoreInfo extends AppCompatActivity {

    private static final String TAG = "Debug";
    private DatabaseReference mDatabase;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_more_info);

        String item_Name = getIntent().getStringExtra("itemName");
        String uid = getIntent().getStringExtra("userID");
        String parentKey = getIntent().getStringExtra("parentKey");
        String parentItemName = getIntent().getStringExtra("parentItemName");
        String viewDetails = getIntent().getStringExtra("view");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        String currentId = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference items;

        if (viewDetails.equals("Offered")) {
            items = FirebaseDatabase.getInstance().getReference().child("items").child(parentKey).child(parentItemName).child("Accepted_Offers").child(uid);
        } else {
            items = FirebaseDatabase.getInstance().getReference().child("items").child(parentKey).child(parentItemName);
        }


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

                            Glide.with(TransactionMoreInfo.this).load(snapshot.child("Item_SizeChart").getValue(String.class)).into(mSizeChart);
                        } else {

                            mSizeChartLayout.setVisibility(View.GONE);
                        }

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

                            Glide.with(TransactionMoreInfo.this).load(snapshot.child("Item_SizeChart").getValue(String.class)).into(wSizeChart);
                        } else {

                            wSizeChartLayout.setVisibility(View.GONE);
                        }

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

                        break;
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
        CustomIntent.customType(TransactionMoreInfo.this, "right-to-left");
    }
}