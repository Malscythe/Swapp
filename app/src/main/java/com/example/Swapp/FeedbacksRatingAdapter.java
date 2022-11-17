package com.example.Swapp;

import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Swapp.R;

public class FeedbacksRatingAdapter extends RecyclerView.Adapter {

    private static final String TAG = "TAG";
    List<FeedbacksRatingFetch> feedbacksRatingFetchList;

    public FeedbacksRatingAdapter(List<FeedbacksRatingFetch> feedbacksRatingFetchList) {
        this.feedbacksRatingFetchList = feedbacksRatingFetchList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_fnr_layout, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;

        final FeedbacksRatingFetch feedbacksRatingFetch = feedbacksRatingFetchList.get(position);

        viewHolderClass.userID.setText(feedbacksRatingFetch.getUser_To_Rate());
        viewHolderClass.dateRated.setText(feedbacksRatingFetch.getDate_Rated());
        viewHolderClass.feedback.setText(feedbacksRatingFetch.getFeedback());
        viewHolderClass.transactionKey.setText(feedbacksRatingFetch.getTransaction_Key());
        viewHolderClass.ratedBy.setText(feedbacksRatingFetch.getRated_By());
        viewHolderClass.rating.setRating(Float.parseFloat(feedbacksRatingFetch.getRate()));
    }

    @Override
    public int getItemCount() {
        return feedbacksRatingFetchList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        TextView userID, feedback, transactionKey, ratedBy, dateRated;
        RatingBar rating;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            dateRated = itemView.findViewById(R.id.dateRated);
            userID = itemView.findViewById(R.id.toRateID);
            feedback = itemView.findViewById(R.id.feedback);
            transactionKey = itemView.findViewById(R.id.transactionKey);
            ratedBy = itemView.findViewById(R.id.ratedByID);
            rating = itemView.findViewById(R.id.ratingBar);
        }
    }
}
