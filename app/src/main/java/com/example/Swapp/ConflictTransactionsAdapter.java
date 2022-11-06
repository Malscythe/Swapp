package com.example.Swapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Swapp.call.fcm.FcmNotificationsSender;
import com.example.Swapp.chat.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Swapp.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import maes.tech.intentanim.CustomIntent;

public class ConflictTransactionsAdapter extends RecyclerView.Adapter {

    private static final String TAG = "TAG";
    List<ConflictTransactionsFetch> conflictTransactionsFetchList;

    public ConflictTransactionsAdapter(List<ConflictTransactionsFetch> conflictTransactionsFetchList) {
        this.conflictTransactionsFetchList = conflictTransactionsFetchList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_waiting_validate_layout, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;

        final ConflictTransactionsFetch conflictTransactionsFetch = conflictTransactionsFetchList.get(position);

        viewHolderClass.transactionKey.setText(conflictTransactionsFetch.getTransaction_Key());
        viewHolderClass.transactionMode.setText(conflictTransactionsFetch.getTransaction_Mode());
        viewHolderClass.posterUID.setText(conflictTransactionsFetch.getPoster_UID());
        viewHolderClass.offererUID.setText(conflictTransactionsFetch.getOfferer_UID());
        viewHolderClass.posterResponse.setText(conflictTransactionsFetch.getPoster_Response());
        viewHolderClass.offererResponse.setText(conflictTransactionsFetch.getOfferer_Response());

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String myUid = firebaseAuth.getCurrentUser().getUid();

        viewHolderClass.chatPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialog pDialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();

                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String mobile = snapshot.child("users").child(conflictTransactionsFetch.getPoster_UID()).child("Phone").getValue(String.class);
                        String userName = snapshot.child("users").child(conflictTransactionsFetch.getPoster_UID()).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(conflictTransactionsFetch.getPoster_UID()).child("Last_Name").getValue(String.class));
                        String currentMobile = snapshot.child("users").child(myUid).child("Phone").getValue(String.class);

                        if (snapshot.child("chat").exists()) {
                            String chatKey = null;
                            for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {

                                String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                    chatKey = dataSnapshot.getKey();
                                }
                            }

