package com.example.theschedule_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DailyScheduleView extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver;
    CalendarView calender_cvDSV;
    String selectedDayNum;

    String selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule_view);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        calender_cvDSV = (CalendarView) findViewById(R.id.calender_cvDSV);

        calender_cvDSV.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String year_str, month_str, day_str;
                year_str = Integer.toString(year);
                if (month<10){
                    month_str = "0" + Integer.toString(month);
                }
                else{
                    month_str = Integer.toString(month);
                }
                if (day<10){
                    day_str = "0" + Integer.toString(day);
                }
                else{
                    day_str = Integer.toString(day);
                }
                selectedDay = year_str + month_str + day_str;
                selectedDayData();
            }
        });

    }

    private void selectedDayData() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent newActivity;
        int id = item.getItemId();
        if (id == R.id.notes) {
            newActivity = new Intent(DailyScheduleView.this, NotesView.class);
            startActivity(newActivity);
        }
        else if (id == R.id.assignments) {
            newActivity = new Intent(DailyScheduleView.this, AssignmentsView.class);
            startActivity(newActivity);
        }
        else if (id == R.id.credits) {
            newActivity = new Intent(DailyScheduleView.this, Credits.class);
            startActivity(newActivity);
        }
        else if (id == R.id.profile) {
            newActivity = new Intent(DailyScheduleView.this, Profile.class);
            startActivity(newActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    public void addEvent(View view) {
        Intent newActivity;
        newActivity = new Intent(DailyScheduleView.this, DailyScheduleEdit.class);
        startActivity(newActivity);
    }

}