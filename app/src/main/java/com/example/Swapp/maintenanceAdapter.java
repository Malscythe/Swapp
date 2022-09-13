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

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img1);
            fname = itemView.findViewById(R.id.fNameText);
            lname = itemView.findViewById(R.id.lNameText);
            email = itemView.findViewById(R.id.useremail);
            gender = itemView.findViewById(R.id.usergender);
            phone = itemView.findViewById(R.id.userPhone);
        }
    }

}
