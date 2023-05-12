package com.example.theschedule_finalproject.Models;

import java.util.ArrayList;

public class Note {
    private String title;
    private String txt;// הפנייה לstorage
    private String dateTime_created;
    private Boolean thumbtack;

    public Note() {
    }

    public Note(String title, String txt, String dateTime_created, Boolean thumbtack) {
        this.title = title;
        this.txt = txt;
        this.dateTime_created = dateTime_created;
        this.thumbtack = thumbtack;
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


    public String getDateTime_created() {
        return dateTime_created;
    }

    public void setDateTime_created(String dateTime_created) {
        this.dateTime_created = dateTime_created;
    }


    public Boolean getThumbtack() {
        return thumbtack;
    }

    public void setThumbtack(Boolean thumbtack) {
        this.thumbtack = thumbtack;
    }
}
