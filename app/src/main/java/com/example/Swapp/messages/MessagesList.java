package com.example.Swapp.messages;

public class MessagesList {

    private String name, mobile, lastMessage, profilePic, chatKey, Status;

    private int unseenMessages;

    public MessagesList(String name, String mobile, String lastMessage, String profilePic, int unseenMessages, String chatKey, String Status) {
        this.name = name;
        this.mobile = mobile;
        this.lastMessage = lastMessage;
        this.profilePic = profilePic;
        this.unseenMessages = unseenMessages;
        this.chatKey = chatKey;
        this.Status = Status;
    }

    public String getStatus() {
        return Status;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public String getChatKey() {return chatKey;}
}
