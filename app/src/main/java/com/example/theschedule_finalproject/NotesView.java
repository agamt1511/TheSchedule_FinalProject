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

public class NotesView extends AppCompatActivity{
    //הכרזה על רכיבי תצוגה, משתנים וכדומה
    BroadcastReceiver broadcastReceiver;

    ListView notes_lvNV;

    DatabaseReference notesDBR_thumbtack, notesDBR_noThumbtack ,notesDBR_delete;
    Query queryThumbtack, queryNoThumbtack;

    NoteAdapter noteAdapter;
    ArrayList<Note> noteArrayList_thumbtack, noteArrayList_noThumbtack, noteArrayList_complete;

    Intent newActivity;

    AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_view);


        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //התאמה בין רכיב תצוגה למשתנה
        notes_lvNV = (ListView) findViewById(R.id.notes_lvNV);

        currentUser = authRef.getCurrentUser(); //קבלת UID של משתמש מחובר

        //הגדרת הפנייה לפתקים נעוצים + יצירת רשימה עצמי Note נעוצים
        notesDBR_thumbtack = notesRef.child(currentUser.getUid()).child("Thumbtack");
        noteArrayList_thumbtack = new ArrayList<>();

        //הגדרת הפנייה לפתקים לא נעוצים + יצירת רשימה עצמי Note לא נעוצים
        notesDBR_noThumbtack = notesRef.child(currentUser.getUid()).child("NoThumbtack");
        noteArrayList_noThumbtack = new ArrayList<>();

        noteArrayList_complete = new ArrayList<>(); //יצירת רשימה מאוחדת


        //טיפול בפתקים נעוצים
        final ProgressDialog progressDialog = ProgressDialog.show(this,"Imports data", "fetching data...",true);//יצירת תצוגת טעינה
        //סידור פתקים נעוצים לפי תאריך יצירה - מקטן לגדול
        queryThumbtack = notesDBR_thumbtack.orderByChild("dateTime_created");
        //יצירת מאזין לשינוי ערכים בNote נעוצים query
        queryThumbtack.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Note note = dataSnapshot.getValue(Note.class); //צור ערך Note חדש והשמה בו ערך שהתקבל בפונקציה
                        noteArrayList_thumbtack.add(note); //השמת ערך Note חדש ברשימה
                    }
                    Collections.reverse(noteArrayList_thumbtack);// הפיכת רשימת Note נעוצים - מגדול לקטן
                    updateNoteAdapter();// עדכון רשימה מחוברת של Note נעוצים ולא נעוצים ועדכון Adapter
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


        //טיפול בפתקים לא נעוצים

        //סידור פתקים לא נעוצים לפי תאריך יצירה - מקטן לגדול
        queryNoThumbtack = notesDBR_noThumbtack.orderByChild("dateTime_created");
        //יצירת מאזין לשינוי ערכים בNote לא נעוצים query
        queryNoThumbtack.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Note note = dataSnapshot.getValue(Note.class); //צור ערך Note חדש והשמה בו ערך שהתקבל בפונקציה
                        noteArrayList_noThumbtack.add(note); //השמת ערך Note חדש ברשימה
                    }
                    Collections.reverse(noteArrayList_noThumbtack);// הפיכת רשימת Note נעוצים - מגדול לקטן
                    updateNoteAdapter();// עדכון רשימה מחוברת של Note נעוצים ולא נעוצים ועדכון Adapter
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
        noteAdapter = new NoteAdapter(this,noteArrayList_complete); //הגדרת Adapter חדש עם ערכי הרשימה המאוחדת
        notes_lvNV.setAdapter(noteAdapter); //קישור בין Adapter לרכיב תצוגה

        setListeners();// יצירת מאזינים
    }

    //פעולת עדכון  רשימה
    private void updateNoteArray() {
        noteArrayList_complete.clear();
        for (int i=0; i<noteArrayList_thumbtack.size(); i++){
            noteArrayList_complete.add(noteArrayList_thumbtack.get(i));
        }
        noteArrayList_complete.addAll(noteArrayList_noThumbtack);
    }

    //הגדרת מאזינים
    private void setListeners() {
        //כאשר נלחץ על אחד מעצמי הNote נקבל את ערכיו ונשלח לActivity עריכה
        notes_lvNV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Note note = (Note) (notes_lvNV.getItemAtPosition(position)); //קבלת ערך Note נבחר
                newActivity = new Intent(NotesView.this, NotesEdit.class);
                newActivity.putExtra("originalNote_title",note.getTitle());
                newActivity.putExtra("originalNote_txt",note.getTxt());
                newActivity.putExtra("originalNote_dateTime",note.getDateTime_created());
                newActivity.putExtra("originalNote_thumbtack",note.getThumbtack());
                startActivity(newActivity);
            }
        });
    }

    //פעולת עדכון Adapter
    private void updateNoteAdapter() {
        updateNoteArray();// עדכון רשימת Note מאוחדת
        noteAdapter.notifyDataSetChanged(); //התראה לAdapter על שינוי שקרה
    }

    //מעבר לActivity יצירת פתק חדש
    public void addNote(View view) {
        newActivity = new Intent(NotesView.this, NotesEdit.class);
        newActivity.putExtra("originalNote_title", "Null"); //השמת ערך כדי לא להפעיל ייבוא פתק
        startActivity(newActivity);
    }

    //תפריט מסכים
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