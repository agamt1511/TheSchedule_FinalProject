package com.example.theschedule_finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.theschedule_finalproject.Models.Event;
import com.example.theschedule_finalproject.R;
import java.util.ArrayList;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 02/01/2023
 * short description - Events Adapter.
 */

public class EventAdapter extends BaseAdapter {
    Context context;
    ArrayList<Event> eventArrayList;
    LayoutInflater layoutInflater;

    /**
     * Instantiates a new Event adapter.
     * <p>
     * @param context        the context
     * @param eventArrayList the event array list
     */
    public EventAdapter(Context context, ArrayList<Event> eventArrayList) {
        this.context = context;
        this.eventArrayList = eventArrayList;
        this.layoutInflater = (LayoutInflater.from(context.getApplicationContext()));
    }

    /**
     * getCount.
     * Short description - Getting the length of a list of Events.
     * <p>
     *
     * @return int list size;
     */
    @Override
    public int getCount() {
        return eventArrayList.size();
    }


    /**
     * getItem.
     * Short description - Getting a value of Event at a given location.
     * <p>
     * @param i
     * @return Assignment object
     */
    @Override
    public Event getItem(int i) {
        return eventArrayList.get(i);
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
        view = layoutInflater.inflate(R.layout.event_cell,viewGroup,false);

        Event event = getItem(i);


        TextView title_tvEC = (TextView) view.findViewById(R.id.title_tvEC);
        TextView time_tvEC = (TextView) view.findViewById(R.id.time_tvEC);

        title_tvEC.setText(event.getTitle());
        time_tvEC.setText(convertToTime(event.getEvent_time()));

        return view;
    }

    /**
     * convertToTime
     * Short description - Conversion of received time into a convenient and orderly form for reading.
     * <p>
     * @param event_time
     * @return String time - new format
     */
    private String convertToTime(String event_time) {
        String time_new = event_time.substring(0,2) +":" + event_time.substring(2,4);
        return time_new;
    }
}
