package com.example.Swapp;

import android.content.Intent;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class MySuccessfulTransactionAdapter extends RecyclerView.Adapter {
    private static final String TAG = "Offer";
    List<CompleteTransactionFetch> completeTransactionFetches;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;


    public MySuccessfulTransactionAdapter(List<CompleteTransactionFetch> completeTransactionFetchList) {
        this.completeTransactionFetches = completeTransactionFetchList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_my_successful_transaction_layout, parent, false);
        MySuccessfulTransactionAdapter.ViewHolderClass viewHolderClass = new MySuccessfulTransactionAdapter.ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        MySuccessfulTransactionAdapter.ViewHolderClass viewHolderClass = (MySuccessfulTransactionAdapter.ViewHolderClass) holder;

        final CompleteTransactionFetch completeTransactionFetch = completeTransactionFetches.get(position);

        viewHolderClass.offeredItemName.setText(completeTransactionFetch.getOffered_ItemName());
        viewHolderClass.postedItemName.setText(completeTransactionFetch.getPosted_ItemName());
        viewHolderClass.posterName.setText(completeTransactionFetch.getPoster_Name());
        viewHolderClass.offererName.setText(completeTransactionFetch.getOfferer_Name());

        Glide.with(viewHolderClass.offeredPic.getContext())
                .load(completeTransactionFetch.getOfferer_Image())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .error(com.google.firebase.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(viewHolderClass.offeredPic);

        Glide.with(viewHolderClass.postedPic.getContext())
                .load(completeTransactionFetch.getPosted_Image())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .error(com.google.firebase.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(viewHolderClass.postedPic);

        viewHolderClass.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(viewHolderClass.buttonsLayout);

                if (viewHolderClass.buttonsLayout.getVisibility() == View.VISIBLE) {
                    viewHolderClass.buttonsLayout.setVisibility(View.GONE);
                } else {
                    viewHolderClass.buttonsLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("trade-transactions").child(completeTransactionFetch.getTransaction_Key()).child("Posted_Item").child("Poster_UID").getValue(String.class).equals(uid)) {
                    String idToPass = snapshot.child("trade-transactions").child(completeTransactionFetch.getTransaction_Key()).child("Offered_Item").child("Poster_UID").getValue(String.class);

                    if (snapshot.child("user-rating").child(idToPass).child("transactions").hasChild(completeTransactionFetch.getTransaction_Key())) {
                        viewHolderClass.submitRating.setVisibility(View.GONE);
                    } else {
                        viewHolderClass.submitRating.setVisibility(View.VISIBLE);
                    }

                } else {
                    String idToPass = snapshot.child("trade-transactions").child(completeTransactionFetch.getTransaction_Key()).child("Posted_Item").child("Poster_UID").getValue(String.class);

                    if (snapshot.child("user-rating").child(idToPass).child("transactions").hasChild(completeTransactionFetch.getTransaction_Key())) {
                        viewHolderClass.submitRating.setVisibility(View.GONE);
                    } else {
                        viewHolderClass.submitRating.setVisibility(View.VISIBLE);
                    }
                }


                viewHolderClass.submitRating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (snapshot.child("trade-transactions").child(completeTransactionFetch.getTransaction_Key()).child("Posted_Item").child("Poster_UID").getValue(String.class).equals(uid)) {
                            String idToPass;
                            String nameToPass;

                            idToPass = snapshot.child("trade-transactions").child(completeTransactionFetch.getTransaction_Key()).child("Offered_Item").child("Poster_UID").getValue(String.class);
                            nameToPass = snapshot.child("trade-transactions").child(completeTransactionFetch.getTransaction_Key()).child("Offered_Item").child("Poster_Name").getValue(String.class);

                            Intent intent = new Intent(v.getContext(), RateUser.class);
                            intent.putExtra("uid", idToPass);
                            intent.putExtra("name", nameToPass);
                            intent.putExtra("transactionKey", completeTransactionFetch.getTransaction_Key());
                            v.getContext().startActivity(intent);
                            CustomIntent.customType(v.getContext(), "left-to-right");
                        } else {
                            String idToPass;
                            String nameToPass;

                            idToPass = snapshot.child("trade-transactions").child(completeTransactionFetch.getTransaction_Key()).child("Posted_Item").child("Poster_UID").getValue(String.class);
                            nameToPass = snapshot.child("trade-transactions").child(completeTransactionFetch.getTransaction_Key()).child("Posted_Item").child("Poster_Name").getValue(String.class);

                            Intent intent = new Intent(v.getContext(), RateUser.class);
                            intent.putExtra("uid", idToPass);
                            intent.putExtra("name", nameToPass);
                            intent.putExtra("transactionKey", completeTransactionFetch.getTransaction_Key());
                            v.getContext().startActivity(intent);
                            CustomIntent.customType(v.getContext(), "left-to-right");
                        }

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewHolderClass.offeredMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), CompleteTransactionMoreInfo.class);
                intent.putExtra("transactionKey", completeTransactionFetch.getTransaction_Key());
                intent.putExtra("view", "Offered");
                view.getContext().startActivity(intent);
                CustomIntent.customType(view.getContext(), "left-to-right");
            }
        });

        viewHolderClass.postedMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), CompleteTransactionMoreInfo.class);
                intent.putExtra("transactionKey", completeTransactionFetch.getTransaction_Key());
                intent.putExtra("view", "Posted");
                view.getContext().startActivity(intent);
                CustomIntent.customType(view.getContext(), "left-to-right");
            }
        });
    }

    @Override
    public int getItemCount() {
        return completeTransactionFetches.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView posterName, offererName, offeredMoreInfo, postedMoreInfo, postedItemName, offeredItemName, submitRating;
        ImageView postedPic, offeredPic;
        LinearLayout rootLayout, buttonsLayout;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            rootLayout = itemView.findViewById(R.id.rootLayout);
            buttonsLayout = itemView.findViewById(R.id.revealButtons);

            submitRating = itemView.findViewById(R.id.submitRating);
            postedMoreInfo = itemView.findViewById(R.id.postedMoreInfo);
            offeredMoreInfo = itemView.findViewById(R.id.offeredMoreInfo);
            posterName = itemView.findViewById(R.id.postername);
            offererName = itemView.findViewById(R.id.offerername);

            postedItemName = itemView.findViewById(R.id.postedItemName);
            offeredItemName = itemView.findViewById(R.id.offeredItemName);

            postedPic = itemView.findViewById(R.id.postedpic);
            offeredPic = itemView.findViewById(R.id.offeredpic);
        }
    }
}
