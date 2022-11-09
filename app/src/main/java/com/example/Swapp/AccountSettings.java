package com.example.Swapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;

import java.util.List;

import Swapp.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class AccountSettings extends AppCompatActivity implements BottomSheetImagePicker.OnImagesSelectedListener {

    CircleImageView userPic;
    TextInputEditText firstName, lastName;
    TextInputLayout uFirstName, uLastName;
    Button upload, confirm;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid;
    boolean uploaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        userPic = findViewById(R.id.userProfilePic);
        firstName = findViewById(R.id.userFirstName);
        lastName = findViewById(R.id.userLastName);
        uFirstName = findViewById(R.id.userFirstNameLayout);
        uLastName = findViewById(R.id.userLastNameLayout);
        upload = findViewById(R.id.uploadPhoto);
        confirm = findViewById(R.id.updateProfile);

        uid = firebaseAuth.getCurrentUser().getUid();

        uploaded = false;

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Uri uri = Uri.parse(snapshot.child("users").child(uid).child("User_Profile").getValue(String.class));
                Glide.with(AccountSettings.this).load(uri).into(userPic);

                firstName.setText(snapshot.child("users").child(uid).child("First_Name").getValue(String.class));
                lastName.setText(snapshot.child("users").child(uid).child("Last_Name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                        .cameraButton(ButtonType.Button)
                        .galleryButton(ButtonType.Button)
                        .singleSelectTitle(R.string.pick_single)
                        .peekHeight(R.dimen.peekHeight)
                        .columnSize(R.dimen.columnSize)
                        .requestTag("single")
                        .show(getSupportFragmentManager(), null);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (firstName.getText().toString().isEmpty()) {
                    uFirstName.setErrorIconDrawable(null);
                    uFirstName.setError("This cannot be empty");
                    return;
                } else {
                    uFirstName.setErrorIconDrawable(null);
                    uFirstName.setError(null);
                }

                if (lastName.getText().toString().isEmpty()) {
                    uLastName.setErrorIconDrawable(null);
                    uLastName.setError("This cannot be empty");
                    return;
                } else {
                    uLastName.setErrorIconDrawable(null);
                    uLastName.setError(null);
                }

                if (uploaded) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/users/" + uid);

                    Uri uri = Uri.parse(MemoryData.getUri(AccountSettings.this));

                    UploadTask uploadTask = storageReference.putFile(uri);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    databaseReference.child("users").child(uid).child("User_Profile").setValue(task.getResult().toString());
                                }
                            });
                        }
                    });
                }

                databaseReference.child("users").child(uid).child("First_Name").setValue(firstName.getText().toString());
                databaseReference.child("users").child(uid).child("Last_Name").setValue(lastName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                new SweetAlertDialog(AccountSettings.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Update Account Details")
                                        .setContentText("Your account details has been updated!")
                                        .show();
                            }
                        });
                    }
                });



            }
        });


    }

    @Override
    public void onImagesSelected(@NonNull List<? extends Uri> list, @Nullable String s) {
        uploaded = true;
        for (Uri uri : list) {

            Glide.with(this).load(uri).into(userPic);

            MemoryData.saveUri(uri.toString(), AccountSettings.this);
        }
    }

    public void clickBack(View view) {
        Intent intent = new Intent(AccountSettings.this, UserHomepage.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        CustomIntent.customType(AccountSettings.this, "right-to-left");
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AccountSettings.this, UserHomepage.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        CustomIntent.customType(AccountSettings.this, "right-to-left");
        finish();
    }
}
