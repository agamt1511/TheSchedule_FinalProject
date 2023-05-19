package com.example.theschedule_finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theschedule_finalproject.Models.Assignment;
import com.example.theschedule_finalproject.Models.Event;
import com.example.theschedule_finalproject.Models.Note;
import com.example.theschedule_finalproject.R;

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

        CheckBox title_cbAC = (CheckBox) view.findViewById(R.id.title_cbAC);
        TextView dateAndTime_tvAC = (TextView) view.findViewById(R.id.dateAndTime_tvAC);

        title_cbAC.setText(assignment.getTitle());
        title_cbAC.setChecked(assignment.isCompleted());

        dateAndTime_tvAC.setText(convertToDateAndTime(assignment.getDateTime_goal()));

        return view;
    }

    private String convertToDateAndTime(String dateAndTime) {
        String dateAndTime_new = "Date: " + dateAndTime.substring(6,8) +"/" + dateAndTime.substring(4,6) +"/" + dateAndTime.substring(0,4) + "\n"+
                "Time:" + dateAndTime.substring(8,10) +":" + dateAndTime.substring(10,12);
        return dateAndTime_new;
    }
}
