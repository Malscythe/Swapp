package com.example.Swapp;

import static com.github.mikephil.charting.animation.Easing.Linear;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.ArrayList;

import Swapp.R;
import Swapp.databinding.ActivityPostItemS1Binding;
import Swapp.databinding.ActivityPostItemS2Binding;
import maes.tech.intentanim.CustomIntent;

public class PostItem_S2 extends AppCompatActivity {

    CheckBox sizeXS, sizeS, sizeM, sizeL, sizeXL, sizeXXL, sizeXXXL;
    RelativeLayout apparelL, gadgetsL, gameL, bagsL, groceriesL, furnitureL, bnkL, appliancesL, motorsL, audioL, schoolL, otherL;
    AutoCompleteTextView clothingTypeW, clothingTypeM;
    TextInputLayout mApparelL, wApparelL;
    Button nextBtn;
    String[] descriptionData = {"Details", "Description", "Location", "Images"};
    String activeLayout;
    ActivityPostItemS2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostItemS2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String category = getIntent().getStringExtra("category");
        String item_name = getIntent().getStringExtra("item_name");
        String reasonForTrading = getIntent().getStringExtra("rft");
        String preferred_item = getIntent().getStringExtra("pref_item");

        StateProgressBar stateProgressBar = findViewById(R.id.stateProgress);
        stateProgressBar.setStateDescriptionData(descriptionData);

        sizeXS = findViewById(R.id.size_XS);
        sizeS = findViewById(R.id.size_S);
        sizeM = findViewById(R.id.size_M);
        sizeL = findViewById(R.id.size_L);
        sizeXL = findViewById(R.id.size_XL);
        sizeXXL = findViewById(R.id.size_2XL);
        sizeXXXL = findViewById(R.id.size_3XL);

        nextBtn = findViewById(R.id.nextBtn);

        mApparelL = findViewById(R.id.clothingTypeML);
        wApparelL = findViewById(R.id.clothingTypeWL);

        gadgetsL = findViewById(R.id.gadgetsInterface);
        gameL = findViewById(R.id.gamesInterface);
        bagsL = findViewById(R.id.bagsInterface);
        groceriesL = findViewById(R.id.groceriesInterface);
        furnitureL = findViewById(R.id.furnitureInterface);
        bnkL = findViewById(R.id.bnkInterface);
        appliancesL = findViewById(R.id.appliancesInterface);
        motorsL = findViewById(R.id.motorInterface);
        audioL = findViewById(R.id.audioInterface);
        schoolL = findViewById(R.id.schoolInterface);
        otherL = findViewById(R.id.otherInterface);
        apparelL = findViewById(R.id.apparelDescription);

        clothingTypeM = findViewById(R.id.clothingTypeM);
        clothingTypeW = findViewById(R.id.clothingTypeW);

        String[] clothingW = getResources().getStringArray(R.array.wApparel);
        ArrayAdapter arrayAdapterW = new ArrayAdapter(this, R.layout.dropdown_item, clothingW);
        clothingTypeW.setAdapter(arrayAdapterW);

        String[] clothingM = getResources().getStringArray(R.array.mApparel);
        ArrayAdapter arrayAdapterM = new ArrayAdapter(this, R.layout.dropdown_item, clothingM);
        clothingTypeM.setAdapter(arrayAdapterM);

