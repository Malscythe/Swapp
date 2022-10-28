package com.example.Swapp.chat;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.daimajia.androidanimations.library.specials.out.TakingOffAnimator;
import com.example.Swapp.AdminHomepage;
import com.example.Swapp.UserHomepage;
import com.example.Swapp.call.CallScreenActivity;
import com.example.Swapp.MemoryData;
import com.example.Swapp.Messages;
import com.example.Swapp.call.BaseActivity;
import com.example.Swapp.call.SinchService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Swapp.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class Chat extends BaseActivity implements BottomSheetImagePicker.OnImagesSelectedListener {

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

//        callTimer = findViewById(R.id.callStatusEstablished);

        final String getName = getIntent().getStringExtra("name");
        chatKey = getIntent().getStringExtra("chat_key");
        final String getMobile = getIntent().getStringExtra("mobile");
        final String getStatus = getIntent().getStringExtra("userStatus");
        final String getUID = getIntent().getStringExtra("userID");

        getUserMobile = MemoryData.getData(Chat.this);

        name.setText(getName);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users-status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.child(getUID).child("Status").getValue(String.class);
                userStatus.setText(status);

                if (status.equals("Online")) {
                    userStatus.setTextColor(Color.parseColor("#00C853"));
                } else if (status.equals("Offline")) {
                    userStatus.setTextColor(Color.parseColor("#818181"));
                }

                callBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "CLICKED1");
                        Dexter.withContext(Chat.this)
                                .withPermissions(RECORD_AUDIO, READ_PHONE_STATE)
                                .withListener(new MultiplePermissionsListener() {
                                    @Override
                                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                        Log.d(TAG, !multiplePermissionsReport.areAllPermissionsGranted() + "" );
                                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                            new SweetAlertDialog(Chat.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Permission required.")
                                                    .setContentText("You need to allow Phone and Microphone permission to run use this feature.")
                                                    .setConfirmText("Go to settings")
                                                    .showCancelButton(false)
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                                            intent.setData(uri);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .show();
                                        } else if (!multiplePermissionsReport.areAllPermissionsGranted()) {
                                            Toast.makeText(Chat.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            getSinchServiceInterface().retryStartAfterPermissionGranted();
                                            if (getSinchServiceInterface().isStarted()) {
                                                if (status.equals("Online")) {
                                                    Call call = getSinchServiceInterface().callUser(getUID);
                                                    String callId = call.getCallId();

                                                    Intent callScreen = new Intent(Chat.this, CallScreenActivity.class);
                                                    callScreen.putExtra(SinchService.CALL_ID, callId);
                                                    callScreen.putExtra("userName", getName);
                                                    startActivity(callScreen);
                                                } else {
                                                    Toast.makeText(Chat.this, "User is offline!", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                getSinchServiceInterface().setStartListener(new SinchService.StartFailedListener() {
                                                    @Override
                                                    public void onFailed(SinchError error) {

                                                    }

                                                    @Override
                                                    public void onStarted() {
                                                        if (status.equals("Online")) {
                                                            Call call = getSinchServiceInterface().callUser(getUID);
                                                            String callId = call.getCallId();

                                                            Intent callScreen = new Intent(Chat.this, CallScreenActivity.class);
                                                            callScreen.putExtra(SinchService.CALL_ID, callId);
                                                            callScreen.putExtra("userName", getName);
                                                            startActivity(callScreen);
                                                        } else {
                                                            Toast.makeText(Chat.this, "User is offline!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                        permissionToken.continuePermissionRequest();
                                    }
                                }).check();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));

        chatAdapter = new ChatAdapter(chatLists, Chat.this);
        chattingRecyclerView.setAdapter(chatAdapter);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

                                MemoryData.saveLastMsgTS(messageTimestamps.substring(0, 14), chatKey, Chat.this, numToSave);

                                if (loadingFirstTime || Long.parseLong(messageTimestamps.substring(0, 14)) > Long.parseLong(MemoryData.getLastMsgTS(Chat.this, chatKey, numToSave).substring(0, 14))) {

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

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                    list.add(dataSnapshot.getKey());
                                }

                                if (snapshot.getChildrenCount() <= 1) {
                                    databaseReference.child("chat").child(chatKey).child("user_1").setValue(getUserMobile);
                                    databaseReference.child("chat").child(chatKey).child("user_2").setValue(getMobile);
                                }

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
                databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("First_Name").getValue(String.class).concat(" " + snapshot.child("Last_Name").getValue(String.class));
                        Intent intent = new Intent(Chat.this, Messages.class);
                        intent.putExtra("mobile", snapshot.child("Phone").getValue(String.class));
                        intent.putExtra("email", snapshot.child("Email").getValue(String.class));
                        intent.putExtra("name", name);
                        startActivity(intent);
                        CustomIntent.customType(Chat.this, "right-to-left");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                startActivity(intent);

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
    public void onImagesSelected(@NotNull List<? extends Uri> uris, @Nullable String tag) {
        for (Uri uri : uris) {
            imageUri = uri;
        }

        chatKey = getIntent().getStringExtra("chat_key");
        final String getMobile = getIntent().getStringExtra("mobile");
        getUserMobile = MemoryData.getData(Chat.this);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyykkmmssaa", Locale.getDefault());

        if (imageUri == null) {
            Uri noimageUri = (new Uri.Builder())
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(getResources().getResourcePackageName(R.drawable.noimage))
                    .appendPath(getResources().getResourcePackageName(R.drawable.noimage))
                    .appendPath(getResources().getResourcePackageName(R.drawable.noimage))
                    .build();
            imageUri = noimageUri;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/chat/" + chatKey + "/" + simpleDateFormat.format(timestamp));
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
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                                    list.add(dataSnapshot.getKey());
                                                }

                                                MemoryData.saveLastMsgTS(list.get(list.size() - 1), chatKey, Chat.this, numToSave);

                                                if (snapshot.getChildrenCount() <= 1) {
                                                    databaseReference.child("chat").child(chatKey).child("user_1").setValue(getUserMobile);
                                                    databaseReference.child("chat").child(chatKey).child("user_2").setValue(getMobile);
                                                }

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