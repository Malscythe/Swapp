package com.example.Swapp;


public class FileMaintenanceModel {

  String First_Name, Last_Name, Email, Gender, Phone, surl;

    public FileMaintenanceModel(String surl) {
        this.surl = surl;
    }

    public String getSurl() {
        return surl;
    }

    public void setSurl(String surl) {
        this.surl = surl;
    }

    FileMaintenanceModel()
{

}
    public FileMaintenanceModel(String first_Name, String last_Name, String email, String gender, String phone) {
        First_Name = first_Name;
        Last_Name = last_Name;
        Email = email;
        Gender = gender;
        Phone = phone;
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public void setLast_Name(String last_Name) {
        Last_Name = last_Name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
