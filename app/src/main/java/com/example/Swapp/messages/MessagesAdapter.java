package com.example.Swapp.messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Swapp.MemoryData;
import com.example.Swapp.Messages;
import com.example.Swapp.UserHomepage;
import com.example.Swapp.chat.Chat;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import Swapp.R;
import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<MessagesList> messagesLists;
    private final Context context;

    public MessagesAdapter(List<MessagesList> messagesListLists, Context context) {
        this.messagesLists = messagesListLists;
        this.context = context;
    }

    @NonNull
    @Override
    public MessagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_adapater_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MyViewHolder holder, int position) {
        MessagesList list2 = messagesLists.get(position);

        holder.name.setText(list2.getName());
        holder.lastMessage.setText(list2.getLastMessage());

        if (list2.getUnseenMessages() == 0) {
            holder.unseenMessages.setVisibility(View.GONE);
            holder.lastMessage.setTextColor(Color.parseColor("#959595"));
        } else {
            holder.unseenMessages.setVisibility(View.VISIBLE);
            holder.unseenMessages.setText(list2.getUnseenMessages()+"");
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.primary2));
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("mobile", list2.getMobile());
                intent.putExtra("name", list2.getName());
                intent.putExtra("profile_pic", list2.getProfilePic());
                intent.putExtra("chat_key", list2.getChatKey());
                intent.putExtra("userStatus", list2.getStatus());
                context.startActivity(intent);
                CustomIntent.customType(context, "left-to-right");
            }
        });


    }

    public void updateData(List<MessagesList> messagesLists) {
        this.messagesLists = messagesLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messagesLists.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profilePic;
        private TextView name;
        private TextView lastMessage;
        private TextView unseenMessages;
        private LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            unseenMessages = itemView.findViewById(R.id.unseenMessages);
            rootLayout = itemView.findViewById(R.id.rootLayout);

        }
    }
}
