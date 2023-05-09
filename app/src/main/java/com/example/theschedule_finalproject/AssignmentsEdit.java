package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.currentUser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.theschedule_finalproject.Models.Assignment;

public class AssignmentsEdit extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    BroadcastReceiver broadcastReceiver;
    EditText title_etAE, txt_etAE, time_etAE, date_etAE;
    Spinner importance_spAE;
    Assignment assignment;
    String[] importance = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_edit);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        title_etAE = (EditText) findViewById(R.id.title_etAE);
        txt_etAE = (EditText) findViewById(R.id.txt_etAE);
        time_etAE = (EditText) findViewById(R.id.time_etAE);
        date_etAE = (EditText) findViewById(R.id.date_etAE);
        importance_spAE = (Spinner) findViewById(R.id.importance_spAE);

        importance_spAE.setOnItemSelectedListener(this);


    }

    public void saveAssignment(View view) {
        assignment = new Assignment();
        getDateAndTime();
        getTitleAndTxt();

        Intent newActivity;
        newActivity = new Intent(AssignmentsEdit.this, AssignmentsView.class);
        startActivity(newActivity);
    }

    private void getDateAndTime() {
    }

    private void getTitleAndTxt() {
        String title  = title_etAE.getText().toString();
        assignment.setTitle(title);

        String txt  = txt_etAE.getText().toString();
        //String selectedTxt_path = "Assignments/"+currentUser.getUid()+"/txt"+".jpg";
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}