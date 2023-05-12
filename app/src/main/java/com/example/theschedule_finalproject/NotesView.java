package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.notesRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.theschedule_finalproject.Adapters.NoteAdapter;
import com.example.theschedule_finalproject.Models.Note;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotesView extends AppCompatActivity{
    BroadcastReceiver broadcastReceiver;
    Intent newActivity;
    ListView notes_lvNV;
    ArrayList<Note> noteArrayList_thumbtack, noteArrayList_noThumbtack, noteArrayList_complete;
    DatabaseReference notesDBR_thumbtack, notesDBR_noThumbtack;

    Query queryThumbtack, queryNoThumbtack;
    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_view);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        notes_lvNV = (ListView) findViewById(R.id.notes_lvNV);
        currentUser = authRef.getCurrentUser();




        notesDBR_thumbtack = notesRef.child(currentUser.getUid()).child("Active").child("Thumbtack");
        noteArrayList_thumbtack = new ArrayList<>();

        notesDBR_noThumbtack = notesRef.child(currentUser.getUid()).child("Active").child("NoThumbtack");
        noteArrayList_noThumbtack = new ArrayList<>();


        queryThumbtack = notesDBR_thumbtack.orderByChild("dateTime_created");
        queryThumbtack.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    noteArrayList_thumbtack.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Note note = dataSnapshot.getValue(Note.class);
                        noteArrayList_thumbtack.add(note);
                    }
                    Collections.reverse(noteArrayList_thumbtack);
                    updateNoteAdapter();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        queryNoThumbtack = notesDBR_noThumbtack.orderByChild("dateTime_created");
        queryNoThumbtack.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Note note = dataSnapshot.getValue(Note.class);
                        noteArrayList_noThumbtack.add(note);
                    }
                    Collections.reverse(noteArrayList_noThumbtack);
                    updateNoteAdapter();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        noteArrayList_thumbtack.addAll(noteArrayList_noThumbtack);
        noteAdapter = new NoteAdapter(this,noteArrayList_thumbtack);
        notes_lvNV.setAdapter(noteAdapter);
    }

    private void updateNoteAdapter() {
        noteArrayList_thumbtack.addAll(noteArrayList_noThumbtack);
        noteAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.dailySchedule) {
            newActivity = new Intent(NotesView.this, DailyScheduleView.class);
            startActivity(newActivity);
        }
        else if (id == R.id.assignments) {
            newActivity = new Intent(NotesView.this, AssignmentsView.class);
            startActivity(newActivity);
        }
        else if (id == R.id.credits) {
            newActivity = new Intent(NotesView.this, Credits.class);
            startActivity(newActivity);
        }
        else if (id == R.id.profile) {
            newActivity = new Intent(NotesView.this, Profile.class);
            startActivity(newActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNote(View view) {
        newActivity = new Intent(NotesView.this, NotesEdit.class);
        startActivity(newActivity);
    }

    public void toRecyclingBin_notes(View view) {
        newActivity = new Intent(NotesView.this, NotesRecyclingBin.class);
        startActivity(newActivity);
    }
}