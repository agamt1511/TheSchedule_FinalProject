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
    CheckBox thumbtack_cbNE;
    Note note;
    String dateTime_created, thumbtack_str;
    Boolean thumbtack;
    Intent noteContent;
    File originalTxtFile;


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

        noteContent = getIntent();
        checkGetNote();
    }

    private void checkGetNote() {
        String originalTitle = noteContent.getStringExtra("originalNote_title");
        if (!(originalTitle.matches("Null"))) {
            note.setTitle(originalTitle);
            title_etNE.setText(originalTitle);
            String originalTxt = noteContent.getStringExtra("originalNote_txt");
            note.setTxt(originalTxt);
            note.setDateTime_created(noteContent.getStringExtra("originalNote_dateTime"));
            Boolean originalThumbtack = noteContent.getBooleanExtra("originalNote_thumbtack", false);
            if (originalThumbtack) {
                thumbtack_cbNE.setChecked(true);
            }
            note.setThumbtack(originalThumbtack);


            originalTxtFile = null;
            try {
                originalTxtFile = File.createTempFile("note", ".txt");
                StorageReference originalTxtFile_ref = FBST.getReference(originalTxt);
                originalTxtFile_ref.getFile(originalTxtFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            readFile();
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
            Toast.makeText(this, originalTxtFile.getName(), Toast.LENGTH_SHORT).show();
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
            txt_etNE.setText(strrd);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            String txt = "Notes/" + currentUser.getUid() + "/" + thumbtack_str +"/" + dateTime_created + ".txt"; //יצירת כתובת להשמה בSTORAGE
            createTxtFile(note_txt, txt); //יצירת קובץ TXT
            note.setTxt(txt); // השמה של כתובת TXT בDB
            note.setThumbtack(thumbtack);//השמת ערך בוליאני של נעץ
            notesRef.child(currentUser.getUid()).child(thumbtack_str).child(dateTime_created).setValue(note);// השמת ערך Note בDB

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