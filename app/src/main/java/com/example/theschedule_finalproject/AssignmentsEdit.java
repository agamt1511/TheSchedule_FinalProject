package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.currentUser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.theschedule_finalproject.Models.Assignment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AssignmentsEdit extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver;
    EditText title_etAE, txt_etAE, time_etAE;
    Button date_btAE;
    Spinner importance_spAE;
    Assignment assignment;
    String[] priorities = {};

    DatePickerDialog datePickerDialog;

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
        date_btAE = (Button) findViewById(R.id.date_btAE);
        importance_spAE = (Spinner) findViewById(R.id.importance_spAE);

        datePickerDialogListener();
    }

    public void saveAssignment(View view) {
        setNewAssignment();



        Intent newActivity;
        newActivity = new Intent(AssignmentsEdit.this, AssignmentsView.class);
        startActivity(newActivity);
    }

    private void datePickerDialogListener() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
            }
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        int style = AlertDialog.THEME_HOLO_LIGHT;
        /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dueDate = dateFormat.format(year+month+day);*/
        datePickerDialog = new DatePickerDialog(this,style, dateSetListener,year,month,day);
    }

    private void setNewAssignment() {
        assignment = new Assignment();
        getDateAndTime();
        getPriority();
        getTitleAndTxt();
    }

    private void getDateAndTime() {
    }

    private void getPriority() {
    }

    private void getTitleAndTxt() {
        String title  = title_etAE.getText().toString();
        assignment.setTitle(title);

        String txt  = txt_etAE.getText().toString();
        //String selectedTxt_path = "Assignments/"+currentUser.getUid()+"/txt"+".jpg";
    }

    public void datePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                date_btAE.setText(year + "/" + month +"/" + day);

            }
        },
        year,month,day);
    }
}