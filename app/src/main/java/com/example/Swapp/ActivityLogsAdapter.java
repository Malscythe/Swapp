package com.example.Swapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class ActivityLogsAdapter extends RecyclerView.Adapter {

    private static final String TAG = "TAG";
    List<ActivityLogsFetch> activityLogsFetchList;

    public ActivityLogsAdapter(List<ActivityLogsFetch> activityLogsFetchList) {
        this.activityLogsFetchList = activityLogsFetchList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_logs_layout, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;

        final ActivityLogsFetch activityLogsFetch = activityLogsFetchList.get(position);

        viewHolderClass.userID.setText(activityLogsFetch.getUser_ID());
        viewHolderClass.activity.setText(activityLogsFetch.getActivity());
        viewHolderClass.date.setText(activityLogsFetch.getDate());
    }

    @Override
    public int getItemCount() {
        return activityLogsFetchList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView userID, activity, date;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            userID = itemView.findViewById(R.id.userID);
            activity = itemView.findViewById(R.id.activity);
            date = itemView.findViewById(R.id.date);
        }
    }
}
