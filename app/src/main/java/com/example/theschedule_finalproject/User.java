package com.example.theschedule_finalproject;

public class User {
    private String user_name, user_email, user_password;
    private Boolean user_activity;

    public User (){}

    public User (String user_name, String user_email, String user_password){
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_password = user_password;
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

    public String getPassword() {
        return user_password;
    }
    public void setPassword(String user_password) {
        this.user_password = user_password;
    }
}
