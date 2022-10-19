package com.example.Swapp;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Swapp.chat.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.FieldOrBuilder;

import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class OfferAdapter3 extends RecyclerView.Adapter {
    private static final String TAG = "Offer";
    List<OfferFetch> offerFetchList;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;


    public OfferAdapter3(List<OfferFetch> offerFetchList) {
        this.offerFetchList = offerFetchList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_transaction_layout, parent, false);
        OfferAdapter3.ViewHolderClass viewHolderClass = new OfferAdapter3.ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();

        OfferAdapter3.ViewHolderClass viewHolderClass = (OfferAdapter3.ViewHolderClass) holder;
        final OfferFetch offerFetch = offerFetchList.get(position);
        viewHolderClass.textView.setText(offerFetch.getItem_Name());
        viewHolderClass.itemlocation.setText(offerFetch.getItem_Location());
        Glide.with(viewHolderClass.img.getContext())
                .load(offerFetch.getImage_Url())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .error(com.google.firebase.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(viewHolderClass.img);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.child("items").getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.child("Accepted_Offers").hasChild(offerFetch.getPoster_UID()) ) {
                            if (dataSnapshot1.child("Accepted_Offers").child(offerFetch.getPoster_UID()).child("Item_Name").getValue(String.class).equals(offerFetch.getItem_Name())) {
                                viewHolderClass.moreInfo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(view.getContext(), TransactionMoreInfo.class);
                                        intent.putExtra("itemName", offerFetch.getItem_Name());
                                        intent.putExtra("userID", offerFetch.getPoster_UID());
                                        intent.putExtra("parentKey", dataSnapshot.getKey());
                                        intent.putExtra("parentItemName", dataSnapshot1.getKey());
                                        view.getContext().startActivity(intent);
                                        CustomIntent.customType(view.getContext(), "left-to-right");
                                    }
                                });

                                if (!dataSnapshot.getKey().equals(uid)) {
                                    viewHolderClass.getDirection.setVisibility(View.GONE);
                                }

                                viewHolderClass.getDirection.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(v.getContext(), currentLoc.class);
                                        intent.putExtra("from", "getDirection");
                                        intent.putExtra("category", "getDirection");
                                        intent.putExtra("latitude", dataSnapshot1.child("Accepted_Offers").child(offerFetch.getPoster_UID()).child("Address").child("Latitude").getValue(String.class));
                                        intent.putExtra("longitude", dataSnapshot1.child("Accepted_Offers").child(offerFetch.getPoster_UID()).child("Address").child("Longitude").getValue(String.class));
                                        v.getContext().startActivity(intent);
                                        CustomIntent.customType(v.getContext(), "left-to-right");
                                    }
                                });

                                viewHolderClass.goToChat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        for (DataSnapshot dataSnapshot2 : snapshot.child("chat").getChildren()) {
                                            String user1 = dataSnapshot2.child("user_1").getValue(String.class);
                                            String user2 = dataSnapshot2.child("user_2").getValue(String.class);

                                            String myPhone;
                                            String traderPhone;
                                            String userName;
                                            String traderID;
                                            String traderStatus;

                                            if (!offerFetch.getPoster_UID().equals(uid)) {
                                                myPhone = snapshot.child("users").child(uid).child("Phone").getValue(String.class);
                                                userName = snapshot.child("users").child(offerFetch.getPoster_UID()).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(offerFetch.getPoster_UID()).child("Last_Name").getValue(String.class));
                                                traderPhone = snapshot.child("users").child(offerFetch.getPoster_UID()).child("Phone").getValue(String.class);
                                                traderID = offerFetch.getPoster_UID();
                                                traderStatus = snapshot.child("users-status").child(offerFetch.getPoster_UID()).child("Status").getValue(String.class);
                                            } else {
                                                myPhone = snapshot.child("users").child(offerFetch.getPoster_UID()).child("Phone").getValue(String.class);
                                                userName = snapshot.child("users").child(dataSnapshot.getKey()).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(dataSnapshot.getKey()).child("Last_Name").getValue(String.class));
                                                traderPhone = snapshot.child("users").child(dataSnapshot.getKey()).child("Phone").getValue(String.class);
                                                traderID = dataSnapshot.getKey();
                                                traderStatus = snapshot.child("users-status").child(dataSnapshot.getKey()).child("Status").getValue(String.class);
                                            }

                                            if (((user1.equals(myPhone) || user2.equals(myPhone)) && ((user1.equals(traderPhone) || user2.equals(traderPhone)))) && (!myPhone.equals(traderPhone))) {
                                                Intent intent = new Intent(v.getContext(), Chat.class);
                                                intent.putExtra("mobile", traderPhone);
                                                intent.putExtra("name", userName);
                                                intent.putExtra("chat_key", dataSnapshot2.getKey());
                                                intent.putExtra("userID", traderID);
                                                intent.putExtra("userStatus", traderStatus);

                                                v.getContext().startActivity(intent);
                                                CustomIntent.customType(v.getContext(), "left-to-right");
                                            }

                                        }
                                    }
                                });

                                viewHolderClass.goToReview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!dataSnapshot.getKey().equals(uid)) {
                                            Intent intent = new Intent(v.getContext(), TransactionReview.class);
                                            intent.putExtra("ParentKey", dataSnapshot.getKey());
                                            intent.putExtra("ParentItemName", dataSnapshot1.getKey());
                                            intent.putExtra("OffererKey", uid);
                                            v.getContext().startActivity(intent);
                                            CustomIntent.customType(v.getContext(), "left-to-right");
                                        } else {
                                            Intent intent = new Intent(v.getContext(), TransactionReview.class);
                                            intent.putExtra("ParentKey", dataSnapshot.getKey());
                                            intent.putExtra("ParentItemName", dataSnapshot1.getKey());
                                            intent.putExtra("OffererKey", offerFetch.getPoster_UID());
                                            v.getContext().startActivity(intent);
                                            CustomIntent.customType(v.getContext(), "left-to-right");
                                        }

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
    }

    @Override
    public int getItemCount() {
        return offerFetchList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView textView, itemlocation, moreInfo, goToReview;
        ImageView img, getDirection, goToChat;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            goToReview = itemView.findViewById(R.id.submitReview);
            goToChat = itemView.findViewById(R.id.goToChat);
            getDirection = itemView.findViewById(R.id.getDirection);
            moreInfo = itemView.findViewById(R.id.moreInfo);
            textView = itemView.findViewById(R.id.offereditemname);
            img = itemView.findViewById(R.id.offeredpic);
            itemlocation = itemView.findViewById(R.id.offeredlocation);
        }
    }
}
