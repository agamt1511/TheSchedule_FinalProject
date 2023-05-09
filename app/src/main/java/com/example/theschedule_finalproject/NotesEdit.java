package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.notesRef;
import static com.example.theschedule_finalproject.FBref.storageRef;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.theschedule_finalproject.Models.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotesEdit extends AppCompatActivity {
    //הכרזה על רכיבי תצוגה, משתנים וכדומה
    BroadcastReceiver broadcastReceiver;
    EditText title_etNE, txt_etNE;
    Note note;
    String dateTime_created;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_edit);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //התאמה בין רכיב תצוגה למשתנה
        title_etNE = (EditText) findViewById(R.id.title_etNE);
        txt_etNE = (EditText) findViewById(R.id.txt_etNE);

        //אתחול והגדרת תוכן למשתנים ועצמים
        note = new Note();
        currentUser = authRef.getCurrentUser();
    }

    public void saveNote(View view) {
        //קליטת נתונים בסיסיים
        String title = title_etNE.getText().toString();
        String note_txt = txt_etNE.getText().toString();

        // אם הנתונים תקניים שמירה והעלאה של הקובץ
        if (dataVerification(title,note_txt)){
            note.setTitle(title);
            dateTime_created = getDateAndTime();
            note.setDateTime_created(dateTime_created);
            String txt = "Notes/" + currentUser.getUid() + "/Active/" + dateTime_created + ".txt";
            createTxtFile(note_txt, txt);
            note.setTxt(txt);
            notesRef.child(currentUser.getUid()).child("Active").child(dateTime_created).setValue(note);

            //סיום ויצאה מהActivity
            Intent newActivity;
            newActivity = new Intent(NotesEdit.this, NotesView.class);
            startActivity(newActivity);
        }
    }

    //יצירה וההעלאה של קובץ Txt לFBST
    private void createTxtFile(String note_txt, String txtPath) {
        byte[] note_byte = note_txt.getBytes();
        storageRef.child(txtPath).putBytes(note_byte);
    }

    //בדיקה האם הפרטים שהוכנסו נכונים
    private boolean dataVerification(String title, String txt) {
        int errorExist = 0;
        if (title.length()<1){
            title_etNE.setError("ERROR! The filed can't be blank.");
            errorExist++;
        }
        if (txt.length()<1){
            txt_etNE.setError("ERROR! Check the email.");
            errorExist++;
        }
        if(errorExist > 0){
            return false;
        }
        return true;
    }

    //המרת תאריך ושעה לפורמט רצוי
    private String getDateAndTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = Calendar.getInstance().getTime();
        return dateFormat.format(date);
    }
}