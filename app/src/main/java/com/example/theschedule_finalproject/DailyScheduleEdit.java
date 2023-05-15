package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.storageRef;

import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.theschedule_finalproject.Models.Event;
import com.example.theschedule_finalproject.databinding.ActivityDailyScheduleEditBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyScheduleEdit extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver;
    EditText title_etDSE, txt_etDSE;
    TextView date_tvDSE, time_tvDSE;
    CheckBox alert_cbDSE;
    Event event;
    Calendar calendar;
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;

    private ActivityDailyScheduleEditBinding dailyScheduleEditBinding;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule_edit);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        title_etDSE = (EditText) findViewById(R.id.title_etDSE);
        txt_etDSE = (EditText) findViewById(R.id.txt_etDSE);
        date_tvDSE = (TextView) findViewById(R.id.date_tvDSE);
        time_tvDSE = (TextView) findViewById(R.id.time_tvDSE);
        alert_cbDSE = (CheckBox) findViewById(R.id.alert_cbDSE);

        createNotificationChannel();

        currentUser = authRef.getCurrentUser();
        calendar = Calendar.getInstance();

        event = new Event();
        setListeners();
    }

    private void setListeners() {
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);

                date_tvDSE.setText((Integer.toString(year) + "/" + Integer.toString(month+1)  + "/" + Integer.toString(day)));
            }
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(Calendar.HOUR,hour);
                calendar.set(MINUTE,minute);

                String hour_str, minute_str;
                if(hour<10){
                    hour_str = "0" + Integer.toString(hour);
                }
                else {
                    hour_str = Integer.toString(hour);
                }

                if(minute<10){
                    minute_str = "0" + Integer.toString(minute);
                }
                else {
                    minute_str = Integer.toString(minute);
                }
                time_tvDSE.setText(hour_str + ":" + minute_str);
            }
        };

    }

    public void saveEvent(View view) {
        setTime();
        setDate();
        setTitleAndTxt();
        setAlarm();
        Intent newActivity;
        newActivity = new Intent(DailyScheduleEdit.this, DailyScheduleView.class);
        startActivity(newActivity);
    }

    private void setAlarm() {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(alert_cbDSE.isChecked()){
            Intent alarmReceiver_intent = new Intent(DailyScheduleEdit.this,AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(DailyScheduleEdit.this,0,alarmReceiver_intent,0);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
            Toast.makeText(this, "aaaa", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent alarmReceiver_intent = new Intent(DailyScheduleEdit.this,AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(DailyScheduleEdit.this,0,alarmReceiver_intent,0);

            if (alarmManager == null){
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            }
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "ccccc", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm");
        event.setEvent_date(dateFormat.format(calendar.getTime()));
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

    public void openTimePicker(View view) {
        new TimePickerDialog(DailyScheduleEdit.this,timeSetListener,calendar.get(HOUR),calendar.get(MINUTE),true).show();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence channelName = "SpringTimeChannel";
            String channelContext = "Channel for Alarm Manger";
            int priority = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("SpringTime",channelName,priority);
            notificationChannel.setDescription(channelContext);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}