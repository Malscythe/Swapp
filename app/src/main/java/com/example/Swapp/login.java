package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.UserController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Swapp.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import maes.tech.intentanim.CustomIntent;

public class login extends AppCompatActivity {

    private static final String TAG = "TAG";
    TextInputEditText email, password;
    Button login;
    FirebaseAuth fAuth;
    TextView forgotPassword;
    CheckBox rememberMe;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");
    String strDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        rememberMe = findViewById(R.id.checkBox);
        fAuth = FirebaseAuth.getInstance();

        strDate = new SimpleDateFormat("MMMM dd, yyyy hh:mm aa", Locale.getDefault()).format(new Date());

        String from = getIntent().getStringExtra("logoutFrom");

        if (from.equals("User")) {
            if (MemoryData.getState(this).equals("true") && fAuth.getCurrentUser().isEmailVerified()) {
                databaseReference.child("users-status").child(fAuth.getCurrentUser().getUid()).child("Status").setValue("Online");
                Intent intent = new Intent(login.this, UserHomepage.class);
                intent.putExtra("mobile", MemoryData.getData(this));
                intent.putExtra("name", "");
                intent.putExtra("email", "");
                startActivity(intent);
                finish();
            }
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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SweetAlertDialog pDialog = new SweetAlertDialog(login.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Logging in");
                pDialog.setCancelable(false);
                pDialog.show();
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

                                    String uid = fAuth.getCurrentUser().getUid();

                                    databaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        private static final String TAG = "TAG";

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.child("isAdmin").getValue(String.class).equals("1")){
                                                MemoryData.saveData(snapshot.child("Phone").getValue().toString(), login.this);
                                                MemoryData.saveState(false, login.this);
                                                startActivity(new Intent(login.this, AdminHomepage.class));
                                                pDialog.dismiss();
                                            } else if (snapshot.child("isAdmin").getValue(String.class).equals("0")){

                                                MemoryData.saveData(snapshot.child("Phone").getValue().toString(), login.this);
                                                MemoryData.saveFirstName(snapshot.child("First_Name").getValue(String.class), com.example.Swapp.login.this);
                                                MemoryData.saveName(snapshot.child("First_Name").getValue().toString().concat(" " + snapshot.child("Last_Name").getValue().toString()), login.this);
                                                databaseReference.child("users-status").child(uid).child("Status").setValue("Online");

                                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.child("activity-logs").hasChildren()) {
                                                            databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                                            databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(uid);
                                                            databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Logged In");
                                                        } else {
                                                            databaseReference.child("activity-logs").child("1").child("Date").setValue(strDate);
                                                            databaseReference.child("activity-logs").child("1").child("User_ID").setValue(uid);
                                                            databaseReference.child("activity-logs").child("1").child("Activity").setValue("Logged In");
                                                        }

                                                        Intent intent = new Intent(login.this, UserHomepage.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        CustomIntent.customType(login.this, "left-to-right");
                                                        pDialog.dismiss();
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
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
                            pDialog.dismiss();
                            fAuth.getCurrentUser().sendEmailVerification();
                            Toast.makeText(login.this, "Please verify your email first", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        pDialog.dismiss();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}