package com.example.Swapp;

import java.io.Serializable;

public class CompleteTransactionFetch implements Serializable {

    String Posted_ItemName, Poster_UID, Poster_Name, Offered_ItemName, Offerer_UID, Offerer_Name, Posted_Image, Offerer_Image, Transaction_Key;

    public CompleteTransactionFetch() {
    }

    public CompleteTransactionFetch(String posted_ItemName, String poster_UID, String poster_Name, String offered_ItemName, String offerer_UID, String offerer_Name, String posted_Image, String offerer_Image, String transaction_Key) {
        Posted_ItemName = posted_ItemName;
        Poster_UID = poster_UID;
        Poster_Name = poster_Name;
        Offered_ItemName = offered_ItemName;
        Offerer_UID = offerer_UID;
        Offerer_Name = offerer_Name;
        Posted_Image = posted_Image;
        Offerer_Image = offerer_Image;
        Transaction_Key = transaction_Key;
    }

    public String getPosted_ItemName() {
        return Posted_ItemName;
    }

    public void setPosted_ItemName(String posted_ItemName) {
        Posted_ItemName = posted_ItemName;
    }

    public String getPoster_UID() {
        return Poster_UID;
    }

    public void setPoster_UID(String poster_UID) {
        Poster_UID = poster_UID;
    }

    public String getPoster_Name() {
        return Poster_Name;
    }

    public void setPoster_Name(String poster_Name) {
        Poster_Name = poster_Name;
    }

    public String getOffered_ItemName() {
        return Offered_ItemName;
    }

    public void setOffered_ItemName(String offered_ItemName) {
        Offered_ItemName = offered_ItemName;
    }

    public String getOfferer_UID() {
        return Offerer_UID;
    }

    public void setOfferer_UID(String offerer_UID) {
        Offerer_UID = offerer_UID;
    }

    public String getOfferer_Name() {
        return Offerer_Name;
    }

    public void setOfferer_Name(String offerer_Name) {
        Offerer_Name = offerer_Name;
    }

    public String getPosted_Image() {
        return Posted_Image;
    }

    public void setPosted_Image(String posted_Image) {
        Posted_Image = posted_Image;
    }

    public String getOfferer_Image() {
        return Offerer_Image;
    }

    public void setOfferer_Image(String offerer_Image) {
        Offerer_Image = offerer_Image;
    }

    public String getTransaction_Key() {
        return Transaction_Key;
    }

    public void setTransaction_Key(String transaction_Key) {
        Transaction_Key = transaction_Key;
    }
}
