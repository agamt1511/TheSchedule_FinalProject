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

public class EventAdapter extends BaseAdapter {
    Context context;
    ArrayList<Event> eventArrayList;
    LayoutInflater layoutInflater;

    public EventAdapter(Context context, ArrayList<Event> eventArrayList) {
        this.context = context;
        this.eventArrayList = eventArrayList;
        this.layoutInflater = (LayoutInflater.from(context.getApplicationContext()));
    }

    @Override
    public int getCount() {
        return eventArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return eventArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = ((AppCompatActivity)context).getLayoutInflater();
        view = layoutInflater.inflate(R.layout.event_cell,viewGroup,false);

        Event event = this.eventArrayList.get(i);

        TextView title_tvEC = (TextView) view.findViewById(R.id.title_tvEC);
        TextView time_tvEC = (TextView) view.findViewById(R.id.time_tvEC);

        title_tvEC.setText(event.getTitle());
        time_tvEC.setText(convertToTime(event.getEvent_time()));

        return view;
    }

    private String convertToTime(String event_time) {
        String time_new = event_time.substring(0,2) +":" + event_time.substring(2,4);
        return time_new;
    }
}
