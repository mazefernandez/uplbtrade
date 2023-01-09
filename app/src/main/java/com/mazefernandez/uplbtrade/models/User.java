package com.mazefernandez.uplbtrade.models;

public class User {

    public String email;
    public Long time;
    public String lastMessage;

    public User() {} //Needed for firebase

    public User(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }

    public Long getTime() { return time; }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getLastMessage() { return lastMessage; }

    public void setLastMessage(String lastMessage ) { this.lastMessage = lastMessage; }
}
