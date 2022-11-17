package com.example.Swapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import Swapp.R;

public class PostedItemsAdapter extends RecyclerView.Adapter {

    private static final String TAG = "TAG";
    List<PostedItemsFetch> postedItemsFetchList;

    public PostedItemsAdapter(List<PostedItemsFetch> postedItemsFetchList) {
        this.postedItemsFetchList = postedItemsFetchList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_posted_items_layout, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;

        final PostedItemsFetch postedItemsFetch = postedItemsFetchList.get(position);

        viewHolderClass.userID.setText(postedItemsFetch.getPoster_UID());
        viewHolderClass.posterName.setText(postedItemsFetch.getItem_PosterName());
        viewHolderClass.location.setText(postedItemsFetch.getItem_Location());
        viewHolderClass.itemName.setText(postedItemsFetch.getItem_Name());
        viewHolderClass.datePosted.setText(postedItemsFetch.getDate_Posted());

        Glide.with(viewHolderClass.userID.getContext()).load(postedItemsFetch.getItem_Image()).into(viewHolderClass.itemImage);
    }

    @Override
    public int getItemCount() {
        return postedItemsFetchList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView userID, posterName, location, itemName, datePosted;
        ImageView itemImage;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            datePosted = itemView.findViewById(R.id.datePosted);
            userID = itemView.findViewById(R.id.posterUID);
            posterName = itemView.findViewById(R.id.posterName);
            location = itemView.findViewById(R.id.postedLocation);
            itemName = itemView.findViewById(R.id.postedItemName);
            itemImage = itemView.findViewById(R.id.postedImage);
        }
    }
}
