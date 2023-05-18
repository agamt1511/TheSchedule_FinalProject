package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.FBST;
import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.eventsRef;
import static com.example.theschedule_finalproject.FBref.notesRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theschedule_finalproject.Adapters.EventAdapter;
import com.example.theschedule_finalproject.Models.Event;
import com.example.theschedule_finalproject.Models.Note;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    ListView events_lvDSV;
    ArrayList<Event> eventArrayList;
    String selectedDayNum;
    DatabaseReference eventsDBR, eventsDBR_delete;
    Query eventQuery;
    EventAdapter eventAdapter;
    Intent newActivity;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    String selectedDay;
    private Boolean message;
    AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule_view);
        currentUser = authRef.getCurrentUser(); //קבלת UID של משתמש מחובר

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        calender_cvDSV = (CalendarView) findViewById(R.id.calender_cvDSV);
        events_lvDSV = (ListView) findViewById(R.id.events_lvDSV);

        calender_cvDSV.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String year_str, month_str, day_str;
                year_str = Integer.toString(year);
                if (month<10){
                    month_str = "0" + Integer.toString(month+1);
                }
                else{
                    month_str = Integer.toString(month+1);
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
        message = true;// איפשור שינוי ListView
        eventArrayList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventArrayList);
        events_lvDSV.setAdapter(eventAdapter);

        startCalender();
        setListeners();// יצירת מאזינים
    }

    private void setListeners() {
        events_lvDSV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Event event = (Event) (events_lvDSV.getItemAtPosition(position)); //קבלת ערך Note נבחר
                newActivity = new Intent(DailyScheduleView.this, DailyScheduleEdit.class);
                newActivity.putExtra("originalEvent_title",event.getTitle());
                newActivity.putExtra("originalEvent_txt",event.getTxt());
                newActivity.putExtra("originalEvent_date",event.getEvent_date());
                newActivity.putExtra("originalEvent_time",event.getEvent_time());
                newActivity.putExtra("originalEvent_count",event.getCount());
                newActivity.putExtra("originalEvent_alarm",event.getAlarm());
                startActivity(newActivity);
            }
        });

        events_lvDSV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                //תיבת דיאלוג לווידוא מחיקה
                adb = new AlertDialog.Builder(DailyScheduleView.this);
                adb.setTitle("Delete Note");
                adb.setMessage("Are you sure you want to delete this note?");

                adb.setPositiveButton("YES", new DialogInterface.OnClickListener() {//כפתור אישור
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Event event = (Event) (events_lvDSV.getItemAtPosition(position)); //קבלת ערך Note נבחר
                        eventAdapter.notifyDataSetChanged(); //התראה בAdapter על שינוי ערך

                        message = false;//הגדרת משתנה כדי שלא יופעלו מאזיני הquery כי כבר ביצענו את המחיקה מהתצוגה

                        if(event.getAlarm()!=0){
                            Intent intent = new Intent(DailyScheduleView.this, AlarmReceiver.class);
                            pendingIntent = PendingIntent.getBroadcast(DailyScheduleView.this, event.getAlarm(), intent, 0);
                            if (alarmManager == null){
                                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            }
                            alarmManager.cancel(pendingIntent);
                            Toast.makeText(DailyScheduleView.this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
                        }

                        eventArrayList.remove(event);
                        FBST.getReference(event.getTxt()).delete();

                        eventsDBR_delete = eventsRef.child(currentUser.getUid()).child(event.getEvent_date()).child(event.getEvent_time()+event.getCount());
                        eventsDBR_delete.removeValue();
                    }
                });

                adb.setNegativeButton("NO", new DialogInterface.OnClickListener() {// כפתור יציאה
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}}
                );

                //יצירת והצגת דיאלוג
                AlertDialog ad = adb.create();
                ad.show();

                return false;
            }
        });
        message = true;
    }

    private void startCalender() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        selectedDay = dateFormat.format(calender_cvDSV.getDate());
        selectedDayData();
    }

    private void selectedDayData() {
            eventsDBR = eventsRef.child(currentUser.getUid()).child(selectedDay);
            eventQuery = eventsDBR.orderByChild("event_time");

            eventQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (message) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Event event = dataSnapshot.getValue(Event.class);
                                eventArrayList.add(event);
                            }
                            eventAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            eventAdapter.notifyDataSetChanged();

    }

    private void updateEventAdapter() {
        eventAdapter.notifyDataSetChanged();
    }

    private void updateEventArray() {
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
        newActivity.putExtra("originalEvent_title", "Null"); //השמת ערך כדי לא להפעיל ייבוא פתק
        startActivity(newActivity);
    }

}