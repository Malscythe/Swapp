package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Swapp.R;

public class TransactionReview extends AppCompatActivity {

    ImageView parentPic, offeredPic;
    TextView parentItemName, parentItemLocation, offeredItemName, offeredItemLocation;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    RadioGroup radioGroup;
    TextInputLayout reasonLayout;
    TextInputEditText reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_review);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        parentPic = findViewById(R.id.parentItemPic);
        offeredPic = findViewById(R.id.offeredItemPic);

        reasonLayout = findViewById(R.id.reviewUnsuccessfulReasonLayout);
        reason = findViewById(R.id.reviewUnsuccessfulReason);

        parentItemName = findViewById(R.id.parentItemName);
        parentItemLocation = findViewById(R.id.parentItemLocation);
        offeredItemName = findViewById(R.id.offeredItemName);
        offeredItemLocation = findViewById(R.id.offeredItemLocation);

        radioGroup = findViewById(R.id.reviewRadioGroup);

        String parentkey = getIntent().getStringExtra("ParentKey");
        String parentitemname = getIntent().getStringExtra("ParentItemName");
        String offererkey = getIntent().getStringExtra("OffererKey");

        databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String parentAddress = snapshot.child(parentkey).child(parentitemname).child("Address").child("City").getValue(String.class).concat(", " + snapshot.child(parentkey).child(parentitemname).child("Address").child("State").getValue(String.class));
                String offeredAddress = snapshot.child(parentkey).child(parentitemname).child("Accepted_Offers").child(offererkey).child("Address").child("City").getValue(String.class).concat(", " + snapshot.child(parentkey).child(parentitemname).child("Accepted_Offers").child(offererkey).child("Address").child("State").getValue(String.class));

                parentItemName.setText(snapshot.child(parentkey).child(parentitemname).child("Item_Name").getValue(String.class));
                parentItemLocation.setText(parentAddress);
                offeredItemName.setText(snapshot.child(parentkey).child(parentitemname).child("Accepted_Offers").child(offererkey).child("Item_Name").getValue(String.class));
                offeredItemLocation.setText(offeredAddress);

                Glide.with(TransactionReview.this).load(snapshot.child(parentkey).child(parentitemname).child("Images").child("1").getValue(String.class)).into(parentPic);
                Glide.with(TransactionReview.this).load(snapshot.child(parentkey).child(parentitemname).child("Accepted_Offers").child(offererkey).child("Images").child("1").getValue(String.class)).into(offeredPic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedButton = findViewById(checkedId);

                switch (selectedButton.getText().toString()) {
                    case "Yes" :
                        reasonLayout.setVisibility(View.GONE);
                        break;
                    case "No" :
                        reasonLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }
}