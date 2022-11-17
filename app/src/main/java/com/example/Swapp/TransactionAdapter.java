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

public class TransactionAdapter extends RecyclerView.Adapter {

    private static final String TAG = "TAG";
    List<TransactionFetch> transactionFetchList;

    public TransactionAdapter(List<TransactionFetch> transactionFetchList) {
        this.transactionFetchList = transactionFetchList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_transactions_layout, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;

        final TransactionFetch transactionFetch = transactionFetchList.get(position);

        viewHolderClass.transactionKey.setText(transactionFetch.getTransactionKey());
        viewHolderClass.posterUID.setText(transactionFetch.getPoster_UID());
        viewHolderClass.offererUID.setText(transactionFetch.getOfferer_UID());
        viewHolderClass.postedItemName.setText(transactionFetch.getPosted_ItemName());
        viewHolderClass.offeredItemName.setText(transactionFetch.getOffered_ItemName());
        viewHolderClass.transactionStatus.setText(transactionFetch.getTransactionStatus());
        viewHolderClass.dateTraded.setText(transactionFetch.getDate_Traded());

    }

    @Override
    public int getItemCount() {
        return transactionFetchList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView transactionKey, posterUID, offererUID, postedItemName, offeredItemName, transactionStatus, dateTraded;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            dateTraded = itemView.findViewById(R.id.dateTraded);
            transactionKey = itemView.findViewById(R.id.transactionKey);
            posterUID = itemView.findViewById(R.id.postedBy);
            offererUID = itemView.findViewById(R.id.offeredBy);
            postedItemName = itemView.findViewById(R.id.postedItemName);
            offeredItemName = itemView.findViewById(R.id.offeredItemName);
            transactionStatus = itemView.findViewById(R.id.transactionStatus);
        }
    }
}
