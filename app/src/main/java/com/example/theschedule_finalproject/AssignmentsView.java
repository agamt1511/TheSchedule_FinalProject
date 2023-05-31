package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.assignmentsRef;
import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import com.example.theschedule_finalproject.Adapters.AssignmentAdapter;
import com.example.theschedule_finalproject.Models.Assignment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
/**
 * @author Agam Toledano
 * @version 1.0
 * @since 13/12/2022
 * short description - Assignment View Screen
 */

public class AssignmentsView extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    BroadcastReceiver broadcastReceiver;

    Spinner importance_spAV;
    ListView assignments_lvAV;

    AlertDialog.Builder adb;
    ArrayList<Assignment> assignmentArrayList;

    String[] priorities;

    AssignmentAdapter assignmentAdapter;

    DatabaseReference assignmentsDBR;
    Query assignmentQuery;

    String importance;

    public static Boolean messageAssignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_view);

        /**
         * Internet connection test using BroadcastReceiver.
         * <p>
         */
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        importance_spAV = (Spinner) findViewById(R.id.importance_spAV);
        assignments_lvAV = (ListView) findViewById(R.id.assignments_lvAV);

        messageAssignment = true;

        currentUser = authRef.getCurrentUser();

        assignmentArrayList = new ArrayList<>();
        assignmentAdapter = new AssignmentAdapter(this,assignmentArrayList);
        assignments_lvAV.setAdapter(assignmentAdapter);

        Resources resources = getResources();
        priorities = resources.getStringArray(R.array.priorities);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,priorities);
        importance_spAV.setAdapter(arrayAdapter);
        importance_spAV.setOnItemSelectedListener(this);

    }

    /**
     * onItemSelected.
     * Short description - When an item is selected, clear a list and insert new values into the list according to the level of importance.
     * <p>
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        messageAssignment = true;
        importance = priorities[i];
        showSelectedPriorityData(importance);
    }

    /**
     * onNothingSelected.
     * <p>
     * @param adapterView
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    /**
     * showSelectedPriorityData.
     * Short description - Presentation in ListView of all objects associated with a selected level of importance.
     * <p>
     * @param importance
     */
    private void showSelectedPriorityData(String importance) {
        assignmentsDBR = assignmentsRef.child(currentUser.getUid()).child(importance);
        assignmentQuery = assignmentsDBR.orderByChild("dateTime_goal");

        final ProgressDialog progressDialog = ProgressDialog.show(this,"Imports data", "fetching data...",true);//יצירת תצוגת טעינה
        assignmentQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(messageAssignment){
                    assignmentArrayList.clear();
                    assignmentAdapter.notifyDataSetChanged();
                }
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Assignment assignmentDataSnapshot = dataSnapshot.getValue(Assignment.class);
                        assignmentArrayList.add(assignmentDataSnapshot);
                    }
                }
                progressDialog.dismiss();
                messageAssignment = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                adb = new AlertDialog.Builder(AssignmentsView.this);
                adb.setTitle("Error Occurred");
                adb.setMessage("There is a problem importing the data. Please try again later.");
                AlertDialog ad = adb.create();
                ad.show();
            }
        });
    }

    /**
     * addAssignment.
     * Short description - Go to the add task screen.
     * <p>
     * @param view
     */
    public void addAssignment(View view) {
        Intent newActivity;
        newActivity = new Intent(AssignmentsView.this, AssignmentsEdit.class);
        newActivity.putExtra("originalAssignment_title", "Null");
        startActivity(newActivity);
    }

    /**
     * Screen menu.
     * <p>
     * @param menu
     * @return super.onOptionsItemSelected(item)
     */
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

}