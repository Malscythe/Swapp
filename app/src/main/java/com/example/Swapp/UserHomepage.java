package com.example.Swapp;

import static com.example.Swapp.call.SinchService.APP_KEY;
import static com.example.Swapp.call.SinchService.APP_SECRET;
import static com.example.Swapp.call.SinchService.ENVIRONMENT;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.Swapp.call.BaseActivity;
import com.example.Swapp.call.SinchService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushTokenRegistrationCallback;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.UserController;
import com.sinch.android.rtc.UserRegistrationCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import Swapp.R;
import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class UserHomepage extends BaseActivity implements SinchService.StartFailedListener, PushTokenRegistrationCallback, UserRegistrationCallback {

    private static final String TAG = "TAG";
    private String mUserId;

    TextView pendingCounts, successfulCounts, unsuccessfulCounts, currentCounts, name;
    Dialog chartDialog;
    ImageView viewChart;
    CircleImageView userProfile;
    CardView tradeButton, postButton;
    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;
    DatabaseReference databaseReferenceUrl = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");
    String strDate;
    DrawerLayout drawerLayout;
    String uid;
    View mDecorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        strDate = new SimpleDateFormat("MMMM dd, yyyy hh:mm aa", Locale.getDefault()).format(new Date());

        mDecorView = getWindow().getDecorView();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewChart = findViewById(R.id.viewBtn);
        chartDialog = new Dialog(this);
        userProfile = findViewById(R.id.userProfile);
        tradeButton = findViewById(R.id.tradeButton);
        postButton = findViewById(R.id.postItemButton);
        pendingCounts = findViewById(R.id.pendingText);
        successfulCounts = findViewById(R.id.successfulText);
        unsuccessfulCounts = findViewById(R.id.unsuccessfulText);
        currentCounts = findViewById(R.id.currentText);
        firebaseAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.userFullName);

        uid = firebaseAuth.getCurrentUser().getUid();

        drawerLayout = findViewById(R.id.drawer_layout);

        databaseReferenceUrl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Uri uri = Uri.parse(snapshot.child("users").child(uid).child("User_Profile").getValue(String.class));

                Glide.with(UserHomepage.this).load(uri).into(userProfile);
                name.setText(snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat("!"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                    case R.id.transactions:
                        Intent intent1 = new Intent(UserHomepage.this, MyItemCurrentTransaction.class);
                        startActivity(intent1);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return true;
            }
        });

        MemoryData.saveUid(uid, UserHomepage.this);

        if (getIntent().hasExtra("Origin")) {
            if (getIntent().getStringExtra("Origin").equals("PostItem")) {
                startActivity(getIntent());
            }
        }

        databaseReferenceUrl.child("user-transactions").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Successful").exists()) {
                    successfulCounts.setText(snapshot.child("Successful").getValue(String.class));
                } else {
                    successfulCounts.setText("0");
                }

                if (snapshot.child("Unsuccessful").exists()) {
                    unsuccessfulCounts.setText(snapshot.child("Unsuccessful").getValue(String.class));
                } else {
                    unsuccessfulCounts.setText("0");
                }

                if (snapshot.child("Pending").exists()) {
                    pendingCounts.setText(snapshot.child("Pending").getValue(String.class));
                } else {
                    pendingCounts.setText("0");
                }

                if (snapshot.child("Current").exists()) {
                    currentCounts.setText(snapshot.child("Current").getValue(String.class));
                } else {
                    currentCounts.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String token = Objects.requireNonNull(task.getResult());
                    databaseReferenceUrl.child("tokens").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(uid).child("User_Token").exists()) {
                                if (!snapshot.child(uid).child("User_Token").getValue(String.class).equals(token)) {
                                    databaseReferenceUrl.child("tokens").child(uid).child("User_Token").setValue(token);
                                }
                            } else {
                                databaseReferenceUrl.child("tokens").child(uid).child("User_Token").setValue(token);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
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

        drawerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer(drawerLayout);
            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RatingBar userRating = findViewById(R.id.userRating);
                TextView userName = findViewById(R.id.userName);
                CircleImageView userProfilePic = findViewById(R.id.profilePic);

                databaseReferenceUrl.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = snapshot.child("users").child(uid).child("First_Name").getValue(String.class).concat(" " + snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));

                        userName.setText(username);
                        userRating.setRating(Float.parseFloat(snapshot.child("user-rating").child(uid).child("Average_Rating").getValue(String.class)));

                        if (snapshot.child("users").child(uid).hasChild("User_Profile")) {
                            Uri uri = Uri.parse(snapshot.child("users").child(uid).child("User_Profile").getValue(String.class));
                            Glide.with(UserHomepage.this).load(uri).into(userProfilePic);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                openDrawer(drawerLayout);
            }
        });
    }

    private void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.END);
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
        closeDrawer(drawerLayout);
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
    }

    @Override
    public void onUserRegistered() {
    }

    @Override
    public void onPushTokenRegistered() {
        startClientAndOpenPlaceCallActivity();
    }

    @Override
    public void onPushTokenRegistrationFailed(SinchError sinchError) {
        Log.d(TAG, sinchError.getMessage());
    }

    @Override
    public void onCredentialsRequired(ClientRegistration clientRegistration) {
        clientRegistration.register(JWT.create(APP_KEY, APP_SECRET, mUserId));

    }

    public void clickLogout(View view) {
        getSinchServiceInterface().stopClient();

        MemoryData.saveUid("", UserHomepage.this);
        MemoryData.saveData("", UserHomepage.this);
        MemoryData.saveName("", UserHomepage.this);
        MemoryData.saveFirstName("", UserHomepage.this);
        MemoryData.saveState(false, UserHomepage.this);

        DatabaseReference df = FirebaseDatabase.getInstance().getReference();

        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                df.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                df.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(uid);
                df.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Logged Out");
                df.child("users-status").child(uid).child("Status").setValue("Offline");

                Intent intent = new Intent(UserHomepage.this, login.class);
                intent.putExtra("logoutFrom", "User");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                CustomIntent.customType(UserHomepage.this, "right-to-left");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void clickChangePass(View view) {
        Intent intent = new Intent(UserHomepage.this, ChangePassword.class);
        startActivity(intent);
        CustomIntent.customType(UserHomepage.this, "left-to-right");
    }

    public void clickAccountDetails(View view) {
        Intent intent = new Intent(UserHomepage.this, AccountSettings.class);
        startActivity(intent);
        CustomIntent.customType(UserHomepage.this, "left-to-right");
    }
}