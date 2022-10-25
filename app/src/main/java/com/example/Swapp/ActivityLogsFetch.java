package com.example.Swapp;

import java.io.Serializable;

public class ActivityLogsFetch implements Serializable {

    String Activity, Date, User_ID;

    public ActivityLogsFetch() {
    }

    public ActivityLogsFetch(String activity, String date, String user_ID) {
        Activity = activity;
        Date = date;
        User_ID = user_ID;
    }

    public String getActivity() {
        return Activity;
    }

    public void setActivity(String activity) {
        Activity = activity;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }
}
