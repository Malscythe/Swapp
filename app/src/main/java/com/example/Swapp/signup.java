package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import Swapp.R;
import Swapp.databinding.SignupBinding;

public class signup extends AppCompatActivity {

    private SignupBinding binding;

    TextInputEditText fullName, email, password, rePassword, birthDate;
    TextInputLayout fullNameL, emailL, passwordL, rePasswordL, birthDateL, genderL;
    AutoCompleteTextView gender;
    Button signUp;
    TextView backToLogin;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String[] genderArr = getResources().getStringArray(R.array.gender);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item, genderArr);
        binding.uGender.setAdapter(arrayAdapter);

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
                TextInputEditText textDate = findViewById(R.id.ubirthDate);

                textDate.setText(materialDatePicker.getHeaderText());
                textDate.setEnabled(false);
            }
        });

        fullName = findViewById(R.id.uFullName);
        email = findViewById(R.id.uEmail);
        password = findViewById(R.id.uPassword);
        rePassword = findViewById(R.id.uRePassword);
        gender = findViewById(R.id.uGender);
        birthDate = findViewById(R.id.ubirthDate);
        signUp = findViewById(R.id.signUpBtn);
        backToLogin = findViewById(R.id.backtologin);

        fullNameL = findViewById(R.id.uFullNameLayout);
        emailL = findViewById(R.id.uEmailLayout);
        passwordL = findViewById(R.id.uPasswordLayout);
        rePasswordL = findViewById(R.id.uRePasswordLayout);
        genderL = findViewById(R.id.uGenderLayout);
        birthDateL = findViewById(R.id.birthDatePicker);

        fAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valEmail = email.getText().toString().trim();
                String valPassword = password.getText().toString().trim();
                String valRePassword = rePassword.getText().toString().trim();

                if(TextUtils.isEmpty(valEmail)) {
                    email.setError("Email must not be empty.");
                    return;
                }

                if(TextUtils.isEmpty(valPassword)) {
                    passwordL.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    password.setError("Password must not be empty.");
                    return;
                } else {
                    passwordL.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                }

                if(valPassword.length() < 6) {
                    passwordL.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    password.setError("Password must be more than 6 characters.");
                    return;
                } else {
                    passwordL.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                }

                if(!valPassword.equals(valRePassword)) {
                    rePasswordL.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    rePassword.setError("Password must be match.");
                    return;
                } else {
                    rePasswordL.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                }

                fAuth.createUserWithEmailAndPassword(valEmail, valPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast toast = new Toast(getApplicationContext());
                            View view = LayoutInflater.from(signup.this).inflate(R.layout.toast_layout, null);
                            TextView toastMessage = view.findViewById(R.id.toastMessage);
                            toastMessage.setText("Account has been successfully created.");
                            toast.setView(view);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0,50);
                            toast.show();

                            startActivity(new Intent(getApplicationContext(), login.class));

                        } else {
                            Toast toast = new Toast(getApplicationContext());
                            View view = LayoutInflater.from(signup.this).inflate(R.layout.toast_error_layout, null);
                            TextView toastMessage = view.findViewById(R.id.toastMessage);
                            toastMessage.setText(task.getException().getMessage());
                            toast.setView(view);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0,50);
                            toast.show();

                        }
                    }
                });
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });
    }
}