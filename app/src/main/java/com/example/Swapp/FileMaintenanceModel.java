package com.example.Swapp;


public class FileMaintenanceModel {

    String Phone, Email, First_Name, Gender, Last_Name, Surl;

    public FileMaintenanceModel() {

    }


    public FileMaintenanceModel(String phone, String email, String first_Name, String gender, String last_Name, String surl) {
        Phone = phone;
        Email = email;
        First_Name = first_Name;
        Gender = gender;
        Last_Name = last_Name;
        Surl = surl;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String birth_Date) {
        Phone = birth_Date;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public void setLast_Name(String last_Name) {
        Last_Name = last_Name;
    }

    public String getSurl() {
        return Surl;
    }

    public void setSurl(String surl) {
        Surl = surl;
    }
}
