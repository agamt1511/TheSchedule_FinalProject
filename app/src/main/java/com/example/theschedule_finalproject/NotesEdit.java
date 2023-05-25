package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.FBDB;
import static com.example.theschedule_finalproject.FBref.FBST;
import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.notesRef;
import static com.example.theschedule_finalproject.FBref.storageRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.theschedule_finalproject.Models.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotesEdit extends AppCompatActivity {
    //הכרזה על רכיבי תצוגה, משתנים וכדומה
    BroadcastReceiver broadcastReceiver;

    EditText title_etNE, txt_etNE;
    Button delete_btnNE;
    CheckBox thumbtack_cbNE;

    File originalTxtFile;

    Intent noteContent;

    Note note;

    String originalTitle;


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
        delete_btnNE = (Button) findViewById(R.id.delete_btnNE);

        delete_btnNE.setVisibility(View.INVISIBLE);//הגדרת כפתור מחיקה - בלתי נראה

        //אתחול והגדרת תוכן למשתנים ועצמים
        note = new Note();

        currentUser = authRef.getCurrentUser(); //קבלת UID של משתמש מחובר

        noteContent = getIntent();//קבלת Intent מפעילות קודמת
        checkGetNote();//קבלת נתונים מIntent פעילות קודמת
    }

    private void checkGetNote() {
        originalTitle = noteContent.getStringExtra("originalNote_title"); //קבלת ערך כותרת של הפתק/ משתנה בדיקה מאיפה הגיע הפתק

        //בדיקה: האם הIntent התקבל מלחמיצה על note או מלחציה על כפתור של new note
        if (!(originalTitle.matches("Null"))) {
            delete_btnNE.setVisibility(View.VISIBLE);

            note.setTitle(originalTitle); //השמת כותרת בעצם Note
            title_etNE.setText(originalTitle);// הצגה כותרת בActivity

            String originalTxt = noteContent.getStringExtra("originalNote_txt");//קבלת ערך כתובת txt של הפתק
            note.setTxt(originalTxt);//השמת כתובת txt בעצם Note

            note.setDateTime_created(noteContent.getStringExtra("originalNote_dateTime"));//השמת תאריך יצירה בעצם Note

            note.setThumbtack(noteContent.getBooleanExtra("originalNote_thumbtack", false));//השמת ערך בוליאני של נעץ בעצם Note
            if (note.getThumbtack()) { // סימון check box בהתאם לערך בוליאני של נעץ
                thumbtack_cbNE.setChecked(true);
            }

            //קבלת תוכן txt של העצם Note
            originalTxtFile = null;
            try {
                originalTxtFile = File.createTempFile("note", ".txt"); //יצירת קובץ לקבלת נתונים
                StorageReference originalTxtFile_ref = FBST.getReference(originalTxt);//יצירת הפנייה למיקום של קובץ txt
                originalTxtFile_ref.getFile(originalTxtFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) { //כאשר ההורדה הסתיימה בהצלחה
                            readFile(); //קריאה והצגה של קובץ txt
                        }
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void readFile() {
        try {
            //קריאת קובץ מקומי
            FileInputStream fis= new FileInputStream (new File(originalTxtFile.getPath()));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String line = br.readLine();
            while (line != null) {
                sb.append(line+'\n');
                line = br.readLine();
            }
            String strrd = sb.toString();
            br.close();

            txt_etNE.setText(strrd);// הצגת מחרוזת Txt

            originalTxtFile.delete(); // מחיקת קובץ מהמכשיר
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteNoteContext() {
        String originalNote_thumbtack = getThumbtackStatus(note.getThumbtack());
        FBST.getReference(note.getTxt()).delete();// מחיקת קובץ Note של txt מStorage
        notesRef.child(currentUser.getUid()).child(originalNote_thumbtack).child(note.getDateTime_created()).removeValue(); //מחיקת ערך Note מהDB

    }

    public void saveNote(View view) {
        if (!(originalTitle.matches("Null"))) {
            deleteNoteContext();// מחיקת עצם Note קודם מDB ומחיקת קובץ txt מStorage
        }

        //קליטת נתונים בסיסיים
        String title = title_etNE.getText().toString();
        String note_txt = txt_etNE.getText().toString();
        Boolean thumbtack = thumbtack_cbNE.isChecked();

        // אם הנתונים תקניים שמירה והעלאה של הקובץ
        if (dataVerification(title,note_txt)){
            note.setTitle(title);//השמה של כותרת בDB

            String dateTime_created = getDateAndTime();//יצירת תבנית של תאריך וזמן
            note.setDateTime_created(dateTime_created);//השמה של מחרזות תאריך וזמן בDB

            note.setThumbtack(thumbtack);//השמת ערך בוליאני של נעץ
            String noteThumbtack = getThumbtackStatus(thumbtack); // יצירת מחרוזת נעץ בהתאם לערכו הבוליאני

            String txt = "Notes/" + currentUser.getUid() + "/" + noteThumbtack +"/" + dateTime_created + ".txt"; //יצירת כתובת להשמה בSTORAGE
            createTxtFile(note_txt, txt); //יצירת קובץ TXT
            note.setTxt(txt); // השמה של כתובת TXT בDB

            notesRef.child(currentUser.getUid()).child(noteThumbtack).child(dateTime_created).setValue(note);// השמת ערך Note בDB

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
            txt_etNE.setError("ERROR! The filed can't be blank.");
            errorExist++;
        }
        if(errorExist > 0){
            return false;
        }
        return true;
    }

    //המרת תאריך ושעה לפורמט רצוי
    private String getDateAndTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); // יצירת פורמט של זמן ותאריך
        Date date = Calendar.getInstance().getTime(); //קבלת זמן ותאריך מהלוח שנה
        return dateFormat.format(date); //שליחה חזרה של תאריך
    }

    //יצירת String לנעץ בהתאם לערכו הבוליאני
    private String getThumbtackStatus(Boolean thumbtack) {
        if (thumbtack){
            return "Thumbtack";
        }
        return "NoThumbtack";
    }

    //יצירה וההעלאה של קובץ Txt לFBST
    private void createTxtFile(String note_txt, String txtPath) {
        byte[] note_byte = note_txt.getBytes();
        storageRef.child(txtPath).putBytes(note_byte);
    }

    public void deleteNote(View view) {
        deleteNoteContext();
        //סיום ויצאה מהActivity
        Intent newActivity;
        newActivity = new Intent(NotesEdit.this, NotesView.class);
        startActivity(newActivity);
    }
}