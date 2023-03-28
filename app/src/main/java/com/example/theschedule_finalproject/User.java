package com.example.theschedule_finalproject;

public class User {
    private String user_name;

    public User (){}

    public User (String user_name){
        this.user_name = user_name;
    }


    public String getName() {
        return user_name;
    }
    public void setName(String user_name) {
        this.user_name = user_name;
    }

}
