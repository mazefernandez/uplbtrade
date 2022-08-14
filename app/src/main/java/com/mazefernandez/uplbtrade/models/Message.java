package com.mazefernandez.uplbtrade.models;

import java.util.Date;

public class Message {
    private String name;
    private String messageText;
    private String email;
    private Long time;

    public Message() {}  // Needed for Firebase

    public Message(String name, String messageText, String email) {
        this.name = name;
        this.messageText = messageText;
        this.email = email;
        this.time = new Date().getTime();
    }


    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getMessage() { return messageText; }

    public void setMessage(String messageText) { this.messageText = messageText; }

    public String getEmail() { return email; }

    public Long getTime() { return time; }
}
