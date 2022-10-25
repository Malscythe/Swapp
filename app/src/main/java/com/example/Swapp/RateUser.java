package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class RateUser extends AppCompatActivity {

    TextView userName, errorMessage;
    ImageView userPic;
    RatingBar userRating;
    TextInputLayout userFeedbackLayout;
    TextInputEditText userFeedback;
    Button submitBtn;
    int selectedRating;
    String strDate;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    float newAvgRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_user);

        strDate = new SimpleDateFormat("MMMM dd, yyyy hh:mm aa", Locale.getDefault()).format(new Date());

        String myUid = firebaseAuth.getCurrentUser().getUid();

        userName = findViewById(R.id.userName);
        userPic = findViewById(R.id.userPicture);
        userRating = findViewById(R.id.userRating);
        userFeedbackLayout = findViewById(R.id.userFeedbackLayout);
        userFeedback = findViewById(R.id.userFeedback);
        submitBtn = findViewById(R.id.submitRating);
        errorMessage = findViewById(R.id.errorMessage);

        String userToRateID = getIntent().getStringExtra("uid");
        String userToRateName = getIntent().getStringExtra("name");
        String transactionKey = getIntent().getStringExtra("transactionKey");

        userName.setText(userToRateName);

        userRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                int int_rating = (int) rating;

                switch (int_rating) {
                    case 1:
                        selectedRating = 1;
                        break;
                    case 2:
                        selectedRating = 2;
                        break;
                    case 3:
                        selectedRating = 3;
                        break;
                    case 4:
                        selectedRating = 4;
                        break;
                    case 5:
                        selectedRating = 5;
                        break;
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userRating.getRating() == 0f) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Rating must greater than 1");
                    return;
                } else {
                    errorMessage.setVisibility(View.GONE);
                }


                if (userFeedback.getText().toString().equals(null) || userFeedback.getText().toString().equals("")) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Please input a feedback");
                    return;
                } else {
                    errorMessage.setVisibility(View.GONE);
                }

                String userRatings = "rating" + selectedRating;
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int newRating = Integer.parseInt(snapshot.child("user-rating").child(userToRateID).child(userRatings).getValue(String.class));
                        newRating = newRating + 1;

                        databaseReference.child("user-rating").child(userToRateID).child(userRatings).setValue(String.valueOf(newRating)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                float rating1 = Float.parseFloat(snapshot1.child("user-rating").child(userToRateID).child("rating1").getValue(String.class));
                                                float rating2 = Float.parseFloat(snapshot1.child("user-rating").child(userToRateID).child("rating2").getValue(String.class));
                                                float rating3 = Float.parseFloat(snapshot1.child("user-rating").child(userToRateID).child("rating3").getValue(String.class));
                                                float rating4 = Float.parseFloat(snapshot1.child("user-rating").child(userToRateID).child("rating4").getValue(String.class));
                                                float rating5 = Float.parseFloat(snapshot1.child("user-rating").child(userToRateID).child("rating5").getValue(String.class));

                                                if (snapshot1.child("user-rating").child(userToRateID).child("Average_Rating").getValue(String.class).equals("0") || snapshot1.child("user-rating").child(userToRateID).child("Average_Rating").getValue(String.class).equals("0.0")) {
                                                    newAvgRating = selectedRating;
                                                } else {
                                                    newAvgRating = ((1 * rating1) + (2 * rating2) + (3 * rating3) + (4 * rating4) + (5 * rating5)) / (rating1 + rating2 + rating3 + rating4 + rating5);
                                                }

                                                databaseReference.child("user-rating").child(userToRateID).child("Average_Rating").setValue(String.format("%.1f", newAvgRating));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                databaseReference.child("user-rating").child(userToRateID).child("transactions").child(transactionKey).child("Rate").setValue(selectedRating);
                databaseReference.child("user-rating").child(userToRateID).child("transactions").child(transactionKey).child("Rated_By").setValue(myUid);
                databaseReference.child("user-rating").child(userToRateID).child("transactions").child(transactionKey).child("Feedback").setValue(userFeedback.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                        databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(myUid);
                                        databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Gave feedback to " + userToRateID + " for transaction " + transactionKey);

                                        Intent intent = new Intent(RateUser.this, MyItemCurrentTransaction.class);
                                        startActivity(intent);
                                        CustomIntent.customType(RateUser.this, "right-to-left");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}