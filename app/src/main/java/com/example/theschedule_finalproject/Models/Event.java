package com.example.theschedule_finalproject.Models;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 02/01/2023
 * short description - Event.
 */
public class Event {
    private String title;
    private String txt;
    private String event_date;
    private String event_time;
    private int count;
    private int alarm;

    /**
     * Instantiates a new Event.
     * <p>
     */
    public Event() {
    }

    /**
     * Instantiates a new Event.
     * <p>
     * @param title      the title
     * @param txt        the txt
     * @param event_date the event date
     * @param event_time the event time
     * @param count      the count
     * @param alarm      the alarm
     */
    public Event(String title, String txt, String event_date, String event_time, int count, int alarm) {
        this.title = title;
        this.txt = txt;
        this.event_date = event_date;
        this.event_time = event_time;
        this.count = count;
        this.alarm = alarm;
    }


    /**
     * Gets title.
     * <p>
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     * <p>
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Gets txt.
     * <p>
     * @return the txt
     */
    public String getTxt() {
        return txt;
    }

    /**
     * Sets txt.
     * <p>
     * @param txt the txt
     */
    public void setTxt(String txt) {
        this.txt = txt;
    }


    /**
     * Gets event date.
     * <p>
     * @return the event date
     */
    public String getEvent_date() {
        return event_date;
    }

    /**
     * Sets event date.
     * <p>
     * @param event_date the event date
     */
    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }


    /**
     * Gets event time.
     * <p>
     * @return the event time
     */
    public String getEvent_time() {
        return event_time;
    }

    /**
     * Sets event time.
     * <p>
     * @param event_time the event time
     */
    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }


    /**
     * Gets count.
     * <p>
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets count.
     * <p>
     * @param count the count
     */
    public void setCount(int count) {
        this.count = count;
    }


    /**
     * Gets alarm.
     * <p>
     * @return the alarm
     */
    public int getAlarm() {
        return alarm;
    }

    /**
     * Sets alarm.
     * <p>
     * @param alarm the alarm
     */
    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }
}
