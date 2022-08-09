package com.example.Swapp;


public class FileMaintenanceModel {

    String Birth_Date, Email, First_Name, Gender, Last_Name;

    public FileMaintenanceModel() {


    }


    public FileMaintenanceModel(String birth_Date, String email, String first_Name, String gender, String last_Name) {
        Birth_Date = birth_Date;
        Email = email;
        First_Name = first_Name;
        Gender = gender;
        Last_Name = last_Name;
    }

    public String getBirth_Date() {
        return Birth_Date;
    }

    public void setBirth_Date(String birth_Date) {
        Birth_Date = birth_Date;
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
}
