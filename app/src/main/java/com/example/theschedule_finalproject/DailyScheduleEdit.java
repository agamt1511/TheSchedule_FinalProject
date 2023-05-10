package com.example.theschedule_finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

public class DailyScheduleEdit extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule_edit);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void saveEvent(View view) {
        Intent newActivity;
        newActivity = new Intent(DailyScheduleEdit.this, DailyScheduleView.class);
        startActivity(newActivity);
    }
}