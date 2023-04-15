package com.example.theschedule_finalproject.Models;

public class User {
    private String user_name, user_image;

    public User (){}

    public User (String user_name, String user_image){
        this.user_name = user_name;
        this.user_image = user_image;
    }


    public String getName() {
        return user_name;
    }
    public void setName(String user_name) {
        this.user_name = user_name;
    }

    public String getImage() {
        return user_image;
    }

    public void setImage(String user_image) {
        this.user_image = user_image;
    }
}
