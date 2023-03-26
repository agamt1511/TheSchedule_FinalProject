package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.usersRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;

public class Login extends AppCompatActivity {
    EditText email_etL, password_etL;
    TextView signUp_tvR;
    Intent si;
    CheckBox rememberMe_cbL;
    boolean stayConnect;
    public static String PREFS_NAME = "MyPrefFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_etL = (EditText) findViewById(R.id.email_etL);
        password_etL = (EditText) findViewById(R.id.password_etL);
        rememberMe_cbL = (CheckBox) findViewById(R.id.rememberMe_cbL);
        signUp_tvR = (TextView) findViewById(R.id.signUp_tvR);

    }

    public void log_in(View view) {
        String email_str = email_etL.getText().toString();
        String password_str = password_etL.getText().toString();

        Boolean authenticated = dataVerification(email_str, password_str);

        if (authenticated == true) {
            authRef.signInWithEmailAndPassword(email_str, password_str);
            SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME,0);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("hasLoggedIn",true);
            editor.commit();
            startActivity(new Intent(Login.this,Profile.class));
            finish();
        }
    }


            public Boolean dataVerification(String email, String password) {
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "ERROR! Check the email.", Toast.LENGTH_SHORT).show();
            return false;
        }//לבדוק האם להוסיף מצב בו הסיסמה ריקה
        if(password.length()<6){
            Toast.makeText(this, "ERROR! Check the password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}