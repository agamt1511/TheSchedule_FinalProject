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

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 02/01/2023
 * short description - Assignments Adapter.
 */
public class AssignmentAdapter extends BaseAdapter {
    Context context;
    ArrayList<Assignment> assignmentArrayList;
    LayoutInflater layoutInflater;

    /**
     * Instantiates a new Assignment adapter.
     * <p>
     * @param context             the context
     * @param assignmentArrayList the assignment array list
     */
    public AssignmentAdapter(Context context, ArrayList<Assignment> assignmentArrayList) {
        this.context = context;
        this.assignmentArrayList = assignmentArrayList;
        this.layoutInflater = (LayoutInflater.from(context.getApplicationContext()));;
    }

    /**
     * getCount.
     * Short description - Getting the length of a list of Assignments.
     * <p>
     *
     * @return int list size;
     */
    @Override
    public int getCount() {
        return assignmentArrayList.size();
    }

    /**
     * getItem.
     * Short description - Getting a value of Assignment at a given location.
     * <p>
     * @param i
     * @return Assignment object
     */
    @Override
    public Assignment getItem(int i) {
        return assignmentArrayList.get(i);
    }

    /**
     * getItemId.
     * Short description - Getting id of the variable - returns 0 for all.
     * <p>
     * @param i
     * @return 0
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * getView.
     * Short description - Display definition of the cell in which the object is displayed.
     * + Changes the status of the task according to the user's click.
     * <p>
     * @param i
     * @param view
     * @param viewGroup
     * @return view
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = ((AppCompatActivity)context).getLayoutInflater();
        view = layoutInflater.inflate(R.layout.assignment_cell,viewGroup,false);

        Assignment assignment = getItem(i);

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

    /**
     * convertToDateAndTime.
     * Short description - Conversion of received connected date and time into a convenient and orderly form for reading.
     * <p>
     * @param dateAndTime
     * @return String date and time - new format
     */
    private String convertToDateAndTime(String dateAndTime) {
        String dateAndTime_new = "Date: " + dateAndTime.substring(6,8) +"/" + dateAndTime.substring(4,6) +"/" + dateAndTime.substring(0,4) + " --- "+
                "Time: " + dateAndTime.substring(8,10) +":" + dateAndTime.substring(10,12);
        return dateAndTime_new;
    }
}