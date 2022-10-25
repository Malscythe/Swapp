package com.example.Swapp;

import android.app.Activity;

import java.io.Serializable;

public class ItemApprovalFetch implements Serializable {

    String Item_Image, Item_Name, Item_PosterName, Item_Location, Poster_UID, Poster_Phone;
    Activity passedActivity;

    public ItemApprovalFetch() {
    }

    public ItemApprovalFetch(String item_Image, String item_Name, String item_PosterName, String item_Location, String poster_UID, String poster_Phone, Activity activity) {
        Item_Image = item_Image;
        Item_Name = item_Name;
        Item_PosterName = item_PosterName;
        Item_Location = item_Location;
        Poster_UID = poster_UID;
        Poster_Phone = poster_Phone;
        passedActivity = activity;
    }

    public Activity getPassedActivity() {
        return passedActivity;
    }

    public void setPassedActivity(Activity passedActivity) {
        this.passedActivity = passedActivity;
    }

    public String getPoster_Phone() {
        return Poster_Phone;
    }

    public void setPoster_Phone(String poster_Phone) {
        Poster_Phone = poster_Phone;
    }

    public String getPoster_UID() {
        return Poster_UID;
    }

    public void setPoster_UID(String poster_UID) {
        Poster_UID = poster_UID;
    }

    public String getItem_Image() {
        return Item_Image;
    }

    public void setItem_Image(String item_Image) {
        Item_Image = item_Image;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }

    public String getItem_PosterName() {
        return Item_PosterName;
    }

    public void setItem_PosterName(String item_PosterName) {
        Item_PosterName = item_PosterName;
    }

    public String getItem_Location() {
        return Item_Location;
    }

    public void setItem_Location(String item_Location) {
        Item_Location = item_Location;
    }
}
