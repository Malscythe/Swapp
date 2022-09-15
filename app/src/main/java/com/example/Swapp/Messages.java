package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.Swapp.chat.Chat;
import com.example.Swapp.messages.MessagesAdapter;
import com.example.Swapp.messages.MessagesList;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import Swapp.R;

public class Messages extends AppCompatActivity {

    private final List<MessagesList> messagesLists = new ArrayList<>();

    private static final String TAG = "TAG";
    private String mobile, email, name;
    private RecyclerView messagesRecyleView;
    private int unseenMessages = 0;
    private String lastMessage = "";
    private String chatKey = "";
    private boolean dataSet = false;

    MessagesAdapter messagesAdapter;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getCurrentUser().getUid();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    ArrayList itemList;
    ArrayList messageKeyList;
    ArrayList arrMessageList;
    String oppositeNum = "";

    ExtendedFloatingActionButton sendMessage;

    int incre = 0;

    ImageView backBtn;

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

        backBtn = findViewById(R.id.backBtn);
        sendMessage = findViewById(R.id.newChat);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Messages.this, UserHomepage.class);
                startActivity(intent);
            }
        });

        messagesRecyleView.setHasFixedSize(false);
        messagesRecyleView.setLayoutManager(new LinearLayoutManager(Messages.this));

        messagesAdapter = new MessagesAdapter(messagesLists, Messages.this);
        messagesRecyleView.setAdapter(messagesAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                unseenMessages = 0;
                lastMessage = "";
                chatKey = "";

                for (DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {

                    dataSet = false;

                    String currentMobile = dataSnapshot.child("Phone").getValue(String.class);

                    for (DataSnapshot secondSnapshot : snapshot.child("chat").getChildren()) {

                        String user1 = secondSnapshot.child("user_1").getValue(String.class);
                        String user2 = secondSnapshot.child("user_2").getValue(String.class);

                        if ((user1.equals(currentMobile) || user2.equals(currentMobile))) {

                            final String getName = dataSnapshot.child("First_Name").getValue(String.class).concat(" " + dataSnapshot.child("Last_Name").getValue(String.class));

                            int getChatCounts = (int) snapshot.child("chat").getChildrenCount();

                            if (getChatCounts > 0) {

                                for (DataSnapshot dataSnapshot1 : snapshot.child("chat").getChildren()) {

                                    final String getKey = dataSnapshot1.getKey();
                                    chatKey = getKey;

                                    if ((dataSnapshot1.child("user_1").getValue(String.class).equals(currentMobile) || dataSnapshot1.child("user_2").getValue(String.class).equals(currentMobile)) && dataSnapshot1.hasChild("messages")) {

                                        if (dataSnapshot1.child("user_1").getValue(String.class).equals(currentMobile)) {
                                            oppositeNum = dataSnapshot1.child("user_2").getValue(String.class);
                                        } else if (dataSnapshot1.child("user_2").getValue(String.class).equals(currentMobile)) {
                                            oppositeNum = dataSnapshot1.child("user_1").getValue(String.class);
                                        }
                                        for (DataSnapshot chatDataSnapshot : dataSnapshot1.child("messages").getChildren()) {

                                            final long getMessageKey = Long.parseLong(chatDataSnapshot.getKey().substring(0, 12));
                                            final long getLastSeenMessage;

                                            if (MemoryData.getLastMsgTS(Messages.this, getKey).equals("")) {
                                                getLastSeenMessage = 0L;
                                            } else {
                                                getLastSeenMessage = Long.parseLong(MemoryData.getLastMsgTS(Messages.this, getKey).substring(0, 12));
                                            }

                                            lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
                                            if (getMessageKey > getLastSeenMessage) {
                                                unseenMessages++;
                                            }
                                        }
                                    }
                                }

                                incre++;


                                if (!dataSet && oppositeNum.equals(mobile)) {
                                    MessagesList messagesList = new MessagesList(getName, oppositeNum, lastMessage, "", unseenMessages, chatKey);
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
    }
}
