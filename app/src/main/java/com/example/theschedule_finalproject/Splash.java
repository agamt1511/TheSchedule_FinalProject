package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class Splash extends AppCompatActivity {
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME,0);
                if (sharedPreferences.getBoolean("hasLoggedIn",false)){
                    Toast.makeText(Splash.this, "eeee", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Splash.this,Profile.class);
                }
                else {
                    Toast.makeText(Splash.this, "rrrrrr", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Splash.this,SignUp.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}