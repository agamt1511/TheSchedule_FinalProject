package com.example.theschedule_finalproject.Models;
import java.util.ArrayList;

public class Assignment {
    private String title;
    private String txt;// הפנייה לstorage
    private String dateTime_goal;
    private int count;


    public Assignment() {
    }

    public Assignment(String title, String txt, String dateTime_goal, int count) {
        this.title = title;
        this.txt = txt;
        this.dateTime_goal = dateTime_goal;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }


    public String getDateTime_goal() {
        return dateTime_goal;
    }

    public void setDateTime_goal(String dateTime_goal) {
        this.dateTime_goal = dateTime_goal;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