                            if (chatKey != null) {

                                pDialog.dismiss();

                                Intent intent = new Intent(view.getContext(), Chat.class);
                                intent.putExtra("from", "admin");
                                intent.putExtra("mobile", mobile);
                                intent.putExtra("name", userName);
                                intent.putExtra("chat_key", chatKey);
                                intent.putExtra("userID", conflictTransactionsFetch.getPoster_UID());
                                intent.putExtra("userStatus", snapshot.child("users-status").child(conflictTransactionsFetch.getPoster_UID()).child("Status").getValue(String.class));

                                view.getContext().startActivity(intent);
                                CustomIntent.customType(view.getContext(), "left-to-right");

                            } else {

                                pDialog.dismiss();

                                Intent intent = new Intent(view.getContext(), Chat.class);
                                intent.putExtra("from", "admin");
                                intent.putExtra("mobile", mobile);
                                intent.putExtra("name", userName);
                                intent.putExtra("chat_key", "");
                                intent.putExtra("userID", conflictTransactionsFetch.getPoster_UID());
                                intent.putExtra("userStatus", snapshot.child("users-status").child(conflictTransactionsFetch.getPoster_UID()).child("Status").getValue(String.class));

                                view.getContext().startActivity(intent);
                                CustomIntent.customType(view.getContext(), "left-to-right");

                            }
                        } else {
                            pDialog.dismiss();

                            Intent intent = new Intent(view.getContext(), Chat.class);
                            intent.putExtra("from", "admin");
                            intent.putExtra("mobile", mobile);
                            intent.putExtra("name", userName);
                            intent.putExtra("chat_key", "");
                            intent.putExtra("userID", conflictTransactionsFetch.getPoster_UID());
                            intent.putExtra("userStatus", snapshot.child("users-status").child(conflictTransactionsFetch.getPoster_UID()).child("Status").getValue(String.class));

                            view.getContext().startActivity(intent);
                            CustomIntent.customType(view.getContext(), "left-to-right");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        viewHolderClass.chatOfferer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialog pDialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();

                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String mobile = snapshot.child("users").child(conflictTransactionsFetch.getOfferer_UID()).child("Phone").getValue(String.class);
                        String userName = snapshot.child("users").child(conflictTransactionsFetch.getOfferer_UID()).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(conflictTransactionsFetch.getOfferer_UID()).child("Last_Name").getValue(String.class));
                        String currentMobile = snapshot.child("users").child(myUid).child("Phone").getValue(String.class);

                        if (snapshot.child("chat").exists()) {
                            String chatKey = null;
                            for (DataSnapshot dataSnapshot : snapshot.child("chat").getChildren()) {

                                String user1 = dataSnapshot.child("user_1").getValue(String.class);
                                String user2 = dataSnapshot.child("user_2").getValue(String.class);

                                if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {
                                    chatKey = dataSnapshot.getKey();
                                }
                            }

                            if (chatKey != null) {

                                pDialog.dismiss();

                                Intent intent = new Intent(view.getContext(), Chat.class);
                                intent.putExtra("from", "admin");
                                intent.putExtra("mobile", mobile);
                                intent.putExtra("name", userName);
                                intent.putExtra("chat_key", chatKey);
                                intent.putExtra("userID", conflictTransactionsFetch.getOfferer_UID());
                                intent.putExtra("userStatus", snapshot.child("users-status").child(conflictTransactionsFetch.getOfferer_UID()).child("Status").getValue(String.class));

                                view.getContext().startActivity(intent);
                                CustomIntent.customType(view.getContext(), "left-to-right");

                            } else {

                                pDialog.dismiss();

                                Intent intent = new Intent(view.getContext(), Chat.class);
                                intent.putExtra("from", "admin");
                                intent.putExtra("mobile", mobile);
                                intent.putExtra("name", userName);
                                intent.putExtra("chat_key", "");
                                intent.putExtra("userID", conflictTransactionsFetch.getOfferer_UID());
                                intent.putExtra("userStatus", snapshot.child("users-status").child(conflictTransactionsFetch.getOfferer_UID()).child("Status").getValue(String.class));

                                view.getContext().startActivity(intent);
                                CustomIntent.customType(view.getContext(), "left-to-right");

                            }
                        } else {
                            pDialog.dismiss();

                            Intent intent = new Intent(view.getContext(), Chat.class);
                            intent.putExtra("from", "admin");
                            intent.putExtra("mobile", mobile);
                            intent.putExtra("name", userName);
                            intent.putExtra("chat_key", "");
                            intent.putExtra("userID", conflictTransactionsFetch.getOfferer_UID());
                            intent.putExtra("userStatus", snapshot.child("users-status").child(conflictTransactionsFetch.getOfferer_UID()).child("Status").getValue(String.class));

                            view.getContext().startActivity(intent);
                            CustomIntent.customType(view.getContext(), "left-to-right");
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
        return conflictTransactionsFetchList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView transactionKey, transactionMode, posterUID, offererUID, posterResponse, offererResponse;
        Button chatPoster, chatOfferer;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            transactionKey = itemView.findViewById(R.id.transactionKey);
            transactionMode = itemView.findViewById(R.id.transactionMode);
            posterUID = itemView.findViewById(R.id.posterUID);
            offererUID = itemView.findViewById(R.id.offererUID);
            posterResponse = itemView.findViewById(R.id.posterResponse);
            offererResponse = itemView.findViewById(R.id.offererResponse);

            chatPoster = itemView.findViewById(R.id.chatPoster);
            chatOfferer = itemView.findViewById(R.id.chatOfferer);

        }
    }
}
