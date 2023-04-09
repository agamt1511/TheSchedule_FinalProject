package com.example.theschedule_finalproject.TasksModel;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class TaskID {
    @Exclude
    public String TaskID;

    public  <T extends  TaskID> T withId(@NonNull final String id){
        this.TaskID = id;
        return  (T) this;
    }

}
