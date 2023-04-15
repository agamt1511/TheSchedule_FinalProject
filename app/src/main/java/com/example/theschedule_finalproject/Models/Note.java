package com.example.theschedule_finalproject.Models;

import java.util.ArrayList;

public class Note {
    private String title;
    private String txt;// הפנייה לstorage
    private ArrayList<String> images;
    private String note_dateTime_created;

    public Note() {
    }

    public Note(String title, String txt, ArrayList<String> images, String note_dateTime_created) {
        this.title = title;
        this.txt = txt;
        this.images = images;
        this.note_dateTime_created = note_dateTime_created;
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


    public String getNote_dateTime_created() {
        return note_dateTime_created;
    }

    public void setNote_dateTime_created(String note_dateTime_created) {
        this.note_dateTime_created = note_dateTime_created;
    }
}
