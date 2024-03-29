package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.FBST;
import static com.example.theschedule_finalproject.FBref.assignmentsRef;
import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.storageRef;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.theschedule_finalproject.Models.Assignment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * @author Agam Toledano
 * @version 1.0
 * @since 13/12/2022
 * short description - AssignmentEdit Screen
 */
public class AssignmentsEdit extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    BroadcastReceiver broadcastReceiver;

    EditText title_etAE, txt_etAE;
    TextView time_tvAE ,date_tvAE;
    Spinner importance_spAE;
    Button delete_btnAE;

    Assignment assignment, assignment_former;
    Calendar calendar;
    File originalTxtFile;

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;

    AlertDialog.Builder adb;

    Intent assignmentContent;

    DatabaseReference assignmentsDBR_delete;

    ArrayAdapter<String>arrayAdapter;

    String[] priorities;
    String time_str, date_str, originalTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_edit);


        /**
         * Internet connection test using BroadcastReceiver.
         * <p>
         */
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        title_etAE = (EditText) findViewById(R.id.title_etAE);
        txt_etAE = (EditText) findViewById(R.id.txt_etAE);
        time_tvAE = (TextView) findViewById(R.id.time_tvAE);
        date_tvAE = (TextView) findViewById(R.id.date_tvAE);
        importance_spAE = (Spinner) findViewById(R.id.importance_spAE);
        delete_btnAE = (Button) findViewById(R.id.delete_btnAE);

        currentUser = authRef.getCurrentUser();
        calendar = Calendar.getInstance();

        time_str = "Null";
        date_str = "Null";

        delete_btnAE.setVisibility(View.INVISIBLE);

        Resources resources = getResources();
        priorities = resources.getStringArray(R.array.priorities);

        arrayAdapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,priorities);
        importance_spAE.setAdapter(arrayAdapter);

        importance_spAE.setOnItemSelectedListener(this);

        assignment = new Assignment();
        assignment_former = new Assignment();

        assignmentContent = getIntent();
        checkGetAssignment();

        setListeners();
    }

    /**
     * checkGetAssignment.
     * Short description - check whether an assignment was imported from a previous screen and change display accordingly.
     * <p>
     */
    private void checkGetAssignment() {
        originalTitle = assignmentContent.getStringExtra("originalAssignment_title");

        if (!(originalTitle.matches("Null"))){
            delete_btnAE.setVisibility(View.VISIBLE);
            assignment_former.setTitle(originalTitle);
            assignment_former.setTxt(assignmentContent.getStringExtra("originalAssignment_txt"));
            assignment_former.setDateTime_goal(assignmentContent.getStringExtra("originalAssignment_dateAndTime"));
            assignment_former.setCount(assignmentContent.getIntExtra("originalAssignment_count",0));
            assignment_former.setPriority(assignmentContent.getStringExtra("originalAssignment_priority"));
            assignment_former.setCompleted(assignmentContent.getBooleanExtra("originalAssignment_isCompleted", false));

            getTitleAndTxt();
            getDateAndTime();

            int position = arrayAdapter.getPosition(assignment_former.getPriority());
            importance_spAE.setSelection(position);
        }
    }


    /**
     * getTitleAndTxt
     * Short description - Representation of downloaded content in header and text box.
     * <p>
     */
    private void getTitleAndTxt() {
        title_etAE.setText(assignment_former.getTitle());

        final ProgressDialog progressDialog = ProgressDialog.show(this,"downloads data", "downloading...",true);

        originalTxtFile = null;
        try {
            originalTxtFile = File.createTempFile("assignment", ".txt");
            StorageReference originalTxtFile_ref = FBST.getReference(assignment_former.getTxt());
            originalTxtFile_ref.getFile(originalTxtFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        readFile();
                    }
                    else{
                        adb = new AlertDialog.Builder(AssignmentsEdit.this);
                        adb.setTitle("Error Occurred");
                        adb.setMessage("Files were not downloaded properly. Please come back later to try to complete the operation you started.");
                        AlertDialog ad = adb.create();
                        ad.show();
                    }
                }
            });
        }
        catch (IOException e) {
            throw new RuntimeException(e);
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

            txt_etAE.setText(strrd);

            originalTxtFile.delete();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * getDateAndTime.
     * Short description - Date and time display.
     * <p>
     */
    private void getDateAndTime() {
        calendar.set(Calendar.YEAR, Integer.parseInt(assignment_former.getDateTime_goal().substring(0,4)));
        calendar.set(Calendar.MONTH,Integer.parseInt(assignment_former.getDateTime_goal().substring(4,6))-1);
        calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(assignment_former.getDateTime_goal().substring(6,8)));

        date_str = calendar.get(YEAR) + "/" + ((Integer) calendar.get(MONTH)+1) + "/" + calendar.get(DAY_OF_MONTH);

        date_tvAE.setText(date_str);

        String hour_str, minute_str;
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(assignment_former.getDateTime_goal().substring(8,10)));
        calendar.set(Calendar.MINUTE,Integer.parseInt(assignment_former.getDateTime_goal().substring(10,12)));
        calendar.set(Calendar.SECOND,0);

        if(calendar.get(HOUR_OF_DAY)<10){
            hour_str = "0" + calendar.get(HOUR_OF_DAY);
        }
        else {
            hour_str = Integer.toString(calendar.get(HOUR_OF_DAY));
        }

        if(calendar.get(MINUTE)<10){
            minute_str = "0" + calendar.get(MINUTE);
        }
        else {
            minute_str = Integer.toString(calendar.get(MINUTE));
        }

        time_str = hour_str + minute_str;
        time_tvAE.setText(hour_str + ":" + minute_str);
    }


    /**
     * setListeners.
     * Short description - listener for date and time pickers.
     * <p>
     */
    private void setListeners() {
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);

                date_str =  Integer.toString(day) + "/" + Integer.toString(month+1)  + "/" + Integer.toString(year) ;

                date_tvAE.setText(date_str);
            }
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String hour_str, minute_str;
                calendar.set(HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE,minute);
                calendar.set(Calendar.SECOND,0);
                calendar.set(MILLISECOND,0);
                if(hour<10){
                    hour_str = "0" + hour;
                }
                else {
                    hour_str = Integer.toString(hour);
                }

                if(minute<10){
                    minute_str = "0" + minute;
                }
                else {
                    minute_str = Integer.toString(minute);
                }
                time_str = hour_str + minute_str;
                time_tvAE.setText(hour_str + ":" + minute_str);
            }
        };

    }

    /**
     * openTimePicker.
     * Short description - openTimePicker.
     * <p>
     * @param view
     */
    public void openTimePicker(View view) {
        new TimePickerDialog(AssignmentsEdit.this,timeSetListener,calendar.get(HOUR_OF_DAY),calendar.get(MINUTE),true).show();
    }

    /**
     * openDatePicker.
     * Short description - openDatePicker.
     * <p>
     * @param view
     */
    public void openDatePicker(View view) {
        new DatePickerDialog(AssignmentsEdit.this, dateSetListener ,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * onItemSelected.
     * Short description - Put a value in the Assignment element according to the chosen level of importance.
     * <p>
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        assignment.setPriority(priorities[i]);
    }

    /**
     * onNothingSelected.
     * Short description - onNothingSelected.
     * <p>
     * @param adapterView
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    /**
     * Save assignment.
     * Short description - Saving a new task and deleting the previous one (if there is one).
     * <p>
     * @param view the view
     */
    public void saveAssignment(View view) {
        if (dataVerification()) {
            if (!(originalTitle.matches("Null"))) {
                deleteFormerAssignment();
            }


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            assignment.setDateTime_goal(dateFormat.format(calendar.getTime()));


            assignment.setCount(((int) (Math.random()*898)+101));


            assignment.setCompleted(false);


            String title  = title_etAE.getText().toString();
            assignment.setTitle(title);

            String txt  = txt_etAE.getText().toString();
            byte[] assignment_byte = txt.getBytes();

            String txt_path = "Assignments/" + currentUser.getUid() + "/" + assignment.getPriority() + "/" + assignment.getDateTime_goal()+String.valueOf(assignment.getCount()) + ".txt";
            storageRef.child(txt_path).putBytes(assignment_byte);
            assignment.setTxt(txt_path);


            assignmentsRef.child(currentUser.getUid()).child(assignment.getPriority()).child(assignment.getDateTime_goal() + String.valueOf(assignment.getCount())).setValue(assignment);


            Intent newActivity;
            newActivity = new Intent(AssignmentsEdit.this, AssignmentsView.class);
            startActivity(newActivity);
        }
    }


    /**
     * dataVerification.
     * Short description - Verification that the data entered is proper.
     * <p>
     * @return the boolean
     */
    private boolean dataVerification() {
        int errorExist = 0;
        if (title_etAE.getText().toString().length()<1){
            title_etAE.setError("ERROR! The filed can't be blank.");
            errorExist++;
        }
        if (txt_etAE.getText().toString().length()<1){
            txt_etAE.setError("ERROR! The filed can't be blank.");
            errorExist++;
        }
        if (time_str.matches("Null")){
            time_tvAE.setText("The filed can't be blank.");
            errorExist++;
        }
        if (date_str.matches("Null")){
            date_tvAE.setText("The filed can't be blank.");
            errorExist++;
        }
        if(errorExist > 0){
            return false;
        }
        return true;
    }


    /**
     * deleteFormerAssignment.
     * Short description - Deleting an original task.
     * <p>
     */
    private void deleteFormerAssignment() {
        FBST.getReference(assignment_former.getTxt()).delete();

        assignmentsDBR_delete = assignmentsRef.child(currentUser.getUid()).child(assignment_former.getPriority()).child(assignment_former.getDateTime_goal() + String.valueOf(assignment_former.getCount()));
        assignmentsDBR_delete.removeValue();

    }


    /**
     * deleteAssignment.
     * Short description - Deleting an existing task and leaves the screen.
     * <p>
     * @param view
     */
    public void deleteAssignment(View view) {
        if(!(originalTitle.matches("Null"))){
            deleteFormerAssignment();
        }

        Intent newActivity;
        newActivity = new Intent(AssignmentsEdit.this, AssignmentsView.class);
        startActivity(newActivity);
    }

}