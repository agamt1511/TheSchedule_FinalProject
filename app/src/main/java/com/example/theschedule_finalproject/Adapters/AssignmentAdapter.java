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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theschedule_finalproject.AssignmentsEdit;
import com.example.theschedule_finalproject.AssignmentsView;
import com.example.theschedule_finalproject.Models.Assignment;
import com.example.theschedule_finalproject.Models.Event;
import com.example.theschedule_finalproject.Models.Note;
import com.example.theschedule_finalproject.R;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class AssignmentAdapter extends BaseAdapter {
    Context context;
    ArrayList<Assignment> assignmentArrayList;
    LayoutInflater layoutInflater;

    public AssignmentAdapter(Context context, ArrayList<Assignment> assignmentArrayList) {
        this.context = context;
        this.assignmentArrayList = assignmentArrayList;
        this.layoutInflater = (LayoutInflater.from(context.getApplicationContext()));;
    }

    @Override
    public int getCount() {
        return assignmentArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return assignmentArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = ((AppCompatActivity)context).getLayoutInflater();
        view = layoutInflater.inflate(R.layout.assignment_cell,viewGroup,false);

        Assignment assignment = this.assignmentArrayList.get(i);


        TextView title_tvAC = (TextView) view.findViewById(R.id.title_tvAC);
        TextView isCompleted_tvAC = (TextView) view.findViewById(R.id.isCompleted_tvAC);
        TextView dateAndTime_tvAC = (TextView) view.findViewById(R.id.dateAndTime_tvAC);

        title_tvAC.setText(assignment.getTitle());
        dateAndTime_tvAC.setText(convertToDateAndTime(assignment.getDateTime_goal()));
        if(assignment.isCompleted()){
            isCompleted_tvAC.setText("Completed");
        }
        else {
            isCompleted_tvAC.setText("Not Complete");
        }

        currentUser = authRef.getCurrentUser();
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

                AssignmentsView.messageAssignment = true;
                assignmentsRef.child(currentUser.getUid()).child(assignment.getPriority()).child(assignment.getDateTime_goal() + String.valueOf(assignment.getCount())).setValue(assignment);
            }
        });


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

    private String convertToDateAndTime(String dateAndTime) {
        String dateAndTime_new = "Date: " + dateAndTime.substring(6,8) +"/" + dateAndTime.substring(4,6) +"/" + dateAndTime.substring(0,4) + " --- "+
                "Time: " + dateAndTime.substring(8,10) +":" + dateAndTime.substring(10,12);
        return dateAndTime_new;
    }
}
