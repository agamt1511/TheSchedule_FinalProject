package com.example.theschedule_finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theschedule_finalproject.Models.Note;
import com.example.theschedule_finalproject.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NoteAdapter extends BaseAdapter {
    Context context;
    ArrayList<Note> noteArrayList;
    LayoutInflater layoutInflater;

    public NoteAdapter(Context context, ArrayList<Note> noteArrayList) {
        this.context = context;
        this.noteArrayList = noteArrayList;
        this.layoutInflater = (LayoutInflater.from(context.getApplicationContext())) ;
    }

    @Override
    public int getCount() {
        return noteArrayList.size();
    }

    @Override
    public Note getItem(int i) {
        return noteArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = ((AppCompatActivity)context).getLayoutInflater();
        view = layoutInflater.inflate(R.layout.note_cell,viewGroup,false);

        Note note = this.noteArrayList.get(i);

        TextView title_tvNC = (TextView) view.findViewById(R.id.title_tvNC);
        TextView dateTime_tvNC = (TextView) view.findViewById(R.id.dateTime_tvNC);

        title_tvNC.setText(note.getTitle());
        dateTime_tvNC.setText(convertToDate(note.getDateTime_created()));

        return view;
    }

    private String convertToDate(String dateTime_created) {
        String dateTime_new = dateTime_created.substring(0,4) +"/" + dateTime_created.substring(4,6) + "/" + dateTime_created.substring(6,8) +
                "\n" + dateTime_created.substring(8,10) +":" + dateTime_created.substring(10,12) + ":" + dateTime_created.substring(12,14);
        return dateTime_new;
    }
}
