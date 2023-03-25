package com.example.theschedule_finalproject;

public class Users {
    private String uid, user_name, user_image, user_email;
    private Boolean user_activity;

    public Users (){}

    public Users (String uid, String user_name, String user_email,  Boolean user_activity){
        this.uid = uid;
        this.user_name = user_name;
        this.user_image = user_image;
        this.user_activity = user_activity;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return user_name;
    }
    public void setName(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return user_email;
    }
    public void setEmail(String user_email) {
        this.user_email = user_email;
    }

    public String getImage() {
        return user_image;
    }
    public void setImage(String user_image) {
        this.user_image = user_image;
    }

    public Boolean getActive() {
        return user_activity;
    }
    public void setActive(Boolean user_activity) {
        this.user_activity = user_activity;
    }


}
