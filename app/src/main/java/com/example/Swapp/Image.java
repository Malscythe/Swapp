package com.example.Swapp;

import android.net.Uri;

public class Image {

    private Uri image;

    public Image(Uri image) {
        this.image = image;
    }

    public Uri getImage() {
        return image;
    }
}
