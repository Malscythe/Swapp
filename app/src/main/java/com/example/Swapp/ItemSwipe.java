package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import Swapp.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import maes.tech.intentanim.CustomIntent;

public class ItemSwipe extends AppCompatActivity {

    private static final String TAG = "Debug";
    private cards cards_data[];
    private arrayAdapter arrayAdapter;

    RelativeLayout noMoreItemBanner;

    ArrayList<cards> rowItems;

    ImageView filterItems;

    Button noMoreItemsBtn;

    String currentItem, poster_uid, parent;

    Boolean type, brand, color, material, size, width, length, height, age, model, artist, releasedate;

    String getAge;

    float getWidth, getLength, getHeight;

    String category;

    String concatinated, uid, itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_swipe);
        ArrayList<String> keysList = (ArrayList<String>) getIntent().getSerializableExtra("keys");

        noMoreItemBanner = findViewById(R.id.noMoreItemBanner);
        noMoreItemsBtn = findViewById(R.id.returnToCategories);


        if (getIntent().getStringExtra("category").equals("Men's Apparel") || getIntent().getStringExtra("category").equals("Women's Apparel")) {
            category = "Apparel";
        } else if (getIntent().getStringExtra("category").equals("Gadgets") || getIntent().getStringExtra("category").equals("Bags") ||
                getIntent().getStringExtra("category").equals("Appliances") || getIntent().getStringExtra("category").equals("School")) {
            category = "GadgetsBagsAppliancesSchool";
        } else {
            category = getIntent().getStringExtra("category");
        }

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

        filterItems = findViewById(R.id.filterItem);

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        if (category.equals("all") || category.equals("Groceries") || category.equals("Others")) {
            filterItems.setVisibility(View.GONE);
        }

        filterItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ItemSwipe.this, R.style.BottomSheetDialogTheme);

                View bottomSheetView = LayoutInflater.from(ItemSwipe.this).inflate(R.layout.bottom_sheet_layout, (LinearLayout) findViewById(R.id.bottomSheetContainer));

                TextView typeText = bottomSheetView.findViewById(R.id.typeText);
                TextView brandText = bottomSheetView.findViewById(R.id.brandText);
                TextView colorText = bottomSheetView.findViewById(R.id.colorText);
                ChipGroup typeChips = bottomSheetView.findViewById(R.id.typeChips);
                ChipGroup brandChips = bottomSheetView.findViewById(R.id.brandChips);
                ChipGroup colorChips = bottomSheetView.findViewById(R.id.colorChips);

                TextView materialText = bottomSheetView.findViewById(R.id.materialText);
                TextView sizeText = bottomSheetView.findViewById(R.id.sizeText);
                ChipGroup materialChips = bottomSheetView.findViewById(R.id.materialChips);
                ChipGroup sizeChips = bottomSheetView.findViewById(R.id.sizeChips);
                Slider sizeNum = bottomSheetView.findViewById(R.id.sizeSlider);

                TextView modelText = bottomSheetView.findViewById(R.id.modelText);
                ChipGroup modelChips = bottomSheetView.findViewById(R.id.modelChips);

                TextView artistText = bottomSheetView.findViewById(R.id.artistText);
                TextView releaseDateText = bottomSheetView.findViewById(R.id.releaseDateText);
                ChipGroup artistChips = bottomSheetView.findViewById(R.id.artistChips);
                ChipGroup releaseDateChips = bottomSheetView.findViewById(R.id.releaseDateChips);

                TextView ageText = bottomSheetView.findViewById(R.id.ageText);
                ChipGroup ageChips = bottomSheetView.findViewById(R.id.ageChips);

                TextView dimensionText = bottomSheetView.findViewById(R.id.dimensionText);
                LinearLayout dimensionLayout = bottomSheetView.findViewById(R.id.dimensionLayout);
                TextInputEditText widthValue = bottomSheetView.findViewById(R.id.width);
                TextInputEditText lengthValue = bottomSheetView.findViewById(R.id.length);
                TextInputEditText heightValue = bottomSheetView.findViewById(R.id.height);

                ArrayList<String> getType = new ArrayList<>();
                ArrayList<String> getBrand = new ArrayList<>();
                ArrayList<String> getColor = new ArrayList<>();
                ArrayList<String> getMaterial = new ArrayList<>();
                ArrayList<String> getSizeText = new ArrayList<>();
                ArrayList<Integer> getSizeNum = new ArrayList<>();
                ArrayList<String> getModel = new ArrayList<>();
                ArrayList<String> getArtist = new ArrayList<>();
                ArrayList<String> getReleaseDate = new ArrayList<>();

                ArrayList<String> typeList = new ArrayList<>();
                ArrayList<String> brandList = new ArrayList<>();
                ArrayList<String> colorList = new ArrayList<>();
                ArrayList<String> materialList = new ArrayList<>();
                ArrayList<String> sizeTextList = new ArrayList<>();
                ArrayList<String> modelList = new ArrayList<>();
                ArrayList<String> artistList = new ArrayList<>();
                ArrayList<String> releaseDateList = new ArrayList<>();

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                ArrayList<String> keysList = (ArrayList<String>) getIntent().getSerializableExtra("keys");

                switch (category) {
                    case "Apparel":
                        type = true;
                        brand = true;
                        color = true;
                        material = true;
                        size = true;
                        width = false;
                        length = false;
                        height = false;
                        age = false;
                        model = false;
                        artist = false;
                        releasedate = false;

                        typeText.setVisibility(View.VISIBLE);
                        brandText.setVisibility(View.VISIBLE);
                        colorText.setVisibility(View.VISIBLE);
                        materialText.setVisibility(View.VISIBLE);
                        sizeText.setVisibility(View.VISIBLE);
                        modelText.setVisibility(View.GONE);
                        artistText.setVisibility(View.GONE);
                        releaseDateText.setVisibility(View.GONE);
                        ageText.setVisibility(View.GONE);
                        dimensionText.setVisibility(View.GONE);

                        typeChips.setVisibility(View.VISIBLE);
                        brandChips.setVisibility(View.VISIBLE);
                        colorChips.setVisibility(View.VISIBLE);
                        materialChips.setVisibility(View.VISIBLE);
                        sizeChips.setVisibility(View.VISIBLE);
                        sizeNum.setVisibility(View.VISIBLE);
                        modelChips.setVisibility(View.GONE);
                        artistChips.setVisibility(View.GONE);
                        releaseDateChips.setVisibility(View.GONE);
                        ageChips.setVisibility(View.GONE);
                        dimensionLayout.setVisibility(View.GONE);

                        break;
                    case "GadgetsBagsAppliancesSchool":
                        type = true;
                        brand = true;
                        color = true;
                        material = false;
                        size = false;
                        width = false;
                        length = false;
                        height = false;
                        age = false;
                        model = false;
                        artist = false;
                        releasedate = false;

                        typeText.setVisibility(View.VISIBLE);
                        brandText.setVisibility(View.VISIBLE);
                        colorText.setVisibility(View.VISIBLE);
                        materialText.setVisibility(View.GONE);
                        sizeText.setVisibility(View.GONE);
                        modelText.setVisibility(View.GONE);
                        artistText.setVisibility(View.GONE);
                        releaseDateText.setVisibility(View.GONE);
                        ageText.setVisibility(View.GONE);
                        dimensionText.setVisibility(View.GONE);

                        typeChips.setVisibility(View.VISIBLE);
                        brandChips.setVisibility(View.VISIBLE);
                        colorChips.setVisibility(View.VISIBLE);
                        materialChips.setVisibility(View.GONE);
                        sizeChips.setVisibility(View.GONE);
                        sizeNum.setVisibility(View.GONE);
                        modelChips.setVisibility(View.GONE);
                        artistChips.setVisibility(View.GONE);
                        releaseDateChips.setVisibility(View.GONE);
                        ageChips.setVisibility(View.GONE);
                        dimensionLayout.setVisibility(View.GONE);

                        break;
                    case "Game":
                        type = true;
                        brand = true;
                        color = false;
                        material = false;
                        size = false;
                        width = false;
                        length = false;
                        height = false;
                        age = false;
                        model = false;
                        artist = false;
                        releasedate = false;

                        typeText.setVisibility(View.VISIBLE);
                        brandText.setVisibility(View.VISIBLE);
                        colorText.setVisibility(View.GONE);
                        materialText.setVisibility(View.GONE);
                        sizeText.setVisibility(View.GONE);
                        modelText.setVisibility(View.GONE);
                        artistText.setVisibility(View.GONE);
                        releaseDateText.setVisibility(View.GONE);
                        ageText.setVisibility(View.GONE);
                        dimensionText.setVisibility(View.GONE);

                        typeChips.setVisibility(View.VISIBLE);
                        brandChips.setVisibility(View.VISIBLE);
                        colorChips.setVisibility(View.GONE);
                        materialChips.setVisibility(View.GONE);
                        sizeChips.setVisibility(View.GONE);
                        sizeNum.setVisibility(View.GONE);
                        modelChips.setVisibility(View.GONE);
                        artistChips.setVisibility(View.GONE);
                        releaseDateChips.setVisibility(View.GONE);
                        ageChips.setVisibility(View.GONE);
                        dimensionLayout.setVisibility(View.GONE);

                        break;
                    case "Furniture":
                        type = false;
                        brand = true;
                        color = true;
                        material = false;
                        size = false;
                        width = true;
                        length = true;
                        height = true;
                        age = false;
                        model = false;
                        artist = false;
                        releasedate = false;

                        typeText.setVisibility(View.GONE);
                        brandText.setVisibility(View.VISIBLE);
                        colorText.setVisibility(View.VISIBLE);
                        materialText.setVisibility(View.GONE);
                        sizeText.setVisibility(View.GONE);
                        modelText.setVisibility(View.GONE);
                        artistText.setVisibility(View.GONE);
                        releaseDateText.setVisibility(View.GONE);
                        ageText.setVisibility(View.GONE);
                        dimensionText.setVisibility(View.VISIBLE);

                        typeChips.setVisibility(View.GONE);
                        brandChips.setVisibility(View.VISIBLE);
                        colorChips.setVisibility(View.VISIBLE);
                        materialChips.setVisibility(View.GONE);
                        sizeChips.setVisibility(View.GONE);
                        sizeNum.setVisibility(View.GONE);
                        modelChips.setVisibility(View.GONE);
                        artistChips.setVisibility(View.GONE);
                        releaseDateChips.setVisibility(View.GONE);
                        ageChips.setVisibility(View.GONE);
                        dimensionLayout.setVisibility(View.VISIBLE);

                        break;
                    case "Babies & Kids":
                        type = true;
                        brand = true;
                        color = false;
                        material = false;
                        size = false;
                        width = false;
                        length = false;
                        height = false;
                        age = true;
                        model = false;
                        artist = false;
                        releasedate = false;

                        typeText.setVisibility(View.VISIBLE);
                        brandText.setVisibility(View.VISIBLE);
                        colorText.setVisibility(View.GONE);
                        materialText.setVisibility(View.GONE);
                        sizeText.setVisibility(View.GONE);
                        modelText.setVisibility(View.GONE);
                        artistText.setVisibility(View.GONE);
                        releaseDateText.setVisibility(View.GONE);
                        ageText.setVisibility(View.VISIBLE);
                        dimensionText.setVisibility(View.GONE);

                        typeChips.setVisibility(View.VISIBLE);
                        brandChips.setVisibility(View.VISIBLE);
                        colorChips.setVisibility(View.GONE);
                        materialChips.setVisibility(View.GONE);
                        sizeChips.setVisibility(View.GONE);
                        sizeNum.setVisibility(View.GONE);
                        modelChips.setVisibility(View.GONE);
                        artistChips.setVisibility(View.GONE);
                        releaseDateChips.setVisibility(View.GONE);
                        ageChips.setVisibility(View.VISIBLE);
                        dimensionLayout.setVisibility(View.GONE);

                        break;
                    case "Motors":
                        type = false;
                        brand = true;
                        color = true;
                        material = false;
                        size = false;
                        width = false;
                        length = false;
                        height = false;
                        age = false;
                        model = true;
                        artist = false;
                        releasedate = false;

                        typeText.setVisibility(View.GONE);
                        brandText.setVisibility(View.VISIBLE);
                        colorText.setVisibility(View.VISIBLE);
                        materialText.setVisibility(View.GONE);
                        sizeText.setVisibility(View.GONE);
                        modelText.setVisibility(View.VISIBLE);
                        artistText.setVisibility(View.GONE);
                        releaseDateText.setVisibility(View.GONE);
                        ageText.setVisibility(View.GONE);
                        dimensionText.setVisibility(View.GONE);

                        typeChips.setVisibility(View.GONE);
                        brandChips.setVisibility(View.VISIBLE);
                        colorChips.setVisibility(View.VISIBLE);
                        materialChips.setVisibility(View.GONE);
                        sizeChips.setVisibility(View.GONE);
                        sizeNum.setVisibility(View.GONE);
                        modelChips.setVisibility(View.VISIBLE);
                        artistChips.setVisibility(View.GONE);
                        releaseDateChips.setVisibility(View.GONE);
                        ageChips.setVisibility(View.GONE);
                        dimensionLayout.setVisibility(View.GONE);

                        break;
                    case "Audio":
                        type = false;
                        brand = false;
                        color = false;
                        material = false;
                        size = false;
                        width = false;
                        length = false;
                        height = false;
                        age = false;
                        model = false;
                        artist = true;
                        releasedate = true;

                        typeText.setVisibility(View.GONE);
                        brandText.setVisibility(View.GONE);
                        colorText.setVisibility(View.GONE);
                        materialText.setVisibility(View.GONE);
                        sizeText.setVisibility(View.GONE);
                        modelText.setVisibility(View.GONE);
                        artistText.setVisibility(View.VISIBLE);
                        releaseDateText.setVisibility(View.VISIBLE);
                        ageText.setVisibility(View.GONE);
                        dimensionText.setVisibility(View.GONE);

                        typeChips.setVisibility(View.GONE);
                        brandChips.setVisibility(View.GONE);
                        colorChips.setVisibility(View.GONE);
                        materialChips.setVisibility(View.GONE);
                        sizeChips.setVisibility(View.GONE);
                        sizeNum.setVisibility(View.GONE);
                        modelChips.setVisibility(View.GONE);
                        artistChips.setVisibility(View.VISIBLE);
                        releaseDateChips.setVisibility(View.VISIBLE);
                        ageChips.setVisibility(View.GONE);
                        dimensionLayout.setVisibility(View.GONE);

                        break;
                }

                for (int i = 0; i < keysList.size(); i++) {

                    String concatinated = keysList.get(i);
                    String uid = concatinated.substring(0, concatinated.indexOf('-'));
                    String itemName = concatinated.substring((concatinated.indexOf("-") + 1), concatinated.length());

                    int finalI = i;

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    if (dataSnapshot1.hasChild("Item_Type")) {
                                        if (dataSnapshot.getKey().equals(uid) && dataSnapshot1.getKey().equals(itemName)) {
                                            if (!getType.contains(dataSnapshot1.child("Item_Type").getValue(String.class))) {
                                                getType.add(dataSnapshot1.child("Item_Type").getValue(String.class));
                                            }
                                        }
                                    }

                                    if (dataSnapshot1.hasChild("Item_Brand")) {
                                        if (dataSnapshot.getKey().equals(uid) && dataSnapshot1.getKey().equals(itemName)) {
                                            if (!getBrand.contains(dataSnapshot1.child("Item_Brand").getValue(String.class))) {
                                                getBrand.add(dataSnapshot1.child("Item_Brand").getValue(String.class));
                                            }
                                        }
                                    }

                                    if (dataSnapshot1.hasChild("Item_Color")) {
                                        if (dataSnapshot.getKey().equals(uid) && dataSnapshot1.getKey().equals(itemName)) {
                                            if (!getColor.contains(dataSnapshot1.child("Item_Color").getValue(String.class))) {
                                                getColor.add(dataSnapshot1.child("Item_Color").getValue(String.class));
                                            }
                                        }
                                    }

                                    if (dataSnapshot1.hasChild("Item_Material")) {
                                        if (dataSnapshot.getKey().equals(uid) && dataSnapshot1.getKey().equals(itemName)) {
                                            if (!getMaterial.contains(dataSnapshot1.child("Item_Material").getValue(String.class))) {
                                                getMaterial.add(dataSnapshot1.child("Item_Material").getValue(String.class));
                                            }
                                        }
                                    }

                                    if (dataSnapshot1.hasChild("Item_Sizes")) {
                                        if (dataSnapshot.getKey().equals(uid) && dataSnapshot1.getKey().equals(itemName)) {
                                            if (isNumber(dataSnapshot1.child("Item_Sizes").getValue(String.class))) {
                                                if (!getSizeNum.contains(Integer.parseInt(dataSnapshot1.child("Item_Sizes").getValue(String.class)))) {
                                                    getSizeNum.add(Integer.parseInt(dataSnapshot1.child("Item_Sizes").getValue(String.class)));
                                                }
                                            } else {
                                                if (!getSizeText.contains(dataSnapshot1.child("Item_Sizes").getValue(String.class))) {
                                                    getSizeText.add(dataSnapshot1.child("Item_Sizes").getValue(String.class));
                                                }
                                            }
                                        }
                                    }

                                    if (dataSnapshot1.hasChild("Item_Model")) {
                                        if (dataSnapshot.getKey().equals(uid) && dataSnapshot1.getKey().equals(itemName)) {
                                            if (!getModel.contains(dataSnapshot1.child("Item_Model").getValue(String.class))) {
                                                getModel.add(dataSnapshot1.child("Item_Model").getValue(String.class));
                                            }
                                        }
                                    }

                                    if (dataSnapshot1.hasChild("Item_Artist")) {
                                        if (dataSnapshot.getKey().equals(uid) && dataSnapshot1.getKey().equals(itemName)) {
                                            if (!getArtist.contains(dataSnapshot1.child("Item_Artist").getValue(String.class))) {
                                                getArtist.add(dataSnapshot1.child("Item_Artist").getValue(String.class));
                                            }
                                        }
                                    }

                                    if (dataSnapshot1.hasChild("Item_ReleaseDate")) {
                                        if (dataSnapshot.getKey().equals(uid) && dataSnapshot1.getKey().equals(itemName)) {
                                            if (!getReleaseDate.contains(dataSnapshot1.child("Item_ReleaseDate").getValue(String.class))) {
                                                getReleaseDate.add(dataSnapshot1.child("Item_ReleaseDate").getValue(String.class));
                                            }
                                        }
                                    }
                                }
                            }

                            if (!getBrand.isEmpty()) {
                                if (getBrand.contains("N/A") || getBrand.contains("n/a")) {
                                    getBrand.remove("N/A");
                                    getBrand.remove("n/a");
                                    getBrand.add("UNBRANDED");
                                }
                            }

                            if (finalI == (keysList.size() - 1)) {

                                ArrayList<String> newGetSizeText = reformatSize(getSizeText);

                                sortAndUpper(getType);
                                sortAndUpper(getColor);
                                sortAndUpper(getBrand);
                                sortAndUpper(getMaterial);
                                sortAndUpper(getModel);
                                sortAndUpper(getArtist);
                                sort(getReleaseDate);
                                sort(newGetSizeText);

                                if (!getSizeNum.isEmpty()) {
                                    sizeNum.setVisibility(View.VISIBLE);
                                    sizeNum.setValueTo(maxNum(getSizeNum));
                                    sizeNum.setValueFrom(minNum(getSizeNum));
                                    sizeNum.setValue(maxNum(getSizeNum));
                                } else {
                                    sizeNum.setVisibility(View.GONE);
                                }

                                if (type) {
                                    for (int i = 0; i < getType.size(); i++) {

                                        LinearLayout.LayoutParams layoutParams = new
                                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams.setMargins(5, 5, 5, 5);

                                        Chip chip = new Chip(bottomSheetView.getContext());

                                        chip.setLayoutParams(layoutParams);
                                        chip.setText(getType.get(i));

                                        chip.setClickable(true);
                                        chip.setCheckable(true);

                                        typeChips.addView(chip);

                                        typeChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                            @Override
                                            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                                                for (int i = 0; i < group.getChildCount(); i++) {
                                                    Chip chip = (Chip) group.getChildAt(i);
                                                    if (chip.isChecked() && !typeList.contains(chip.getText().toString())) {
                                                        typeList.add(chip.getText().toString());
                                                    } else if (!chip.isChecked() && typeList.contains(chip.getText().toString())){
                                                        typeList.remove(chip.getText().toString());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                                if (brand) {
                                    for (int i = 0; i < getBrand.size(); i++) {

                                        LinearLayout.LayoutParams layoutParams = new
                                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams.setMargins(5, 5, 5, 5);

                                        Chip chip = new Chip(bottomSheetView.getContext());

                                        chip.setLayoutParams(layoutParams);
                                        chip.setText(getBrand.get(i));

                                        chip.setClickable(true);
                                        chip.setCheckable(true);

                                        brandChips.addView(chip);

                                        brandChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                            @Override
                                            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                                                for (int i = 0; i < group.getChildCount(); i++) {
                                                    Chip chip = (Chip) group.getChildAt(i);
                                                    if (chip.isChecked() && !brandList.contains(chip.getText().toString())) {
                                                        brandList.add(chip.getText().toString());
                                                    } else if (!chip.isChecked() && brandList.contains(chip.getText().toString())) {
                                                        brandList.remove(chip.getText().toString());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                                if (age) {
                                    ageChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                        @Override
                                        public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                                                Chip chip = (Chip) group.getChildAt(0);
                                                if (chip.getText().toString().equals("0-5")) {
                                                    getAge = "0-5";
                                                } else {
                                                    getAge = "5-10";
                                                }
                                        }
                                    });
                                }

                                if (color) {
                                    for (int i = 0; i < getColor.size(); i++) {

                                        LinearLayout.LayoutParams layoutParams = new
                                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams.setMargins(5, 5, 5, 5);

                                        Chip chip = new Chip(bottomSheetView.getContext());

                                        chip.setLayoutParams(layoutParams);
                                        chip.setText(getColor.get(i));

                                        chip.setClickable(true);
                                        chip.setCheckable(true);

                                        colorChips.addView(chip);

                                        colorChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                            @Override
                                            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                                                for (int i = 0; i < group.getChildCount(); i++) {
                                                    Chip chip = (Chip) group.getChildAt(i);
                                                    if (chip.isChecked() && !colorList.contains(chip.getText().toString())) {
                                                        colorList.add(chip.getText().toString());
                                                    } else if (!chip.isChecked() && colorList.contains(chip.getText().toString())){
                                                        colorList.remove(chip.getText().toString());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                                if (material) {
                                    for (int i = 0; i < getMaterial.size(); i++) {

                                        LinearLayout.LayoutParams layoutParams = new
                                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams.setMargins(5, 5, 5, 5);

                                        Chip chip = new Chip(bottomSheetView.getContext());

                                        chip.setLayoutParams(layoutParams);
                                        chip.setText(getMaterial.get(i));

                                        chip.setClickable(true);
                                        chip.setCheckable(true);

                                        materialChips.addView(chip);

                                        materialChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                            @Override
                                            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                                                for (int i = 0; i < group.getChildCount(); i++) {
                                                    Chip chip = (Chip) group.getChildAt(i);
                                                    if (chip.isChecked() && !materialList.contains(chip.getText().toString())) {
                                                        materialList.add(chip.getText().toString());
                                                    } else if (!chip.isChecked() && materialList.contains(chip.getText().toString())){
                                                        materialList.remove(chip.getText().toString());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                                if (size) {
                                    for (int i = 0; i < newGetSizeText.size(); i++) {

                                        LinearLayout.LayoutParams layoutParams = new
                                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams.setMargins(5, 5, 5, 5);

                                        Chip chip = new Chip(bottomSheetView.getContext());

                                        chip.setLayoutParams(layoutParams);
                                        chip.setText(newGetSizeText.get(i));

                                        chip.setClickable(true);
                                        chip.setCheckable(true);

                                        sizeChips.addView(chip);

                                        sizeChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                            @Override
                                            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                                                for (int i = 0; i < group.getChildCount(); i++) {
                                                    Chip chip = (Chip) group.getChildAt(i);
                                                    if (chip.isChecked() && !sizeTextList.contains(chip.getText().toString())) {
                                                        sizeTextList.add(chip.getText().toString());
                                                    } else if (!chip.isChecked() && sizeTextList.contains(chip.getText().toString())){
                                                        sizeTextList.remove(chip.getText().toString());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                                if (model) {
                                    for (int i = 0; i < getModel.size(); i++) {

                                        LinearLayout.LayoutParams layoutParams = new
                                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams.setMargins(5, 5, 5, 5);

                                        Chip chip = new Chip(bottomSheetView.getContext());

                                        chip.setLayoutParams(layoutParams);
                                        chip.setText(getModel.get(i));

                                        chip.setClickable(true);
                                        chip.setCheckable(true);

                                        modelChips.addView(chip);

                                        modelChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                            @Override
                                            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                                                for (int i = 0; i < group.getChildCount(); i++) {
                                                    Chip chip = (Chip) group.getChildAt(i);
                                                    if (chip.isChecked() && !modelList.contains(chip.getText().toString())) {
                                                        modelList.add(chip.getText().toString());
                                                    } else if (!chip.isChecked() && modelList.contains(chip.getText().toString())){
                                                        modelList.remove(chip.getText().toString());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                                if (artist) {
                                    for (int i = 0; i < getArtist.size(); i++) {

                                        LinearLayout.LayoutParams layoutParams = new
                                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams.setMargins(5, 5, 5, 5);

                                        Chip chip = new Chip(bottomSheetView.getContext());

                                        chip.setLayoutParams(layoutParams);
                                        chip.setText(getArtist.get(i));

                                        chip.setClickable(true);
                                        chip.setCheckable(true);

                                        artistChips.addView(chip);

                                        artistChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                            @Override
                                            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                                                for (int i = 0; i < group.getChildCount(); i++) {
                                                    Chip chip = (Chip) group.getChildAt(i);
                                                    if (chip.isChecked() && !artistList.contains(chip.getText().toString())) {
                                                        artistList.add(chip.getText().toString());
                                                    } else if (!chip.isChecked() && artistList.contains(chip.getText().toString())){
                                                        artistList.remove(chip.getText().toString());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                                if (releasedate) {
                                    for (int i = 0; i < getReleaseDate.size(); i++) {

                                        LinearLayout.LayoutParams layoutParams = new
                                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams.setMargins(5, 5, 5, 5);

                                        Chip chip = new Chip(bottomSheetView.getContext());

                                        chip.setLayoutParams(layoutParams);
                                        chip.setText(getReleaseDate.get(i));

                                        chip.setClickable(true);
                                        chip.setCheckable(true);

                                        releaseDateChips.addView(chip);

                                        releaseDateChips.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                                            @Override
                                            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                                                for (int i = 0; i < group.getChildCount(); i++) {
                                                    Chip chip = (Chip) group.getChildAt(i);
                                                    if (chip.isChecked() && !releaseDateList.contains(chip.getText().toString())) {
                                                        releaseDateList.add(chip.getText().toString());
                                                    } else if (!chip.isChecked() && releaseDateList.contains(chip.getText().toString())){
                                                        releaseDateList.remove(chip.getText().toString());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                bottomSheetView.findViewById(R.id.applyBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        getWidth = 0;
                        getHeight = 0;
                        getLength = 0;

                        if (width && !widthValue.getText().toString().isEmpty()) {
                            getWidth = Float.parseFloat(widthValue.getText().toString());
                        }

                        if (length && !lengthValue.getText().toString().isEmpty()) {
                            getLength = Float.parseFloat(lengthValue.getText().toString());
                        }

                        if (height && !heightValue.getText().toString().isEmpty()) {
                            getHeight = Float.parseFloat(heightValue.getText().toString());
                        }

                        getFilterItems(getWidth, getLength, getHeight, getAge, sizeNum.getValue(), bottomSheetDialog, typeList, brandList, colorList, sizeTextList, materialList, modelList, artistList, releaseDateList);
                    }
                });

                getModel.clear();
                getArtist.clear();
                getReleaseDate.clear();
                getSizeNum.clear();
                getSizeText.clear();
                getMaterial.clear();
                getType.clear();
                getBrand.clear();
                getColor.clear();

            }
        });

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
                intent.putExtra("from", "Swipe");
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

    private void getFilterItems(float getWidth, float getLength, float getHeight, String getAge, float value, BottomSheetDialog bottomSheetDialog, ArrayList<String> typeList, ArrayList<String> brandList, ArrayList<String> colorList, ArrayList<String> sizeTextList, ArrayList<String> materialList, ArrayList<String> modelList, ArrayList<String> artistList, ArrayList<String> releaseDateList) {

        SweetAlertDialog pDialog = new SweetAlertDialog(ItemSwipe.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        rowItems.clear();
        arrayAdapter.notifyDataSetChanged();
        noMoreItemBanner.setVisibility(View.GONE);

        if (brandList.contains("UNBRANDED")) {

            brandList.add("N/A");
            brandList.add("n/a");

        }

        ArrayList<String> keysList = (ArrayList<String>) getIntent().getSerializableExtra("keys");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("user-rating").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {

                        switch (category) {
                            case "Apparel":

                                for (int i = 0; i < keysList.size(); i++) {

                                    concatinated = keysList.get(i);
                                    uid = concatinated.substring(0, concatinated.indexOf('-'));
                                    itemName = concatinated.substring((concatinated.indexOf("-") + 1), concatinated.length());

                                    if ((snapshot.child(uid).getKey().equals(uid)) && (snapshot.child(uid).child(itemName).getKey().equals(itemName))) {

                                        if (isNumber(snapshot.child(uid).child(itemName).child("Item_Sizes").getValue(String.class))) {

                                            if ((typeList.contains(snapshot.child(uid).child(itemName).child("Item_Type").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || typeList.size() == 0) &&
                                                    (brandList.contains(snapshot.child(uid).child(itemName).child("Item_Brand").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || brandList.size() == 0) &&
                                                    (colorList.contains(snapshot.child(uid).child(itemName).child("Item_Color").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || colorList.size() == 0) &&
                                                    (materialList.contains(snapshot.child(uid).child(itemName).child("Item_Material").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || materialList.size() == 0) &&
                                                    Float.parseFloat(snapshot.child(uid).child(itemName).child("Item_Sizes").getValue(String.class)) <= value) {

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

                                        } else {

                                            ArrayList<String> newSizeText = reformatSize(sizeTextList);

                                            if (newSizeText.size() != 0) {

                                                for (int j = 0; j < newSizeText.size(); j++) {

                                                    if ((typeList.contains(snapshot.child(uid).child(itemName).child("Item_Type").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || typeList.size() == 0) &&
                                                            (brandList.contains(snapshot.child(uid).child(itemName).child("Item_Brand").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || brandList.size() == 0) &&
                                                            (colorList.contains(snapshot.child(uid).child(itemName).child("Item_Color").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || colorList.size() == 0) &&
                                                            (materialList.contains(snapshot.child(uid).child(itemName).child("Item_Material").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || materialList.size() == 0) &&
                                                            (sizeTextList.contains(newSizeText.get(j)) || sizeTextList.size() == 0)) {

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
                                                }
                                            } else {

                                                if ((typeList.contains(snapshot.child(uid).child(itemName).child("Item_Type").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || typeList.size() == 0) &&
                                                        (brandList.contains(snapshot.child(uid).child(itemName).child("Item_Brand").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || brandList.size() == 0) &&
                                                        (colorList.contains(snapshot.child(uid).child(itemName).child("Item_Color").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || colorList.size() == 0) &&
                                                        (materialList.contains(snapshot.child(uid).child(itemName).child("Item_Material").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || materialList.size() == 0)) {


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
                                                            keysList, snapshot1.child(uid).child("Average_Rating").getValue(String.class)
                                                    );

                                                    rowItems.add(item);
                                                    arrayAdapter.notifyDataSetChanged();

                                                }
                                            }
                                        }
                                    }

                                    ArrayList<cards> newItems = new ArrayList<>();

                                    for (int counter = 0; counter < rowItems.size(); counter++) {
                                        if ((rowItems.get(counter).getPoster_UID() + "-" + rowItems.get(counter).getItem_Name()).equals(concatinated)) {
                                            newItems.add(rowItems.get(counter));
                                        }
                                    }

                                    rowItems.removeAll(newItems);

                                    if (newItems.size() > 1) {
                                        Log.d(TAG, newItems.size() + "");
                                        for (int itemSize = 0; itemSize < newItems.size(); itemSize++) {
                                            newItems.remove(itemSize);
                                        }
                                    }

                                    rowItems.addAll(newItems);
                                    arrayAdapter.notifyDataSetChanged();

                                }

                                break;
                            case "GadgetsBagsAppliancesSchool":

                                for (int i = 0; i < keysList.size(); i++) {

                                    concatinated = keysList.get(i);
                                    uid = concatinated.substring(0, concatinated.indexOf('-'));
                                    itemName = concatinated.substring((concatinated.indexOf("-") + 1), concatinated.length());

                                    if ((snapshot.child(uid).getKey().equals(uid)) && (snapshot.child(uid).child(itemName).getKey().equals(itemName))) {

                                        if ((typeList.contains(snapshot.child(uid).child(itemName).child("Item_Type").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || typeList.size() == 0) &&
                                                (brandList.contains(snapshot.child(uid).child(itemName).child("Item_Brand").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || brandList.size() == 0) &&
                                                (colorList.contains(snapshot.child(uid).child(itemName).child("Item_Color").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || colorList.size() == 0)) {

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
                                    }

                                    ArrayList<cards> newItems = new ArrayList<>();

                                    for (int counter = 0; counter < rowItems.size(); counter++) {
                                        if ((rowItems.get(counter).getPoster_UID() + "-" + rowItems.get(counter).getItem_Name()).equals(concatinated)) {
                                            newItems.add(rowItems.get(counter));
                                        }
                                    }

                                    rowItems.removeAll(newItems);

                                    if (newItems.size() > 1) {
                                        Log.d(TAG, newItems.size() + "");
                                        for (int itemSize = 0; itemSize < newItems.size(); itemSize++) {
                                            newItems.remove(itemSize);
                                        }
                                    }

                                    rowItems.addAll(newItems);
                                    arrayAdapter.notifyDataSetChanged();

                                }

                                break;
                            case "Game":

                                for (int i = 0; i < keysList.size(); i++) {

                                    concatinated = keysList.get(i);
                                    uid = concatinated.substring(0, concatinated.indexOf('-'));
                                    itemName = concatinated.substring((concatinated.indexOf("-") + 1), concatinated.length());

                                    if ((snapshot.child(uid).getKey().equals(uid)) && (snapshot.child(uid).child(itemName).getKey().equals(itemName))) {

                                        if ((typeList.contains(snapshot.child(uid).child(itemName).child("Item_Type").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || typeList.size() == 0) &&
                                            (brandList.contains(snapshot.child(uid).child(itemName).child("Item_Brand").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || brandList.size() == 0)) {

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
                                    }

                                    ArrayList<cards> newItems = new ArrayList<>();

                                    for (int counter = 0; counter < rowItems.size(); counter++) {
                                        if ((rowItems.get(counter).getPoster_UID() + "-" + rowItems.get(counter).getItem_Name()).equals(concatinated)) {
                                            newItems.add(rowItems.get(counter));
                                        }
                                    }

                                    rowItems.removeAll(newItems);

                                    if (newItems.size() > 1) {
                                        Log.d(TAG, newItems.size() + "");
                                        for (int itemSize = 0; itemSize < newItems.size(); itemSize++) {
                                            newItems.remove(itemSize);
                                        }
                                    }

                                    rowItems.addAll(newItems);
                                    arrayAdapter.notifyDataSetChanged();

                                }

                                break;
                            case "Furniture":

                                for (int i = 0; i < keysList.size(); i++) {

                                    concatinated = keysList.get(i);
                                    uid = concatinated.substring(0, concatinated.indexOf('-'));
                                    itemName = concatinated.substring((concatinated.indexOf("-") + 1), concatinated.length());

                                    float newWidth =  Float.parseFloat(snapshot.child(uid).child(itemName).child("Item_Width").getValue(String.class));
                                    float newLength = Float.parseFloat(snapshot.child(uid).child(itemName).child("Item_Length").getValue(String.class));
                                    float newHeight = Float.parseFloat(snapshot.child(uid).child(itemName).child("Item_Height").getValue(String.class));

                                    if ((snapshot.child(uid).getKey().equals(uid)) && (snapshot.child(uid).child(itemName).getKey().equals(itemName))) {

                                        if ((brandList.contains(snapshot.child(uid).child(itemName).child("Item_Brand").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || brandList.size() == 0) &&
                                            (colorList.contains(snapshot.child(uid).child(itemName).child("Item_Color").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || colorList.size() == 0) &&
                                            (getWidth == 0 || newWidth <= getWidth) &&
                                            (getLength == 0 || newLength <= getLength) &&
                                            (getHeight == 0 || newHeight <= getHeight)) {

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
                                    }

                                    ArrayList<cards> newItems = new ArrayList<>();

                                    for (int counter = 0; counter < rowItems.size(); counter++) {
                                        if ((rowItems.get(counter).getPoster_UID() + "-" + rowItems.get(counter).getItem_Name()).equals(concatinated)) {
                                            newItems.add(rowItems.get(counter));
                                        }
                                    }

                                    rowItems.removeAll(newItems);

                                    if (newItems.size() > 1) {
                                        Log.d(TAG, newItems.size() + "");
                                        for (int itemSize = 0; itemSize < newItems.size(); itemSize++) {
                                            newItems.remove(itemSize);
                                        }
                                    }

                                    rowItems.addAll(newItems);
                                    arrayAdapter.notifyDataSetChanged();

                                }

                                break;
                            case "Babies & Kids":

                                for (int i = 0; i < keysList.size(); i++) {

                                    concatinated = keysList.get(i);
                                    uid = concatinated.substring(0, concatinated.indexOf('-'));
                                    itemName = concatinated.substring((concatinated.indexOf("-") + 1), concatinated.length());

                                    if (getAge != null) {

                                        int newInt = Integer.parseInt(snapshot.child(uid).child(itemName).child("Item_Age").getValue(String.class).toUpperCase(Locale.ROOT).trim());

                                        if (getAge.equals("0-5")) {

                                            if ((snapshot.child(uid).getKey().equals(uid)) && (snapshot.child(uid).child(itemName).getKey().equals(itemName))) {

                                                if ((typeList.contains(snapshot.child(uid).child(itemName).child("Item_Type").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || typeList.size() == 0) &&
                                                        (brandList.contains(snapshot.child(uid).child(itemName).child("Item_Brand").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || brandList.size() == 0) &&
                                                        (newInt >= 0 && newInt <= 5)) {

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
                                            }
                                        } else {

                                            if ((snapshot.child(uid).getKey().equals(uid)) && (snapshot.child(uid).child(itemName).getKey().equals(itemName))) {

                                                if ((typeList.contains(snapshot.child(uid).child(itemName).child("Item_Type").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || typeList.size() == 0) &&
                                                        (brandList.contains(snapshot.child(uid).child(itemName).child("Item_Brand").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || brandList.size() == 0) &&
                                                        (newInt >= 5 && newInt <= 10)) {

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
                                            }
                                        }
                                    } else {
                                        if ((typeList.contains(snapshot.child(uid).child(itemName).child("Item_Type").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || typeList.size() == 0) &&
                                            (brandList.contains(snapshot.child(uid).child(itemName).child("Item_Brand").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || brandList.size() == 0)) {

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
                                    }

                                    ArrayList<cards> newItems = new ArrayList<>();

                                    for (int counter = 0; counter < rowItems.size(); counter++) {
                                        if ((rowItems.get(counter).getPoster_UID() + "-" + rowItems.get(counter).getItem_Name()).equals(concatinated)) {
                                            newItems.add(rowItems.get(counter));
                                        }
                                    }

                                    rowItems.removeAll(newItems);

                                    if (newItems.size() > 1) {
                                        Log.d(TAG, newItems.size() + "");
                                        for (int itemSize = 0; itemSize < newItems.size(); itemSize++) {
                                            newItems.remove(itemSize);
                                        }
                                    }

                                    rowItems.addAll(newItems);
                                    arrayAdapter.notifyDataSetChanged();

                                }

                                break;
                            case "Motors":

                                for (int i = 0; i < keysList.size(); i++) {

                                    concatinated = keysList.get(i);
                                    uid = concatinated.substring(0, concatinated.indexOf('-'));
                                    itemName = concatinated.substring((concatinated.indexOf("-") + 1), concatinated.length());

                                    if ((snapshot.child(uid).getKey().equals(uid)) && (snapshot.child(uid).child(itemName).getKey().equals(itemName))) {

                                        if ((modelList.contains(snapshot.child(uid).child(itemName).child("Item_Model").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || modelList.size() == 0) &&
                                                (brandList.contains(snapshot.child(uid).child(itemName).child("Item_Brand").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || brandList.size() == 0) &&
                                                (colorList.contains(snapshot.child(uid).child(itemName).child("Item_Color").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || colorList.size() == 0)) {

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
                                    }

                                    ArrayList<cards> newItems = new ArrayList<>();

                                    for (int counter = 0; counter < rowItems.size(); counter++) {
                                        if ((rowItems.get(counter).getPoster_UID() + "-" + rowItems.get(counter).getItem_Name()).equals(concatinated)) {
                                            newItems.add(rowItems.get(counter));
                                        }
                                    }

                                    rowItems.removeAll(newItems);

                                    if (newItems.size() > 1) {
                                        Log.d(TAG, newItems.size() + "");
                                        for (int itemSize = 0; itemSize < newItems.size(); itemSize++) {
                                            newItems.remove(itemSize);
                                        }
                                    }

                                    rowItems.addAll(newItems);
                                    arrayAdapter.notifyDataSetChanged();

                                }

                                break;
                            case "Audio":

                                for (int i = 0; i < keysList.size(); i++) {

                                    concatinated = keysList.get(i);
                                    uid = concatinated.substring(0, concatinated.indexOf('-'));
                                    itemName = concatinated.substring((concatinated.indexOf("-") + 1), concatinated.length());

                                    if ((snapshot.child(uid).getKey().equals(uid)) && (snapshot.child(uid).child(itemName).getKey().equals(itemName))) {

                                        if ((artistList.contains(snapshot.child(uid).child(itemName).child("Item_Artist").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || artistList.size() == 0) &&
                                            (releaseDateList.contains(snapshot.child(uid).child(itemName).child("Item_ReleaseDate").getValue(String.class).toUpperCase(Locale.ROOT).trim()) || releaseDateList.size() == 0)) {

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
                                    }

                                    ArrayList<cards> newItems = new ArrayList<>();

                                    for (int counter = 0; counter < rowItems.size(); counter++) {
                                        if ((rowItems.get(counter).getPoster_UID() + "-" + rowItems.get(counter).getItem_Name()).equals(concatinated)) {
                                            newItems.add(rowItems.get(counter));
                                        }
                                    }

                                    rowItems.removeAll(newItems);

                                    if (newItems.size() > 1) {
                                        Log.d(TAG, newItems.size() + "");
                                        for (int itemSize = 0; itemSize < newItems.size(); itemSize++) {
                                            newItems.remove(itemSize);
                                        }
                                    }

                                    rowItems.addAll(newItems);
                                    arrayAdapter.notifyDataSetChanged();

                                }

                                break;
                        }

                        bottomSheetDialog.dismiss();
                        pDialog.dismiss();

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

    private ArrayList<String> upperCase(ArrayList<String> getMaterial) {
        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < getMaterial.size(); i++) {
            arrayList.add(getMaterial.get(i).toUpperCase(Locale.ROOT).trim());
        }

        return arrayList;
    }

    public boolean isNumber(String str) {
        return str.matches("-?\\d+");
    }

    public Integer maxNum(ArrayList arrayList) {
        int max = 0;

        for (int i = 0; i < arrayList.size(); i++) {
            if (Integer.parseInt(arrayList.get(i).toString()) > max) {
                max = Integer.parseInt(arrayList.get(i).toString());
            }
        }

        return max;
    }

    public Integer minNum(ArrayList arrayList) {
        int min = 0;

        for (int i = 0; i < arrayList.size(); i++) {
            if (Integer.parseInt(arrayList.get(i).toString()) < min) {
                min = Integer.parseInt(arrayList.get(i).toString());
            }
        }

        return min;
    }

    public void sort(ArrayList arrayList) {

        Set<String> set = new HashSet<>(arrayList);
        arrayList.clear();
        arrayList.addAll(set);

    }

    public void sortAndUpper(ArrayList arrayList) {

        Set<String> set = new HashSet<>(upperCase(arrayList));
        arrayList.clear();
        arrayList.addAll(set);

    }

    public ArrayList<String> reformatSize(ArrayList<String> arrayList) {
        ArrayList<String> newGetSizeText = new ArrayList<>();
        String newStr;

        for (int i = 0; i < arrayList.size(); i++) {
            newStr = arrayList.get(i).replace("[", "");
            newStr = newStr.replace("]", "");
            int counter = i;
            if (newStr.contains(",")) {
                String[] sArr = newStr.split(",");

                for (int j = 0; j < sArr.length; j++) {
                    sArr[j] = sArr[j].trim();
                }

                List<String> sList = Arrays.asList(sArr);

                newGetSizeText.addAll(sList);
            } else {
                newGetSizeText.add(newStr);
            }
        }

        return newGetSizeText;
    }


}