package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import Swapp.R;
import Swapp.databinding.SignupBinding;

public class signup extends AppCompatActivity {

    private SignupBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String[] genderArr = getResources().getStringArray(R.array.gender);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item, genderArr);
        binding.autoCompleteTextView.setAdapter(arrayAdapter);

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Date");
        MaterialDatePicker materialDatePicker = builder.build();

        TextInputLayout textInputLayout = findViewById(R.id.birthDatePicker);

        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "Date_Picker");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                TextInputEditText textDate = findViewById(R.id.birthDate);

                textDate.setText(materialDatePicker.getHeaderText());
                textDate.setEnabled(false);
            }
        });
    }
}