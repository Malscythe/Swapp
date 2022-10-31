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

public class ViewItemsAdapter extends RecyclerView.Adapter {

    private static final String TAG = "TAG";
    List<ViewItemsFetch> viewItemsFetchList;

    public ViewItemsAdapter(List<ViewItemsFetch> viewItemsFetchList) {
        this.viewItemsFetchList = viewItemsFetchList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view_items_layout, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;

        final ViewItemsFetch viewItemsFetch = viewItemsFetchList.get(position);

        viewHolderClass.location.setText(viewItemsFetch.getItem_Location());
        viewHolderClass.itemName.setText(viewItemsFetch.getItem_Name());

        Glide.with(viewHolderClass.location.getContext()).load(viewItemsFetch.getItem_Image()).into(viewHolderClass.itemImage);
    }

    @Override
    public int getItemCount() {
        return viewItemsFetchList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView location, itemName;
        ImageView itemImage;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            location = itemView.findViewById(R.id.postedLocation);
            itemName = itemView.findViewById(R.id.postedItemName);
            itemImage = itemView.findViewById(R.id.postedImage);
        }
    }
}
