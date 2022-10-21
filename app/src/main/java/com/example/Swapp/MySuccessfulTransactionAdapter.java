package com.example.Swapp;

import android.content.Intent;
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

        databaseReference.child("trade-transactions").child(completeTransactionFetch.getTransaction_Key()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return completeTransactionFetches.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView posterName, offererName, offeredMoreInfo, postedMoreInfo, postedItemName, offeredItemName;
        ImageView postedPic, offeredPic;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

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
