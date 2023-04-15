package com.example.theschedule_finalproject.Models;

import java.util.ArrayList;

public class Event {
    private String title;
    private String txt;// הפנייה לstorage
    private ArrayList<String> images; //הפנייה לתקייה בstorage
    private String event_dateTime;


    public Event() {
    }

    public Event (String title, String txt, ArrayList<String>images, String event_dateTime){
        this.title = title;
        this.txt = txt;
        this.images = images;
        this.event_dateTime = event_dateTime;
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


    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }


    public String getEvent_dateTime() {
        return event_dateTime;
    }

    public void setEvent_dateTime(String event_dateTime) {
        this.event_dateTime = event_dateTime;
    }
}