package com.example.Swapp;

public class cards {

    String Image_Url, Item_Category, Item_Description, Item_Location, Item_Name, Item_Preferred, Poster_Name;

    public cards(String image_Url, String item_Category, String item_Description, String item_Location, String item_Name, String item_Preferred, String poster_Name) {
        Image_Url = image_Url;
        Item_Category = item_Category;
        Item_Description = item_Description;
        Item_Location = item_Location;
        Item_Name = item_Name;
        Item_Preferred = item_Preferred;
        Poster_Name = poster_Name;
    }

    public String getImage_Url() {
        return Image_Url;
    }

    public void setImage_Name(String image_Url) {
        Image_Url = image_Url;
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

    public String getItem_Location() {
        return Item_Location;
    }

    public void setItem_Location(String item_Location) {
        Item_Location = item_Location;
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
}
