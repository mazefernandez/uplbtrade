package com.mazefernandez.uplbtrade.models;

import java.util.Date;

public class AdminChat {
    private String email;
    private String messageText;
    private Long time;


    public AdminChat() {}

    public AdminChat(String email, String messageText) {
        this.email = email;
        this.messageText = messageText;
        this.time = new Date().getTime();
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

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
