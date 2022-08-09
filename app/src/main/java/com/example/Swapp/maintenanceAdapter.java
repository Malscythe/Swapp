package com.example.Swapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Swapp.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class maintenanceAdapter extends RecyclerView.Adapter<maintenanceAdapter.MyViewHolder> {

    Context context;
    public static final String TAG = "TAG";
    ArrayList<FileMaintenanceModel> userArrayList;
    FirebaseFirestore db;


    public maintenanceAdapter(Context context, ArrayList<FileMaintenanceModel> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public maintenanceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(context).inflate(R.layout.maintenance_item,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        FileMaintenanceModel user = userArrayList.get(position);

        holder.First_Name.setText(user.First_Name);
        holder.Last_Name.setText(user.Last_Name);
        holder.Email.setText(user.Email);
        holder.Gender.setText(user.Gender);
        holder.Birth_Date.setText(user.Birth_Date);


        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {

                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.First_Name.getContext())
                        .setContentHolder(new ViewHolder(R.layout.maintenance_popup))
                        .setExpanded(true,1280)
                        .create();


                View view = dialogPlus.getHolderView();

                EditText First_Name = view.findViewById(R.id.firstn);
                EditText Last_Name = view.findViewById(R.id.lastn);
                EditText Email = view.findViewById(R.id.emailt);
                EditText Gender = view.findViewById(R.id.gendert);
                EditText Birth_Date = view.findViewById(R.id.bdayt);


                Button btnUpdate = view.findViewById(R.id.btnupdate1);

                First_Name.setText(user.First_Name);
                Last_Name.setText(user.Last_Name);
                Email.setText(user.Email);
                Gender.setText(user.Gender);
                Birth_Date.setText(user.Birth_Date);

                dialogPlus.show();

                db = FirebaseFirestore.getInstance();


                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                            Map<String,Object> map = new HashMap<>();
                            map.put("First_Name",First_Name.getText().toString());
                            map.put("Last_Name",Last_Name.getText().toString());
                            map.put("Email",Email.getText().toString());
                            map.put("Gender",Gender.getText().toString());
                            map.put("Birth_Date",Birth_Date.getText().toString());


        //                    db.collection("users")

                        /**FirebaseDatabase.getInstance().getReference().child("users")
                                .child(userArrayList.get(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                }); **/


                        db.collection("users")
                                .whereEqualTo("Email", Email.getText().toString())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String uid = document.getId();

                                                db.collection("users").document(uid)
                                                        .set(map)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error writing document", e);
                                                            }
                                                        });
                                            }
                                        } else {
                                            Log.w(TAG, "Error getting documents.", task.getException());
                                        }
                                    }
                                });
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {

        return userArrayList.size();
    }

        public static class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView img;
        TextView First_Name, Last_Name, Gender, Birth_Date, Email;

        Button btnUpdate, btnDelete;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                First_Name = itemView.findViewById(R.id.fNameText);
                Last_Name = itemView.findViewById(R.id.lNameText);
                Gender = itemView.findViewById(R.id.usergender);
                Birth_Date = itemView.findViewById(R.id.userbday);
                Email = itemView.findViewById(R.id.useremail);

                btnUpdate = (Button)itemView.findViewById(R.id.btnUpdate);
                btnDelete = (Button)itemView.findViewById(R.id.btnDelete);

            }
        }
}
