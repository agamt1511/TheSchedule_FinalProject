package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.FBST;
import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.eventsRef;
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

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.theschedule_finalproject.Models.Event;
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

public class DailyScheduleEdit extends AppCompatActivity {
    //הצהרה על רכיבי תצוגה, משתנים וכדומה
    BroadcastReceiver broadcastReceiver;

    EditText title_etDSE, txt_etDSE;
    TextView date_tvDSE, time_tvDSE;
    CheckBox alert_cbDSE;
    Button delete_btnDSE;

    Event event;
    Calendar calendar;
    AlarmManager alarmManager;

    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;

    DatabaseReference eventsDBR_delete;

    File originalTxtFile;

    PendingIntent pendingIntent;
    Intent eventContent;

    String originalTitle, time_str, date_str;

    public static boolean allowed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule_edit);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //התאמה בין רכיב תצוגה למשתנה
        title_etDSE = (EditText) findViewById(R.id.title_etDSE);
        txt_etDSE = (EditText) findViewById(R.id.txt_etDSE);
        date_tvDSE = (TextView) findViewById(R.id.date_tvDSE);
        time_tvDSE = (TextView) findViewById(R.id.time_tvDSE);
        alert_cbDSE = (CheckBox) findViewById(R.id.alert_cbDSE);
        delete_btnDSE = (Button) findViewById(R.id.delete_btnDSE);


        delete_btnDSE.setVisibility(View.INVISIBLE);//כםתור מחיקה בלתי נראה

        createNotificationChannel();// יצירת ערוץ להתראה

        currentUser = authRef.getCurrentUser(); //קבלת UID של משתמש מחובר
        calendar = Calendar.getInstance(); //יישום לוח שנה

        //אתחול תוכן משתמים ועצמי בסיס
        time_str = "Null";
        date_str = "Null";
        event = new Event();

        eventContent = getIntent();//קבלת Intent מפעילות קודמת


        checkGetEvent();//קבלת נתונים מIntent פעילות קודמת
        setListeners();//הגדרת מאזינים

    }

    //בדיקת נתונים שהתקבלו מActivity קודם
    private void checkGetEvent() {
        originalTitle = eventContent.getStringExtra("originalEvent_title");
        if (!(originalTitle.matches("Null"))){
            delete_btnDSE.setVisibility(View.VISIBLE);
            event.setTitle(originalTitle);
            event.setTxt(eventContent.getStringExtra("originalEvent_txt"));
            event.setEvent_date(eventContent.getStringExtra("originalEvent_date"));
            event.setEvent_time(eventContent.getStringExtra("originalEvent_time"));
            event.setCount(eventContent.getIntExtra("originalEvent_count",0));
            event.setAlarm(eventContent.getIntExtra("originalEvent_alarm",0));

            getTitleAndTxt();
            getDateAndTime();
            getAlarm();
        }
    }

    // כותרת +טקסט - קבלה והשמה במשתנים מתאימים
    private void getTitleAndTxt() {
        title_etDSE.setText(event.getTitle());

        originalTxtFile = null;
        try {
            originalTxtFile = File.createTempFile("event", ".txt"); //יצירת קובץ לקבלת נתונים
            StorageReference originalTxtFile_ref = FBST.getReference(event.getTxt());//יצירת הפנייה למיקום של קובץ txt

            final ProgressDialog progressDialog = ProgressDialog.show(this,"downloads data", "downloading...",true);
            originalTxtFile_ref.getFile(originalTxtFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) { //כאשר ההורדה הסתיימה בהצלחה
                        readFile(); //קריאה והצגה של קובץ txt
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //קריאת קובץ מקומי
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

            txt_etDSE.setText(strrd);// הצגת מחרוזת Txt

            originalTxtFile.delete(); // מחיקת קובץ מהמכשיר
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // תאריך + שעה - קבלה והשמה במשתנים מתאימים
    private void getDateAndTime() {
        //הגדרת תאריך
        calendar.set(Calendar.YEAR, Integer.parseInt(event.getEvent_date().substring(0,4)));
        calendar.set(Calendar.MONTH,Integer.parseInt(event.getEvent_date().substring(4,6))-1);
        calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(event.getEvent_date().substring(6,8)));

        date_str = calendar.get(YEAR) + "/" + ((Integer) calendar.get(MONTH)+1) + "/" + calendar.get(DAY_OF_MONTH);

        date_tvDSE.setText(date_str);

        //הגדרת זמן
        String hour_str, minute_str;
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(event.getEvent_time().substring(0,2)));
        calendar.set(Calendar.MINUTE,Integer.parseInt(event.getEvent_time().substring(2,4)));
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
        time_tvDSE.setText(hour_str + ":" + minute_str);
    }

    // התראה - קבלה והשמה במשתנים מתאימים
    private void getAlarm() {
        if(event.getAlarm()!=0){
            alert_cbDSE.setChecked(true);
        }
    }

    //TimePicker - יצירה ואתחול
    public void openTimePicker(View view) {
        new TimePickerDialog(DailyScheduleEdit.this,timeSetListener,calendar.get(HOUR_OF_DAY),calendar.get(MINUTE),true).show();
    }

    //DatePicker - יצירה ואתחול
    public void openDatePicker(View view) {
        new DatePickerDialog(DailyScheduleEdit.this, dateSetListener ,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // יצירת מאזיני Picker ותוכנם
    private void setListeners() {
        //מאזין לבחירת תאריך
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
                time_tvDSE.setText(hour_str + ":" + minute_str);
            }
        };

    }

    //שמירה/ עדכון של אירוע חדש
    public void saveEvent(View view) {
        if (dataVerification()){ //אימות נתונים
            if (!(originalTitle.matches("Null"))) { //אתחול התראה קיימת (אם יש)
                deleteFormerEvent();
            }

            setTime();
            setDate();
            setCount();
            setTitleAndTxt();
            setAlarm();

            //השמת עצם Event בDB
            eventsRef.child(currentUser.getUid()).child(event.getEvent_date()).child(event.getEvent_time()+ String.valueOf(event.getCount())).setValue(event);

            if(allowed) {
                Intent newActivity;
                newActivity = new Intent(DailyScheduleEdit.this, DailyScheduleView.class);
                startActivity(newActivity);
            }
        }
    }

    //בדיקה האם הפרטים שהוכנסו נכונים
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
        if (time_str.matches("Null")){
            time_tvDSE.setText("The filed can't be blank.");
            errorExist++;
        }
        if (date_str.matches("Null")){
            date_tvDSE.setText("The filed can't be blank.");
            errorExist++;
        }
        if(errorExist > 0){
            return false;
        }
        return true;
    }

    // מחיקת אירוע קודם שעליו לחצנו לפני שנכנסו לActivity זה
    private void deleteFormerEvent() {
        //מחיקת התראה מקורית
        if(event.getAlarm()!=0){
            deleteAlert(event.getAlarm());
        }

        FBST.getReference(event.getTxt()).delete(); //מחיקת תוכן txt של עצם Note מStorage

        //מחיקת עצם Note מDB
        eventsDBR_delete = eventsRef.child(currentUser.getUid()).child(event.getEvent_date()).child(event.getEvent_time()+event.getCount());
        eventsDBR_delete.removeValue();
    }

    //יצירת התראה חדשה/ הגדרת ערך ברירת מחדל להתראה
    private void setAlarm() {
        if(alert_cbDSE.isChecked()){
            event.setAlarm(((int) (Math.random()*898)+101));
            createAlert(event.getAlarm());
        }
        else {
            event.setAlarm(0);
            allowed = true;
        }

        if(!allowed){
            AlertDialog.Builder adb;
            adb = new AlertDialog.Builder(DailyScheduleEdit.this);
            adb.setTitle("Error Occurred");
            adb.setMessage("Please allow the app to send you notifications. Change permissions.");
            AlertDialog ad = adb.create();
            ad.show();
        }
    }

    //מחיקת התראה קיימת בהתאם לrequestCode הספציפי לה
    private void deleteAlert(Integer requestCode) {
        Intent intent = new Intent(DailyScheduleEdit.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(DailyScheduleEdit.this, requestCode, intent, 0);
        if (alarmManager == null){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
    }

    //יצירת התראה עם לrequestCode ספציפי עבורה
    private void createAlert(Integer requestCode) {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(DailyScheduleEdit.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(DailyScheduleEdit.this, requestCode, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()- 60, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    //הגדרת זמן לevent
    private void setTime() {
        event.setEvent_time(time_str);
    }

    //הגדרת תאריך לevent
    private void setDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        event.setEvent_date(dateFormat.format(calendar.getTime()));
    }

    //קבוע count לכל תאריך ושעה ספציפים כדי שיהיה אפשר לשים כמה events על אותו תאריך ושעה
    private void setCount() {
        event.setCount(((int) (Math.random()*898)+101));
    }

    //הגדרת כתרת ותוכן לevent
    private void setTitleAndTxt() {
        event.setTitle(title_etDSE.getText().toString());
        byte[] txt_context = txt_etDSE.getText().toString().getBytes();
        String txt_path = "Events/" + currentUser.getUid() + "/" + event.getEvent_date() + "/" + event.getEvent_time() + event.getCount() + ".txt";
        storageRef.child(txt_path).putBytes(txt_context);
        event.setTxt(txt_path);
    }

    //יצירת ערוץ התראות
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

    //מחיקת Event נוכחי
    public void deleteEvent(View view) {
        deleteFormerEvent();

        Intent newActivity;
        newActivity = new Intent(DailyScheduleEdit.this, DailyScheduleView.class);
        startActivity(newActivity);
    }
}