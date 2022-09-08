package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Swapp.R;
import Swapp.databinding.SignupBinding;

public class signup extends AppCompatActivity {

    public static final String TAG = "TAG";
    private SignupBinding binding;

    EditText firstName;
    TextInputEditText lastName, email, password, rePassword, birthDate;
    TextInputLayout firstNameL, lastNameL, emailL, passwordL, rePasswordL, birthDateL, genderL;
    AutoCompleteTextView gender;
    Button signUp;
    TextView backToLogin;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, birthDay, birthMonth, birthYear, strMonth;


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

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        firstName = findViewById(R.id.uFirstName);
        lastName = findViewById(R.id.uLastName);
        email = findViewById(R.id.uEmail);
        password = findViewById(R.id.uPassword);
        rePassword = findViewById(R.id.uRePassword);
        gender = findViewById(R.id.uGender);
        birthDate = findViewById(R.id.ubirthDate);
        signUp = findViewById(R.id.signUpBtn);
        backToLogin = findViewById(R.id.backtologin);

        firstNameL = findViewById(R.id.uFirstNameLayout);
        lastNameL = findViewById(R.id.uLastNameLayout);
        emailL = findViewById(R.id.uEmailLayout);
        passwordL = findViewById(R.id.uPasswordLayout);
        rePasswordL = findViewById(R.id.uRePasswordLayout);
        genderL = findViewById(R.id.uGenderLayout);
        birthDateL = findViewById(R.id.birthDatePicker);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailValidator(email);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valEmail = email.getText().toString().trim();
                String valPassword = password.getText().toString().trim();
                String valRePassword = rePassword.getText().toString().trim();
                String userFirstName = firstName.getText().toString();
                String userLastName = lastName.getText().toString();
                String userBirthDate = birthDate.getText().toString();
                String userGender = gender.getText().toString();
                TextInputEditText strBirthDate = findViewById(R.id.ubirthDate);
                String valBirthDate = strBirthDate.getText().toString();
                SimpleDateFormat sdf = null;

                if (!valBirthDate.equals("")){
                    strMonth = strBirthDate.getText().toString().substring(0, 3);
                    sdf = new SimpleDateFormat("dd/MM/yyyy");
                } else {
                    birthDateL.setError("Birth cannot be empty.");
                    birthDateL.setErrorIconDrawable(null);
                    return;
                }

                switch (strMonth) {
                    case "Jan":
                        birthMonth = "01";
                        break;
                    case "Feb":
                        birthMonth = "02";
                        break;
                    case "Mar":
                        birthMonth = "03";
                        break;
                    case "Apr":
                        birthMonth = "04";
                        break;
                    case "May":
                        birthMonth = "05";
                        break;
                    case "Jun":
                        birthMonth = "06";
                        break;
                    case "Jul":
                        birthMonth = "07";
                        break;
                    case "Aug":
                        birthMonth = "08";
                        break;
                    case "Sep":
                        birthMonth = "09";
                        break;
                    case "Oct":
                        birthMonth = "10";
                        break;
                    case "Nov":
                        birthMonth = "11";
                        break;
                    case "Dec":
                        birthMonth = "12";
                        break;
                }

                if(strBirthDate.getText().toString().substring(4, strBirthDate.getText().toString().indexOf(",")).length() < 1){
                    birthDay = "0" + strBirthDate.getText().toString().substring(4, strBirthDate.getText().toString().indexOf(","));
                } else {
                    birthDay = strBirthDate.getText().toString().substring(4, strBirthDate.getText().toString().indexOf(","));
                }

                birthYear = strBirthDate.getText().toString().substring(strBirthDate.getText().toString().length() - 4);

                Date strDate = null;

                LocalDate startDate = LocalDate.parse(birthYear + "-" + birthMonth + "-" + birthDay);
                LocalDate endDate = LocalDate.now();

                int years = Years.yearsBetween(startDate, endDate).getYears();

                birthDate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (years < 18 || TextUtils.isEmpty(valBirthDate)) {
                            final ViewGroup.LayoutParams params = birthDateL.getLayoutParams();
                            birthDateL.setError("You must be 18 years old and above.");
                            birthDateL.setErrorIconDrawable(null);
                            birthDateL.setLayoutParams(params);
                            return;
                        } else {
                            final ViewGroup.LayoutParams params = birthDateL.getLayoutParams();
                            birthDateL.setError(null);
                            birthDateL.setLayoutParams(params);
                        }
                    }
                });



                if(TextUtils.isEmpty(valPassword)) {
                    passwordL.setError("Password must not be empty.");
                    passwordL.setErrorIconDrawable(null);
                    return;
                }

                if(valPassword.length() < 6) {
                    passwordL.setErrorIconDrawable(null);
                    passwordL.setError("Password must be more than 6 characters.");
                    return;
                }

                if(!valPassword.equals(valRePassword)) {
                    rePasswordL.setErrorIconDrawable(null);
                    rePasswordL.setError("Password must be match.");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(valEmail, valPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            fAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {


                                        Toast toast = new Toast(getApplicationContext());
                                        View view = LayoutInflater.from(signup.this).inflate(R.layout.toast_layout, null);
                                        TextView toastMessage = view.findViewById(R.id.toastMessage);
                                        toastMessage.setText("Account has been successfully created,Please check your email for verification.");
                                        toast.setView(view);
                                        toast.setDuration(Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0,50);
                                        toast.show();

                                        userID = fAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = fStore.collection("users").document(userID);
                                        Map<String, Object>user = new HashMap<>();
                                        user.put("First_Name", userFirstName);
                                        user.put("Last_Name", userLastName);
                                        user.put("Birth_Date", userBirthDate);
                                        user.put("Gender", userGender);
                                        user.put("Email", valEmail);
                                        user.put("isAdmin", "0");
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, userID + " has been created.");
                                            }
                                        });

                                    }
                                }
                            });


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

    public void emailValidator(EditText editText) {

        // extract the entered data from the EditText
        String emailToText = editText.getText().toString();

        // Android offers the inbuilt patterns which the entered
        // data from the EditText field needs to be compared with
        // In this case the entered data needs to compared with
        // the EMAIL_ADDRESS, which is implemented same below
        if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
            emailL.setError(null);
            emailL.setErrorIconDrawable(null);
        } else {
            emailL.setError("Email must valid & not empty.");
            emailL.setErrorIconDrawable(null);
        }
    }
}