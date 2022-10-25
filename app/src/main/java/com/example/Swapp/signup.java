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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.Locale;
import java.util.Map;

import Swapp.R;
import Swapp.databinding.SignupBinding;
import maes.tech.intentanim.CustomIntent;

public class signup extends AppCompatActivity {

    public static final String TAG = "TAG";
    private SignupBinding binding;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");

    TextInputEditText firstName, lastName, email, password, rePassword, birthDate, mobileNumber;
    TextInputLayout firstNameL, lastNameL, emailL, passwordL, rePasswordL, birthDateL, genderL, mobileNumberL;
    AutoCompleteTextView gender;
    Button signUp;
    TextView backToLogin;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, birthDay, birthMonth, birthYear, strMonth;
    String strDate;

    Boolean emailError = false;
    Boolean passError = false;
    Boolean rePassError = false;
    Boolean phoneError = false;
    Boolean genderError = false;
    Boolean birthError = false;
    Boolean alreadyUsed = false;

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

        strDate = new SimpleDateFormat("MMMM dd, yyyy hh:mm aa", Locale.getDefault()).format(new Date());

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

        firstName = findViewById(R.id.uFirstName);
        lastName = findViewById(R.id.uLastName);
        email = findViewById(R.id.uEmail);
        password = findViewById(R.id.uPassword);
        rePassword = findViewById(R.id.uRePassword);
        gender = findViewById(R.id.uGender);
        birthDate = findViewById(R.id.ubirthDate);
        signUp = findViewById(R.id.signUpBtn);
        backToLogin = findViewById(R.id.backtologin);
        mobileNumber = findViewById(R.id.uPhone);

