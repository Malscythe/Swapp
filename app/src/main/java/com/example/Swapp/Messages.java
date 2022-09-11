package com.example.Swapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getCurrentUser().getUid();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    ArrayList itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        itemList = new ArrayList<>();
        Log.w(TAG, "" + uid);


        readData(new FirebaseCallback() {
            @Override
            public void onCallback(List<String> list) {
                mobile = list.get(0);
                name = list.get(2);
                email = list.get(1);

                messagesRecyleView = findViewById(R.id.messagesRecyclerView);

                messagesRecyleView.setHasFixedSize(true);
                messagesRecyleView.setLayoutManager(new LinearLayoutManager(Messages.this));

                firebaseFirestore.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    messagesLists.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                        final String getMobile = document.get("Phone").toString();

                                        if (!getMobile.equals(mobile)) {
                                            final String getName = document.get("First_Name").toString().concat(" " + document.get("Last_Name"));

                                            MessagesList messagesList = new MessagesList(getName, getMobile, "", "", 0);
                                            messagesLists.add(messagesList);
                                        }
                                    }

                                    messagesRecyleView.setAdapter(new MessagesAdapter(messagesLists, Messages.this));
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }});
            }
        });
    }

    private void readData(FirebaseCallback firebaseCallback) {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        itemList.add(document.get("Phone").toString());
                        itemList.add(document.get("Email").toString());
                        itemList.add(document.get("First_Name").toString().concat(" " + document.get("Last_Name").toString()));

                        firebaseCallback.onCallback(itemList);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private interface FirebaseCallback {
        void onCallback(List<String> list);
    }
}