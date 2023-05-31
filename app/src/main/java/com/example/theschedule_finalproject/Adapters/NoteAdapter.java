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
/**
 * @author Agam Toledano
 * @version 1.0
 * @since 02/01/2023
 * short description - Notes Adapter.
 */
public class NoteAdapter extends BaseAdapter {
    Context context;
    ArrayList<Note> noteArrayList;
    LayoutInflater layoutInflater;

    /**
     * Instantiates a new Note adapter.
     * <p>
     * @param context       the context
     * @param noteArrayList the note array list
     */
    public NoteAdapter(Context context, ArrayList<Note> noteArrayList) {
        this.context = context;
        this.noteArrayList = noteArrayList;
        this.layoutInflater = (LayoutInflater.from(context.getApplicationContext())) ;
    }


    /**
     * getCount.
     * Short description - Getting the length of a list of Notes.
     * <p>
     *
     * @return int list size;
     */
    @Override
    public int getCount() {
        return noteArrayList.size();
    }

    /**
     * getItem.
     * Short description - Getting a value of Event at a given location.
     * <p>
     * @param i
     * @return Note object
     */
    @Override
    public Note getItem(int i) {
        return noteArrayList.get(i);
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
     * <p>
     * @param i
     * @param view
     * @param viewGroup
     * @return view
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = ((AppCompatActivity)context).getLayoutInflater();
        view = layoutInflater.inflate(R.layout.note_cell,viewGroup,false);

        Note note = getItem(i);

        TextView title_tvNC = (TextView) view.findViewById(R.id.title_tvNC);
        TextView dateTime_tvNC = (TextView) view.findViewById(R.id.dateTime_tvNC);

        title_tvNC.setText(note.getTitle());
        dateTime_tvNC.setText(convertToDate(note.getDateTime_created()));

        return view;
    }

    /**
     * convertToDate
     * Short description - Conversion of received date and time into a convenient and orderly form for reading.
     * <p>
     * @param dateTime_created
     * @return String date and time - new format
     */
    private String convertToDate(String dateTime_created) {
        String dateTime_new = dateTime_created.substring(0,4) +"/" + dateTime_created.substring(4,6) + "/" + dateTime_created.substring(6,8) +
                "\n" + dateTime_created.substring(8,10) +":" + dateTime_created.substring(10,12) + ":" + dateTime_created.substring(12,14);
        return dateTime_new;
    }
}
