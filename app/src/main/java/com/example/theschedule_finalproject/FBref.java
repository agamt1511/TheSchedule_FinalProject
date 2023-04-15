package com.example.theschedule_finalproject;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBref {

    public static FirebaseAuth authRef = FirebaseAuth.getInstance();
    public static FirebaseUser currentUser;

    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference usersRef = FBDB.getReference("Users");
    public static DatabaseReference tasksRef = FBDB.getReference("Tasks");

    public static FirebaseStorage FBST = FirebaseStorage.getInstance();
    public static StorageReference storageRef = FBST.getReference();
    public static StorageReference profilePic_ref;

    public static FirebaseFirestore FBFS = FirebaseFirestore.getInstance();

}
