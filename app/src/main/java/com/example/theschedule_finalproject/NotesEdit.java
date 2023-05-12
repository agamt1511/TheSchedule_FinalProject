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
    CheckBox thumbtack_cbNE;
    Note note;
    String dateTime_created, thumbtack_str;
    Boolean thumbtack;


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
        thumbtack_cbNE = (CheckBox) findViewById(R.id.thumbtack_cbNE);

        //אתחול והגדרת תוכן למשתנים ועצמים
        note = new Note();
        currentUser = authRef.getCurrentUser();
    }

    public void saveNote(View view) {
        //קליטת נתונים בסיסיים
        String title = title_etNE.getText().toString();
        String note_txt = txt_etNE.getText().toString();
        thumbtack = thumbtack_cbNE.isChecked();

        // אם הנתונים תקניים שמירה והעלאה של הקובץ
        if (dataVerification(title,note_txt)){
            note.setTitle(title);//השמה של כותרת בDB
            dateTime_created = getDateAndTime();//יצירת תבנית של תאריך וזמן
            note.setDateTime_created(dateTime_created);//השמה של מחרזות תאריך וזמן בDB
            getThumbtackStatus(); // יצירת מחרוזת נעץ בהתאם לערכו הבוליאני
            String txt = "Notes/" + currentUser.getUid() + "/Active/" + thumbtack_str +"/" + dateTime_created + ".txt"; //יצירת כתובת להשמה בSTORAGE
            createTxtFile(note_txt, txt); //יצירת קובץ TXT
            note.setTxt(txt); // השמה של כתובת TXT בDB
            note.setThumbtack(thumbtack);//השמת ערך בוליאני של נעץ
            notesRef.child(currentUser.getUid()).child("Active").child(thumbtack_str).child(dateTime_created).setValue(note);// השמת ערך Note בDB

            //סיום ויצאה מהActivity
            Intent newActivity;
            newActivity = new Intent(NotesEdit.this, NotesView.class);
            startActivity(newActivity);
        }
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

    //יצירת String לנעץ בהתאם לערכו הבוליאני
    private void getThumbtackStatus() {
        if (thumbtack){
            thumbtack_str = "Thumbtack";
        }
        else {
            thumbtack_str = "NoThumbtack";
        }
    }

    //יצירה וההעלאה של קובץ Txt לFBST
    private void createTxtFile(String note_txt, String txtPath) {
        byte[] note_byte = note_txt.getBytes();
        storageRef.child(txtPath).putBytes(note_byte);
    }

}