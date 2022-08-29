package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import Swapp.R;
import Swapp.databinding.ActivityOfferItemBinding;
import Swapp.databinding.ActivityPostItemBinding;

public class offerItem extends AppCompatActivity {

    private ActivityOfferItemBinding binding;
    public static final String TAG = "TAG";
    Uri imageUri;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;

    FirebaseAuth firebaseAuth;


    PostingItemDialog postingItemDialog = new PostingItemDialog(offerItem.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOfferItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String item_id = getIntent().getStringExtra("itemid");
        Log.w(TAG, item_id);

        String[] categoriesArr = getResources().getStringArray(R.array.categories);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item, categoriesArr);
        binding.itemCategory.setAdapter(arrayAdapter);

        String[] locationsArr = getResources().getStringArray(R.array.locations);
        arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item, locationsArr);
        binding.itemLocation.setAdapter(arrayAdapter);

        binding.selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        binding.offerItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadItem();

            }
        });
    }

    private void uploadItem() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        String fileName  = binding.itemName.getText() + "-" + uid;
        String item_id = getIntent().getStringExtra("itemid");

        if (TextUtils.isEmpty(binding.itemName.getText().toString())) {
            binding.itemNameL.setError("This cannot be empty.");
            binding.itemNameL.setErrorIconDrawable(null);
            return;
        } else {
            binding.itemNameL.setError(null);
            binding.itemNameL.setErrorIconDrawable(null);
        }

        if (TextUtils.isEmpty(binding.itemCategory.getText().toString())) {
            binding.itemCategoryL.setError("This cannot be empty.");
            binding.itemCategoryL.setErrorIconDrawable(null);
            return;
        } else {
            binding.itemCategoryL.setError(null);
            binding.itemCategoryL.setErrorIconDrawable(null);
        }

        if (TextUtils.isEmpty(binding.itemLocation.getText().toString())) {
            binding.itemLocationL.setError("This cannot be empty.");
            binding.itemLocationL.setErrorIconDrawable(null);
            return;
        } else {
            binding.itemLocationL.setError(null);
            binding.itemLocationL.setErrorIconDrawable(null);
        }

        if (TextUtils.isEmpty(binding.itemDesc.getText().toString())) {
            binding.itemDescL.setError("This cannot be empty.");
            binding.itemDescL.setErrorIconDrawable(null);
            return;
        } else {
            binding.itemDescL.setError(null);
            binding.itemDescL.setErrorIconDrawable(null);
        }

        if (imageUri == null){
            Uri noimageUri = (new Uri.Builder())
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(getResources().getResourcePackageName(R.drawable.noimage))
                    .appendPath(getResources().getResourcePackageName(R.drawable.noimage))
                    .appendPath(getResources().getResourcePackageName(R.drawable.noimage))
                    .build();
            imageUri = noimageUri;
        }

        postingItemDialog.startLoadingDialog();

        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);
        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap = Bitmap.createScaledBitmap(bitmap, 720, 720, false);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                binding.itemImage.setImageResource(R.drawable.noimage);
                DatabaseReference insertItems = FirebaseDatabase.getInstance().getReference().child("items").child(item_id).child("Offers").child(uid);
                DocumentReference docRef = firebaseFirestore.collection("users").document(uid);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        insertItems.child("Image_Url").setValue(task.getResult().toString());
                                    }
                                });

                                insertItems.child("Item_Name").setValue(binding.itemName.getText().toString());
                                insertItems.child("Item_Description").setValue(binding.itemDesc.getText().toString());
                                insertItems.child("Item_Category").setValue(binding.itemCategory.getText().toString());
                                insertItems.child("Item_Location").setValue(binding.itemLocation.getText().toString());

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

                postingItemDialog.DismissDialog();

                Toast toast = new Toast(getApplicationContext());
                View view = LayoutInflater.from(offerItem.this).inflate(R.layout.toast_layout, null);
                TextView toastMessage = view.findViewById(R.id.toastMessage);
                toastMessage.setText("Item successfully posted!");
                toast.setView(view);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0,50);
                toast.show();

                startActivity(new Intent(offerItem.this, UserHomepage.class));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                postingItemDialog.DismissDialog();

                Toast toast = new Toast(getApplicationContext());
                View view = LayoutInflater.from(offerItem.this).inflate(R.layout.toast_error_layout, null);
                TextView toastMessage = view.findViewById(R.id.toastMessage);
                toastMessage.setText("Item image is required.");
                toast.setView(view);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0,50);
                toast.show();
            }
        });
    }

    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && data != null && data.getData() != null) {

            imageUri = data.getData();
            binding.itemImage.setImageURI(imageUri);

        }


    }
}