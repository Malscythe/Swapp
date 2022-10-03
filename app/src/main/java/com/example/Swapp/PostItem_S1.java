package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.kofigyan.stateprogressbar.StateProgressBar;

import Swapp.R;
import Swapp.databinding.ActivityPostItemS1Binding;
import maes.tech.intentanim.CustomIntent;

public class PostItem_S1 extends AppCompatActivity {

    private ActivityPostItemS1Binding binding;
    public static final String TAG = "TAG";
    String[] descriptionData = {"Details", "Description", "Location", "Images"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostItemS1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StateProgressBar stateProgressBar = findViewById(R.id.stateProgress);
        stateProgressBar.setStateDescriptionData(descriptionData);

        String[] categoriesArr = getResources().getStringArray(R.array.categories);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item, categoriesArr);
        binding.itemCategory.setAdapter(arrayAdapter);

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFields();
            }
        });
    }

    private void checkFields() {

        if (TextUtils.isEmpty(binding.itemName.getText().toString())) {
            binding.itemNameL.setError("This cannot be empty.");
            binding.itemNameL.setErrorIconDrawable(null);
            return;
        } else {
            binding.itemNameL.setError(null);
            binding.itemNameL.setErrorIconDrawable(null);
        }

        if (TextUtils.isEmpty(binding.itemCategory.getText().toString())) {
            binding.itemCategoryL.setError("This cannot be empty.");
            binding.itemCategoryL.setErrorIconDrawable(null);
            return;
        } else {
            binding.itemCategoryL.setError(null);
            binding.itemCategoryL.setErrorIconDrawable(null);
        }

        if (TextUtils.isEmpty(binding.itemPref.getText().toString())) {
            binding.itemPrefL.setError("This cannot be empty.");
            binding.itemPrefL.setErrorIconDrawable(null);
            return;
        } else {
            binding.itemPrefL.setError(null);
            binding.itemPrefL.setErrorIconDrawable(null);
        }

        if (TextUtils.isEmpty(binding.itemRFT.getText().toString())) {
            binding.itemRFTL.setError("This cannot be empty.");
            binding.itemRFTL.setErrorIconDrawable(null);
            return;
        } else {
            binding.itemRFTL.setError(null);
            binding.itemRFTL.setErrorIconDrawable(null);
        }

        Intent intent = new Intent(PostItem_S1.this, PostItem_S2.class);
        intent.putExtra("category", binding.itemCategory.getText().toString());
        intent.putExtra("item_name", binding.itemName.getText().toString());
        intent.putExtra("rft", binding.itemRFT.getText().toString());
        intent.putExtra("pref_item", binding.itemPref.getText().toString());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PostItem_S1.this, Categories.class));
        CustomIntent.customType(PostItem_S1.this, "right-to-left");
        finish();
    }
}