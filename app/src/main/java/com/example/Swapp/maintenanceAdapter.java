package com.example.Swapp;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class maintenanceAdapter extends FirebaseRecyclerAdapter<FileMaintenanceModel, maintenanceAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public maintenanceAdapter(@NonNull FirebaseRecyclerOptions<FileMaintenanceModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull FileMaintenanceModel model) {
        holder.fname.setText(model.getFirst_Name());
        holder.lname.setText(model.getLast_Name());
        holder.email.setText(model.getEmail());
        holder.phone.setText(model.getPhone());
        holder.gender.setText(model.getGender());

        Glide.with(holder.img.getContext())
                .load(model.getSurl())
                .placeholder(R.drawable.default_picture)
                .circleCrop()
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.img);

        holder.btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.phone.getContext())
                        .setContentHolder(new ViewHolder(R.layout.maintenance_popup))
                        .setExpanded(true, 1200)
                        .create();

               // dialogPlus.show();

                View view = dialogPlus.getHolderView();

                EditText fname = view.findViewById(R.id.firstn);
                EditText lname = view.findViewById(R.id.lastn);
                EditText email = view.findViewById(R.id.emailt);
                EditText phone = view.findViewById(R.id.phonet);
                EditText gender = view.findViewById(R.id.gendert);

               Button btnupdate1 = view.findViewById(R.id.btnupdate1);

               fname.setText(model.getFirst_Name());
               lname.setText(model.getLast_Name());
               email.setText(model.getEmail());
               phone.setText(model.getPhone());
               gender.setText(model.getGender());

               dialogPlus.show();

               btnupdate1.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       Map<String,Object> map = new HashMap<>();
                       map.put("First_Name",fname.getText().toString());
                       map.put("Last_Name",lname.getText().toString());
                       map.put("Email",email.getText().toString());
                       map.put("Gender",gender.getText().toString());
                       map.put("Phone",phone.getText().toString());


                       FirebaseDatabase.getInstance().getReference().child("users")
                               .child(getRef(position).getKey()).updateChildren(map)
                               .addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void unused) {
                                       Toast.makeText(holder.fname.getContext(), "Data Updated Succesfully", Toast.LENGTH_SHORT).show();
                                       dialogPlus.dismiss();
                                   }
                               })
                               .addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(holder.fname.getContext(), "Data Updating Failed", Toast.LENGTH_SHORT).show();
                                   }
                               });

                   }
               });


            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.fname.getContext());
                builder.setTitle("Are you sure to Delete?");
                builder.setMessage("You cannot undo this action!");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(getRef(position).getKey()).removeValue();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.fname.getContext(), "Action Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maintenance_item, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView fname, lname, email, gender, phone;

        Button btnupdate, btnDelete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img1);
            fname = itemView.findViewById(R.id.fNameText);
            lname = itemView.findViewById(R.id.lNameText);
            email = itemView.findViewById(R.id.useremail);
            gender = itemView.findViewById(R.id.usergender);
            phone = itemView.findViewById(R.id.userPhone);

            btnupdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);

        }
    }

}
