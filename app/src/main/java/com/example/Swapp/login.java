package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Swapp.R;

public class login extends AppCompatActivity {

    TextInputEditText email, password;
    Button login;
    FirebaseAuth fAuth;
    TextView forgotPassword;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        TextView goCreateAcc = findViewById(R.id.gotosignup);

        goCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), signup.class));
            }
        });

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        login = findViewById(R.id.btnLogin);
        forgotPassword = findViewById(R.id.uForgotPassword);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString().trim();
                String userPass = password.getText().toString().trim();


                if(TextUtils.isEmpty(userEmail)) {
                    email.setError("Email must not be empty.");
                    return;
                }

                if(TextUtils.isEmpty(userPass)) {
                    password.setError("Password must not be empty.");
                    return;
                }

                fAuth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener((task -> {

                    if (task.isSuccessful()) {
                        if(fAuth.getCurrentUser().isEmailVerified())
                        {
                            fAuth.signInWithEmailAndPassword(userEmail, userPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    checkAccessLevel(authResult.getUser().getUid());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast toast = new Toast(getApplicationContext());
                                    View view = LayoutInflater.from(login.this).inflate(R.layout.toast_error_layout, null);
                                    TextView toastMessage = view.findViewById(R.id.toastMessage);
                                    toastMessage.setText(e.getMessage());
                                    toast.setView(view);
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 0,50);
                                    toast.show();
                                }
                            });
                        }else
                        {
                            fAuth.getCurrentUser().sendEmailVerification();
                            Toast.makeText(login.this, "Please verify your email first", Toast.LENGTH_SHORT).show();

                        }
                    }
                }));

//place here
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), forgotpassword.class));
            }
        });
    }

    private void checkAccessLevel(String uid) {
        DocumentReference df = fStore.collection("users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("isAdmin").equals("1")) {
                    startActivity(new Intent(getApplicationContext(), AdminHomepage.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), UserHomepage.class));

                    finish();
                }
            }
        });
    }


}