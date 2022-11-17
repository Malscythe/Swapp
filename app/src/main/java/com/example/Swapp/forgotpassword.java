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
import maes.tech.intentanim.CustomIntent;

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
                Intent intent = new Intent(forgotpassword.this, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("logoutFrom", "forgotPassword");
                startActivity(intent);
                CustomIntent.customType(forgotpassword.this, "right-to-left");
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
                        CustomIntent.customType(forgotpassword.this, "left-to-right");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(forgotpassword.this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("logoutFrom", "forgotPassword");
        startActivity(intent);
        CustomIntent.customType(forgotpassword.this, "right-to-left");
    }
}