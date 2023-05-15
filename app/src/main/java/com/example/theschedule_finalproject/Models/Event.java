package com.example.theschedule_finalproject.Models;

public class Event {
    private String title;
    private String txt;// הפנייה לstorage
    private String event_date;
    private String event_time;
    private int count;
    private int alarm; //request קוד ייחודי לכל התראה


    public Event() {
    }

    public Event(String title, String txt, String event_date, String event_time, int count, int alarm) {
        this.title = title;
        this.txt = txt;
        this.event_date = event_date;
        this.event_time = event_time;
        this.count = count;
        this.alarm = alarm;
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


    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }


    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }
}
