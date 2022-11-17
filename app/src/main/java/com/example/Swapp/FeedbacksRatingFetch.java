package com.example.Swapp;

import java.io.Serializable;

public class FeedbacksRatingFetch implements Serializable {

    String Rate, Rated_By, Feedback, User_To_Rate, Transaction_Key, Date_Rated;

    public FeedbacksRatingFetch() {
    }

    public FeedbacksRatingFetch(String rate, String rated_By, String feedback, String user_To_Rate, String transaction_Key, String date_Rated) {
        Rate = rate;
        Rated_By = rated_By;
        Feedback = feedback;
        User_To_Rate = user_To_Rate;
        Transaction_Key = transaction_Key;
        Date_Rated = date_Rated;
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

    public String getUser_To_Rate() {
        return User_To_Rate;
    }

    public void setUser_To_Rate(String user_To_Rate) {
        User_To_Rate = user_To_Rate;
    }

    public String getTransaction_Key() {
        return Transaction_Key;
    }

    public void setTransaction_Key(String transaction_Key) {
        Transaction_Key = transaction_Key;
    }

    public String getDate_Rated() {
        return Date_Rated;
    }

    public void setDate_Rated(String date_Rated) {
        Date_Rated = date_Rated;
    }
}
