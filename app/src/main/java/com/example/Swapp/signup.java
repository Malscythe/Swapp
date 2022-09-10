package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.LinearGradient;
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

        String valEmail = email.getText().toString().trim();
        String valPassword = password.getText().toString().trim();
        String valRePassword = rePassword.getText().toString().trim();
        String userFirstName = firstName.getText().toString();
        String userLastName = lastName.getText().toString();
        String userBirthDate = birthDate.getText().toString();
        String userGender = gender.getText().toString();

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

        birthDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                strMonth = birthDate.getText().toString().substring(0, 3);

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

                if(birthDate.getText().toString().substring(4, birthDate.getText().toString().indexOf(",")).length() < 1){
                    birthDay = "0" + birthDate.getText().toString().substring(4, birthDate.getText().toString().indexOf(","));
                } else {
                    birthDay = birthDate.getText().toString().substring(4, birthDate.getText().toString().indexOf(","));
                }

                birthYear = birthDate.getText().toString().substring(birthDate.getText().toString().length() - 4);

                Date strDate = null;

                LocalDate startDate = LocalDate.parse(birthYear + "-" + birthMonth + "-" + birthDay);
                LocalDate endDate = LocalDate.now();

                int years = Years.yearsBetween(startDate, endDate).getYears();

                if (years < 18) {
                    birthDateL.setError("You must be 18 years old and above.");
                    birthDateL.setErrorIconDrawable(null);
                } else {
                    birthDateL.setError(null);
                    birthDateL.setErrorIconDrawable(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordValidator(password);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        rePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                confirmPasswordValidator(password, rePassword);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        gender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                genderValidation(gender);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!birthDate.getText().toString().isEmpty()){
                    birthDateL.setError(null);
                    birthDateL.setErrorIconDrawable(null);
                } else {
                    birthDateL.setError("Birth cannot be empty.");
                    birthDateL.setErrorIconDrawable(null);
                    return;
                }

                if (!email.getText().toString().isEmpty()) {
                    emailL.setError(null);
                    emailL.setErrorIconDrawable(null);
                } else {
                    emailL.setError("Email cannot be empty.");
                    emailL.setErrorIconDrawable(null);
                    return;
                }

                if (!password.getText().toString().isEmpty()) {
                    passwordL.setError(null);
                    passwordL.setErrorIconDrawable(null);
                } else {
                    passwordL.setError("Password cannot be empty.");
                    passwordL.setErrorIconDrawable(null);
                    return;
                }

                if (!rePassword.getText().toString().isEmpty()) {
                    rePasswordL.setError(null);
                    rePasswordL.setErrorIconDrawable(null);
                } else {
                    rePasswordL.setError("Confirm password cannot be empty.");
                    rePasswordL.setErrorIconDrawable(null);
                    return;
                }

                if (!gender.getText().toString().isEmpty()) {
                    rePasswordL.setErrorIconDrawable(null);
                    rePasswordL.setError(null);
                } else {
                    rePasswordL.setErrorIconDrawable(null);
                    rePasswordL.setError("Gender cannot be unspecified.");
                    return;
                }

                String getEmailError = emailL.getError().toString();
                String getPasswordError = passwordL.getError().toString();
                String getRePasswordError = rePasswordL.getError().toString();
                String getBirthError = birthDateL.getError().toString();
                String getGenderError = genderL.getError().toString();

                if (!getEmailError.equals(null) || !getPasswordError.equals(null) || !getRePasswordError.equals(null) || !getBirthError.equals(null) || !getGenderError.equals(null)) {
                    Toast toast = new Toast(getApplicationContext());
                    view = LayoutInflater.from(signup.this).inflate(R.layout.toast_error_layout, null);
                    TextView toastMessage = view.findViewById(R.id.toastMessage);
                    toastMessage.setText("Check the errors in required field.");
                    toast.setView(view);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0,50);
                    toast.show();
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

        String emailToText = editText.getText().toString();

        if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
            emailL.setError(null);
            emailL.setErrorIconDrawable(null);
        } else {
            emailL.setError("Email must valid & not empty.");
            emailL.setErrorIconDrawable(null);
        }
    }

    public void passwordValidator(EditText editText) {

        String passToText = editText.getText().toString();

        if (passToText.isEmpty()) {
            passwordL.setErrorIconDrawable(null);
            passwordL.setError("Password cannot be empty.");
        } else if (passToText.length() < 6) {
            passwordL.setErrorIconDrawable(null);
            passwordL.setError("Password must be more than 6 characters.");
        } else {
            passwordL.setErrorIconDrawable(null);
            passwordL.setError(null);
        }

    }

    public void confirmPasswordValidator(EditText editText, EditText editText1) {

        String password = editText.getText().toString();
        String confirmPassword = editText1.getText().toString();

        if (confirmPassword.isEmpty()){
            rePasswordL.setErrorIconDrawable(null);
            rePasswordL.setError("Confirm password cannot be empty.");
        } else if (!password.equals(confirmPassword)) {
            rePasswordL.setErrorIconDrawable(null);
            rePasswordL.setError("Password must be match.");
        } else {
            rePasswordL.setErrorIconDrawable(null);
            rePasswordL.setError(null);
        }
    }

    public void genderValidation(EditText editText) {

        String gender = editText.getText().toString();

        if (gender.isEmpty()) {
            rePasswordL.setErrorIconDrawable(null);
            rePasswordL.setError("Gender cannot be unspecified.");
        } else {
            rePasswordL.setErrorIconDrawable(null);
            rePasswordL.setError(null);
        }
    }
}