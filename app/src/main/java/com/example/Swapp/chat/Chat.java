package com.example.Swapp.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Swapp.MemoryData;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Swapp.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bugsbusters-de865-default-rtdb.asia-southeast1.firebasedatabase.app/");

    private final List<ChatList> chatLists = new ArrayList<>();
    private String chatKey;
    String getUserMobile = "";
    private RecyclerView chattingRecyclerView;
    private ChatAdapter chatAdapter;
    private boolean loadingFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ImageView backBtn = findViewById(R.id.backBtn);
        final TextView name = findViewById(R.id.name);
        final EditText messageEditText = findViewById(R.id.messageEditText);
        final CircleImageView profilePic = findViewById(R.id.profilePic);
        final ImageView sendBtn = findViewById(R.id.sendBtn);
        chattingRecyclerView = findViewById(R.id.chattingRecyclerView);

        final String getName = getIntent().getStringExtra("name");
        final String getProfilePic = getIntent().getStringExtra("profile_pic");
        chatKey = getIntent().getStringExtra("chat_key");
        final String getMobile = getIntent().getStringExtra("mobile");

        getUserMobile = MemoryData.getData(Chat.this);

        name.setText(getName);
        //Picasso.get().load(getProfilePic).into(profilePic);

        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));


        chatAdapter = new ChatAdapter(chatLists, Chat.this);
        chattingRecyclerView.setAdapter(chatAdapter);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(chatKey.isEmpty()) {
                        chatKey = "1";
                        if (snapshot.hasChild("chat")) {
                            chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() + 1);
                        }
                    }

                    if (snapshot.hasChild("chat")) {
                        chatLists.clear();
                        for (DataSnapshot messagesSnapshot : snapshot.child("chat").child(chatKey).child("messages").getChildren()) {
                            if (messagesSnapshot.hasChild("msg") && messagesSnapshot.hasChild("mobile")) {
                                final String messageTimestamps = messagesSnapshot.getKey();
                                final String getMobile = messagesSnapshot.child("mobile").getValue(String.class);
                                final String getMsg = messagesSnapshot.child("msg").getValue(String.class);

                                java.sql.Timestamp timestamp = new java.sql.Timestamp(Long.parseLong(messageTimestamps));
                                Date date = new Date(timestamp.getTime());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

                                ChatList chatList = new ChatList(getMobile, getName, getMsg, simpleDateFormat.format(date), simpleTimeFormat.format(date));
                                chatLists.add(chatList);

                                if (loadingFirstTime || Long.parseLong(messageTimestamps) > Long.parseLong(MemoryData.getLastMsgTS(Chat.this, chatKey))) {
                                    MemoryData.saveLastMsgTS(messageTimestamps, chatKey, Chat.this);

                                    loadingFirstTime = false;
                                    chatAdapter.updateChatList(chatLists);

                                    chattingRecyclerView.scrollToPosition(chatLists.size());

                                }
                            }

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getTxtMessage = messageEditText.getText().toString();

                final String currenTimeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);

                MemoryData.saveLastMsgTS(currenTimeStamp, chatKey, Chat.this);

                databaseReference.child("chat").child(chatKey).child("user_1").setValue(getUserMobile);
                databaseReference.child("chat").child(chatKey).child("user_2").setValue(getMobile);
                databaseReference.child("chat").child(chatKey).child("messages").child(currenTimeStamp).child("msg").setValue(getTxtMessage);
                databaseReference.child("chat").child(chatKey).child("messages").child(currenTimeStamp).child("mobile").setValue(getUserMobile);

                messageEditText.setText("");
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}