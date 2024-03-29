package com.example.theschedule_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 13/12/2022
 * short description - Credits Screen.
 */
public class Credits extends AppCompatActivity {

    Intent newActivity;
    TextView showCredits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        showCredits = (TextView) findViewById(R.id.showCredits);

        writeIF();
        readIF();
    }

    /**
     * writeIF.
     * Short description - Writing to an internal file.
     * <p>
     */
    private void writeIF() {
        try {
            FileOutputStream fos = openFileOutput("credits.txt",MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            String strwr = "Created by Agam Toledano.\n" +
                    "Teacher: Albert Levy.\n";
            bw.write(strwr);
            bw.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * readIF.
     * Short description - Reading an internal file.
     * <p>
     */
    private void readIF() {
        try {
            FileInputStream fis= openFileInput("credits.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String line = br.readLine();
            while (line != null) {
                sb.append(line+'\n');
                line = br.readLine();
            }
            String strrd = sb.toString();
            br.close();
            showCredits.setText(strrd);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Screen menu.
     * <p>
     * @param menu
     * @return super.onOptionsItemSelected(item)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.dailySchedule) {
            newActivity = new Intent(Credits.this, DailyScheduleView.class);
            startActivity(newActivity);
        }
        else if (id == R.id.assignments) {
            newActivity = new Intent(Credits.this, AssignmentsView.class);
            startActivity(newActivity);
        }
        else if (id == R.id.notes) {
            newActivity = new Intent(Credits.this, NotesView.class);
            startActivity(newActivity);
        }
        else if (id == R.id.profile) {
            newActivity = new Intent(Credits.this, Profile.class);
            startActivity(newActivity);
        }
        return super.onOptionsItemSelected(item);
    }
}