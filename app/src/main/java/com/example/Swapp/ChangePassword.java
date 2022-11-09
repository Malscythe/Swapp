package com.example.Swapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;

import java.util.List;

import Swapp.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class ChangePassword extends AppCompatActivity {

    TextInputEditText oldPass, newPass, confirmNewPass;
    TextInputLayout uOldPass, uNewPass, uConfirmNewPass;
    Button changePass;
    Boolean passError, rePassError;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        changePass = findViewById(R.id.changePassBtn);

        oldPass = findViewById(R.id.oldPass);
        newPass = findViewById(R.id.newPass);
        confirmNewPass = findViewById(R.id.confirmNewPass);

        uOldPass = findViewById(R.id.oldPassLayout);
        uNewPass = findViewById(R.id.newPassLayout);
        uConfirmNewPass = findViewById(R.id.confirmNewPassLayout);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        newPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordValidator(newPass);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        confirmNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                confirmPasswordValidator(newPass, confirmNewPass);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("TAG", "CLICKED");

                if (TextUtils.isEmpty(oldPass.getText().toString())) {
                    Log.d("TAG", "IF");
                    uOldPass.setError("This cannot be empty!");
                    uOldPass.setErrorIconDrawable(null);
                } else {
                    Log.d("TAG", "ELSE");
                    uOldPass.setError(null);
                    uOldPass.setErrorIconDrawable(null);

                    reAuthenticator(user);
                }
            }
        });
    }

    private void reAuthenticator(FirebaseUser user) {

        SweetAlertDialog pDialog = new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        Log.d("TAG", "AUTHENTICATE");
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass.getText().toString());

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    uOldPass.setError(null);
                    uOldPass.setErrorIconDrawable(null);

                    if (!passError && !rePassError) {

                        user.updatePassword(newPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        pDialog.dismiss();

                                        new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Change Password")
                                                .setContentText("Your password has been changed!")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        sweetAlertDialog.dismiss();

                                                        Intent intent = new Intent(ChangePassword.this, UserHomepage.class);
                                                        startActivity(intent);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        CustomIntent.customType(ChangePassword.this, "right-to-left");
                                                        finish();

                                                    }
                                                })
                                                .show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pDialog.dismiss();
                                        new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Change Password")
                                                .setContentText(e.getMessage())
                                                .show();
                                    }
                                });
                            }
                        });
                    }

                } else {
                    uOldPass.setError("Incorrect old password!");
                    uOldPass.setErrorIconDrawable(null);


                }
            }
        });
    }

    public void clickBack(View view) {
        Intent intent = new Intent(ChangePassword.this, UserHomepage.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        CustomIntent.customType(ChangePassword.this, "right-to-left");
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChangePassword.this, UserHomepage.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        CustomIntent.customType(ChangePassword.this, "right-to-left");
        finish();
    }

    public void passwordValidator(EditText editText) {

        String passToText = editText.getText().toString();

        if (passToText.isEmpty()) {
            uNewPass.setErrorIconDrawable(null);
            uNewPass.setError("Password cannot be empty.");
            passError = true;
        } else if (passToText.length() < 6) {
            uNewPass.setErrorIconDrawable(null);
            uNewPass.setError("Password must be more than 6 characters.");
            passError = true;
        } else {
            uNewPass.setErrorIconDrawable(null);
            uNewPass.setError(null);
            passError = false;
        }

    }

    public void confirmPasswordValidator(EditText editText, EditText editText1) {

        String password = editText.getText().toString();
        String confirmPassword = editText1.getText().toString();

        if (confirmPassword.isEmpty()){
            uConfirmNewPass.setErrorIconDrawable(null);
            uConfirmNewPass.setError("Confirm password cannot be empty.");
            rePassError = true;
        } else if (!password.equals(confirmPassword)) {
            uConfirmNewPass.setErrorIconDrawable(null);
            uConfirmNewPass.setError("Password must be match.");
            rePassError = true;
        } else {
            uConfirmNewPass.setErrorIconDrawable(null);
            uConfirmNewPass.setError(null);
            rePassError = false;
        }
    }

}
