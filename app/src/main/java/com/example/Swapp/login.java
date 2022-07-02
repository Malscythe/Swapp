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
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import Swapp.R;

public class login extends AppCompatActivity {

    TextInputEditText email, password;
    Button login;
    FirebaseAuth fAuth;

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
        fAuth = FirebaseAuth.getInstance();

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

                fAuth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Snackbar snackbar = Snackbar.make(view, "Logged in successfully.", Snackbar.LENGTH_LONG);
                            snackbar.setBackgroundTint(getResources().getColor(R.color.primary));
                            snackbar.setTextColor(getResources().getColor((R.color.white)));
                            snackbar.show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    startActivity(new Intent(getApplicationContext(), UserHomepage.class));
                                }
                            }, 3000);

                        } else {
                            Snackbar snackbar = Snackbar.make(view, (task.getException().getMessage()), Snackbar.LENGTH_LONG);
                            snackbar.setBackgroundTint(getResources().getColor(R.color.primary));
                            snackbar.setTextColor(getResources().getColor((R.color.white)));
                            snackbar.show();
                        }
                    }
                });
            }
        });
    }
}