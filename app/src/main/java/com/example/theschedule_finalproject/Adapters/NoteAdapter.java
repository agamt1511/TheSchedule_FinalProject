package com.example.theschedule_finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theschedule_finalproject.Models.Note;
import com.example.theschedule_finalproject.R;

import java.util.ArrayList;

public class NoteAdapter extends BaseAdapter {
    //הכרזה על רכיבי תצוגה, משתנים וכדומה
    Context context;
    ArrayList<Note> noteArrayList;
    LayoutInflater layoutInflater;

    //בנאי
    public NoteAdapter(Context context, ArrayList<Note> noteArrayList) {
        this.context = context;
        this.noteArrayList = noteArrayList;
        this.layoutInflater = (LayoutInflater.from(context.getApplicationContext())) ;
    }

    //קבלת אורך רשימת עצמים
    @Override
    public int getCount() {
        return noteArrayList.size();
    }

    //קבלת עצם במיקום i
    @Override
    public Note getItem(int i) {
        return noteArrayList.get(i);
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
        view = layoutInflater.inflate(R.layout.note_cell,viewGroup,false);

        Note note = getItem(i);

        //התאמה בין רכיב תצוגה למשתנה
        TextView title_tvNC = (TextView) view.findViewById(R.id.title_tvNC);
        TextView dateTime_tvNC = (TextView) view.findViewById(R.id.dateTime_tvNC);

        title_tvNC.setText(note.getTitle());//הצגת כותרת של עצם
        dateTime_tvNC.setText(convertToDate(note.getDateTime_created()));//הצגת תאריך של עצם

        return view;
    }

    //המרה של תאריך ושעה מחוברים שהתקבלו לצורה נוחה ומסודרת לקריאה
    private String convertToDate(String dateTime_created) {
        String dateTime_new = dateTime_created.substring(0,4) +"/" + dateTime_created.substring(4,6) + "/" + dateTime_created.substring(6,8) +
                "\n" + dateTime_created.substring(8,10) +":" + dateTime_created.substring(10,12) + ":" + dateTime_created.substring(12,14);
        return dateTime_new;
    }
}
