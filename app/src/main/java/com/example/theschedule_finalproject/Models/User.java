package com.example.theschedule_finalproject.Models;

public class User {
    private String user_uid, user_name, user_image;

    public User (){}

    public User (String user_uid, String user_name, String user_image){
        this.user_uid = user_uid;
        this.user_name = user_name;
        this.user_image = user_image;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }
}
