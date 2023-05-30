package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.eventsRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
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

import com.example.theschedule_finalproject.Adapters.EventAdapter;
import com.example.theschedule_finalproject.Models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DailyScheduleView extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver;

    CalendarView calender_cvDSV;
    ListView events_lvDSV;

    ArrayList<Event> eventArrayList;

    EventAdapter eventAdapter;

    DatabaseReference eventsDBR;
    Query eventQuery;

    Intent newActivity;

    AlertDialog.Builder adb;

    String selectedDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule_view);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //התאמה בין רכיב תצוגה למשתנה
        calender_cvDSV = (CalendarView) findViewById(R.id.calender_cvDSV);
        events_lvDSV = (ListView) findViewById(R.id.events_lvDSV);

        currentUser = authRef.getCurrentUser(); //קבלת UID של משתמש מחובר

        //מאזין - לשינוי תאריך לחוץ בלוח חודש
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

                selectedDay = year_str + month_str + day_str; //קבלת תאריך של יום לחוץ
                eventArrayList.clear(); //ניקוי רשימת ערכי Event
                selectedDayData();// הצגת אירועי יום נבחר בList View
            }
        });

        eventArrayList = new ArrayList<>(); // יצירת רשימה חדשה
        eventAdapter = new EventAdapter(this, eventArrayList); //קישור בין רשימה לAdapter
        events_lvDSV.setAdapter(eventAdapter); //קישור בין Adapter לListView

        startCalender(); //קבלת יום נבחר והצגה בהתאם
        setListeners();// יצירת מאזינים
    }

    //שינוי תאריך שהתקבל לפורמט תאריך נבחר
    private void startCalender() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        selectedDay = dateFormat.format(calender_cvDSV.getDate());
        selectedDayData();
    }

    // הצגת אירועי יום נבחר בList View
    private void selectedDayData() {
        final ProgressDialog progressDialog = ProgressDialog.show(this,"downloads data", "downloading...",true);
        eventsDBR = eventsRef.child(currentUser.getUid()).child(selectedDay);
        eventQuery = eventsDBR.orderByChild("event_time");

        eventQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Event event = dataSnapshot.getValue(Event.class);//צור ערך Event חדש והשמה בו ערך שהתקבל בפונקציה
                        eventArrayList.add(event); //השמת ערך Event חדש ברשימה
                    }
                    eventAdapter.notifyDataSetChanged(); //עדכון Adapter
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                adb = new AlertDialog.Builder(DailyScheduleView.this);
                adb.setTitle("Error Occurred");
                adb.setMessage("There is a problem importing the data. Please try again later.");
                AlertDialog ad = adb.create();
                ad.show();
            }
        });
        eventAdapter.notifyDataSetChanged(); //עדכון Adapter
    }

    // יצירת מאזיני לחיצה
    private void setListeners() {
        //מאזין ללחיצה רגילה - פתיחה ועריכה של Event
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
    }

    //מעבר לActivity יצירת אירוע חדש
    public void addEvent(View view) {
        Intent newActivity;
        newActivity = new Intent(DailyScheduleView.this, DailyScheduleEdit.class);
        newActivity.putExtra("originalEvent_title", "Null"); //השמת ערך כדי לא להפעיל ייבוא פתק
        startActivity(newActivity);
    }

    //תפריט מסכים
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

}