package com.example.theschedule_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class AssignmentsView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_view);
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

    public void toRecyclingBin_assignments(View view) {
        Intent newActivity;
        newActivity = new Intent(AssignmentsView.this, AssignmentsRecyclingBin.class);
        startActivity(newActivity);
    }
}