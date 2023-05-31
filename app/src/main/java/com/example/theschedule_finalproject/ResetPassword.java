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
/**
 * @author Agam Toledano
 * @version 1.0
 * @since 02/01/2023
 * short description - ResetPassword Screen
 */
public class ResetPassword extends AppCompatActivity {
    EditText email_etF;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        /**
         * Internet connection test using BroadcastReceiver.
         * <p>
         */
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        email_etF = (EditText) findViewById(R.id.email_etF);
    }

    /**
     * Reset password.
     * Short description - Receiving an email and sending a password recovery email to this email.
     * <p>
     * @param view the view
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