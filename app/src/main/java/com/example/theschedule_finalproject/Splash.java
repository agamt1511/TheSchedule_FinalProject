package com.example.theschedule_finalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 13/12/2022
 * short description - Splash (opening screen)
 */
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

    /**
     * Short description:
     * Creating a fixed splash screen for the application that sends the user to the
     * appropriate screen according to the "hasLoggedIn" boolean value of SharedPreference.
     * <p>
     */
    @Override
    protected void onStart() {
        super.onStart();

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