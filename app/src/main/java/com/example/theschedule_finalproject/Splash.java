package com.example.theschedule_finalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {
    public static String PREFS_NAME = "PrefFile";
    public static String userHasLoggedIn = "hasLoggedIn";
    private static int SPLASH_TIME_OUT = 3000;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //יצירת מסך פתיחה קבוע לאפלקיציה ששולח את משתמש למסך המתאים בהתאם לערך הבוליאני "hasLoggedIn" של SharedPrefrance
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Splash.PREFS_NAME,MODE_PRIVATE);
                if (sharedPreferences.getBoolean(Splash.userHasLoggedIn,false)){
                    intent = new Intent(Splash.this,DailyScheduleView.class);
                }
                else {
                    intent = new Intent(Splash.this, SignUp.class);
                }
                startActivity(intent);
            }
        }, SPLASH_TIME_OUT);
    }
}