        firstNameL = findViewById(R.id.uFirstNameLayout);
        lastNameL = findViewById(R.id.uLastNameLayout);
        emailL = findViewById(R.id.uEmailLayout);
        passwordL = findViewById(R.id.uPasswordLayout);
        rePasswordL = findViewById(R.id.uRePasswordLayout);
        genderL = findViewById(R.id.uGenderLayout);
        birthDateL = findViewById(R.id.birthDatePicker);
        mobileNumberL = findViewById(R.id.uPhoneLayout);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.w(TAG, "" + emailError);
                emailValidator(email);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneValidation(mobileNumber);

                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.child("Phone").getValue(String.class).substring(1, 11).equals(charSequence.toString())) {
                                mobileNumberL.setError("This mobile number is already in used.");
                                mobileNumberL.setErrorIconDrawable(null);
                                alreadyUsed = true;
                                return;
                            } else {
                                alreadyUsed = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                    birthError = true;
                } else {
                    birthDateL.setError(null);
                    birthDateL.setErrorIconDrawable(null);
                    birthError = false;
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

                if (!email.getText().toString().isEmpty() && emailError == false) {
                    emailL.setError(null);
                    emailL.setErrorIconDrawable(null);
                    emailError = false;
                } else {
                    if (email.getText().toString().isEmpty()) {
                        emailL.setError("Email cannot be empty.");
                    } else if (emailError == true) {
                        emailL.setError("Email must be valid");
                    }

                    emailL.setErrorIconDrawable(null);
                    emailError = true;
                    return;
                }

                Log.d("GUSTO KO NA MATULOG", alreadyUsed + "");

                if (!mobileNumber.getText().toString().isEmpty() && phoneError == false && alreadyUsed == false) {
                    mobileNumberL.setError(null);
                    mobileNumberL.setErrorIconDrawable(null);
                    phoneError = false;
                    alreadyUsed = false;
                } else {
                    if (mobileNumber.getText().toString().isEmpty()) {
                        mobileNumberL.setError("Mobile number cannot be empty.");
                    } else if (phoneError == true) {
                        mobileNumberL.setError("Invalid phone number.");
                    } else if (alreadyUsed == true) {
                        mobileNumberL.setError("This mobile number is already in used.");
                    }
                    mobileNumberL.setErrorIconDrawable(null);
                    return;
                }

                if (!password.getText().toString().isEmpty() && passError == false) {
                    passwordL.setError(null);
                    passwordL.setErrorIconDrawable(null);
                    passError = false;
                } else {
                    if (password.getText().toString().isEmpty()) {
                        passwordL.setError("Password cannot be empty.");
                    } else if (passError == true) {
                        passwordL.setError("Password must be more than 6 characters.");
                    }
                    passwordL.setErrorIconDrawable(null);
                    passError = true;
                    return;
                }

                if (!rePassword.getText().toString().isEmpty() && rePassError == false) {
                    rePasswordL.setError(null);
                    rePasswordL.setErrorIconDrawable(null);
                    rePassError = false;
                } else {
                    if (rePassword.getText().toString().isEmpty()) {
                        rePasswordL.setError("Confirm password cannot be empty.");
                    } else if (rePassError == true) {
                        rePasswordL.setError("Password must be match.");
                    }
                    rePasswordL.setErrorIconDrawable(null);
                    rePassError = true;
                    return;
                }

                if (!gender.getText().toString().isEmpty() && genderError == false) {
                    genderL.setErrorIconDrawable(null);
                    genderL.setError(null);
                    genderError = false;
                } else {
                    genderL.setError("Gender cannot be unspecified.");
                    genderL.setErrorIconDrawable(null);
                    genderError = true;
                    return;
                }

                if (!birthDate.getText().toString().isEmpty() && birthError == false){
                    birthDateL.setError(null);
                    birthDateL.setErrorIconDrawable(null);
                    birthError = false;
                } else {
                    if (birthDate.getText().toString().isEmpty()) {
                        birthDateL.setError("Birth cannot be empty.");
                    } else if (birthError == true) {
                        birthDateL.setError("You must be 18 years old and above.");
                    }
                    birthDateL.setErrorIconDrawable(null);
                    birthError = true;
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            fAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {

                                        userID = fAuth.getCurrentUser().getUid();
                                        databaseReference.child("users").child(userID).child("First_Name").setValue(firstName.getText().toString());
                                        databaseReference.child("users").child(userID).child("Last_Name").setValue(lastName.getText().toString());
                                        databaseReference.child("users").child(userID).child("Birth_Date").setValue(birthDate.getText().toString());
                                        databaseReference.child("users").child(userID).child("Gender").setValue(gender.getText().toString());
                                        databaseReference.child("users").child(userID).child("Email").setValue(email.getText().toString());
                                        databaseReference.child("users").child(userID).child("Phone").setValue("0" + mobileNumber.getText().toString());
                                        databaseReference.child("users").child(userID).child("isAdmin").setValue("0");
                                        databaseReference.child("user-rating").child(userID).child("rating1").setValue("0");
                                        databaseReference.child("user-rating").child(userID).child("rating2").setValue("0");
                                        databaseReference.child("user-rating").child(userID).child("rating3").setValue("0");
                                        databaseReference.child("user-rating").child(userID).child("rating4").setValue("0");
                                        databaseReference.child("user-rating").child(userID).child("rating5").setValue("0");
                                        databaseReference.child("user-rating").child(userID).child("Average_Rating").setValue("0.0");

                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.child("activity-logs").hasChildren()) {
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(userID);
                                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Registered");
                                                } else {
                                                    databaseReference.child("activity-logs").child("1").child("Date").setValue(strDate);
                                                    databaseReference.child("activity-logs").child("1").child("User_ID").setValue(userID);
                                                    databaseReference.child("activity-logs").child("1").child("Activity").setValue("Registered");
                                                }

                                                Intent intent = new Intent(signup.this, login.class);
                                                intent.putExtra("logoutFrom", "User");
                                                startActivity(intent);
                                                CustomIntent.customType(signup.this, "right-to-left");

                                                Toast toast = new Toast(getApplicationContext());
                                                View view = LayoutInflater.from(signup.this).inflate(R.layout.toast_layout, null);
                                                TextView toastMessage = view.findViewById(R.id.toastMessage);
                                                toastMessage.setText("Account has been successfully created, Please check your email for verification.");
                                                toast.setView(view);
                                                toast.setDuration(Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.TOP, 0,50);
                                                toast.show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            });
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
                CustomIntent.customType(signup.this, "right-to-left");
            }
        });

    }

    public void emailValidator(EditText editText) {

        String emailToText = editText.getText().toString();

        if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
            emailL.setError(null);
            emailL.setErrorIconDrawable(null);
            emailError = false;
        } else {
            emailL.setError("Email must be valid & not empty.");
            emailL.setErrorIconDrawable(null);
            emailError = true;
        }
    }

    public void passwordValidator(EditText editText) {

        String passToText = editText.getText().toString();

        if (passToText.isEmpty()) {
            passwordL.setErrorIconDrawable(null);
            passwordL.setError("Password cannot be empty.");
            passError = true;
        } else if (passToText.length() < 6) {
            passwordL.setErrorIconDrawable(null);
            passwordL.setError("Password must be more than 6 characters.");
            passError = true;
        } else {
            passwordL.setErrorIconDrawable(null);
            passwordL.setError(null);
            passError = false;
        }

    }

    public void confirmPasswordValidator(EditText editText, EditText editText1) {

        String password = editText.getText().toString();
        String confirmPassword = editText1.getText().toString();

        if (confirmPassword.isEmpty()){
            rePasswordL.setErrorIconDrawable(null);
            rePasswordL.setError("Confirm password cannot be empty.");
            rePassError = true;
        } else if (!password.equals(confirmPassword)) {
            rePasswordL.setErrorIconDrawable(null);
            rePasswordL.setError("Password must be match.");
            rePassError = true;
        } else {
            rePasswordL.setErrorIconDrawable(null);
            rePasswordL.setError(null);
            rePassError = false;
        }
    }

    public void genderValidation(EditText editText) {

        String gender = editText.getText().toString();

        if (gender.isEmpty()) {
            genderL.setErrorIconDrawable(null);
            genderL.setError("Gender cannot be unspecified.");
            genderError = true;
        } else {
            genderL.setErrorIconDrawable(null);
            genderL.setError(null);
            genderError = false;
        }
    }

    public void phoneValidation(EditText editText) {

        String number = editText.getText().toString();

        if (number.length() < 10) {
            mobileNumberL.setErrorIconDrawable(null);
            mobileNumberL.setError("Invalid phone number.");
            phoneError = true;
        } else {
            mobileNumberL.setErrorIconDrawable(null);
            mobileNumberL.setError(null);
            phoneError = false;
        }
    }
}