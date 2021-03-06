package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import Swapp.R;

public class forgotpassword extends AppCompatActivity {

    public static final String TAG = "TAG";

    TextView returnLogin;
    TextInputEditText email;
    Button sendRequest;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        returnLogin = findViewById(R.id.returnToLogin);
        email = findViewById(R.id.uEmail);
        sendRequest = findViewById(R.id.btnSendRequest);
        fAuth = FirebaseAuth.getInstance();

        returnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });

        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString();
                fAuth.sendPasswordResetEmail(userEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Success");
                        startActivity(new Intent(getApplicationContext(), postforgotpassword.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast toast = new Toast(getApplicationContext());
                        View view = LayoutInflater.from(forgotpassword.this).inflate(R.layout.toast_error_layout, null);
                        TextView toastMessage = view.findViewById(R.id.toastMessage);
                        toastMessage.setText(e.getMessage());
                        toast.setView(view);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0,50);
                        toast.show();
                    }
                });
            }
        });

    }

}