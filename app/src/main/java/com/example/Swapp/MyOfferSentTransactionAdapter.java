package com.example.Swapp;

import android.content.Intent;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.example.Swapp.chat.Chat;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class MyOfferSentTransactionAdapter extends RecyclerView.Adapter {
    private static final String TAG = "Offer";
    List<OfferFetch> offerFetchList;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    String myPhone;
    String traderPhone;
    String userName;
    String traderID;
    String traderStatus;
    String profile;

    String user1;
    String user2;

    String chatKey = null;

    public MyOfferSentTransactionAdapter(List<OfferFetch> offerFetchList) {
        this.offerFetchList = offerFetchList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_my_offers_sent_transaction_layout, parent, false);
        MyOfferSentTransactionAdapter.ViewHolderClass viewHolderClass = new MyOfferSentTransactionAdapter.ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        MyOfferSentTransactionAdapter.ViewHolderClass viewHolderClass = (MyOfferSentTransactionAdapter.ViewHolderClass) holder;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(viewHolderClass.textView.getContext(), R.style.BottomSheetDialogTheme);

        View bottomSheetView = LayoutInflater.from(viewHolderClass.textView.getContext()).inflate(R.layout.bottom_sheet_layout_offered, null);

        Button viewPosted = bottomSheetView.findViewById(R.id.postedItem);
        Button viewOffered = bottomSheetView.findViewById(R.id.offeredItem);
        Button goToChat = bottomSheetView.findViewById(R.id.goToChat);
        Button getDirection = bottomSheetView.findViewById(R.id.getDirection);
        Button goToReview = bottomSheetView.findViewById(R.id.goToReview);

        final OfferFetch offerFetch = offerFetchList.get(position);
        viewHolderClass.textView.setText(offerFetch.getItem_Name());
        viewHolderClass.itemlocation.setText(offerFetch.getItem_Location());
        Glide.with(viewHolderClass.img.getContext())
                .load(offerFetch.getImage_Url())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .error(com.google.firebase.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(viewHolderClass.img);

        databaseReference.child("trade-transactions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("Poster_Response").exists()) {

                        if (dataSnapshot.child("Offered_Item").child("Poster_UID").getValue(String.class).equals(offerFetch.getPoster_UID()) &&
                                dataSnapshot.child("Offered_Item").child("Item_Name").getValue(String.class).equals(offerFetch.getItem_Name()) &&
                                dataSnapshot.child("Transaction_Status").getValue(String.class).equals("Waiting for review")) {

                            goToReview.setVisibility(View.VISIBLE);
                            viewHolderClass.transactionStatus.setText("Waiting for your review");

                        } else if (dataSnapshot.child("Offered_Item").child("Poster_UID").getValue(String.class).equals(offerFetch.getPoster_UID()) &&
                                dataSnapshot.child("Offered_Item").child("Item_Name").getValue(String.class).equals(offerFetch.getItem_Name()) &&
                                dataSnapshot.child("Transaction_Status").getValue(String.class).equals("Waiting for validation")) {

                            goToReview.setVisibility(View.GONE);
                            viewHolderClass.transactionStatus.setText("Waiting for poster review");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewHolderClass.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.child("items").getChildren()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                if (dataSnapshot1.child("Accepted_Offers").hasChild(offerFetch.getPoster_UID())) {
                                    if (dataSnapshot1.child("Accepted_Offers").child(offerFetch.getPoster_UID()).child("Item_Name").getValue(String.class).equals(offerFetch.getItem_Name())) {
                                        viewOffered.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                Intent intent = new Intent(view.getContext(), TransactionMoreInfo.class);
                                                intent.putExtra("itemName", offerFetch.getItem_Name());
                                                intent.putExtra("userID", offerFetch.getPoster_UID());
                                                intent.putExtra("parentKey", dataSnapshot.getKey());
                                                intent.putExtra("parentItemName", dataSnapshot1.getKey());
                                                intent.putExtra("view", "Offered");
                                                view.getContext().startActivity(intent);
                                                CustomIntent.customType(view.getContext(), "left-to-right");
                                            }
                                        });

                                        viewPosted.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                Intent intent = new Intent(view.getContext(), TransactionMoreInfo.class);
                                                intent.putExtra("itemName", offerFetch.getItem_Name());
                                                intent.putExtra("userID", offerFetch.getPoster_UID());
                                                intent.putExtra("parentKey", dataSnapshot.getKey());
                                                intent.putExtra("parentItemName", dataSnapshot1.getKey());
                                                intent.putExtra("view", "Posted");
                                                view.getContext().startActivity(intent);
                                                CustomIntent.customType(view.getContext(), "left-to-right");
                                            }
                                        });

                                        getDirection.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(v.getContext(), currentLoc.class);
                                                intent.putExtra("from", "getDirection");
                                                intent.putExtra("category", "getDirection");
                                                intent.putExtra("latitude", dataSnapshot1.child("Address").child("Latitude").getValue(String.class));
                                                intent.putExtra("longitude", dataSnapshot1.child("Address").child("Longitude").getValue(String.class));
                                                v.getContext().startActivity(intent);
                                                CustomIntent.customType(v.getContext(), "left-to-right");
                                            }
                                        });

                                        goToChat.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (snapshot.child("chat").exists()) {

                                                    for (DataSnapshot dataSnapshot2 : snapshot.child("chat").getChildren()) {
                                                        user1 = dataSnapshot2.child("user_1").getValue(String.class);
                                                        user2 = dataSnapshot2.child("user_2").getValue(String.class);

                                                        if (!offerFetch.getPoster_UID().equals(uid)) {
                                                            myPhone = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                            userName = snapshot.child("users").child(offerFetch.getPoster_UID()).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(offerFetch.getPoster_UID()).child("Last_Name").getValue(String.class));
                                                            traderPhone = snapshot.child("users").child(offerFetch.getPoster_UID()).child("Phone").getValue(String.class);
                                                            traderID = offerFetch.getPoster_UID();
                                                            traderStatus = snapshot.child("users-status").child(offerFetch.getPoster_UID()).child("Status").getValue(String.class);
                                                            profile = snapshot.child("users").child(offerFetch.getPoster_UID()).child("User_Profile").getValue(String.class);
                                                        } else {
                                                            myPhone = snapshot.child("users").child(offerFetch.getPoster_UID()).child("Phone").getValue(String.class);
                                                            userName = snapshot.child("users").child(dataSnapshot.getKey()).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(dataSnapshot.getKey()).child("Last_Name").getValue(String.class));
                                                            traderPhone = snapshot.child("users").child(dataSnapshot.getKey()).child("Phone").getValue(String.class);
                                                            traderID = dataSnapshot.getKey();
                                                            traderStatus = snapshot.child("users-status").child(dataSnapshot.getKey()).child("Status").getValue(String.class);
                                                            profile = snapshot.child("users").child(dataSnapshot.getKey()).child("User_Profile").getValue(String.class);
                                                        }

                                                        if (((user1.equals(myPhone) || user2.equals(myPhone)) && ((user1.equals(traderPhone) || user2.equals(traderPhone)))) && (!myPhone.equals(traderPhone))) {
                                                            chatKey = dataSnapshot2.getKey();
                                                        }
                                                    }
                                                }

                                                if (snapshot.child("chat").exists()) {

                                                    if (chatKey != null) {

                                                        Log.d("CHAT", chatKey);
                                                        Intent intent = new Intent(v.getContext(), Chat.class);
                                                        intent.putExtra("mobile", traderPhone);
                                                        intent.putExtra("name", userName);
                                                        intent.putExtra("chat_key", chatKey);
                                                        intent.putExtra("profile_pic", profile);
                                                        intent.putExtra("userID", traderID);
                                                        intent.putExtra("from", "my_offers");
                                                        intent.putExtra("userStatus", traderStatus);

                                                        v.getContext().startActivity(intent);
                                                        CustomIntent.customType(v.getContext(), "left-to-right");

                                                    } else {

                                                        Log.d("CHAT", "ELSE");
                                                        Intent intent = new Intent(v.getContext(), Chat.class);
                                                        intent.putExtra("mobile", traderPhone);
                                                        intent.putExtra("name", userName);
                                                        intent.putExtra("chat_key", "");
                                                        intent.putExtra("profile_pic", profile);
                                                        intent.putExtra("userID", traderID);
                                                        intent.putExtra("from", "my_offers");
                                                        intent.putExtra("userStatus", traderStatus);

                                                        v.getContext().startActivity(intent);
                                                        CustomIntent.customType(v.getContext(), "left-to-right");

                                                    }
                                                } else {

                                                    Log.d("CHAT", "ELSE ELSE");
                                                    Intent intent = new Intent(v.getContext(), Chat.class);
                                                    intent.putExtra("mobile", traderPhone);
                                                    intent.putExtra("name", userName);
                                                    intent.putExtra("chat_key", "");
                                                    intent.putExtra("profile_pic", profile);
                                                    intent.putExtra("userID", traderID);
                                                    intent.putExtra("from", "my_offers");
                                                    intent.putExtra("userStatus", traderStatus);

                                                    v.getContext().startActivity(intent);
                                                    CustomIntent.customType(v.getContext(), "left-to-right");
                                                }
                                            }
                                        });

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        databaseReference.child("trade-transactions").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                                    if (dataSnapshot2.child("Posted_Item").child("Poster_UID").getValue(String.class).equals(dataSnapshot.getKey()) &&
                                                            dataSnapshot2.child("Posted_Item").child("Item_Name").getValue(String.class).equals(dataSnapshot1.getKey()) &&
                                                            (dataSnapshot2.child("Offered_Item").child("Poster_UID").getValue(String.class).equals(uid) || dataSnapshot2.child("Offered_Item").child("Poster_UID").getValue(String.class).equals(offerFetch.getPoster_UID()))) {

                                                        String transactKey = dataSnapshot2.getKey();

                                                        goToReview.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                if (!dataSnapshot.getKey().equals(uid)) {
                                                                    Intent intent = new Intent(v.getContext(), TransactionReview.class);
                                                                    intent.putExtra("ParentKey", dataSnapshot.getKey());
                                                                    intent.putExtra("ParentItemName", dataSnapshot1.getKey());
                                                                    intent.putExtra("OffererKey", uid);
                                                                    intent.putExtra("transactionKey", transactKey);
                                                                    v.getContext().startActivity(intent);
                                                                    CustomIntent.customType(v.getContext(), "left-to-right");
                                                                } else {
                                                                    Intent intent = new Intent(v.getContext(), TransactionReview.class);
                                                                    intent.putExtra("ParentKey", dataSnapshot.getKey());
                                                                    intent.putExtra("ParentItemName", dataSnapshot1.getKey());
                                                                    intent.putExtra("OffererKey", offerFetch.getPoster_UID());
                                                                    intent.putExtra("transactionKey", transactKey);
                                                                    v.getContext().startActivity(intent);
                                                                    CustomIntent.customType(v.getContext(), "left-to-right");
                                                                }

                                                            }
                                                        });
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return offerFetchList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView textView, itemlocation, transactionStatus;
        ImageView img;
        LinearLayout rootLayout;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            rootLayout = itemView.findViewById(R.id.rootLayout);
            transactionStatus = itemView.findViewById(R.id.transactionStatus);
            textView = itemView.findViewById(R.id.offereditemname);
            img = itemView.findViewById(R.id.offeredpic);
            itemlocation = itemView.findViewById(R.id.offeredlocation);
        }
    }
}
