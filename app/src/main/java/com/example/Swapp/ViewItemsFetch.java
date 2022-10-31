package com.example.Swapp;

import java.io.Serializable;

public class ViewItemsFetch implements Serializable {

    String Item_Image, Item_Name, Item_Location;

    public ViewItemsFetch() {
    }

    public ViewItemsFetch(String item_Image, String item_Name, String item_Location) {
        Item_Image = item_Image;
        Item_Name = item_Name;
        Item_Location = item_Location;
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

    public String getItem_Location() {
        return Item_Location;
    }

    public void setItem_Location(String item_Location) {
        Item_Location = item_Location;
    }
}
