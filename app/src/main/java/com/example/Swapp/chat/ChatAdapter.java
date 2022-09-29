package com.example.Swapp.chat;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.Swapp.ItemSwipe;
import com.example.Swapp.MemoryData;
import com.example.Swapp.MoreInfo;
import com.example.Swapp.UserHomepage;
import com.example.Swapp.imageFullScreen;
import com.example.Swapp.popup;

import java.text.SimpleDateFormat;
import java.util.List;

import Swapp.R;
import maes.tech.intentanim.CustomIntent;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<ChatList> chatLists;
    private final Context context;
    private String userMobile;
    boolean isImageFitToScreen;

    public ChatAdapter(List<ChatList> chatLists, Context context) {
        this.chatLists = chatLists;
        this.context = context;
        this.userMobile = MemoryData.getData(context);
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        ChatList list2 = chatLists.get(position);

        Boolean isImage = false;

        if (list2.getMessage().length() > 100) {
            if (list2.getMessage().substring(0, 55).equals("https://firebasestorage.googleapis.com/v0/b/bugsbusters")) {
                isImage = true;
            } else {
                isImage = false;
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa");


        if (list2.getMobile().equals(userMobile) && !isImage) {
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.oppoLayout.setVisibility(View.GONE);
            holder.oppoImgSentLayout.setVisibility(View.GONE);
            holder.myImgSentLayout.setVisibility(View.GONE);

            holder.myMessage.setText(list2.getMessage());
            holder.myTime.setText(list2.getDate() + " " + list2.getTime());
        } else if (list2.getMobile().equals(userMobile) && isImage) {
            holder.myLayout.setVisibility(View.GONE);
            holder.oppoLayout.setVisibility(View.GONE);
            holder.oppoImgSentLayout.setVisibility(View.GONE);
            holder.myImgSentLayout.setVisibility(View.VISIBLE);
            Glide.with(holder.myImgSent.getContext()).load(list2.getMessage()).into(holder.myImgSent);
            holder.myImgSentTime.setText(list2.getDate() + " " + list2.getTime());
        } else if (!list2.getMobile().equals(userMobile) && !isImage) {
            holder.myLayout.setVisibility(View.GONE);
            holder.oppoLayout.setVisibility(View.VISIBLE);
            holder.oppoImgSentLayout.setVisibility(View.GONE);
            holder.myImgSentLayout.setVisibility(View.GONE);

            holder.oppoMessage.setText(list2.getMessage());
            holder.oppoTime.setText(list2.getDate() + " " + list2.getTime());
        } else if (!list2.getMobile().equals(userMobile) && isImage) {
            holder.myLayout.setVisibility(View.GONE);
            holder.oppoLayout.setVisibility(View.GONE);
            holder.oppoImgSentLayout.setVisibility(View.VISIBLE);
            holder.myImgSentLayout.setVisibility(View.GONE);

            Glide.with(holder.oppoImgSent.getContext()).load(list2.getMessage()).into(holder.oppoImgSent);
            holder.oppoImgSentTime.setText(list2.getDate() + " " + list2.getTime());
        }

        holder.myTime.setVisibility(View.GONE);
        holder.oppoTime.setVisibility(View.GONE);

        holder.myLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.myTime.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp)
                        .duration(100)
                        .onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                YoYo.with(Techniques.SlideOutDown)
                                        .delay(3000)
                                        .duration(500)
                                        .onEnd(new YoYo.AnimatorCallback() {
                                            @Override
                                            public void call(Animator animator) {
                                                holder.myTime.setVisibility(View.GONE);
                                            }
                                        })
                                        .playOn(holder.myTime);
                            }
                        })
                        .playOn(holder.myTime);
            }
        });

        holder.oppoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.oppoTime.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp)
                        .duration(100)
                        .onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                YoYo.with(Techniques.SlideOutDown)
                                        .delay(3000)
                                        .duration(500)
                                        .onEnd(new YoYo.AnimatorCallback() {
                                            @Override
                                            public void call(Animator animator) {
                                                holder.oppoTime.setVisibility(View.GONE);
                                            }
                                        })
                                        .playOn(holder.oppoTime);
                            }
                        })
                        .playOn(holder.oppoTime);
            }
        });

        holder.myImgSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.myImgSentLayout.getVisibility() == View.VISIBLE || holder.oppoImgSentLayout.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(holder.itemView.getContext(), imageFullScreen.class);
                    intent.putExtra("imageUrl", list2.getMessage());
                    context.startActivity(intent);
                    CustomIntent.customType(context, "fadein-to-fadeout");
                }
            }
        });

        holder.oppoImgSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.myImgSentLayout.getVisibility() == View.VISIBLE || holder.oppoImgSentLayout.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(holder.itemView.getContext(), imageFullScreen.class);
                    intent.putExtra("imageUrl", list2.getMessage());
                    context.startActivity(intent);
                    CustomIntent.customType(context, "fadein-to-fadeout");
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public void updateChatList(List<ChatList> chatLists) {
        this.chatLists = chatLists;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout oppoLayout, myLayout, oppoImgSentLayout, myImgSentLayout;
        private TextView oppoMessage, myMessage;
        private ImageView oppoImgSent, myImgSent;
        private TextView oppoTime, myTime, oppoImgSentTime, myImgSentTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            oppoImgSentLayout = itemView.findViewById(R.id.oppoImgSentLayout);
            oppoImgSent = itemView.findViewById(R.id.oppoImgSent);
            oppoImgSentTime = itemView.findViewById(R.id.oppoImgSentTime);

            myImgSentLayout = itemView.findViewById(R.id.myImgSentLayout);
            myImgSent = itemView.findViewById(R.id.myImgSent);
            myImgSentTime = itemView.findViewById(R.id.myImgSentTime);

            oppoLayout = itemView.findViewById(R.id.oppoLayout);
            myLayout = itemView.findViewById(R.id.myLayout);
            oppoMessage = itemView.findViewById(R.id.oppoMessage);
            myMessage = itemView.findViewById(R.id.myMessage);
            oppoTime = itemView.findViewById(R.id.oppoMsgTime);
            myTime = itemView.findViewById(R.id.myMsgTime);
        }
    }
}
