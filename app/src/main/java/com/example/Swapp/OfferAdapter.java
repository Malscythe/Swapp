package com.example.Swapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import Swapp.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class OfferAdapter extends RecyclerView.Adapter {

    private static final String TAG = "TAG";
    List<OfferFetch> offerFetchList;

    public OfferAdapter(List<OfferFetch> offerFetchList) {
        this.offerFetchList = offerFetchList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offeritem, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;
        final OfferFetch offerFetch = offerFetchList.get(position);
        viewHolderClass.textView.setText(offerFetch.getItem_Name());
        viewHolderClass.itemlocation.setText(offerFetch.getItem_Location());
        String itemID = offerFetch.getItem_Name().concat("-" + offerFetch.getPoster_UID());
        Glide.with(viewHolderClass.img.getContext())
                .load(offerFetch.getImage_Url())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(com.google.firebase.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(viewHolderClass.img);

        viewHolderClass.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OfferSecondActivity.class);
                intent.putExtra("itemid", itemID);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return offerFetchList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView textView, itemlocation;
        CircleImageView img;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.posteditemname);
            img = itemView.findViewById(R.id.postedpic);
            itemlocation = itemView.findViewById(R.id.postedlocation);
        }
    }
}
