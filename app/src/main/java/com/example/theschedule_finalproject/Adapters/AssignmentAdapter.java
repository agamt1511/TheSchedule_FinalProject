package com.example.theschedule_finalproject.Adapters;

import static com.example.theschedule_finalproject.FBref.assignmentsRef;
import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theschedule_finalproject.AssignmentsEdit;
import com.example.theschedule_finalproject.AssignmentsView;
import com.example.theschedule_finalproject.Models.Assignment;
import com.example.theschedule_finalproject.R;

import java.util.ArrayList;

public class AssignmentAdapter extends BaseAdapter {
    //הכרזה על רכיבי תצוגה, משתנים וכדומה
    Context context;
    ArrayList<Assignment> assignmentArrayList;
    LayoutInflater layoutInflater;

    //בנאי
    public AssignmentAdapter(Context context, ArrayList<Assignment> assignmentArrayList) {
        this.context = context;
        this.assignmentArrayList = assignmentArrayList;
        this.layoutInflater = (LayoutInflater.from(context.getApplicationContext()));;
    }

    //קבלת אורך רשימת עצמים
    @Override
    public int getCount() {
        return assignmentArrayList.size();
    }

    //קבלת עצם במיקום i
    @Override
    public Object getItem(int i) {
        return assignmentArrayList.get(i);
    }

    // קבלת id של המשתנה - מחזיר 0 לכולם
    @Override
    public long getItemId(int i) {
        return 0;
    }

    //הגדרת תצוגת של התא בו מוצג העצם
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = ((AppCompatActivity)context).getLayoutInflater();
        view = layoutInflater.inflate(R.layout.assignment_cell,viewGroup,false);

        Assignment assignment = this.assignmentArrayList.get(i); //קבלת ערך במיקום נבחר

        //התאמה בין רכיב תצוגה למשתנה
        TextView title_tvAC = (TextView) view.findViewById(R.id.title_tvAC);
        TextView isCompleted_tvAC = (TextView) view.findViewById(R.id.isCompleted_tvAC);
        TextView dateAndTime_tvAC = (TextView) view.findViewById(R.id.dateAndTime_tvAC);


        title_tvAC.setText(assignment.getTitle());//הצגת כותרת של עצם

        dateAndTime_tvAC.setText(convertToDateAndTime(assignment.getDateTime_goal()));//הצגת תאריך של עצם

        //הגדרת סטטוס עצם התחלתי
        if(assignment.isCompleted()){
            isCompleted_tvAC.setText("Completed");
        }
        else {
            isCompleted_tvAC.setText("Not Complete");
        }

        currentUser = authRef.getCurrentUser(); //קבלת UID של משתמש מחובר

        //שינוי ערך של העצם בהתאם ללחיצת המשתש
        isCompleted_tvAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (assignment.isCompleted()){
                    assignment.setCompleted(false);
                    isCompleted_tvAC.setText("Not Complete");
                }
                else {
                    assignment.setCompleted(true);
                    isCompleted_tvAC.setText("Completed");
                }

                AssignmentsView.messageAssignment = true; // משתנה לאתחול רשימה
                //הגדרת סטטוס חדש בAssignment
                assignmentsRef.child(currentUser.getUid()).child(assignment.getPriority()).child(assignment.getDateTime_goal() + String.valueOf(assignment.getCount())).setValue(assignment);
            }
        });


        // מעבר לActivity עריכת עצם נבחר
        title_tvAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivity;
                newActivity = new Intent(view.getContext(), AssignmentsEdit.class);
                newActivity.putExtra("originalAssignment_title", assignment.getTitle());
                newActivity.putExtra("originalAssignment_txt", assignment.getTxt());
                newActivity.putExtra("originalAssignment_dateAndTime", assignment.getDateTime_goal());
                newActivity.putExtra("originalAssignment_count", assignment.getCount());
                newActivity.putExtra("originalAssignment_priority", assignment.getPriority());
                newActivity.putExtra("originalAssignment_isCompleted", assignment.isCompleted());
                view.getContext().startActivity(newActivity);
            }
        });
        return view;
    }

    //המרה של תאריך ושעה מחוברים שהתקבלו לצורה נוחה ומסודרת לקריאה
    private String convertToDateAndTime(String dateAndTime) {
        String dateAndTime_new = "Date: " + dateAndTime.substring(6,8) +"/" + dateAndTime.substring(4,6) +"/" + dateAndTime.substring(0,4) + " --- "+
                "Time: " + dateAndTime.substring(8,10) +":" + dateAndTime.substring(10,12);
        return dateAndTime_new;
    }
}