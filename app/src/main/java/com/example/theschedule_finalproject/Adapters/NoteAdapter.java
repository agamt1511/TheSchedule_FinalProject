package com.example.theschedule_finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theschedule_finalproject.Models.Note;
import com.example.theschedule_finalproject.R;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    Context context;
    ArrayList<Note> noteArrayList;

    public NoteAdapter(Context context, ArrayList<Note> noteArrayList) {
        this.context = context;
        this.noteArrayList = noteArrayList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.note_cell,parent,false);
        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteArrayList.get(position);
        holder.note_cell.setText(note.getTitle());
    }

    @Override
    public int getItemCount() {
        return noteArrayList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView note_cell;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            note_cell = itemView.findViewById(R.id.note_cell);
        }
    }
}
