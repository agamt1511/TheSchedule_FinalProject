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
/**
 * @author Agam Toledano
 * @version 1.0
 * @since 13/12/2022
 * short description - DailyScheduleEdit Screen
 */
public class DailyScheduleEdit extends AppCompatActivity {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule_edit);

        /**
         * Internet connection test using BroadcastReceiver.
         * <p>
         */
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        title_etDSE = (EditText) findViewById(R.id.title_etDSE);
        txt_etDSE = (EditText) findViewById(R.id.txt_etDSE);
        date_tvDSE = (TextView) findViewById(R.id.date_tvDSE);
        time_tvDSE = (TextView) findViewById(R.id.time_tvDSE);
        alert_cbDSE = (CheckBox) findViewById(R.id.alert_cbDSE);
        delete_btnDSE = (Button) findViewById(R.id.delete_btnDSE);


        delete_btnDSE.setVisibility(View.INVISIBLE);

        createNotificationChannel();

        currentUser = authRef.getCurrentUser();
        calendar = Calendar.getInstance();

        time_str = "Null";
        date_str = "Null";
        event = new Event();

        eventContent = getIntent();


        checkGetEvent();
        setListeners();

    }

    /**
     * checkGetEvent.
     * Short description - check whether an event was imported from a previous screen and change display accordingly.
     * <p>
     */
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

    /**
     * getTitleAndTxt
     * Short description - Representation of downloaded content in header and text box.
     * <p>
     */
    private void getTitleAndTxt() {
        title_etDSE.setText(event.getTitle());

        originalTxtFile = null;
        try {
            originalTxtFile = File.createTempFile("event", ".txt");
            StorageReference originalTxtFile_ref = FBST.getReference(event.getTxt());

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

            txt_etDSE.setText(strrd);

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
        calendar.set(Calendar.YEAR, Integer.parseInt(event.getEvent_date().substring(0,4)));
        calendar.set(Calendar.MONTH,Integer.parseInt(event.getEvent_date().substring(4,6))-1);
        calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(event.getEvent_date().substring(6,8)));

        date_str = calendar.get(YEAR) + "/" + ((Integer) calendar.get(MONTH)+1) + "/" + calendar.get(DAY_OF_MONTH);

        date_tvDSE.setText(date_str);

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

    /**
     * getAlarm.
     * Short description - Check a notification checkbox if one has been received.
     * <p>
     */
    private void getAlarm() {
        if(event.getAlarm()!=0){
            alert_cbDSE.setChecked(true);
        }
    }

    /**
     * openTimePicker.
     * Short description - openTimePicker.
     * <p>
     * @param view
     */
    public void openTimePicker(View view) {
        new TimePickerDialog(DailyScheduleEdit.this,timeSetListener,calendar.get(HOUR_OF_DAY),calendar.get(MINUTE),true).show();
    }

    /**
     * openDatePicker.
     * Short description - openDatePicker.
     * <p>
     * @param view
     */
    public void openDatePicker(View view) {
        new DatePickerDialog(DailyScheduleEdit.this, dateSetListener ,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
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

    /**
     * saveEvent.
     * Short description - Saving a new event and deleting the previous one (if there is one).
     * <p>
     * @param view the view
     */
    public void saveEvent(View view) {
        if (dataVerification()){
            if (!(originalTitle.matches("Null"))) {
                deleteFormerEvent();
            }

            setTime();
            setDate();
            setCount();
            setTitleAndTxt();
            setAlarm();

            eventsRef.child(currentUser.getUid()).child(event.getEvent_date()).child(event.getEvent_time()+ String.valueOf(event.getCount())).setValue(event);

            Intent newActivity;
            newActivity = new Intent(DailyScheduleEdit.this, DailyScheduleView.class);
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

    /**
     * deleteFormerEvent.
     * Short description - Deleting an original event.
     * <p>
     */
    private void deleteFormerEvent() {
        if(event.getAlarm()!=0){
            deleteAlert(event.getAlarm());
        }

        FBST.getReference(event.getTxt()).delete();

        eventsDBR_delete = eventsRef.child(currentUser.getUid()).child(event.getEvent_date()).child(event.getEvent_time()+event.getCount());
        eventsDBR_delete.removeValue();
    }

    /**
     * setAlarm.
     * Short description - Create an alert.
     * <p>
     */
    private void setAlarm() {
        if(alert_cbDSE.isChecked()){
            event.setAlarm(((int) (Math.random()*898)+101));
            createAlert(event.getAlarm());
        }
        else {
            event.setAlarm(0);
        }
    }


    /**
     * deleteAlert.
     * Short description - Deleting an alert according to its specific code.
     * <p>
     * @param requestCode
     */
    private void deleteAlert(Integer requestCode) {
        Intent intent = new Intent(DailyScheduleEdit.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(DailyScheduleEdit.this, requestCode, intent, 0);
        if (alarmManager == null){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
    }

    /**
     * createAlert.
     * Short description - Creating an alert according to its specific code.
     * <p>
     * @param requestCode
     */
    private void createAlert(Integer requestCode) {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(DailyScheduleEdit.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(DailyScheduleEdit.this, requestCode, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    /**
     * setTime.
     * Short description - Setting a time for the event.
     * <p>
     */
    private void setTime() {
        event.setEvent_time(time_str);
    }

    /**
     * setDate.
     * Short description - Setting a date for the event.
     * <p>
     */
    private void setDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        event.setEvent_date(dateFormat.format(calendar.getTime()));
    }

    /**
     * setCount.
     * Short description - Setting a count for the event.
     * <p>
     */
    private void setCount() {
        event.setCount(((int) (Math.random()*898)+101));
    }

    /**
     * setCount.
     * Short description - Setting a title and txt for the event.
     * <p>
     */
    private void setTitleAndTxt() {
        event.setTitle(title_etDSE.getText().toString());
        byte[] txt_context = txt_etDSE.getText().toString().getBytes();
        String txt_path = "Events/" + currentUser.getUid() + "/" + event.getEvent_date() + "/" + event.getEvent_time() + event.getCount() + ".txt";
        storageRef.child(txt_path).putBytes(txt_context);
        event.setTxt(txt_path);
    }

    /**
     * createNotificationChannel.
     * Short description - create Notification Channel.
     * <p>
     */
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

    /**
     * deleteEvent.
     * Short description - Deleting an existing event and leaves the screen.
     * <p>
     * @param view
     */
    public void deleteEvent(View view) {
        deleteFormerEvent();

        Intent newActivity;
        newActivity = new Intent(DailyScheduleEdit.this, DailyScheduleView.class);
        startActivity(newActivity);
    }
}