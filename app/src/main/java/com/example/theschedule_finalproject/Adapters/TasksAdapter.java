package com.example.theschedule_finalproject.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theschedule_finalproject.R;
import com.example.theschedule_finalproject.TasksEdit;
import com.example.theschedule_finalproject.TasksModel.TasksModel;
import com.example.theschedule_finalproject.TasksView;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    private List<TasksModel> todoList;
    private TasksView activity;
    private FirebaseFirestore firestore;

    public TasksAdapter(TasksView mainActivity , List<TasksModel> todoList){
        this.todoList = todoList;
        activity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.task_form , parent , false);
        firestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    public void deleteTask(int position){
        TasksModel toDoModel = todoList.get(position);
        firestore.collection("task").document(toDoModel.TaskID).delete();
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public Context getContext(){
        return activity;
    }

    public void editTask(int position){
        TasksModel toDoModel = todoList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("task" , toDoModel.getTask());
        bundle.putString("due" , toDoModel.getDue());
        bundle.putString("id" , toDoModel.TaskID);

        TasksEdit addNewTask = new TasksEdit();
        addNewTask.setArguments(bundle);
        addNewTask.show(activity.getSupportFragmentManager() , addNewTask.getTag());
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TasksModel toDoModel = todoList.get(position);
        holder.mCheckBox.setText(toDoModel.getTask());

        holder.mDueDateTv.setText("Due On " + toDoModel.getDue());

        holder.mCheckBox.setChecked (toBoolean(TasksModel.getStatus()));

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    firestore.collection("task").document(toDoModel.TaskID).update("status", 1);
                }
                else{
                    firestore.collection("task").document(toDoModel.TaskID).update("status" , 0);
                }
            }
        });
    }

    private boolean toBoolean(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mDueDateTv;
        CheckBox mCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mDueDateTv = itemView.findViewById(R.id.dueDate_tv);
            mCheckBox = itemView.findViewById(R.id.mark_cb);

        }
    }
}
