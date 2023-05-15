package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.storageRef;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.theschedule_finalproject.Models.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyScheduleEdit extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver;
    EditText title_etDSE, txt_etDSE, date_etDSE;
    Event event;
    Calendar calendar;
    DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule_edit);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        title_etDSE = (EditText) findViewById(R.id.title_etDSE);
        txt_etDSE = (EditText) findViewById(R.id.txt_etDSE);

        currentUser = authRef.getCurrentUser();
        calendar = Calendar.getInstance();


        setListeners();
    }

    private void setListeners() {
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);

                Toast.makeText(DailyScheduleEdit.this, (Integer.toString(year) + "/" + Integer.toString(month)  + "/" + Integer.toString(day) ), Toast.LENGTH_SHORT).show();
            }
        };

    }

    public void saveEvent(View view) {
        event = new Event();
        setDate();
        //setTitleAndTxt();
        Intent newActivity;
        newActivity = new Intent(DailyScheduleEdit.this, DailyScheduleView.class);
        startActivity(newActivity);
    }

    private void setDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        event.setEvent_date(dateFormat.format(calendar.getTime()));
    }

    private void setTitleAndTxt() {
        event.setTitle(title_etDSE.getText().toString());
        byte[] txt_context = txt_etDSE.getText().toString().getBytes();
        String txt_path = "Events/" + currentUser.getUid() + "/" + event.getEvent_date() + "/" + event.getEvent_time() + ".txt";
        storageRef.child(txt_path).putBytes(txt_context);
    }

    public void openDatePicker(View view) {
        new DatePickerDialog(DailyScheduleEdit.this, dateSetListener ,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}