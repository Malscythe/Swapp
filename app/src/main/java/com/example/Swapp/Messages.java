package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.Swapp.messages.MessagesAdapter;
import com.example.Swapp.messages.MessagesList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;
import okhttp3.internal.Util;

public class Messages extends AppCompatActivity {

    private final List<MessagesList> messagesLists = new ArrayList<>();

    private static final String TAG = "TAG";
    private String mobile, email, name;
    private RecyclerView messagesRecyleView;
    private int unseenMessages = 0;
    private String lastMessage = "";
    private String chatKey = "";
    private boolean dataSet = false;
    String oppositeNum = "";

    MessagesAdapter messagesAdapter;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");

    ArrayList itemList;
    ArrayList messageKeyList;
    ArrayList arrMessageList;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mobile = getIntent().getStringExtra("mobile");
        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");

        itemList = new ArrayList<>();
        messageKeyList = new ArrayList<>();
        arrMessageList = new ArrayList<>();

        messagesRecyleView = findViewById(R.id.messagesRecyclerView);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.inbox);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.inbox:
                        return true;
                    case R.id.home:
                        Intent intent = new Intent(Messages.this, UserHomepage.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    case  R.id.offers:
                        Intent intent1 = new Intent(Messages.this, OfferMainAcitvity.class);
                        startActivity(intent1);
                        overridePendingTransition(0,0);
                        return true;
                }
                return true;
            }
        });

        messagesRecyleView.setHasFixedSize(true);
        messagesRecyleView.setLayoutManager(new LinearLayoutManager(Messages.this));

        messagesAdapter = new MessagesAdapter(messagesLists, Messages.this);
        messagesRecyleView.setAdapter(messagesAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesLists.clear();
                unseenMessages = 0;
                lastMessage = "";
                chatKey = "";
                oppositeNum = "";

                    for (DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {

                        dataSet = false;

                        String currentMobile = dataSnapshot.child("Phone").getValue(String.class);

                        for (DataSnapshot dataSnapshot1 : snapshot.child("chat").getChildren()) {

                            String user1 = dataSnapshot1.child("user_1").getValue(String.class);
                            String user2 = dataSnapshot1.child("user_2").getValue(String.class);

                            if (((user1.equals(currentMobile) || user2.equals(currentMobile)) && ((user1.equals(mobile) || user2.equals(mobile)))) && (!currentMobile.equals(mobile))) {

                                Log.w(TAG, currentMobile);

                                final String getName = dataSnapshot.child("First_Name").getValue(String.class).concat(" " + dataSnapshot.child("Last_Name").getValue(String.class));
                                final String getCurrentId = dataSnapshot.getKey();

                                int getChatCounts = (int) snapshot.getChildrenCount();

                                if (getChatCounts > 0) {

                                    final String getKey = dataSnapshot1.getKey();
                                    chatKey = getKey;

                                    if ((dataSnapshot1.child("user_1").getValue(String.class).equals(currentMobile) || dataSnapshot1.child("user_2").getValue(String.class).equals(currentMobile)) && dataSnapshot1.hasChild("messages")) {

                                        final String getUserOne = dataSnapshot1.child("user_1").getValue(String.class);
                                        final String getUserTwo = dataSnapshot1.child("user_2").getValue(String.class);

                                        if ((getUserOne.equals(currentMobile) && getUserTwo.equals(mobile)) || (getUserOne.equals(mobile) && getUserTwo.equals(currentMobile))) {

                                            if (dataSnapshot1.child("user_1").getValue(String.class).equals(currentMobile)) {
                                                oppositeNum = dataSnapshot1.child("user_2").getValue(String.class);
                                            } else if (dataSnapshot1.child("user_2").getValue(String.class).equals(currentMobile)) {
                                                oppositeNum = dataSnapshot1.child("user_1").getValue(String.class);
                                            }

                                            for (DataSnapshot chatDataSnapshot : dataSnapshot1.child("messages").getChildren()) {

                                                String messengerNumber = chatDataSnapshot.child("mobile").getValue(String.class);
                                                String message = chatDataSnapshot.child("msg").getValue(String.class);

                                                final long getMessageKey = Long.parseLong(chatDataSnapshot.getKey().substring(0, 14));
                                                final long getLastSeenMessage;

                                                if (MemoryData.getLastMsgTS(Messages.this, getKey, mobile).equals("") || MemoryData.getLastMsgTS(Messages.this, getKey, mobile) == null || MemoryData.getLastMsgTS(Messages.this, getKey, mobile).length() < 3) {
                                                    getLastSeenMessage = 0L;
                                                } else {
                                                    getLastSeenMessage = Long.parseLong(MemoryData.getLastMsgTS(Messages.this, getKey, mobile).substring(0, 14));
                                                }

                                                if (message != null)
                                                    if (chatDataSnapshot.child("msg").getValue(String.class).length() > 100) {
                                                        if (messengerNumber != null) {
                                                            if (chatDataSnapshot.child("msg").getValue(String.class).substring(0, 55).equals("https://firebasestorage.googleapis.com/v0/b/bugsbusters") && (chatDataSnapshot.child("mobile").getValue(String.class).equals(mobile))) {
                                                                lastMessage = "You sent a photo";
                                                            } else if (chatDataSnapshot.child("msg").getValue(String.class).substring(0, 55).equals("https://firebasestorage.googleapis.com/v0/b/bugsbusters") && (chatDataSnapshot.child("mobile").getValue(String.class).equals(currentMobile))) {

                                                                lastMessage = getName + " sent a photo";
                                                            } else {
                                                                lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
                                                            }
                                                        }

                                                    } else {
                                                        lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
                                                    }


                                                if (getMessageKey > getLastSeenMessage) {
                                                    unseenMessages++;
                                                }
                                            }
                                        }
                                    }
                                    if (!dataSet && oppositeNum.equals(mobile)) {
                                        MessagesList messagesList = new MessagesList(getName, currentMobile, lastMessage, "", unseenMessages, chatKey, snapshot.child("users-status").child(getCurrentId).child("Status").getValue(String.class));
                                        messagesLists.add(messagesList);
                                        messagesAdapter.updateData(messagesLists);
                                        dataSet = true;
                                    }
                                }
                            }
                        }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Messages.this, UserHomepage.class);
        startActivity(intent);
        CustomIntent.customType(Messages.this, "right-to-left");
    }

}