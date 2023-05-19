package com.example.theschedule_finalproject;

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

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.theschedule_finalproject.Models.Assignment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AssignmentsEdit extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    BroadcastReceiver broadcastReceiver;
    EditText title_etAE, txt_etAE;
    TextView time_tvAE ,date_tvAE;
    Spinner importance_spAE;
    Assignment assignment;
    String time_str, date_str;
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    Calendar calendar;

    String[] priorities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_edit);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //התאמה בין רכיב תצוגה למשתנה
        title_etAE = (EditText) findViewById(R.id.title_etAE);
        txt_etAE = (EditText) findViewById(R.id.txt_etAE);
        time_tvAE = (TextView) findViewById(R.id.time_tvAE);
        date_tvAE = (TextView) findViewById(R.id.date_tvAE);
        importance_spAE = (Spinner) findViewById(R.id.importance_spAE);

        assignment = new Assignment();

        currentUser = authRef.getCurrentUser(); //קבלת UID של משתמש מחובר
        calendar = Calendar.getInstance(); //יישום לוח שנה

        //אתחול תוכן משתמים ועצמי בסיס
        time_str = "Null";
        date_str = "Null";

        Resources resources = getResources();
        priorities = resources.getStringArray(R.array.priorities);
        setArrayPriorities();
        importance_spAE.setOnItemSelectedListener(this);
        setListeners();//הגדרת מאזינים
    }

    private void setArrayPriorities() {
        ArrayAdapter<String>arrayAdapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,priorities);
        importance_spAE.setAdapter(arrayAdapter);
    }

    private void setListeners() {
        //מאזין לבחירת תאריך
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

        //מאזין לבחירת זמן
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

    public void openTimePicker(View view) {
        new TimePickerDialog(AssignmentsEdit.this,timeSetListener,calendar.get(HOUR_OF_DAY),calendar.get(MINUTE),true).show();
    }

    public void openDatePicker(View view) {
        new DatePickerDialog(AssignmentsEdit.this, dateSetListener ,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void saveAssignment(View view) {
        setNewAssignment();

        Intent newActivity;
        newActivity = new Intent(AssignmentsEdit.this, AssignmentsView.class);
        startActivity(newActivity);
    }

    private void setNewAssignment() {
        setDateAndTime();
        setCount();
        assignment.setCompleted(false);
        setTitleAndTxt();


        assignmentsRef.child(currentUser.getUid()).child(assignment.getPriority()).child(assignment.getDateTime_goal() + String.valueOf(assignment.getCount())).setValue(assignment);
    }

    private void setDateAndTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        assignment.setDateTime_goal(dateFormat.format(calendar.getTime()));
    }

    private void setCount() {
        assignment.setCount(((int) (Math.random()*898)+101));
    }


    private void setTitleAndTxt() {
        String title  = title_etAE.getText().toString();
        assignment.setTitle(title);

        String txt  = txt_etAE.getText().toString();
        byte[] assignment_byte = txt.getBytes();

        String txt_path = "Assignments/" + currentUser.getUid() + "/" + assignment.getPriority() + "/" + assignment.getDateTime_goal()+String.valueOf(assignment.getCount()) + ".txt";
        storageRef.child(txt_path).putBytes(assignment_byte);
        assignment.setTxt(txt_path);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        assignment.setPriority(priorities[i]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}