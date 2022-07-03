package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Swapp.R;
import Swapp.databinding.ActivityFileMaintenanceBinding;

public class FileMaintenance extends AppCompatActivity {

    FirebaseDatabase rootNode;
    ActivityFileMaintenanceBinding binding;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFileMaintenanceBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_file_maintenance);


    }
}