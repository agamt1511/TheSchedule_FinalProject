package com.example.theschedule_finalproject;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 07/02/2022
 * short description - FBref
 */

public class FBref {

    /**
     * The constant authRef.
     */
    public static FirebaseAuth authRef = FirebaseAuth.getInstance();
    /**
     * The constant currentUser.
     */
    public static FirebaseUser currentUser;

    /**
     * The constant FBDB.
     */
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    /**
     * The constant usersRef.
     */
    public static DatabaseReference usersRef = FBDB.getReference("Users");
    /**
     * The constant eventsRef.
     */
    public static DatabaseReference eventsRef = FBDB.getReference("Events");
    /**
     * The constant notesRef.
     */
    public static DatabaseReference notesRef = FBDB.getReference("Notes");
    /**
     * The constant assignmentsRef.
     */
    public static DatabaseReference assignmentsRef = FBDB.getReference("Assignments");

    /**
     * The constant FBST.
     */
    public static FirebaseStorage FBST = FirebaseStorage.getInstance();
    /**
     * The constant storageRef.
     */
    public static StorageReference storageRef = FBST.getReference();

}
