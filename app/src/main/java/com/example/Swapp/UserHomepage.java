package com.example.Swapp;

import static com.example.Swapp.call.SinchService.APP_KEY;
import static com.example.Swapp.call.SinchService.APP_SECRET;
import static com.example.Swapp.call.SinchService.ENVIRONMENT;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Swapp.call.BaseActivity;
import com.example.Swapp.call.SinchService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushTokenRegistrationCallback;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.UserController;
import com.sinch.android.rtc.UserRegistrationCallback;

import Swapp.R;
import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class UserHomepage extends BaseActivity implements SinchService.StartFailedListener, PushTokenRegistrationCallback, UserRegistrationCallback {

    private static final String TAG = "TAG";
    private String mUserId;

    TextView pendingCounts, successfulCounts, unsuccessfulCounts, currentCounts, name;
    Dialog chartDialog;
    ImageView viewChart;
    CircleImageView accsetting;
    CardView tradeButton, postButton;
    Long pendingTrades = 0L;
    Long currentTrades = 0L;
    Long successfulTrades = 0L;
    Long unsuccessfulTrades = 0L;
    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;
    DatabaseReference databaseReferenceUrl = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewChart = findViewById(R.id.viewBtn);
        chartDialog = new Dialog(this);
//        logoutBtn = findViewById(R.id.logoutBtn);
        tradeButton = findViewById(R.id.tradeButton);
        postButton = findViewById(R.id.postItemButton);
        accsetting = findViewById(R.id.accountSetting);
        pendingCounts = findViewById(R.id.pendingText);
        successfulCounts = findViewById(R.id.successfulText);
        unsuccessfulCounts = findViewById(R.id.unsuccessfulText);
        currentCounts = findViewById(R.id.currentText);
        firebaseAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.userFullName);

        String uid = firebaseAuth.getCurrentUser().getUid();

        bottomNavigationView.setSelectedItemId(R.id.homepage);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.inbox:
                        databaseReferenceUrl.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name = snapshot.child("First_Name").getValue(String.class).concat(" " + snapshot.child("Last_Name").getValue(String.class));
                                Intent intent = new Intent(UserHomepage.this, Messages.class);
                                intent.putExtra("mobile", snapshot.child("Phone").getValue(String.class));
                                intent.putExtra("email", snapshot.child("Email").getValue(String.class));
                                intent.putExtra("name", name);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        return true;
                    case R.id.home:
                        return true;
                    case R.id.offers:
                        Intent intent = new Intent(UserHomepage.this, OfferMainAcitvity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return true;
            }
        });

        name.setText(MemoryData.getFirstName(UserHomepage.this) + "!");

        MemoryData.saveUid(uid, UserHomepage.this);

        databaseReferenceUrl.child("items").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d(TAG,"HERE");
                    pendingTrades = pendingTrades + dataSnapshot.child("Offers").getChildrenCount();
                    pendingCounts.setText(pendingTrades.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserHomepage.this, popup.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("pending", pendingCounts.getText().toString());
                intent.putExtra("current", currentCounts.getText().toString());
                intent.putExtra("successful", successfulCounts.getText().toString());
                intent.putExtra("unsuccessful", unsuccessfulCounts.getText().toString());
                startActivity(intent);
                CustomIntent.customType(UserHomepage.this, "fadein-to-fadeout");
            }
        });

        tradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomepage.this, Categories.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                CustomIntent.customType(UserHomepage.this, "left-to-right");
                finish();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomepage.this, PostItem_S1.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                CustomIntent.customType(UserHomepage.this, "left-to-right");
                finish();
            }
        });

        accsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSinchServiceInterface().stopClient();

                MemoryData.saveUid("", UserHomepage.this);
                MemoryData.saveData("", UserHomepage.this);
                MemoryData.saveName("", UserHomepage.this);
                MemoryData.saveFirstName("", UserHomepage.this);
                MemoryData.saveState(false, UserHomepage.this);

                DatabaseReference df = FirebaseDatabase.getInstance().getReference();
                df.child("users-status").child(uid).child("Status").setValue("Offline");

                Intent intent = new Intent(UserHomepage.this, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onServiceConnected() {

        if (getSinchServiceInterface().isStarted()) {

        } else {

            getSinchServiceInterface().setStartListener(this);
            initializeSinch();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    private void startClientAndOpenPlaceCallActivity() {
        if (!getSinchServiceInterface().isStarted()) {

            getSinchServiceInterface().startClient();
        }
    }

    @Override
    public void onFailed(SinchError error) {

    }

    @Override
    public void onStarted() {

    }

    private void initializeSinch() {
        String username = firebaseAuth.getCurrentUser().getUid();
        getSinchServiceInterface().setUsername(username);

        mUserId = username;
        UserController uc = Sinch.getUserControllerBuilder()
                .context(getApplicationContext())
                .applicationKey(APP_KEY)
                .userId(mUserId)
                .environmentHost(ENVIRONMENT)
                .build();
        uc.registerUser(this, this);
    }

    @Override
    public void onUserRegistrationFailed(SinchError sinchError) {
        Toast.makeText(this, "Registration failed!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserRegistered() {
        // Instance is registered, but we'll wait for another callback, assuring that the push token is
        // registered as well, meaning we can receive incoming calls.
    }

    @Override
    public void onPushTokenRegistered() {
        startClientAndOpenPlaceCallActivity();
    }

    @Override
    public void onPushTokenRegistrationFailed(SinchError sinchError) {
        Toast.makeText(this, "Push token registration failed - incoming calls can't be received!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCredentialsRequired(ClientRegistration clientRegistration) {
        clientRegistration.register(JWT.create(APP_KEY, APP_SECRET, mUserId));
    }
}