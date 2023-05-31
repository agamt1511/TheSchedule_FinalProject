package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
/**
 * @author Agam Toledano
 * @version 1.0
 * @since 13/12/2022
 * short description - Login Screen
 */
public class Login extends AppCompatActivity {
    EditText email_etL, password_etL;
    Intent intent;
    BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * Internet connection test using BroadcastReceiver.
         * <p>
         */
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        email_etL = (EditText) findViewById(R.id.email_etL);
        password_etL = (EditText) findViewById(R.id.password_etL);
    }

    /**
     * log_in.
     * Short description - Login to an existing user.
     * <p>
     * @param view
     */
    public void log_in(View view) {
        String email_str = email_etL.getText().toString();
        String password_str = password_etL.getText().toString();

        Boolean authenticated = dataVerification(email_str, password_str);

        if (authenticated){
            final ProgressDialog progressDialog = ProgressDialog.show(this,"Logging Account", "Logging ...",true);
            authRef.signInWithEmailAndPassword(email_str,password_str).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        SharedPreferences sharedPreferences = getSharedPreferences(Splash.PREFS_NAME,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Splash.userHasLoggedIn,true);
                        editor.commit();

                        intent = new Intent(Login.this,DailyScheduleView.class);
                        startActivity(intent);
                    }
                    else {
                        AlertDialog.Builder adb;
                        adb = new AlertDialog.Builder(Login.this);
                        adb.setTitle("Login Error");
                        adb.setMessage("E-mail or Password are wrong!");
                        AlertDialog ad = adb.create();
                        ad.show();
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    /**
     * toSignUpAct.
     * Short description - Go to the registration screen.
     * <p>
     * @param view
     */
    public void toSignUpAct(View view) {
        intent = new Intent(Login.this,SignUp.class);
        startActivity(intent);
    }

    /**
     * forgotPassword.
     * Short description - Go to the forgot password screen.
     * <p>
     * @param view
     */
    public void forgotPassword(View view) {
        intent = new Intent(Login.this,ResetPassword.class);
        startActivity(intent);
    }

    /**
     * dataVerification.
     * Short description - Verification that the data entered is proper.
     * <p>
     * @return the boolean
     */
    public Boolean dataVerification(String email, String password) {
        int errorExist = 0;
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_etL.setError("ERROR! Check the email.");
            errorExist++;
        }
        if (password.length()<6){
            password_etL.setError("ERROR! Enter at least 6 characters.");
            errorExist++;
        }

        if(errorExist > 0){
            return false;
        }
        return true;
    }
}