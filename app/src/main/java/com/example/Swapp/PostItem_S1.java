package com.example.Swapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.kofigyan.stateprogressbar.StateProgressBar;

import Swapp.R;
import Swapp.databinding.ActivityPostItemS1Binding;
import maes.tech.intentanim.CustomIntent;

public class PostItem_S1 extends AppCompatActivity {

    private ActivityPostItemS1Binding binding;
    public static final String TAG = "TAG";
    PostingItemDialog postingItemDialog = new PostingItemDialog(PostItem_S1.this);
    String[] descriptionData = {"Details", "Description", "Location", "Images"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostItemS1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StateProgressBar stateProgressBar = findViewById(R.id.stateProgress);
        stateProgressBar.setStateDescriptionData(descriptionData);

        String[] categoriesArr = getResources().getStringArray(R.array.categories);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item, categoriesArr);
        binding.itemCategory.setAdapter(arrayAdapter);

       /* binding.selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });*/

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadItem();
            }
        });


        /*binding.itemLocationL.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostItem.this, currentLoc.class);
                intent.putExtra("from", "postitem");
                startActivity(intent);
                CustomIntent.customType(PostItem.this, "left-to-right");
            }
        });

        if (!getIntent().getStringExtra("address").equals("")) {
            binding.itemLocation.setEnabled(true);
            binding.itemLocation.setText(getIntent().getStringExtra("address"));
        } else {
            binding.itemLocation.setEnabled(false);
        }*/
    }

    private void uploadItem() {

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

//        if (TextUtils.isEmpty(binding.itemLocation.getText().toString())) {
//            binding.itemLocationL.setError("This cannot be empty.");
//            binding.itemLocationL.setErrorIconDrawable(null);
//            return;
//        } else {
//            binding.itemLocationL.setError(null);
//            binding.itemLocationL.setErrorIconDrawable(null);
//        }

        if (TextUtils.isEmpty(binding.itemPref.getText().toString())) {
            binding.itemPrefL.setError("This cannot be empty.");
            binding.itemPrefL.setErrorIconDrawable(null);
            return;
        } else {
            binding.itemPrefL.setError(null);
            binding.itemPrefL.setErrorIconDrawable(null);
        }

//        if (imageUri == null){
//            Uri noimageUri = (new Uri.Builder())
//                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
//                    .authority(getResources().getResourcePackageName(R.drawable.noimage))
//                    .appendPath(getResources().getResourcePackageName(R.drawable.noimage))
//                    .appendPath(getResources().getResourcePackageName(R.drawable.noimage))
//                    .build();
//            imageUri = noimageUri;
//        }

//        postingItemDialog.startLoadingDialog();
//
//        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);
//        Bitmap bitmap = null;
//
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), imageUri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap = Bitmap.createScaledBitmap(bitmap, 720, 720, false);
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//        UploadTask uploadTask = storageReference.putBytes(data);
//
//        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                        binding.itemImage.setImageResource(R.drawable.noimage);
//                        DatabaseReference insertItems = FirebaseDatabase.getInstance().getReference().child("items").child(fileName);
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");
//                        databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                String firstName = snapshot.child("First_Name").getValue(String.class);
//                                String lastName = snapshot.child("Last_Name").getValue(String.class);
//                                String userName = firstName + " " + lastName;
//
//                                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Uri> task) {
//                                        insertItems.child("Image_Url").setValue(task.getResult().toString());
//                                        insertItems.child("Item_Category").setValue(binding.itemCategory.getText().toString());
//                                        insertItems.child("Item_Location").setValue(binding.itemLocation.getText().toString());
//                                        insertItems.child("Item_Name").setValue(binding.itemName.getText().toString());
//                                        insertItems.child("Item_Preferred").setValue(binding.itemPref.getText().toString());
//                                        insertItems.child("Poster_Name").setValue(userName);
//                                        insertItems.child("Poster_UID").setValue(uid);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//                        postingItemDialog.DismissDialog();
//
//                        Toast toast = new Toast(getApplicationContext());
//                        View view = LayoutInflater.from(PostItem.this).inflate(R.layout.toast_layout, null);
//                        TextView toastMessage = view.findViewById(R.id.toastMessage);
//                        toastMessage.setText("Item successfully posted!");
//                        toast.setView(view);
//                        toast.setDuration(Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.TOP, 0,50);
//                        toast.show();
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                        postingItemDialog.DismissDialog();
//
//                        Toast toast = new Toast(getApplicationContext());
//                        View view = LayoutInflater.from(PostItem.this).inflate(R.layout.toast_error_layout, null);
//                        TextView toastMessage = view.findViewById(R.id.toastMessage);
//                        toastMessage.setText("Item image is required.");
//                        toast.setView(view);
//                        toast.setDuration(Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.TOP, 0,50);
//                        toast.show();
//                    }
//                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PostItem_S1.this, Categories.class));
        CustomIntent.customType(PostItem_S1.this, "right-to-left");
        finish();
    }
}