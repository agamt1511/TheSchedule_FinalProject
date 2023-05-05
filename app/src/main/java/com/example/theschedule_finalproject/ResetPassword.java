package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ResetPassword extends AppCompatActivity {
    EditText email_etF;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //בדיקת חיבור לאינטרנט באמצעות BrodcastReciever
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        email_etF = (EditText) findViewById(R.id.email_etF);
    }

    /*קבלת String של אימייל ושליחת מייל לשחזור סיסמה למייל זה
    הערה: אם למשתמש אין חשבון קיים באפליקציה אז לא יישלח אימייל.
     */
    public void resetPassword(View view) {
        String email = email_etF.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_etF.setError("ERROR! Check the email.");
        }
        else {
            authRef.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    AlertDialog.Builder adb;
                    adb = new AlertDialog.Builder(ResetPassword.this);
                    adb.setTitle("Reset Password");
                    adb.setMessage("The email was sent successfully.");
                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    AlertDialog ad = adb.create();
                    ad.show();
                }
            });
        }
    }
}