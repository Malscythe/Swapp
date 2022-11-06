package com.example.Swapp;

import android.app.Activity;

import java.io.Serializable;

public class ConflictTransactionsFetch implements Serializable {

    String Transaction_Key, Transaction_Mode, Poster_UID, Offerer_UID, Poster_Response, Offerer_Response;

    public ConflictTransactionsFetch() {
    }

    public ConflictTransactionsFetch(String transaction_Key, String transaction_Mode, String poster_UID, String offerer_UID, String poster_Response, String offerer_Response) {
        Transaction_Key = transaction_Key;
        Transaction_Mode = transaction_Mode;
        Poster_UID = poster_UID;
        Offerer_UID = offerer_UID;
        Poster_Response = poster_Response;
        Offerer_Response = offerer_Response;
    }

    public String getTransaction_Key() {
        return Transaction_Key;
    }

    public void setTransaction_Key(String transaction_Key) {
        Transaction_Key = transaction_Key;
    }

    public String getTransaction_Mode() {
        return Transaction_Mode;
    }

    public void setTransaction_Mode(String transaction_Mode) {
        Transaction_Mode = transaction_Mode;
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

    public String getPoster_Response() {
        return Poster_Response;
    }

    public void setPoster_Response(String poster_Response) {
        Poster_Response = poster_Response;
    }

    public String getOfferer_Response() {
        return Offerer_Response;
    }

    public void setOfferer_Response(String offerer_Response) {
        Offerer_Response = offerer_Response;
    }
}
