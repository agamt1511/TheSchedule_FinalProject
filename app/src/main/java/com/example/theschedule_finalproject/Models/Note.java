package com.example.theschedule_finalproject.Models;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 02/01/2023
 * short description - Note.
 */
public class Note {
    private String title;
    private String txt;
    private String dateTime_created;
    private Boolean thumbtack;


    /**
     * Instantiates a new Note.
     * <p>
     */
    public Note() {
    }


    /**
     * Instantiates a new Note.
     * <p>
     * @param title            the title
     * @param txt              the txt
     * @param dateTime_created the date time created
     * @param thumbtack        the thumbtack
     */
    public Note(String title, String txt, String dateTime_created, Boolean thumbtack) {
        this.title = title;
        this.txt = txt;
        this.dateTime_created = dateTime_created;
        this.thumbtack = thumbtack;
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
     * Gets date time created.
     * <p>
     * @return the date time created
     */
    public String getDateTime_created() {
        return dateTime_created;
    }

    /**
     * Sets date time created.
     * <p>
     * @param dateTime_created the date time created
     */
    public void setDateTime_created(String dateTime_created) {
        this.dateTime_created = dateTime_created;
    }


    /**
     * Gets thumbtack.
     * <p>
     * @return the thumbtack
     */
    public Boolean getThumbtack() {
        return thumbtack;
    }

    /**
     * Sets thumbtack.
     * <p>
     * @param thumbtack the thumbtack
     */
    public void setThumbtack(Boolean thumbtack) {
        this.thumbtack = thumbtack;
    }
}
