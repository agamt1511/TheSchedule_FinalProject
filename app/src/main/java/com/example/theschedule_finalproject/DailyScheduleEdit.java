package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.FBST;
import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.eventsRef;
import static com.example.theschedule_finalproject.FBref.storageRef;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
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
import com.example.theschedule_finalproject.Models.Note;
import com.example.theschedule_finalproject.databinding.ActivityDailyScheduleEditBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

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
    Query query;
    String time_str, date_str;
    Intent eventContent;
    String originalTitle;
    File originalTxtFile;



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

        time_str = "null";
        date_str = "null";
        event = new Event();

        eventContent = getIntent();//קבלת Intent מפעילות קודמת
        checkGetEvent();//קבלת נתונים מIntent פעילות קודמת


        setListeners();

    }

    private void checkGetEvent() {
        originalTitle = eventContent.getStringExtra("originalEvent_title");
        if (!(originalTitle.matches("Null"))){
            event.setTitle(originalTitle);
            event.setTxt(eventContent.getStringExtra("originalEvent_txt"));
            event.setEvent_date(eventContent.getStringExtra("originalEvent_date"));
            event.setEvent_time(eventContent.getStringExtra("originalEvent_time"));
            event.setCount(eventContent.getIntExtra("originalEvent_count",0));
            event.setAlarm(eventContent.getIntExtra("originalEvent_alarm",0));


            getTitleAndTxt();
            getDateAndTime();
        }
    }

    private void getDateAndTime() {
        //הגדרת תאריך
        calendar.set(Calendar.YEAR, Integer.parseInt(event.getEvent_date().substring(0,4)));
        calendar.set(Calendar.MONTH,Integer.parseInt(event.getEvent_date().substring(4,6))-1);
        calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(event.getEvent_date().substring(6,8)));

        date_str = calendar.get(YEAR) + "/" + ((Integer) calendar.get(MONTH)+1) + "/" + calendar.get(DAY_OF_MONTH);

        date_tvDSE.setText(date_str);

        //הגדרת זמן
        String hour_str, minute_str;
        Toast.makeText(this, event.getEvent_time().substring(0,2), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, event.getEvent_time().substring(2,4), Toast.LENGTH_SHORT).show();


        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(event.getEvent_time().substring(0,2)));
        calendar.set(Calendar.MINUTE,Integer.parseInt(event.getEvent_time().substring(2,4)));
        calendar.set(Calendar.SECOND,0);
        if(Calendar.HOUR_OF_DAY<10){
            hour_str = "0" + Calendar.HOUR_OF_DAY;
        }
        else {
            hour_str = Integer.toString(Calendar.HOUR_OF_DAY);
        }

        if(Calendar.MINUTE<10){
            minute_str = "0" + Calendar.MINUTE;
        }
        else {
            minute_str = Integer.toString(Calendar.MINUTE);
        }

        hour_str = String.valueOf(Calendar.HOUR_OF_DAY);
        minute_str = String.valueOf(Calendar.MINUTE);
        time_str = hour_str + minute_str;
        time_tvDSE.setText(hour_str + ":" + minute_str);
    }

    private void getTitleAndTxt() {
        title_etDSE.setText(event.getTitle());
        originalTxtFile = null;
        try {
            originalTxtFile = File.createTempFile("event", ".txt"); //יצירת קובץ לקבלת נתונים
            StorageReference originalTxtFile_ref = FBST.getReference(event.getTxt());//יצירת הפנייה למיקום של קובץ txt
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

            txt_etDSE.setText(strrd);// הצגת מחרוזת Txt

            originalTxtFile.delete(); // מחיקת קובץ מהמכשיר
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCount() {
        event.setCount(((int) (Math.random()*898)+101));
    }

    private void setListeners() {
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);

                date_str =  Integer.toString(day) + "/" + Integer.toString(month+1)  + "/" + Integer.toString(year) ;

                date_tvDSE.setText(date_str);
            }
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String hour_str, minute_str;
                calendar.set(HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE,minute);
                calendar.set(Calendar.SECOND,0);
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
                time_tvDSE.setText(hour_str + ":" + minute_str);
            }
        };

    }

    public void saveEvent(View view) {
        if (dataVerification()){
            setTime();
            setDate();
            setCount();
            setTitleAndTxt();
            event.setAlarm(0);
            setAlarm();

            eventsRef.child(currentUser.getUid()).child(event.getEvent_date()).child(event.getEvent_time()+ String.valueOf(event.getCount())).setValue(event);

            Intent newActivity;
            newActivity = new Intent(DailyScheduleEdit.this, DailyScheduleView.class);
            startActivity(newActivity);
        }
    }

    private boolean dataVerification() {
        int errorExist = 0;
        if (title_etDSE.getText().toString().length()<1){
            title_etDSE.setError("ERROR! The filed can't be blank.");
            errorExist++;
        }
        if (txt_etDSE.getText().toString().length()<1){
            txt_etDSE.setError("ERROR! The filed can't be blank.");
            errorExist++;
        }
        if (time_str.matches("null")){
            time_tvDSE.setText("The filed can't be blank.");
            errorExist++;
        }
        if (date_str.matches("null")){
            date_tvDSE.setText("The filed can't be blank.");
            errorExist++;
        }
        if(errorExist > 0){
            return false;
        }
        return true;
    }


    private void setAlarm() {
        if(alert_cbDSE.isChecked()){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(DailyScheduleEdit.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(DailyScheduleEdit.this, 0, intent, 0);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
            Toast.makeText(DailyScheduleEdit.this, "Alarm Set", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent(DailyScheduleEdit.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(DailyScheduleEdit.this, 0, intent, 0);
            if (alarmManager == null){
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            }
            alarmManager.cancel(pendingIntent);
            Toast.makeText(DailyScheduleEdit.this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTime() {
        event.setEvent_time(time_str);
    }

    private void setDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        event.setEvent_date(dateFormat.format(calendar.getTime()));
    }

    private void setTitleAndTxt() {
        event.setTitle(title_etDSE.getText().toString());
        byte[] txt_context = txt_etDSE.getText().toString().getBytes();
        String txt_path = "Events/" + currentUser.getUid() + "/" + event.getEvent_date() + "/" + event.getEvent_time() + event.getCount() + ".txt";
        storageRef.child(txt_path).putBytes(txt_context);
        event.setTxt(txt_path);
    }

    public void openDatePicker(View view) {
        new DatePickerDialog(DailyScheduleEdit.this, dateSetListener ,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void openTimePicker(View view) {
        new TimePickerDialog(DailyScheduleEdit.this,timeSetListener,calendar.get(HOUR_OF_DAY),calendar.get(MINUTE),true).show();
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