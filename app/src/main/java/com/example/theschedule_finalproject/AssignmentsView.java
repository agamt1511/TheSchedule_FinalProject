package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.theschedule_finalproject.Models.Assignment;

public class AssignmentsView extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    BroadcastReceiver broadcastReceiver;

    Spinner importance_spAV;
    ListView assignments_lvAV;

    String[] priorities;
    Assignment assignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_view);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        importance_spAV = (Spinner) findViewById(R.id.importance_spAV);
        assignments_lvAV = (ListView) findViewById(R.id.assignments_lvAV);

        assignment = new Assignment();

        currentUser = authRef.getCurrentUser(); //קבלת UID של משתמש מחובר

        Resources resources = getResources();
        priorities = resources.getStringArray(R.array.priorities);
        setArrayPriorities();
        importance_spAV.setOnItemSelectedListener(this);

    }

    private void setArrayPriorities() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,priorities);
        importance_spAV.setAdapter(arrayAdapter);
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
            newActivity = new Intent(AssignmentsView.this, NotesView.class);
            startActivity(newActivity);
        }
        else if (id == R.id.dailySchedule) {
            newActivity = new Intent(AssignmentsView.this, DailyScheduleView.class);
            startActivity(newActivity);
        }
        else if (id == R.id.credits) {
            newActivity = new Intent(AssignmentsView.this, Credits.class);
            startActivity(newActivity);
        }
        else if (id == R.id.profile) {
            newActivity = new Intent(AssignmentsView.this, Profile.class);
            startActivity(newActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    public void addAssignment(View view) {
        Intent newActivity;
        newActivity = new Intent(AssignmentsView.this, AssignmentsEdit.class);
        startActivity(newActivity);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String importance = priorities[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}