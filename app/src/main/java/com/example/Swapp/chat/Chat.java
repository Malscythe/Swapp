package com.example.Swapp.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.Swapp.MemoryData;
import com.example.Swapp.Messages;
import com.example.Swapp.PostItem;
import com.example.Swapp.UserHomepage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.database.snapshot.ChildrenNode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Swapp.R;
import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class Chat extends AppCompatActivity implements BottomSheetImagePicker.OnImagesSelectedListener {

    private static final String TAG = "TAG";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");

    private final List<ChatList> chatLists = new ArrayList<>();
    private String chatKey;
    String getUserMobile = "";
    private RecyclerView chattingRecyclerView;
    private ChatAdapter chatAdapter;
    private boolean loadingFirstTime = true;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getCurrentUser().getUid();
    Button sendImg;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ImageView callBtn = findViewById(R.id.voiceCall);
        final ImageView backBtn = findViewById(R.id.backBtn);
        final TextView name = findViewById(R.id.name);
        final EditText messageEditText = findViewById(R.id.messageEditText);
        final CircleImageView profilePic = findViewById(R.id.profilePic);
        final ImageView sendBtn = findViewById(R.id.sendBtn);
        final TextView userStatus = findViewById(R.id.userStatus);
        chattingRecyclerView = findViewById(R.id.chattingRecyclerView);

        final String getName = getIntent().getStringExtra("name");
        chatKey = getIntent().getStringExtra("chat_key");
        final String getMobile = getIntent().getStringExtra("mobile");
        final String getStatus = getIntent().getStringExtra("userStatus");

        getUserMobile = MemoryData.getData(Chat.this);

        name.setText(getName);
        if (getStatus.equals("Online")) {
            userStatus.setText("Online");
            userStatus.setTextColor(Color.parseColor("#00C853"));
        } else if (getStatus.equals("Offline")) {
            userStatus.setText("Offline");
            userStatus.setTextColor(Color.parseColor("#818181"));
        }
        //Picasso.get().load(getProfilePic).into(profilePic);

        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));

        chatAdapter = new ChatAdapter(chatLists, Chat.this);
        chattingRecyclerView.setAdapter(chatAdapter);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (chatKey.isEmpty()) {
                    if (snapshot.hasChild("chat")) {
                        chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() + 1);
                    } else {
                        chatKey = "1";
                    }
                }

                if (snapshot.hasChild("chat")) {
                    chatLists.clear();
                    for (DataSnapshot messagesSnapshot : snapshot.child("chat").child(chatKey).child("messages").getChildren()) {
                        if (messagesSnapshot.hasChild("msg") && messagesSnapshot.hasChild("mobile")) {
                            final String messageTimestamps = messagesSnapshot.getKey();
                            final String getMobile = messagesSnapshot.child("mobile").getValue(String.class);
                            final String getMsg = messagesSnapshot.child("msg").getValue(String.class);

                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyykkmmssaa");
                                Date parsedDate = dateFormat.parse(messageTimestamps);
                                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa");

                                String date = simpleDateFormat.format(timestamp);
                                String time = simpleTimeFormat.format(timestamp);

                                ChatList chatList = new ChatList(getMobile, getName, getMsg, date, time);
                                chatLists.add(chatList);

                                String numToSave = snapshot.child("users").child(MemoryData.getUid(Chat.this)).child("Phone").getValue(String.class);

                                MemoryData.saveLastMsgTS(messageTimestamps.substring(0,14), chatKey, Chat.this, numToSave);

                                if (loadingFirstTime || Long.parseLong(messageTimestamps.substring(0,14)) > Long.parseLong(MemoryData.getLastMsgTS(Chat.this, chatKey, numToSave).substring(0, 14))) {

                                    loadingFirstTime = false;
                                    chatAdapter.updateChatList(chatLists);
                                }


                                chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
                            } catch (Exception e) {
                                Log.w(TAG, e.getMessage());
                            }
                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageEditText.setShowSoftInputOnFocus(true);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String getTxtMessage = messageEditText.getText().toString();

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyykkmmssaa", Locale.getDefault());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String numToSave = snapshot.child("users").child(uid).child("Phone").getValue(String.class);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("chat").child(chatKey).child("messages").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<String> list = new ArrayList<>();

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                    list.add(dataSnapshot.getKey());
                                }

                                databaseReference.child("chat").child(chatKey).child("user_1").setValue(getUserMobile);
                                databaseReference.child("chat").child(chatKey).child("user_2").setValue(getMobile);
                                databaseReference.child("chat").child(chatKey).child("messages").child(simpleDateFormat.format(timestamp)).child("msg").setValue(getTxtMessage);
                                databaseReference.child("chat").child(chatKey).child("messages").child(simpleDateFormat.format(timestamp)).child("mobile").setValue(getUserMobile);
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

                messageEditText.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                CustomIntent.customType(Chat.this, "right-to-left");
            }
        });

        sendImg = findViewById(R.id.sendImg);

        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

    }

    private void makePhoneCall() {
        final String getMobile = getIntent().getStringExtra("mobile");
        String number = getMobile;

        if (number.length() > 0) {
            if (ContextCompat.checkSelfPermission(Chat.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Chat.this, new String[] {Manifest.permission.CALL_PHONE}, 1);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(this, "Cannot contact the number.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("First_Name").getValue(String.class).concat(" " + snapshot.child("Last_Name").getValue(String.class));
                Intent intent = new Intent(Chat.this, Messages.class);
                intent.putExtra("mobile", snapshot.child("Phone").getValue(String.class));
                intent.putExtra("email", snapshot.child("Email").getValue(String.class));
                intent.putExtra("name", name);
                startActivity(intent);;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        CustomIntent.customType(Chat.this, "right-to-left");
    }

    private void selectImage() {

        new BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                .cameraButton(ButtonType.Button)
                .galleryButton(ButtonType.Button)
                .singleSelectTitle(R.string.pick_single)
                .requestTag("single")
                .show(getSupportFragmentManager(), null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && data != null && data.getData() != null) {


        }
    }


    @Override
    public void onImagesSelected(@NotNull List<? extends Uri> uris, @Nullable String tag) {
        for (Uri uri : uris) {
            imageUri = uri;
        }

        chatKey = getIntent().getStringExtra("chat_key");
        final String getMobile = getIntent().getStringExtra("mobile");
        getUserMobile = MemoryData.getData(Chat.this);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyykkmmssaa", Locale.getDefault());

        if (imageUri == null){
            Uri noimageUri = (new Uri.Builder())
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(getResources().getResourcePackageName(R.drawable.noimage))
                    .appendPath(getResources().getResourcePackageName(R.drawable.noimage))
                    .appendPath(getResources().getResourcePackageName(R.drawable.noimage))
                    .build();
            imageUri = noimageUri;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/chat/"+ chatKey + "/" + simpleDateFormat.format(timestamp));
        Bitmap bitmap = null;

        Glide.with(Chat.this).asBitmap().load(imageUri).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();
                UploadTask uploadTask = storageReference.putBytes(data1);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");
                        databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String numToSave = snapshot.child("Phone").getValue(String.class);

                                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {

                                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyykkmmssaa", Locale.getDefault());

                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                        ref.child("chat").child(chatKey).child("messages").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                List<String> list = new ArrayList<>();
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                                    list.add(dataSnapshot.getKey());
                                                }

                                                MemoryData.saveLastMsgTS(list.get(list.size() - 1), chatKey, Chat.this, numToSave);
                                                databaseReference.child("chat").child(chatKey).child("user_1").setValue(getUserMobile);
                                                databaseReference.child("chat").child(chatKey).child("user_2").setValue(getMobile);
                                                databaseReference.child("chat").child(chatKey).child("messages").child(simpleDateFormat.format(timestamp)).child("msg").setValue(task.getResult().toString());
                                                databaseReference.child("chat").child(chatKey).child("messages").child(simpleDateFormat.format(timestamp)).child("mobile").setValue(getUserMobile);

                                                chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
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
}