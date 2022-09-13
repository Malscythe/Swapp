package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Swapp.chat.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Swapp.R;

public class login extends AppCompatActivity {

    TextInputEditText email, password;
    Button login;
    FirebaseAuth fAuth;
    TextView forgotPassword;
    CheckBox rememberMe;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        rememberMe = findViewById(R.id.checkBox);

        if (MemoryData.getState(this).equals("true")) {
            Intent intent = new Intent(login.this, UserHomepage.class);
            intent.putExtra("mobile", MemoryData.getData(this));
            intent.putExtra("name", "");
            intent.putExtra("email", "");
            startActivity(intent);
            finish();
        }

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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString().trim();
                String userPass = password.getText().toString().trim();

                if (rememberMe.isChecked()) {
                    MemoryData.saveState(true, login.this);
                }

                if(TextUtils.isEmpty(userEmail)) {
                    email.setError("Email must not be empty.");
                    return;
                }

                if(TextUtils.isEmpty(userPass)) {
                    password.setError("Password must not be empty");
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
                    } else {
                        Toast toast = new Toast(getApplicationContext());
                        View view2 = LayoutInflater.from(login.this).inflate(R.layout.toast_error_layout, null);
                        TextView toastMessage = view2.findViewById(R.id.toastMessage);
                        toastMessage.setText("Email or password doesn't exist.");
                        toast.setView(view2);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0,50);
                        toast.show();
                    }
                }));
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

        databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("isAdmin").getValue(String.class).equals("1")){
                    startActivity(new Intent(getApplicationContext(), AdminHomepage.class));
                    finish();
                } else {
                    MemoryData.saveData(snapshot.child("Phone").getValue().toString(), login.this);
                    MemoryData.saveName(snapshot.child("First_Name").getValue().toString().concat(" " + snapshot.child("First_Name").getValue().toString()), login.this);
                    startActivity(new Intent(getApplicationContext(), UserHomepage.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}