package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.notesRef;
import static com.example.theschedule_finalproject.FBref.usersRef;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.RelativeDateTimeFormatter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.theschedule_finalproject.Adapters.NoteAdapter;
import com.example.theschedule_finalproject.Models.Note;
import com.example.theschedule_finalproject.Models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class NotesView extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver;
    Intent newActivity;
    RecyclerView notes_rvNV;
    NoteAdapter noteAdapter;
    ArrayList<Note> noteArrayList;
    DatabaseReference notesDBR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_view);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        notes_rvNV = (RecyclerView) findViewById(R.id.notes_rvNV);
        notes_rvNV.setHasFixedSize(true);
        notes_rvNV.setLayoutManager(new LinearLayoutManager(this));
        currentUser = authRef.getCurrentUser();

        notesDBR = notesRef.child(currentUser.getUid()).child("Active");

        noteArrayList = new ArrayList<>();
        noteAdapter= new NoteAdapter(this,noteArrayList);
        notes_rvNV.setAdapter(noteAdapter);

        Query query = notesDBR.orderByChild("dateTime_created");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Note note = dataSnapshot.getValue(Note.class);
                        noteArrayList.add(note);
                        Collections.reverse(noteArrayList);
                    }
                    noteAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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