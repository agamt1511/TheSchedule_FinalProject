package com.example.theschedule_finalproject.TasksModel;

public class TasksModel extends TaskID  {

    private String task , due;
    private static int status;

    public String getTask() {
        return task;
    }

    public String getDue() {
        return due;
    }

    public static int getStatus() {
        return status;
    }
}
