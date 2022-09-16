package com.example.Swapp;

import java.io.Serializable;

public class OfferFetch implements Serializable {

    String Item_Name, Item_Location, Image_Url , Poster_UID, ParentKey;

    public OfferFetch() {
    }

    public OfferFetch(String item_Name, String item_Location, String image_Url, String poster_UID, String parentKey) {
        Item_Name = item_Name;
        Item_Location = item_Location;
        Image_Url = image_Url;
        Poster_UID = poster_UID;
        ParentKey = parentKey;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }

    public String getItem_Location() {
        return Item_Location;
    }

    public void setItem_Location(String item_Location) {
        Item_Location = item_Location;
    }

    public String getImage_Url() {
        return Image_Url;
    }

    public void setImage_Url(String image_Url) {
        Image_Url = image_Url;
    }

    public String getPoster_UID() {
        return Poster_UID;
    }

    public void setPoster_UID(String poster_UID) {
        Poster_UID = poster_UID;
    }

    public String getParentKey() {
        return ParentKey;
    }

    public void setParentKey(String parentKey) {
        ParentKey = parentKey;
    }
}
