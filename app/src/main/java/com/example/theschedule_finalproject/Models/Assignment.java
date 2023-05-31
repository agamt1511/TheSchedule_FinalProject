package com.example.theschedule_finalproject.Models;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 02/01/2023
 * short description - Assignment
 */
public class Assignment {
    private String title;
    private String txt;
    private String dateTime_goal;
    private int count;
    private String priority;
    private boolean completed;


    /**
     * Instantiates a new Assignment.
     * <p>
     */
    public Assignment() {
    }


    /**
     * Instantiates a new Assignment.
     * <p>
     * @param title         the title
     * @param txt           the txt
     * @param dateTime_goal the date time goal
     * @param count         the count
     * @param priority      the priority
     * @param completed     the completed
     */
    public Assignment(String title, String txt, String dateTime_goal, int count, String priority, boolean completed) {
        this.title = title;
        this.txt = txt;
        this.dateTime_goal = dateTime_goal;
        this.count = count;
        this.priority = priority;
        this.completed = completed;
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
     * Gets date time goal.
     * <p>
     * @return the date time goal
     */
    public String getDateTime_goal() {
        return dateTime_goal;
    }

    /**
     * Sets date time goal.
     * <p>
     * @param dateTime_goal the date time goal
     */
    public void setDateTime_goal(String dateTime_goal) {
        this.dateTime_goal = dateTime_goal;
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
     * Gets priority.
     * <p>
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets priority.
     * <p>
     * @param priority the priority
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }


    /**
     * Is completed boolean.
     * <p>
     * @return the boolean
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets completed.
     * <p>
     * @param completed the completed
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
