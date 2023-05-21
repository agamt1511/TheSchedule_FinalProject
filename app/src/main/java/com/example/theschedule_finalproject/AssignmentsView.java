package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.assignmentsRef;
import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.eventsRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.theschedule_finalproject.Adapters.AssignmentAdapter;
import com.example.theschedule_finalproject.Models.Assignment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AssignmentsView extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    BroadcastReceiver broadcastReceiver;

    Spinner importance_spAV;
    ListView assignments_lvAV;

    String[] priorities;
    Assignment assignment;
    public static ArrayList<Assignment> assignmentArrayList;
    DatabaseReference assignmentsDBR;
    Query assignmentQuery;
    public static AssignmentAdapter assignmentAdapter;
    String importance;
    public static Boolean messageAssignment;

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
        messageAssignment = true;

        currentUser = authRef.getCurrentUser(); //קבלת UID של משתמש מחובר

        assignmentArrayList = new ArrayList<>();
        assignmentAdapter = new AssignmentAdapter(this,assignmentArrayList);
        assignments_lvAV.setAdapter(assignmentAdapter);

        assignments_lvAV.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

            }
        });
        assignments_lvAV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(AssignmentsView.this, "aa", Toast.LENGTH_SHORT).show();
                Assignment assignment_newActivity = (Assignment) (assignments_lvAV.getItemAtPosition(position));
                Intent newActivity;
                newActivity = new Intent(AssignmentsView.this, AssignmentsEdit.class);
                newActivity.putExtra("originalAssignment_title", assignment_newActivity.getTitle());
                newActivity.putExtra("originalAssignment_txt", assignment_newActivity.getTxt());
                newActivity.putExtra("originalAssignment_dateAndTime", assignment_newActivity.getDateTime_goal());
                newActivity.putExtra("originalAssignment_count", assignment_newActivity.getCount());
                newActivity.putExtra("originalAssignment_priority", assignment_newActivity.getPriority());
                newActivity.putExtra("originalAssignment_isCompleted", assignment_newActivity.isCompleted());
                startActivity(newActivity);
            }
        });

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
        newActivity.putExtra("originalAssignment_title", "Null"); //השמת ערך כדי לא להפעיל ייבוא מטלה
        startActivity(newActivity);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        assignmentArrayList.clear();
        importance = priorities[i];
        showSelectedPriorityData(importance);
    }

    private void showSelectedPriorityData(String importance) {
        assignmentsDBR = assignmentsRef.child(currentUser.getUid()).child(importance);
        assignmentQuery = assignmentsDBR.orderByChild("dateTime_goal");

        assignmentQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (messageAssignment) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Assignment assignmentDataSnapshot = dataSnapshot.getValue(Assignment.class);
                            assignmentArrayList.add(assignmentDataSnapshot);
                        }
                        assignmentAdapter.notifyDataSetChanged();
                    }
                }
                messageAssignment = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}