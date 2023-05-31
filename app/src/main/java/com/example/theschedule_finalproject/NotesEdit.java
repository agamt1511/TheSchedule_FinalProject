package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.FBST;
import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.notesRef;
import static com.example.theschedule_finalproject.FBref.storageRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.theschedule_finalproject.Models.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * @author Agam Toledano
 * @version 1.0
 * @since 13/12/2022
 * short description - Notes Edit Screen
 */
public class NotesEdit extends AppCompatActivity {
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

        /**
         * Internet connection test using BroadcastReceiver.
         * <p>
         */
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        title_etNE = (EditText) findViewById(R.id.title_etNE);
        txt_etNE = (EditText) findViewById(R.id.txt_etNE);
        thumbtack_cbNE = (CheckBox) findViewById(R.id.thumbtack_cbNE);
        delete_btnNE = (Button) findViewById(R.id.delete_btnNE);

        delete_btnNE.setVisibility(View.INVISIBLE);

        note = new Note();

        currentUser = authRef.getCurrentUser();

        noteContent = getIntent();
        checkGetNote();
    }

    /**
     * checkGetNote.
     * Short description - check whether an note was imported from a previous screen and change display accordingly.
     * <p>
     */
    private void checkGetNote() {
        originalTitle = noteContent.getStringExtra("originalNote_title");

        if (!(originalTitle.matches("Null"))) {
            delete_btnNE.setVisibility(View.VISIBLE);

            note.setTitle(originalTitle);
            title_etNE.setText(originalTitle);

            String originalTxt = noteContent.getStringExtra("originalNote_txt");
            note.setTxt(originalTxt);

            note.setDateTime_created(noteContent.getStringExtra("originalNote_dateTime"));

            note.setThumbtack(noteContent.getBooleanExtra("originalNote_thumbtack", false));
            if (note.getThumbtack()) {
                thumbtack_cbNE.setChecked(true);
            }


            originalTxtFile = null;
            try {
                originalTxtFile = File.createTempFile("note", ".txt");
                StorageReference originalTxtFile_ref = FBST.getReference(originalTxt);

                final ProgressDialog progressDialog = ProgressDialog.show(this,"downloads data", "downloading...",true);
                originalTxtFile_ref.getFile(originalTxtFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        progressDialog.dismiss();
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

    /**
     * readFile.
     * Short description - Reading a local file.
     * <p>
     */
    private void readFile() {
        try {
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

            originalTxtFile.delete();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * deleteNoteContext.
     * Short description - Deleting the original note.
     * <p>
     */
    private void deleteNoteContext() {
        String originalNote_thumbtack = getThumbtackStatus(note.getThumbtack());
        FBST.getReference(note.getTxt()).delete();
        notesRef.child(currentUser.getUid()).child(originalNote_thumbtack).child(note.getDateTime_created()).removeValue();

    }

    /**
     * saveNote.
     * Short description - Saving a new note and deleting the previous one (if there is one).
     * <p>
     * @param view
     */
    public void saveNote(View view) {
        if (!(originalTitle.matches("Null"))) {
            deleteNoteContext();
        }

        String title = title_etNE.getText().toString();
        String note_txt = txt_etNE.getText().toString();
        Boolean thumbtack = thumbtack_cbNE.isChecked();

        if (dataVerification(title,note_txt)){
            note.setTitle(title);

            String dateTime_created = getDateAndTime();
            note.setDateTime_created(dateTime_created);

            note.setThumbtack(thumbtack);
            String noteThumbtack = getThumbtackStatus(thumbtack);

            String txt = "Notes/" + currentUser.getUid() + "/" + noteThumbtack +"/" + dateTime_created + ".txt";
            createTxtFile(note_txt, txt);
            note.setTxt(txt);

            notesRef.child(currentUser.getUid()).child(noteThumbtack).child(dateTime_created).setValue(note);

            Intent newActivity;
            newActivity = new Intent(NotesEdit.this, NotesView.class);
            startActivity(newActivity);
        }
    }

    /**
     * dataVerification.
     * Short description - Verification that the data entered is proper.
     * <p>
     * @return the boolean
     */
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

    /**
     * getDateAndTime.
     * Short description - Getting the date and time of the current day.
     * <p>
     * @return Date object - new format
     */
    private String getDateAndTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = Calendar.getInstance().getTime();
        return dateFormat.format(date);
    }

    /**
     * getThumbtackStatus.
     * Short description - Creating a string according to the boolean value of the thumbtack.
     * <p>
     * @param thumbtack
     * @return String describes thumbtack.
     */
    private String getThumbtackStatus(Boolean thumbtack) {
        if (thumbtack){
            return "Thumbtack";
        }
        return "NoThumbtack";
    }

    /**
     * createTxtFile.
     * Short description - upload txt file.
     * <p>
     * @param note_txt
     * @param txtPath
     */
    private void createTxtFile(String note_txt, String txtPath) {
        byte[] note_byte = note_txt.getBytes();
        storageRef.child(txtPath).putBytes(note_byte);
    }

    /**
     * deleteNote
     * Short description - Delete current note.
     * <p>
     * @param view
     */
    public void deleteNote(View view) {
        deleteNoteContext();

        Intent newActivity;
        newActivity = new Intent(NotesEdit.this, NotesView.class);
        startActivity(newActivity);
    }
}