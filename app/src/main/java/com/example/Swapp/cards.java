package com.example.Swapp;

import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class cards {

    String Item_Category, Item_Description, Item_City, Item_State, Item_Name, Item_Preferred, Poster_Name, Poster_UID, ImageUrls, Item_RFT, Avg_Rating;
    ArrayList<String> keyList;

    public cards(String image_Url, String item_RFT, String item_Category, String item_Description, String item_City, String item_State, String item_Name, String item_Preferred, String poster_Name, String poster_Uid, ArrayList<String> KeyList, String avg_rating) {
        ImageUrls = image_Url;
        Item_Category = item_Category;
        Item_Description = item_Description;
        Item_City = item_City;
        Item_State = item_State;
        Item_Name = item_Name;
        Item_RFT = item_RFT;
        Item_Preferred = item_Preferred;
        Poster_Name = poster_Name;
        Poster_UID = poster_Uid;
        keyList = KeyList;
        Avg_Rating = avg_rating;

    }

    public String getAvg_Rating() {
        return Avg_Rating;
    }

    public void setAvg_Rating(String avg_Rating) {
        Avg_Rating = avg_Rating;
    }

    public ArrayList<String> getKeyList() {
        return keyList;
    }

    public void setKeyList(ArrayList<String> keyList) {
        this.keyList = keyList;
    }

    public String getItem_RFT() {
        return Item_RFT;
    }

    public void setItem_RFT(String item_RFT) {
        Item_RFT = item_RFT;
    }

    public String getItem_Category() {
        return Item_Category;
    }

    public void setItem_Category(String item_Category) {
        Item_Category = item_Category;
    }

    public String getItem_Description() {
        return Item_Description;
    }

    public void setItem_Description(String item_Description) {
        Item_Description = item_Description;
    }

    public String getItem_City() {
        return Item_City;
    }

    public void setItem_City(String item_City) {
        Item_City = item_City;
    }

    public String getItem_State() {
        return Item_State;
    }

    public void setItem_State(String item_State) {
        Item_State = item_State;
    }

    public String getImageUrls() {
        return ImageUrls;
    }

    public void setImageUrls(String imageUrls) {
        ImageUrls = imageUrls;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }

    public String getItem_Preferred() {
        return Item_Preferred;
    }

    public void setItem_Preferred(String item_Preferred) {
        Item_Preferred = item_Preferred;
    }

    public String getPoster_Name() {
        return Poster_Name;
    }

    public void setPoster_Name(String poster_Name) {
        Poster_Name = poster_Name;
    }

    public String getPoster_UID() {
        return Poster_UID;
    }

    public void setPoster_UID(String poster_Uid) {
        Poster_UID = poster_Uid;
    }
}