        switch (category) {
            case "Men's Apparel":
                apparelL.setVisibility(View.VISIBLE);
                mApparelL.setVisibility(View.VISIBLE);
                wApparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.GONE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.GONE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (TextUtils.isEmpty(binding.clothingTypeM.getText().toString())) {
                            binding.clothingTypeML.setError("This cannot be empty.");
                            binding.clothingTypeML.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.clothingTypeML.setError(null);
                            binding.clothingTypeML.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.brand.getText().toString())) {
                            binding.brandL.setError("This cannot be empty.");
                            binding.brandL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.brandL.setError(null);
                            binding.brandL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.color.getText().toString())) {
                            binding.colorL.setError("This cannot be empty.");
                            binding.colorL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.colorL.setError(null);
                            binding.colorL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.material.getText().toString())) {
                            binding.materialL.setError("This cannot be empty.");
                            binding.materialL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.materialL.setError(null);
                            binding.materialL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.usage.getText().toString())) {
                            binding.usageL.setError("This cannot be empty.");
                            binding.usageL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.usageL.setError(null);
                            binding.usageL.setErrorIconDrawable(null);
                        }

                        if (!sizeXS.isChecked() && !sizeS.isChecked() && !sizeM.isChecked() && !sizeL.isChecked() && !sizeXL.isChecked() && !sizeXXL.isChecked() && !sizeXXXL.isChecked()) {
                            binding.sizeError.setVisibility(View.VISIBLE);
                            return;
                        } else {
                            binding.sizeError.setVisibility(View.GONE);
                        }

                        if (TextUtils.isEmpty(binding.description.getText().toString())) {
                            binding.descriptionL.setError("This cannot be empty.");
                            binding.descriptionL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.descriptionL.setError(null);
                            binding.descriptionL.setErrorIconDrawable(null);
                        }

                        ArrayList<String> sizesArr = new ArrayList<String>();

                        if (sizeXS.isChecked()){
                            sizesArr.add("XS");
                        } else {
                            sizesArr.remove("XS");
                        }

                        if (sizeS.isChecked()) {
                            sizesArr.add("S");
                        } else {
                            sizesArr.remove("S");
                        }

                        if (sizeM.isChecked()){
                            sizesArr.add("M");
                        } else {
                            sizesArr.remove("M");
                        }

                        if (sizeL.isChecked()) {
                            sizesArr.add("L");
                        } else {
                            sizesArr.remove("L");
                        }

                        if (sizeXL.isChecked()){
                            sizesArr.add("XL");
                        } else {
                            sizesArr.remove("XL");
                        }

                        if (sizeXXL.isChecked()) {
                            sizesArr.add("2XL");
                        } else {
                            sizesArr.remove("2XL");
                        }

                        if (sizeXXXL.isChecked()){
                            sizesArr.add("3XL");
                        } else {
                            sizesArr.remove("3XL");
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("mensClothingType", binding.clothingTypeM.getText().toString());
                        intent.putExtra("mensBrand", binding.brand.getText().toString());
                        intent.putExtra("mensColor", binding.color.getText().toString());
                        intent.putExtra("mensMaterial", binding.material.getText().toString());
                        intent.putExtra("mensUsage", binding.usage.getText().toString());
                        intent.putExtra("mensSizes", sizesArr.toString());
                        intent.putExtra("mensDescription", binding.description.getText().toString());
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });

                break;
            case "Gadgets":
                apparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.VISIBLE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.GONE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(binding.gadgetsType.getText().toString())) {
                            binding.gadgetsTypeL.setError("This cannot be empty.");
                            binding.gadgetsTypeL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.gadgetsTypeL.setError(null);
                            binding.gadgetsTypeL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.gadgetsBrand.getText().toString())) {
                            binding.gadgetsBrandL.setError("This cannot be empty.");
                            binding.gadgetsBrandL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.gadgetsBrandL.setError(null);
                            binding.gadgetsBrandL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.gadgetColor.getText().toString())) {
                            binding.gadgetColorL.setError("This cannot be empty.");
                            binding.gadgetColorL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.gadgetColorL.setError(null);
                            binding.gadgetColorL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.gadgetUsage.getText().toString())) {
                            binding.gadgetUsageL.setError("This cannot be empty.");
                            binding.gadgetUsageL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.gadgetUsageL.setError(null);
                            binding.gadgetUsageL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.gadgetDescription.getText().toString())) {
                            binding.gadgetDescriptionL.setError("This cannot be empty.");
                            binding.gadgetDescriptionL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.gadgetDescriptionL.setError(null);
                            binding.gadgetDescriptionL.setErrorIconDrawable(null);
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("gadgetType", binding.gadgetsType.getText().toString());
                        intent.putExtra("gadgetBrand", binding.gadgetsBrand.getText().toString());
                        intent.putExtra("gadgetColor", binding.gadgetColor.getText().toString());
                        intent.putExtra("gadgetUsage", binding.gadgetUsage.getText().toString());
                        intent.putExtra("gadgetDescription", binding.gadgetDescription.getText().toString());
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });
                break;
            case "Game":
                apparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.GONE);
                gameL.setVisibility(View.VISIBLE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.GONE);

                apparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.VISIBLE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.GONE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(binding.gameType.getText().toString())) {
                            binding.gameTypeL.setError("This cannot be empty.");
                            binding.gameTypeL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.gameTypeL.setError(null);
                            binding.gameTypeL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.gameBrand.getText().toString())) {
                            binding.gameBrandL.setError("This cannot be empty.");
                            binding.gameBrandL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.gameBrandL.setError(null);
                            binding.gameBrandL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.gametUsage.getText().toString())) {
                            binding.gameUsageL.setError("This cannot be empty.");
                            binding.gameUsageL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.gameUsageL.setError(null);
                            binding.gameUsageL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.gameDescription.getText().toString())) {
                            binding.gameDescriptionL.setError("This cannot be empty.");
                            binding.gameDescriptionL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.gameDescriptionL.setError(null);
                            binding.gameDescriptionL.setErrorIconDrawable(null);
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("gameType", binding.gameType.getText().toString());
                        intent.putExtra("gameBrand", binding.gameBrand.getText().toString());
                        intent.putExtra("gameUsage", binding.gametUsage.getText().toString());
                        intent.putExtra("gameDescription", binding.gameDescription.getText().toString());
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });

                break;
            case "Bags":
                apparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.GONE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.VISIBLE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.GONE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(binding.bagType.getText().toString())) {
                            binding.bagTypeL.setError("This cannot be empty.");
                            binding.bagTypeL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.bagTypeL.setError(null);
                            binding.bagTypeL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.bagBrand.getText().toString())) {
                            binding.bagBrandL.setError("This cannot be empty.");
                            binding.bagBrandL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.bagBrandL.setError(null);
                            binding.bagBrandL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.bagColor.getText().toString())) {
                            binding.bagColorL.setError("This cannot be empty.");
                            binding.bagColorL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.bagColorL.setError(null);
                            binding.bagColorL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.bagUsage.getText().toString())) {
                            binding.bagUsageL.setError("This cannot be empty.");
                            binding.bagUsageL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.bagUsageL.setError(null);
                            binding.bagUsageL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.bagDescription.getText().toString())) {
                            binding.bagDescriptionL.setError("This cannot be empty.");
                            binding.bagDescriptionL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.bagDescriptionL.setError(null);
                            binding.bagDescriptionL.setErrorIconDrawable(null);
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("bagType", binding.bagType.getText().toString());
                        intent.putExtra("bagBrand", binding.bagBrand.getText().toString());
                        intent.putExtra("bagColor", binding.bagColor.getText().toString());
                        intent.putExtra("bagUsage", binding.bagUsage.getText().toString());
                        intent.putExtra("bagDescription", binding.bagDescription.getText().toString());
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });

                break;
            case "Groceries":
                apparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.GONE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.VISIBLE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.GONE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(binding.groceryList.getText().toString())) {
                            binding.groceryListL.setError("This cannot be empty.");
                            binding.groceryListL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.groceryListL.setError(null);
                            binding.groceryListL.setErrorIconDrawable(null);
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("groceryList", binding.groceryList.getText().toString());
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });

                break;
            case "Furniture":
                apparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.GONE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.VISIBLE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.GONE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(binding.furnitureBrand.getText().toString())) {
                            binding.furnitureBrandL.setError("This cannot be empty.");
                            binding.furnitureBrandL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.furnitureBrandL.setError(null);
                            binding.furnitureBrandL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.furnitureColor.getText().toString())) {
                            binding.furnitureColorL.setError("This cannot be empty.");
                            binding.furnitureColorL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.furnitureColorL.setError(null);
                            binding.furnitureColorL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.furnitureUsage.getText().toString())) {
                            binding.furnitureUsageL.setError("This cannot be empty.");
                            binding.furnitureUsageL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.furnitureUsageL.setError(null);
                            binding.furnitureUsageL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.furnitureSizeHeight.getText().toString()) || TextUtils.isEmpty(binding.furnitureSizeWidth.getText().toString()) || TextUtils.isEmpty(binding.furnitureSizeLength.getText().toString())) {
                            binding.furnitureDimensionError.setVisibility(View.VISIBLE);
                            binding.furnitureDimensionError.setText("Dimensions cannot be empty.");
                            return;
                        } else {
                            binding.furnitureDimensionError.setVisibility(View.GONE);
                            binding.furnitureDimensionError.setText("");
                        }

                        if (TextUtils.isEmpty(binding.furnitureDescription.getText().toString())) {
                            binding.furnitureDescriptionL.setError("This cannot be empty.");
                            binding.furnitureDescriptionL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.furnitureDescriptionL.setError(null);
                            binding.furnitureDescriptionL.setErrorIconDrawable(null);
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("furnitureBrand", binding.furnitureBrand.getText().toString());
                        intent.putExtra("furnitureColor", binding.furnitureColor.getText().toString());
                        intent.putExtra("furnitureUsage", binding.furnitureUsage.getText().toString());
                        intent.putExtra("furnitureHeight", binding.furnitureSizeHeight.getText().toString());
                        intent.putExtra("furnitureWidth", binding.furnitureSizeWidth.getText().toString());
                        intent.putExtra("furnitureLength", binding.furnitureSizeLength.getText().toString());
                        intent.putExtra("furnitureDescription", binding.furnitureDescription.getText().toString());
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });

                break;
            case "Babies & Kids":
                apparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.GONE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.VISIBLE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.GONE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(binding.bnkType.getText().toString())) {
                            binding.bnkTypeL.setError("This cannot be empty.");
                            binding.bnkTypeL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.bnkTypeL.setError(null);
                            binding.bnkTypeL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.bnkBrand.getText().toString())) {
                            binding.bnkBrandL.setError("This cannot be empty.");
                            binding.bnkBrandL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.bnkBrandL.setError(null);
                            binding.bnkBrandL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.bnkUsage.getText().toString())) {
                            binding.bnkUsageL.setError("This cannot be empty.");
                            binding.bnkUsageL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.bnkUsageL.setError(null);
                            binding.bnkUsageL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.bnkAge.getText().toString())) {
                            binding.bnkAgeL.setError("This cannot be empty.");
                            binding.bnkAgeL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.bnkAgeL.setError(null);
                            binding.bnkAgeL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.bnkDescription.getText().toString())) {
                            binding.bnkDescriptionL.setError("This cannot be empty.");
                            binding.bnkDescriptionL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.bnkDescriptionL.setError(null);
                            binding.bnkDescriptionL.setErrorIconDrawable(null);
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("bnkAge", binding.bnkAge.getText().toString());
                        intent.putExtra("bnkBrand", binding.bnkBrand.getText().toString());
                        intent.putExtra("bnkType", binding.bnkType.getText().toString());
                        intent.putExtra("bnkUsage", binding.bnkUsage.getText().toString());
                        intent.putExtra("bnkDescription", binding.bnkDescription.getText().toString());
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });

                break;
            case "Appliances":
                apparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.GONE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.VISIBLE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.GONE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(binding.appliancesType.getText().toString())) {
                            binding.appliancesTypeL.setError("This cannot be empty.");
                            binding.appliancesTypeL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.appliancesTypeL.setError(null);
                            binding.appliancesTypeL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.appliancesBrand.getText().toString())) {
                            binding.appliancesBrandL.setError("This cannot be empty.");
                            binding.appliancesBrandL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.appliancesBrandL.setError(null);
                            binding.appliancesBrandL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.appliancesColor.getText().toString())) {
                            binding.appliancesColorL.setError("This cannot be empty.");
                            binding.appliancesColorL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.appliancesColorL.setError(null);
                            binding.appliancesColorL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.appliancesUsage.getText().toString())) {
                            binding.appliancesUsageL.setError("This cannot be empty.");
                            binding.appliancesUsageL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.appliancesUsageL.setError(null);
                            binding.appliancesUsageL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.appliancesDescription.getText().toString())) {
                            binding.appliancesDescriptionL.setError("This cannot be empty.");
                            binding.appliancesDescriptionL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.appliancesDescriptionL.setError(null);
                            binding.appliancesDescriptionL.setErrorIconDrawable(null);
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("appliancesType", binding.appliancesType.getText().toString());
                        intent.putExtra("appliancesBrand", binding.appliancesBrand.getText().toString());
                        intent.putExtra("appliancesColor", binding.appliancesColor.getText().toString());
                        intent.putExtra("appliancesUsage", binding.appliancesUsage.getText().toString());
                        intent.putExtra("appliancesDescription", binding.appliancesDescription.getText().toString());
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });

                break;
            case "Motors":
                apparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.GONE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.VISIBLE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.GONE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(binding.motorModel.getText().toString())) {
                            binding.motorModelL.setError("This cannot be empty.");
                            binding.motorModelL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.motorModelL.setError(null);
                            binding.motorModelL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.motorBrand.getText().toString())) {
                            binding.motorBrandL.setError("This cannot be empty.");
                            binding.motorBrandL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.motorBrandL.setError(null);
                            binding.motorBrandL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.motorColor.getText().toString())) {
                            binding.motorColorL.setError("This cannot be empty.");
                            binding.motorColorL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.motorColorL.setError(null);
                            binding.motorColorL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.motorUsage.getText().toString())) {
                            binding.motorUsageL.setError("This cannot be empty.");
                            binding.motorUsageL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.motorUsageL.setError(null);
                            binding.motorUsageL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.motorDescription.getText().toString())) {
                            binding.motorDescriptionL.setError("This cannot be empty.");
                            binding.motorDescriptionL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.motorDescriptionL.setError(null);
                            binding.motorDescriptionL.setErrorIconDrawable(null);
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("motorModel", binding.motorModel.getText().toString());
                        intent.putExtra("motorBrand", binding.motorBrand.getText().toString());
                        intent.putExtra("motorColor", binding.motorColor.getText().toString());
                        intent.putExtra("motorUsage", binding.motorUsage.getText().toString());
                        intent.putExtra("motorDescription", binding.motorDescription.getText().toString());
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });

                break;
            case "Audio":
                apparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.GONE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.VISIBLE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.GONE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (TextUtils.isEmpty(binding.audioArtist.getText().toString())) {
                            binding.audioArtistL.setError("This cannot be empty.");
                            binding.audioArtistL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.audioArtistL.setError(null);
                            binding.audioArtistL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.audioReleaseDate.getText().toString())) {
                            binding.audioReleaseDateL.setError("This cannot be empty.");
                            binding.audioReleaseDateL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.audioReleaseDateL.setError(null);
                            binding.audioReleaseDateL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.audioUsage.getText().toString())) {
                            binding.audioUsageL.setError("This cannot be empty.");
                            binding.audioUsageL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.audioUsageL.setError(null);
                            binding.audioUsageL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.audioDescription.getText().toString())) {
                            binding.audioDescriptionL.setError("This cannot be empty.");
                            binding.audioDescriptionL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.audioDescriptionL.setError(null);
                            binding.audioDescriptionL.setErrorIconDrawable(null);
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("audioArtist", binding.audioArtist.getText().toString());
                        intent.putExtra("audioReleaseDate", binding.audioReleaseDate.getText().toString());
                        intent.putExtra("audioUsage", binding.audioUsage.getText().toString());
                        intent.putExtra("audioDescription", binding.audioDescription.getText().toString());
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });

                break;
            case "School":
                apparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.GONE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.VISIBLE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(binding.schoolType.getText().toString())) {
                            binding.schoolTypeL.setError("This cannot be empty.");
                            binding.schoolTypeL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.schoolTypeL.setError(null);
                            binding.schoolTypeL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.schoolBrand.getText().toString())) {
                            binding.schoolBrandL.setError("This cannot be empty.");
                            binding.schoolBrandL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.schoolBrandL.setError(null);
                            binding.schoolBrandL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.schoolColor.getText().toString())) {
                            binding.schoolColorL.setError("This cannot be empty.");
                            binding.schoolColorL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.schoolColorL.setError(null);
                            binding.schoolColorL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.schoolUsage.getText().toString())) {
                            binding.schoolUsageL.setError("This cannot be empty.");
                            binding.schoolUsageL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.schoolUsageL.setError(null);
                            binding.schoolUsageL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.schoolDescription.getText().toString())) {
                            binding.schoolDescriptionL.setError("This cannot be empty.");
                            binding.schoolDescriptionL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.schoolDescriptionL.setError(null);
                            binding.schoolDescriptionL.setErrorIconDrawable(null);
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("schoolType", binding.schoolType.getText().toString());
                        intent.putExtra("schoolBrand", binding.schoolBrand.getText().toString());
                        intent.putExtra("schoolColor", binding.schoolColor.getText().toString());
                        intent.putExtra("schoolUsage", binding.schoolUsage.getText().toString());
                        intent.putExtra("schoolDescription", binding.schoolDescription.getText().toString());
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });

                break;
            case "Women's Apparel":
                mApparelL.setVisibility(View.GONE);
                wApparelL.setVisibility(View.VISIBLE);
                apparelL.setVisibility(View.VISIBLE);
                gadgetsL.setVisibility(View.GONE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.GONE);
                schoolL.setVisibility(View.GONE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(binding.clothingTypeW.getText().toString())) {
                            binding.clothingTypeWL.setError("This cannot be empty.");
                            binding.clothingTypeWL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.clothingTypeWL.setError(null);
                            binding.clothingTypeWL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.brand.getText().toString())) {
                            binding.brandL.setError("This cannot be empty.");
                            binding.brandL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.brandL.setError(null);
                            binding.brandL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.color.getText().toString())) {
                            binding.colorL.setError("This cannot be empty.");
                            binding.colorL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.colorL.setError(null);
                            binding.colorL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.material.getText().toString())) {
                            binding.materialL.setError("This cannot be empty.");
                            binding.materialL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.materialL.setError(null);
                            binding.materialL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.usage.getText().toString())) {
                            binding.usageL.setError("This cannot be empty.");
                            binding.usageL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.usageL.setError(null);
                            binding.usageL.setErrorIconDrawable(null);
                        }

                        if (!sizeXS.isChecked() && !sizeS.isChecked() && !sizeM.isChecked() && !sizeL.isChecked() && !sizeXL.isChecked() && !sizeXXL.isChecked() && !sizeXXXL.isChecked()) {
                            binding.sizeError.setVisibility(View.VISIBLE);
                            return;
                        } else {
                            binding.sizeError.setVisibility(View.GONE);
                        }

                        if (TextUtils.isEmpty(binding.description.getText().toString())) {
                            binding.descriptionL.setError("This cannot be empty.");
                            binding.descriptionL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.descriptionL.setError(null);
                            binding.descriptionL.setErrorIconDrawable(null);
                        }

                        ArrayList<String> sizesArr = new ArrayList<String>();

                        if (sizeXS.isChecked()){
                            sizesArr.add("XS");
                        }

                        if (sizeS.isChecked()) {
                            sizesArr.add("S");
                        }

                        if (sizeM.isChecked()){
                            sizesArr.add("M");
                        }

                        if (sizeL.isChecked()) {
                            sizesArr.add("L");
                        }

                        if (sizeXL.isChecked()){
                            sizesArr.add("XL");
                        }

                        if (sizeXXL.isChecked()) {
                            sizesArr.add("2XL");
                        }

                        if (sizeXXXL.isChecked()){
                            sizesArr.add("3XL");
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("womensClothingType", binding.clothingTypeW.getText().toString());
                        intent.putExtra("womensBrand", binding.brand.getText().toString());
                        intent.putExtra("womensColor", binding.color.getText().toString());
                        intent.putExtra("womensMaterial", binding.material.getText().toString());
                        intent.putExtra("womensUsage", binding.usage.getText().toString());
                        intent.putExtra("womensSizes", sizesArr.toString());
                        intent.putExtra("womensDescription", binding.description.getText().toString());
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });

                break;
            case "Others":
                apparelL.setVisibility(View.GONE);
                gadgetsL.setVisibility(View.GONE);
                gameL.setVisibility(View.GONE);
                bagsL.setVisibility(View.GONE);
                groceriesL.setVisibility(View.GONE);
                furnitureL.setVisibility(View.GONE);
                bnkL.setVisibility(View.GONE);
                appliancesL.setVisibility(View.GONE);
                motorsL.setVisibility(View.GONE);
                audioL.setVisibility(View.GONE);
                otherL.setVisibility(View.VISIBLE);
                schoolL.setVisibility(View.GONE);

                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(binding.otherType.getText().toString())) {
                            binding.otherTypeL.setError("This cannot be empty.");
                            binding.otherTypeL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.otherTypeL.setError(null);
                            binding.otherTypeL.setErrorIconDrawable(null);
                        }

                        if (TextUtils.isEmpty(binding.otherDescription.getText().toString())) {
                            binding.otherDescriptionL.setError("This cannot be empty.");
                            binding.otherDescriptionL.setErrorIconDrawable(null);
                            return;
                        } else {
                            binding.otherDescriptionL.setError(null);
                            binding.otherDescriptionL.setErrorIconDrawable(null);
                        }

                        Intent intent = new Intent(PostItem_S2.this, PostItem_S3.class);
                        intent.putExtra("otherType", binding.otherType.getText().toString());
                        intent.putExtra("otherDescription", binding.otherDescription.getText().toString());
                        intent.putExtra("currentState", "preLocation");
                        intent.putExtra("item_name", item_name);
                        intent.putExtra("rft", reasonForTrading);
                        intent.putExtra("category", category);
                        intent.putExtra("pref_item", preferred_item);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        CustomIntent.customType(PostItem_S2.this, "left-to-right");
                    }
                });

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PostItem_S2.this, PostItem_S1.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        CustomIntent.customType(PostItem_S2.this, "right-to-left");
    }
}