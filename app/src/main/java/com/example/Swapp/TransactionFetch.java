package com.example.Swapp;

import java.io.Serializable;

public class TransactionFetch implements Serializable {

    String TransactionKey, Posted_ItemName, Offered_ItemName, Poster_UID, Offerer_UID, TransactionStatus;

    public TransactionFetch() {
    }

    public TransactionFetch(String transactionKey, String posted_ItemName, String offered_ItemName, String poster_UID, String offerer_UID, String transactionStatus) {
        TransactionKey = transactionKey;
        Posted_ItemName = posted_ItemName;
        Offered_ItemName = offered_ItemName;
        Poster_UID = poster_UID;
        Offerer_UID = offerer_UID;
        TransactionStatus = transactionStatus;
    }

    public String getTransactionKey() {
        return TransactionKey;
    }

    public void setTransactionKey(String transactionKey) {
        TransactionKey = transactionKey;
    }

    public String getPosted_ItemName() {
        return Posted_ItemName;
    }

    public void setPosted_ItemName(String posted_ItemName) {
        Posted_ItemName = posted_ItemName;
    }

    public String getOffered_ItemName() {
        return Offered_ItemName;
    }

    public void setOffered_ItemName(String offered_ItemName) {
        Offered_ItemName = offered_ItemName;
    }

    public String getPoster_UID() {
        return Poster_UID;
    }

    public void setPoster_UID(String poster_UID) {
        Poster_UID = poster_UID;
    }

    public String getOfferer_UID() {
        return Offerer_UID;
    }

    public void setOfferer_UID(String offerer_UID) {
        Offerer_UID = offerer_UID;
    }

    public String getTransactionStatus() {
        return TransactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        TransactionStatus = transactionStatus;
    }
}
