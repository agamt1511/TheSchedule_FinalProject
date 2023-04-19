package com.example.theschedule_finalproject.Models;
import java.util.ArrayList;

public class Assignment {
    private String title;
    private String txt;// הפנייה לstorage
    private String assignment_goal_dateTime;


    public Assignment() {
    }

    public Assignment(String title, String txt, String assignment_goal_dateTime) {
        this.title = title;
        this.txt = txt;
        this.assignment_goal_dateTime = assignment_goal_dateTime;
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

    public String getAssignment_goal_dateTime() {
        return assignment_goal_dateTime;
    }

    public void setAssignment_goal_dateTime(String assignment_goal_dateTime) {
        this.assignment_goal_dateTime = assignment_goal_dateTime;
    }
}
