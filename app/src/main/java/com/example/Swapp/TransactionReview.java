package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.Swapp.chat.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class TransactionReview extends AppCompatActivity implements BottomSheetImagePicker.OnImagesSelectedListener {

    ImageView parentPic, offeredPic, proofImage;
    TextView parentItemName, parentItemLocation, offeredItemName, offeredItemLocation, uploadError;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    RadioGroup radioGroup;
    TextInputLayout reasonLayout;
    TextInputEditText reason;
    String selectedRdo;
    Button submitReview, uploadBtn;
    CardView transactionRDO, proofImageLayout;
    LoadingDialog loadingDialog;
    String strDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_review);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        String myUid = firebaseAuth.getCurrentUser().getUid();

        strDate = new SimpleDateFormat("MMMM dd, yyyy hh:mm aa", Locale.getDefault()).format(new Date());

        parentPic = findViewById(R.id.parentItemPic);
        offeredPic = findViewById(R.id.offeredItemPic);

        uploadBtn = findViewById(R.id.uploadProof);

        uploadError = findViewById(R.id.noProofError);

        reasonLayout = findViewById(R.id.reviewUnsuccessfulReasonLayout);
        reason = findViewById(R.id.reviewUnsuccessfulReason);

        submitReview = findViewById(R.id.submitReview);

        proofImageLayout = findViewById(R.id.proofImageLayout);
        proofImage = findViewById(R.id.proofImage);

        parentItemName = findViewById(R.id.parentItemName);
        parentItemLocation = findViewById(R.id.parentItemLocation);
        offeredItemName = findViewById(R.id.offeredItemName);
        offeredItemLocation = findViewById(R.id.offeredItemLocation);

        loadingDialog = new LoadingDialog(TransactionReview.this);

        transactionRDO = findViewById(R.id.transactionRdo);

        radioGroup = findViewById(R.id.reviewRadioGroup);

        String transactionKey = getIntent().getStringExtra("transactionKey");

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
                    case "Yes":
                        reasonLayout.setVisibility(View.GONE);
                        reason.setText(null);
                        uploadBtn.setVisibility(View.VISIBLE);
                        selectedRdo = "Successful";
                        break;
                    case "No":
                        uploadError.setVisibility(View.GONE);
                        reasonLayout.setVisibility(View.VISIBLE);
                        uploadBtn.setVisibility(View.GONE);
                        proofImageLayout.setVisibility(View.GONE);
                        proofImage.setImageURI(null);
                        selectedRdo = "Unsuccessful";
                        break;
                }
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(TransactionReview.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TransactionReview.this, new String[]{Manifest.permission.CAMERA}, 0);
                    return;
                }

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

        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedRdo.equals("") || selectedRdo.equals(null)) {
                    uploadError.setVisibility(View.VISIBLE);
                    uploadError.setText("Please select an answer");
                } else {
                    uploadError.setVisibility(View.GONE);
                    uploadError.setText(null);
                }

                if (reasonLayout.getVisibility() == View.VISIBLE) {
                    if (reason.getText().toString().equals(null) || reason.getText().toString().equals("")) {
                        reasonLayout.setError("This cannot be empty");
                        reasonLayout.setErrorIconDrawable(null);
                        return;
                    } else {
                        reasonLayout.setError(null);
                        reasonLayout.setErrorIconDrawable(null);
                    }
                } else if (uploadBtn.getVisibility() == View.VISIBLE) {
                    if (MemoryData.getUri(TransactionReview.this).equals(null) || MemoryData.getUri(TransactionReview.this).equals("") || proofImageLayout.getVisibility() == View.GONE) {
                        uploadError.setVisibility(View.VISIBLE);
                        uploadError.setText("Please upload proof of transaction");
                        return;
                    } else {
                        uploadError.setVisibility(View.GONE);
                        uploadError.setText(null);
                    }
                }

                loadingDialog.startLoadingDialog();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String transactionName = UUID.randomUUID().toString();

                        DatabaseReference databaseReferenceParent = FirebaseDatabase.getInstance().getReference().child("trade-transactions").child(transactionName).child("Posted_Item");
                        DatabaseReference databaseReferenceOfferer = FirebaseDatabase.getInstance().getReference().child("trade-transactions").child(transactionName).child("Offered_Item");

                        DatabaseReference parentItems = FirebaseDatabase.getInstance().getReference().child("items").child(parentkey).child(parentitemname);
                        DatabaseReference offererItems = FirebaseDatabase.getInstance().getReference().child("items").child(parentkey).child(parentitemname).child("Accepted_Offers").child(offererkey);

                        if (parentkey.equals(myUid)) {
                            UploadToTransact(parentItems, databaseReferenceParent);
                            databaseReference.child("trade-transactions").child(transactionName).child("Poster_Response").setValue(selectedRdo);
                            databaseReference.child("trade-transactions").child(transactionName).child("Transaction_Status").setValue("Waiting for review");
                            UploadToTransact(offererItems, databaseReferenceOfferer);

                            if (selectedRdo.equals("Successful")) {
                                parentItems.child("Open_For_Offers").setValue("false");
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/trades-transact/" + transactionName + "/" + "Poster_Proof");

                                Uri uri = Uri.parse(MemoryData.getUri(TransactionReview.this));

                                UploadTask uploadTask = storageReference.putFile(uri);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                databaseReference.child("trade-transactions").child(transactionName).child("Proof_Images").child("Poster_Proof").setValue(task.getResult().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                                                        databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Submitted a review for transaction " + transactionName);

                                                                        loadingDialog.DismissDialog();
                                                                        Intent intent = new Intent(TransactionReview.this, MyItemCurrentTransaction.class);
                                                                        startActivity(intent);
                                                                        CustomIntent.customType(TransactionReview.this, "left-to-right");
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
                                });
                            } else {
                                parentItems.child("Open_For_Offers").setValue("true");
                                databaseReference.child("trade-transactions").child(transactionName).child("Poster_Unsuccessful_Reason").setValue(reason.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                                        databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Submitted a review for transaction " + transactionName);

                                                        loadingDialog.DismissDialog();
                                                        Intent intent = new Intent(TransactionReview.this, MyItemCurrentTransaction.class);
                                                        startActivity(intent);
                                                        CustomIntent.customType(TransactionReview.this, "left-to-right");
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
                        } else {
                            databaseReference.child("trade-transactions").child(transactionKey).child("Offerer_Response").setValue(selectedRdo);

                            databaseReference.child("trade-transactions").child(transactionKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.child("Poster_Response").getValue(String.class).equals(selectedRdo)) {
                                        if (selectedRdo.equals("Successful")) {
                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/trades-transact/" + transactionKey + "/" + "Offerer_Proof");

                                            Uri uri = Uri.parse(MemoryData.getUri(TransactionReview.this));

                                            UploadTask uploadTask = storageReference.putFile(uri);

                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {
                                                            databaseReference.child("trade-transactions").child(transactionKey).child("Proof_Images").child("Offerer_Proof").setValue(task.getResult().toString());
                                                        }
                                                    });
                                                }
                                            });

                                            databaseReference.child("trade-transactions").child(transactionKey).child("Transaction_Status").setValue("Successful");

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    parentItems.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                            Log.w("TAG", snapshot1.child("Accepted_Offers").getChildrenCount() + "");
                                                            long removeCurrent = snapshot1.child("Accepted_Offers").getChildrenCount();
                                                            long newCurrentParent = Long.parseLong(snapshot.child("user-transactions").child(parentkey).child("Current").getValue(String.class));
                                                            newCurrentParent = newCurrentParent - removeCurrent;

                                                            databaseReference.child("user-transactions").child(parentkey).child("Current").setValue(String.valueOf(newCurrentParent));

                                                            long newCurrentOfferer = Long.parseLong(snapshot.child("user-transactions").child(offererkey).child("Current").getValue(String.class));
                                                            newCurrentOfferer = newCurrentOfferer - 1;

                                                            databaseReference.child("user-transactions").child(offererkey).child("Current").setValue(String.valueOf(newCurrentOfferer));

                                                            if (snapshot.child("user-transactions").child(parentkey).child("Successful").exists()) {
                                                                int newSuccessParent = Integer.parseInt(snapshot.child("user-transactions").child(parentkey).child("Successful").getValue(String.class));
                                                                newSuccessParent = newSuccessParent + 1;

                                                                databaseReference.child("user-transactions").child(parentkey).child("Successful").setValue(String.valueOf(newSuccessParent));
                                                            } else {
                                                                databaseReference.child("user-transactions").child(parentkey).child("Successful").setValue("1");
                                                            }

                                                            if (snapshot.child("user-transactions").child(offererkey).child("Successful").exists()) {
                                                                int newSuccessOfferer = Integer.parseInt(snapshot.child("user-transactions").child(offererkey).child("Successful").getValue(String.class));
                                                                newSuccessOfferer = newSuccessOfferer + 1;

                                                                databaseReference.child("user-transactions").child(offererkey).child("Successful").setValue(String.valueOf(newSuccessOfferer));
                                                            } else {
                                                                databaseReference.child("user-transactions").child(offererkey).child("Successful").setValue("1");
                                                            }

                                                            parentItems.removeValue();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        } else {
                                            databaseReference.child("trade-transactions").child(transactionKey).child("Offerer_Unsuccessful_Reason").setValue(reason.getText().toString());

                                            databaseReference.child("trade-transactions").child(transactionKey).child("Transaction_Status").setValue("Unsuccessful");

                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    int newCurrentParent = Integer.parseInt(snapshot.child("user-transactions").child(parentkey).child("Current").getValue(String.class));
                                                    newCurrentParent = newCurrentParent - 1;

                                                    int newCurrentOfferer = Integer.parseInt(snapshot.child("user-transactions").child(offererkey).child("Current").getValue(String.class));
                                                    newCurrentOfferer = newCurrentOfferer - 1;

                                                    databaseReference.child("user-transactions").child(parentkey).child("Current").setValue(String.valueOf(newCurrentParent));
                                                    databaseReference.child("user-transactions").child(offererkey).child("Current").setValue(String.valueOf(newCurrentOfferer));

                                                    if (snapshot.child("user-transactions").child(parentkey).child("Unsuccessful").exists()) {
                                                        int newUnsuccessfulParent = Integer.parseInt(snapshot.child("user-transactions").child(parentkey).child("Unsuccessful").getValue(String.class));
                                                        newUnsuccessfulParent = newUnsuccessfulParent + 1;

                                                        databaseReference.child("user-transactions").child(parentkey).child("Unsuccessful").setValue(String.valueOf(newUnsuccessfulParent));
                                                    } else {
                                                        databaseReference.child("user-transactions").child(parentkey).child("Unsuccessful").setValue("1");
                                                    }

                                                    if (snapshot.child("user-transactions").child(offererkey).child("Unsuccessful").exists()) {
                                                        int newUnsuccessfulOfferer = Integer.parseInt(snapshot.child("user-transactions").child(offererkey).child("Unsuccessful").getValue(String.class));
                                                        newUnsuccessfulOfferer = newUnsuccessfulOfferer + 1;

                                                        databaseReference.child("user-transactions").child(offererkey).child("Unsuccessful").setValue(String.valueOf(newUnsuccessfulOfferer));
                                                    } else {
                                                        databaseReference.child("user-transactions").child(offererkey).child("Unsuccessful").setValue("1");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            offererItems.removeValue();
                                        }


                                    } else {
                                        parentItems.child("Open_For_Offers").setValue("false");
                                        databaseReference.child("trade-transactions").child(transactionKey).child("Transaction_Status").setValue("On hold");

                                        if (selectedRdo.equals("Successful")) {
                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/trades-transact/" + transactionKey + "/" + "Offerer_Proof");

                                            Uri uri = Uri.parse(MemoryData.getUri(TransactionReview.this));

                                            UploadTask uploadTask = storageReference.putFile(uri);

                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {
                                                            databaseReference.child("trade-transactions").child(transactionKey).child("Proof_Images").child("Offerer_Proof").setValue(task.getResult().toString());
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            databaseReference.child("trade-transactions").child(transactionKey).child("Offerer_Unsuccessful_Reason").setValue(reason.getText().toString());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Date").setValue(strDate);
                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("User_ID").setValue(offererkey);
                                    databaseReference.child("activity-logs").child(String.valueOf(snapshot.child("activity-logs").getChildrenCount() + 1)).child("Activity").setValue("Submitted a review for transaction " + transactionKey);

                                    loadingDialog.DismissDialog();
                                    Intent intent = new Intent(TransactionReview.this, MyItemCurrentTransaction.class);
                                    startActivity(intent);
                                    CustomIntent.customType(TransactionReview.this, "left-to-right");
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
        });
    }

    private void UploadToTransact(DatabaseReference items, DatabaseReference toPath) {
        items.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String category = snapshot.child("Item_Category").getValue(String.class);
                switch (category) {
                    case "Men's Apparel":
                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Type").setValue(snapshot.child("Item_Type").getValue(String.class));
                                toPath.child("Item_Brand").setValue(snapshot.child("Item_Brand").getValue(String.class));
                                toPath.child("Item_Color").setValue(snapshot.child("Item_Color").getValue(String.class));
                                toPath.child("Item_Material").setValue(snapshot.child("Item_Material").getValue(String.class));
                                toPath.child("Item_Usage").setValue(snapshot.child("Item_Usage").getValue(String.class));
                                toPath.child("Item_Sizes").setValue(snapshot.child("Item_Sizes").getValue(String.class));
                                toPath.child("Item_Description").setValue(snapshot.child("Item_Description").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        break;
                    case "Women's Apparel":
                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                Log.w("WOMENS", "");

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Type").setValue(snapshot.child("Item_Type").getValue(String.class));
                                toPath.child("Item_Brand").setValue(snapshot.child("Item_Brand").getValue(String.class));
                                toPath.child("Item_Color").setValue(snapshot.child("Item_Color").getValue(String.class));
                                toPath.child("Item_Material").setValue(snapshot.child("Item_Material").getValue(String.class));
                                toPath.child("Item_Usage").setValue(snapshot.child("Item_Usage").getValue(String.class));
                                toPath.child("Item_Sizes").setValue(snapshot.child("Item_Sizes").getValue(String.class));
                                toPath.child("Item_Description").setValue(snapshot.child("Item_Description").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    case "Gadgets":
                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Type").setValue(snapshot.child("Item_Type").getValue(String.class));
                                toPath.child("Item_Brand").setValue(snapshot.child("Item_Brand").getValue(String.class));
                                toPath.child("Item_Color").setValue(snapshot.child("Item_Color").getValue(String.class));
                                toPath.child("Item_Usage").setValue(snapshot.child("Item_Usage").getValue(String.class));
                                toPath.child("Item_Description").setValue(snapshot.child("Item_Description").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        break;
                    case "Game":

                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Type").setValue(snapshot.child("Item_Type").getValue(String.class));
                                toPath.child("Item_Brand").setValue(snapshot.child("Item_Brand").getValue(String.class));
                                toPath.child("Item_Usage").setValue(snapshot.child("Item_Usage").getValue(String.class));
                                toPath.child("Item_Description").setValue(snapshot.child("Item_Description").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    case "Bags":

                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Type").setValue(snapshot.child("Item_Type").getValue(String.class));
                                toPath.child("Item_Brand").setValue(snapshot.child("Item_Brand").getValue(String.class));
                                toPath.child("Item_Usage").setValue(snapshot.child("Item_Usage").getValue(String.class));
                                toPath.child("Item_Color").setValue(snapshot.child("Item_Color").getValue(String.class));
                                toPath.child("Item_Description").setValue(snapshot.child("Item_Description").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    case "Groceries":

                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Type").setValue(snapshot.child("Item_Type").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    case "Furniture":

                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Height").setValue(snapshot.child("Item_Height").getValue(String.class));
                                toPath.child("Item_Width").setValue(snapshot.child("Item_Width").getValue(String.class));
                                toPath.child("Item_Length").setValue(snapshot.child("Item_Length").getValue(String.class));
                                toPath.child("Item_Brand").setValue(snapshot.child("Item_Brand").getValue(String.class));
                                toPath.child("Item_Usage").setValue(snapshot.child("Item_Usage").getValue(String.class));
                                toPath.child("Item_Color").setValue(snapshot.child("Item_Color").getValue(String.class));
                                toPath.child("Item_Description").setValue(snapshot.child("Item_Description").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    case "Babies & Kids":

                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Type").setValue(snapshot.child("Item_Type").getValue(String.class));
                                toPath.child("Item_Brand").setValue(snapshot.child("Item_Brand").getValue(String.class));
                                toPath.child("Item_Usage").setValue(snapshot.child("Item_Usage").getValue(String.class));
                                toPath.child("Item_Age").setValue(snapshot.child("Item_Age").getValue(String.class));
                                toPath.child("Item_Description").setValue(snapshot.child("Item_Description").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    case "Appliances":

                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                Log.w("TAG", "Appliances");

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Type").setValue(snapshot.child("Item_Type").getValue(String.class));
                                toPath.child("Item_Brand").setValue(snapshot.child("Item_Brand").getValue(String.class));
                                toPath.child("Item_Usage").setValue(snapshot.child("Item_Usage").getValue(String.class));
                                toPath.child("Item_Color").setValue(snapshot.child("Item_Color").getValue(String.class));
                                toPath.child("Item_Description").setValue(snapshot.child("Item_Description").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    case "Motors":

                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Model").setValue(snapshot.child("Item_Model").getValue(String.class));
                                toPath.child("Item_Brand").setValue(snapshot.child("Item_Brand").getValue(String.class));
                                toPath.child("Item_Usage").setValue(snapshot.child("Item_Usage").getValue(String.class));
                                toPath.child("Item_Color").setValue(snapshot.child("Item_Color").getValue(String.class));
                                toPath.child("Item_Description").setValue(snapshot.child("Item_Description").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    case "Audio":

                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Artist").setValue(snapshot.child("Item_Artist").getValue(String.class));
                                toPath.child("Item_Usage").setValue(snapshot.child("Item_Usage").getValue(String.class));
                                toPath.child("Item_ReleaseDate").setValue(snapshot.child("Item_ReleaseDate").getValue(String.class));
                                toPath.child("Item_Description").setValue(snapshot.child("Item_Description").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    case "School":

                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                Log.w("TAG", "School");

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Type").setValue(snapshot.child("Item_Type").getValue(String.class));
                                toPath.child("Item_Brand").setValue(snapshot.child("Item_Brand").getValue(String.class));
                                toPath.child("Item_Usage").setValue(snapshot.child("Item_Usage").getValue(String.class));
                                toPath.child("Item_Color").setValue(snapshot.child("Item_Color").getValue(String.class));
                                toPath.child("Item_Description").setValue(snapshot.child("Item_Description").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    case "Others":

                        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                                toPath.child("Poster_Name").setValue(snapshot.child("Poster_Name").getValue(String.class));
                                toPath.child("Poster_UID").setValue(snapshot.child("Poster_UID").getValue(String.class));
                                toPath.child("Item_Type").setValue(snapshot.child("Item_Type").getValue(String.class));
                                toPath.child("Item_Description").setValue(snapshot.child("Item_Description").getValue(String.class));
                                toPath.child("Item_Name").setValue(snapshot.child("Item_Name").getValue(String.class));
                                toPath.child("Item_Category").setValue(snapshot.child("Item_Category").getValue(String.class));
                                toPath.child("Address").child("Street").setValue(snapshot.child("Address").child("Street").getValue(String.class));
                                toPath.child("Address").child("Barangay").setValue(snapshot.child("Address").child("Barangay").getValue(String.class));
                                toPath.child("Address").child("City").setValue(snapshot.child("Address").child("City").getValue(String.class));
                                toPath.child("Address").child("State").setValue(snapshot.child("Address").child("State").getValue(String.class));
                                toPath.child("Address").child("Country").setValue(snapshot.child("Address").child("Country").getValue(String.class));
                                toPath.child("Address").child("Latitude").setValue(snapshot.child("Address").child("Latitude").getValue(String.class));
                                toPath.child("Address").child("Longitude").setValue(snapshot.child("Address").child("Longitude").getValue(String.class));

                                for (int i = 1; i <= snapshot.child("Images").getChildrenCount(); i++) {
                                    toPath.child("Images").child(String.valueOf(i)).setValue(snapshot.child("Images").child(String.valueOf(i)).getValue(String.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onImagesSelected(@NotNull List<? extends Uri> uris, @Nullable String tag) {
        for (Uri uri : uris) {
            proofImageLayout.setVisibility(View.VISIBLE);
            Glide.with(this).load(uri).into(proofImage);

            MemoryData.saveUri(uri.toString(), TransactionReview.this);
        }
    }
}