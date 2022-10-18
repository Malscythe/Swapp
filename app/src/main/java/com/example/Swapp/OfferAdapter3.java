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
                                        Log.w(TAG, dataSnapshot1.child("Accepted_Offers").child(offerFetch.getPoster_UID()).child("Address").child("Latitude").getValue(String.class));
                                        Log.w(TAG, dataSnapshot1.child("Accepted_Offers").child(offerFetch.getPoster_UID()).child("Address").child("Longitude").getValue(String.class));

                                        Intent intent = new Intent(v.getContext(), currentLoc.class);
                                        intent.putExtra("from", "getDirection");
                                        intent.putExtra("category", "getDirection");
                                        intent.putExtra("latitude", dataSnapshot1.child("Accepted_Offers").child(offerFetch.getPoster_UID()).child("Address").child("Latitude").getValue(String.class));
                                        intent.putExtra("longitude", dataSnapshot1.child("Accepted_Offers").child(offerFetch.getPoster_UID()).child("Address").child("Longitude").getValue(String.class));
                                        v.getContext().startActivity(intent);
                                        CustomIntent.customType(v.getContext(), "left-to-right");
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

        TextView textView, itemlocation, moreInfo;
        ImageView img, getDirection;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            getDirection = itemView.findViewById(R.id.getDirection);
            moreInfo = itemView.findViewById(R.id.moreInfo);
            textView = itemView.findViewById(R.id.offereditemname);
            img = itemView.findViewById(R.id.offeredpic);
            itemlocation = itemView.findViewById(R.id.offeredlocation);
        }
    }
}
