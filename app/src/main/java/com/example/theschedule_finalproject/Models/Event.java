package com.example.theschedule_finalproject.Models;

import java.util.ArrayList;

public class Event {
    private String title;
    private String txt;// הפנייה לstorage
    private String event_dateTime;
    private int count;


    public Event() {
    }

    public Event (String title, String txt, String event_dateTime){
        this.title = title;
        this.txt = txt;
        this.event_dateTime = event_dateTime;
        this.count = 0;
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


    public String getEvent_dateTime() {
        return event_dateTime;
    }

    public void setEvent_dateTime(String event_dateTime) {
        this.event_dateTime = event_dateTime;
    }
}
