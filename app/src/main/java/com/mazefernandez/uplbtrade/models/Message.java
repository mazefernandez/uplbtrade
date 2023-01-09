package com.mazefernandez.uplbtrade.models;

import java.util.Date;

public class Message {
    private String sender;
    private String receiver;
    private String messageText;
    private Long time;

    public Message() {}  // Needed for Firebase

    public Message(String sender, String receiver, String messageText) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageText = messageText;
        this.time = new Date().getTime();
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Long getTime(){
        return time;
    }
}
