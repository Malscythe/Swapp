package com.example.Swapp;

import java.io.Serializable;

public class FeedbacksRatingFetch implements Serializable {

    String Rate, Rated_By, Feedback, User_ID, Transaction_Key;

    public FeedbacksRatingFetch() {
    }

    public FeedbacksRatingFetch(String rate, String rated_By, String feedback, String user_ID, String transaction_Key) {
        Rate = rate;
        Rated_By = rated_By;
        Feedback = feedback;
        User_ID = user_ID;
        Transaction_Key = transaction_Key;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getRated_By() {
        return Rated_By;
    }

    public void setRated_By(String rated_By) {
        Rated_By = rated_By;
    }

    public String getFeedback() {
        return Feedback;
    }

    public void setFeedback(String feedback) {
        Feedback = feedback;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getTransaction_Key() {
        return Transaction_Key;
    }

    public void setTransaction_Key(String transaction_Key) {
        Transaction_Key = transaction_Key;
    }
}
