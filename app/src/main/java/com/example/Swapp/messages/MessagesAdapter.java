package com.example.Swapp.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Swapp.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private final List<MessagesList> messagesLists;
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
        } else {
            holder.unseenMessages.setVisibility(View.VISIBLE);
        }
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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            unseenMessages = itemView.findViewById(R.id.unseenMessages);
        }
    }
}
