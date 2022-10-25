package com.example.Swapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Swapp.call.fcm.FcmNotificationsSender;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class ItemApprovalAdapter extends RecyclerView.Adapter {

    private static final String TAG = "TAG";
    List<ItemApprovalFetch> itemApprovalFetchList;

    public ItemApprovalAdapter(List<ItemApprovalFetch> itemApprovalFetchList) {
        this.itemApprovalFetchList = itemApprovalFetchList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_waiting_approval_layout, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;

        final ItemApprovalFetch itemApprovalFetch = itemApprovalFetchList.get(position);

        viewHolderClass.userID.setText(itemApprovalFetch.getPoster_UID());
        viewHolderClass.posterName.setText(itemApprovalFetch.getItem_PosterName());
        viewHolderClass.location.setText(itemApprovalFetch.getItem_Location());
        viewHolderClass.itemName.setText(itemApprovalFetch.getItem_Name());

        Glide.with(viewHolderClass.userID.getContext()).load(itemApprovalFetch.getItem_Image()).into(viewHolderClass.itemImage);

        viewHolderClass.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewHolderClass.userID.getContext(), MoreInfo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("poster_uid", itemApprovalFetch.getPoster_UID());
                intent.putExtra("item_name", itemApprovalFetch.getItem_Name());
                intent.putExtra("from", "Admin");
                viewHolderClass.userID.getContext().startActivity(intent);
                CustomIntent.customType(viewHolderClass.userID.getContext(), "left-to-right");
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Swapp_ID", "Swapp_Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = viewHolderClass.userID.getContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        viewHolderClass.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("tokens").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(itemApprovalFetch.getPoster_UID()).exists()) {
                            String token = snapshot.child(itemApprovalFetch.getPoster_UID()).child("User_Token").getValue(String.class);
                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,
                                    "Item Validation",
                                    "Your item " + itemApprovalFetch.getItem_Name() + " has been approved.",
                                    viewHolderClass.userID.getContext(), itemApprovalFetch.getPassedActivity());
                            notificationsSender.SendNotifications();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        viewHolderClass.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("tokens").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(itemApprovalFetch.getPoster_UID()).exists()) {
                            String token = snapshot.child(itemApprovalFetch.getPoster_UID()).child("User_Token").getValue(String.class);
                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,
                                    "Item Validation",
                                    "Your item " + itemApprovalFetch.getItem_Name() + " has been rejected.",
                                    viewHolderClass.userID.getContext(), itemApprovalFetch.getPassedActivity());
                            notificationsSender.SendNotifications();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemApprovalFetchList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView userID, posterName, location, itemName;
        ImageView itemImage;
        RelativeLayout rootLayout;
        Button accept, reject;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            accept = itemView.findViewById(R.id.acceptBtn);
            reject = itemView.findViewById(R.id.rejectBtn);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            userID = itemView.findViewById(R.id.posterUID);
            posterName = itemView.findViewById(R.id.posterName);
            location = itemView.findViewById(R.id.postedLocation);
            itemName = itemView.findViewById(R.id.postedItemName);
            itemImage = itemView.findViewById(R.id.postedImage);
        }
    }
}
