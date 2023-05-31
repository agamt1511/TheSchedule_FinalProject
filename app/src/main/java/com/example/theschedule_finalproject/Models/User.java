package com.example.theschedule_finalproject.Models;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 02/01/2023
 * short description - User.
 */
public class User {

    private String user_uid, user_name, user_image;

    /**
     * Instantiates a new User.
     * <p>
     */
    public User (){}


    /**
     * Instantiates a new User.
     * <p>
     *
     * @param user_uid   the user uid
     * @param user_name  the user name
     * @param user_image the user image
     */
    public User (String user_uid, String user_name, String user_image){
        this.user_uid = user_uid;
        this.user_name = user_name;
        this.user_image = user_image;
    }


    /**
     * Gets user uid.
     * <p>
     *
     * @return the user uid
     */
    public String getUser_uid() {
        return user_uid;
    }

    /**
     * Sets user uid.
     * <p>
     *
     * @param user_uid the user uid
     */
    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }


    /**
     * Gets user name.
     * <p>
     *
     * @return the user name
     */
    public String getUser_name() {
        return user_name;
    }

    /**
     * Sets user name.
     * <p>
     *
     * @param user_name the user name
     */
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    /**
     * Gets user image.
     * <p>
     *
     * @return the user image
     */
    public String getUser_image() {
        return user_image;
    }

    /**
     * Sets user image.
     * <p>
     *
     * @param user_image the user image
     */
    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

}
