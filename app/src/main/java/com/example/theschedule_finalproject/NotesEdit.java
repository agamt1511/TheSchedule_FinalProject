package com.example.theschedule_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NotesEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_edit);
    }

    public void saveNote(View view) {
        Intent newActivity;
        newActivity = new Intent(NotesEdit.this, NotesView.class);
        startActivity(newActivity);
    }
}