package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class PostItem_S4 extends AppCompatActivity implements BottomSheetImagePicker.OnImagesSelectedListener {

    Button uploadBtn;
    ImageSlider imageSlider;
    List<SlideModel> slideModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item_s4);

        uploadBtn = findViewById(R.id.insertImages);

        imageSlider = findViewById(R.id.imagePreview);

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

        imageSlider = findViewById(R.id.imagePreview);

        for (Uri uri : list) {
            slideModelList.add(new SlideModel(String.valueOf(uri), null));
        }

        imageSlider.setImageList(slideModelList, null);

        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {

                Intent intent = new Intent(PostItem_S4.this, imageFullScreen.class);
                intent.putExtra("imageUrl", slideModelList.get(i).getImageUrl());
                startActivity(intent);

            }
        });
    }
}