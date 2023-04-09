package com.example.theschedule_finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.example.theschedule_finalproject.Adapters.TasksAdapter;
import com.example.theschedule_finalproject.TasksModel.TasksModel;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TasksView extends AppCompatActivity implements OnDialogCloseListner {
    private RecyclerView viewTasks_rv;
    private FirebaseFirestore firestore;
    private TasksAdapter adapter;
    private List<TasksModel> mList;
    private Query query;
    private ListenerRegistration listenerRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_view);

        viewTasks_rv = (RecyclerView) findViewById(R.id.viewTasks_rv);
        firestore = FirebaseFirestore.getInstance();

        viewTasks_rv.setHasFixedSize(true);
        viewTasks_rv.setLayoutManager(new LinearLayoutManager(TasksView.this));

        mList = new ArrayList<>();
        adapter = new TasksAdapter(TasksView.this,mList);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(viewTasks_rv);
        showData();
        viewTasks_rv.setAdapter(adapter);
    }

    private void showData() {
        query = firestore.collection("task").orderBy("tim e" , Query.Direction.DESCENDING);

        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        String id = documentChange.getDocument().getId();
                        TasksModel toDoModel = documentChange.getDocument().toObject(TasksModel.class).withId(id);
                        mList.add(toDoModel);
                        adapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }

    public void addTask(View view) {
        TasksEdit.newInstance().show(getSupportFragmentManager() , TasksEdit.TAG);
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}