package com.example.theschedule_finalproject.Models;

public class Assignment {
    //הכרזה על רכיבי תצוגה, משתנים וכדומה
    private String title;
    private String txt;// הפנייה לstorage
    private String dateTime_goal;
    private int count;
    private String priority;
    private boolean completed;


    //בנאי ריק
    public Assignment() {
    }


    //בנאי
    public Assignment(String title, String txt, String dateTime_goal, int count, String priority, boolean completed) {
        this.title = title;
        this.txt = txt;
        this.dateTime_goal = dateTime_goal;
        this.count = count;
        this.priority = priority;
        this.completed = completed;
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


    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }


    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
