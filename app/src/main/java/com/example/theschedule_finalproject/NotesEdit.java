package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.notesRef;
import static com.example.theschedule_finalproject.FBref.storageRef;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.theschedule_finalproject.Models.Note;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesEdit extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver;
    EditText title_etNE, txt_etNE;
    CheckBox thumbtack_cbNE;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_edit);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        title_etNE = (EditText) findViewById(R.id.title_etNE);
        txt_etNE = (EditText) findViewById(R.id.txt_etNE);
        thumbtack_cbNE = (CheckBox) findViewById(R.id.thumbtack_cbNE);
        note = new Note();
    }

    public void saveNote(View view) {

        if (dataVerification()){
            String title = title_etNE.getText().toString();
            note.setTitle(title);
            String dateTime_created = getDateAndTime();
            note.setDateTime_created(dateTime_created);
            String txt = getTxtPath();
            createTxtFile(txt);
        }




        Intent newActivity;
        newActivity = new Intent(NotesEdit.this, NotesView.class);
        startActivity(newActivity);
    }

    private void createTxtFile(String txtPath) {
        String note_txt = txt_etNE.getText().toString();
        byte[] note_byte = note_txt.getBytes();
        storageRef.child(txtPath).putBytes(note_byte);
    }

    private String getTxtPath() {
        if (thumbtack_cbNE.isChecked())
            return ("Notes/" + currentUser.getUid() + "/thumbtack" + ".txt");
        else
            return ("Notes/" + currentUser.getUid() + "/thumbtackNo" + ".txt");
    }

    private boolean dataVerification() {
        return true;
    }

    private String getDateAndTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMDDhhmmss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}