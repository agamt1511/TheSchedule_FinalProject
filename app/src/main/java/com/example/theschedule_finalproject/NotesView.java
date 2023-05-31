package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.notesRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.theschedule_finalproject.Adapters.NoteAdapter;
import com.example.theschedule_finalproject.Models.Note;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 02 /01/2023
 * short description - NotesView Screen.
 */


public class NotesView extends AppCompatActivity{

    BroadcastReceiver broadcastReceiver;

    ListView notes_lvNV;

    DatabaseReference notesDBR_thumbtack, notesDBR_noThumbtack;
    Query queryThumbtack, queryNoThumbtack;

    NoteAdapter noteAdapter;
    ArrayList<Note> noteArrayList_thumbtack, noteArrayList_noThumbtack, noteArrayList_complete;

    Intent newActivity;

    AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_view);


        /**
         * Internet connection test using BroadcastReceiver.
         * <p>
         */
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        notes_lvNV = (ListView) findViewById(R.id.notes_lvNV);

        currentUser = authRef.getCurrentUser();

        /**
         * Handling of stuck and non-stuck notes
         * <p>
         */

        notesDBR_thumbtack = notesRef.child(currentUser.getUid()).child("Thumbtack");
        noteArrayList_thumbtack = new ArrayList<>();

        notesDBR_noThumbtack = notesRef.child(currentUser.getUid()).child("NoThumbtack");
        noteArrayList_noThumbtack = new ArrayList<>();

        noteArrayList_complete = new ArrayList<>();


        final ProgressDialog progressDialog = ProgressDialog.show(this,"Imports data", "fetching data...",true);
        queryThumbtack = notesDBR_thumbtack.orderByChild("dateTime_created");
        queryThumbtack.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Note note = dataSnapshot.getValue(Note.class);
                        noteArrayList_thumbtack.add(note);
                    }
                    Collections.reverse(noteArrayList_thumbtack);
                    updateNoteAdapter();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                adb = new AlertDialog.Builder(NotesView.this);
                adb.setTitle("Error Occurred");
                adb.setMessage("There is a problem importing the data. Please try again later.");
                AlertDialog ad = adb.create();
                ad.show();
            }
        });


        queryNoThumbtack = notesDBR_noThumbtack.orderByChild("dateTime_created");
        queryNoThumbtack.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Note note = dataSnapshot.getValue(Note.class);
                        noteArrayList_noThumbtack.add(note);
                    }
                    Collections.reverse(noteArrayList_noThumbtack);
                    updateNoteAdapter();
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                adb = new AlertDialog.Builder(NotesView.this);
                adb.setTitle("Error Occurred");
                adb.setMessage("There is a problem importing the data. Please try again later.");
                AlertDialog ad = adb.create();
                ad.show();
            }
        });

        updateNoteArray();
        noteAdapter = new NoteAdapter(this,noteArrayList_complete);
        notes_lvNV.setAdapter(noteAdapter);

        setListeners();
    }

    /**
     * updateNoteArray.
     * Short description - Note list update.
     * <p>
     */
    private void updateNoteArray() {
        noteArrayList_complete.clear();
        for (int i=0; i<noteArrayList_thumbtack.size(); i++){
            noteArrayList_complete.add(noteArrayList_thumbtack.get(i));
        }
        noteArrayList_complete.addAll(noteArrayList_noThumbtack);
    }


    /**
     * setListeners.
     * Short description - Listens for clicking on a note in the list.
     * <p>
     */
    private void setListeners() {
        notes_lvNV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Note note = (Note) (notes_lvNV.getItemAtPosition(position));
                newActivity = new Intent(NotesView.this, NotesEdit.class);
                newActivity.putExtra("originalNote_title",note.getTitle());
                newActivity.putExtra("originalNote_txt",note.getTxt());
                newActivity.putExtra("originalNote_dateTime",note.getDateTime_created());
                newActivity.putExtra("originalNote_thumbtack",note.getThumbtack());
                startActivity(newActivity);
            }
        });
    }

    /**
     * updateNoteAdapter.
     * Short description - Initialize a list and alert the adapter.
     * <p>
     */
    private void updateNoteAdapter() {
        updateNoteArray();
        noteAdapter.notifyDataSetChanged();
    }

    /**
     * addNote.
     * Short description - Go to the create a new note screen.
     * <p>
     * @param view the view
     */
    public void addNote(View view) {
        newActivity = new Intent(NotesView.this, NotesEdit.class);
        newActivity.putExtra("originalNote_title", "Null");
        startActivity(newActivity);
    }

    /**
     * Screen menu.
     * <p>
     * @param menu
     * @return super.onOptionsItemSelected(item)
     */
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
}