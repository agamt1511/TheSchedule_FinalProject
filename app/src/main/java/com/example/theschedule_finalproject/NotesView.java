package com.example.theschedule_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NotesView extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_view);
    }

    public void addNewNote(View view) {
        intent = new Intent(NotesView.this,NotesEdit.class);
        startActivity(intent);
    }
}