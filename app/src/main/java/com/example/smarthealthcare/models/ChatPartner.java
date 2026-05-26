package com.example.smarthealthcare.models;

public class ChatPartner {
    private String userId;
    private String userName;
    private String lastMessage;
    private long lastMessageTime;

    public ChatPartner(String userId, String userName, String lastMessage, long lastMessageTime) {
        this.userId = userId;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getLastMessage() { return lastMessage; }
    public long getLastMessageTime() { return lastMessageTime; }
}
