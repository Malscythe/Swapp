package com.example.Swapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Swapp.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.item_info_layout, null);
    }

    private void itemInfo(Marker marker, View view) {

        TextView item_name = view.findViewById(R.id.item_name);
        TextView item_poster = view.findViewById(R.id.item_poster);

        item_name.setText(marker.getTitle());
        item_poster.setText(marker.getSnippet());
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        itemInfo(marker, mWindow);
        return mWindow;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        itemInfo(marker, mWindow);
        return mWindow;
    }
}
