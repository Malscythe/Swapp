package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.Swapp.messages.MessagesAdapter;
import com.example.Swapp.messages.MessagesList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Messages.this, UserHomepage.class);
                startActivity(intent);
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

                for (DataSnapshot dataSnapshot : snapshot.child("users").getChildren()){

                    final String getMobile = dataSnapshot.child("Phone").getValue(String.class);

                    databaseReference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            dataSet = false;

                            for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {

                                String user1 = dataSnapshot2.child("user_1").getValue(String.class);
                                String user2 = dataSnapshot2.child("user_2").getValue(String.class);

                                if (!getMobile.equals(mobile) && (user1.equals(getMobile) || user2.equals(getMobile))) {

                                    final String getName = dataSnapshot.child("First_Name").getValue(String.class).concat(" " + dataSnapshot.child("Last_Name").getValue(String.class));

                                    databaseReference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            int getChatCounts = (int) snapshot.getChildrenCount();

                                            if (getChatCounts > 0) {

                                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {

                                                    Log.d(TAG, "Outer Loop");

                                                    final String getKey = dataSnapshot1.getKey();
                                                    chatKey = getKey;

                                                    Log.d(TAG, "chatKey : " + chatKey);

                                                    if ((dataSnapshot1.child("user_1").getValue(String.class).equals(mobile) || dataSnapshot1.child("user_2").getValue(String.class).equals(mobile)) && dataSnapshot1.hasChild("messages")) {

                                                        final String getUserOne = dataSnapshot1.child("user_1").getValue(String.class);
                                                        final String getUserTwo = dataSnapshot1.child("user_2").getValue(String.class);

                                                        if ((getUserOne.equals(getMobile) && getUserTwo.equals(mobile)) || (getUserOne.equals(mobile) && getUserTwo.equals(getMobile))) {

                                                            for (DataSnapshot chatDataSnapshot : dataSnapshot1.child("messages").getChildren()) {

                                                                Log.d(TAG, "Inside Loop");

                                                                final long getMessageKey = Long.parseLong(chatDataSnapshot.getKey());
                                                                final long getLastSeenMessage = Long.parseLong(MemoryData.getLastMsgTS(Messages.this, getKey));

                                                                lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
                                                                if (getMessageKey > getLastSeenMessage) {
                                                                    unseenMessages++;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            incre++;


                                            Log.d(TAG, "Loop : " + incre + " " + dataSet + " " + chatKey);

                                            Log.d(TAG, "--------");

                                            if (!dataSet) {

                                                dataSet = true;

                                                MessagesList messagesList = new MessagesList(getName, getMobile, lastMessage, "", unseenMessages, chatKey);
                                                messagesLists.add(messagesList);
                                                messagesAdapter.updateData(messagesLists);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Messages.this, UserHomepage.class);
        startActivity(intent);
    }

}