package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
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
import android.widget.CompoundButton;
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
    Intent intent;
    public static String PREFS_NAME = "PrefFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_etL = (EditText) findViewById(R.id.email_etL);
        password_etL = (EditText) findViewById(R.id.password_etL);
    }

    public void log_in(View view) {
        String email_str = email_etL.getText().toString();
        String password_str = password_etL.getText().toString();

        Boolean authenticated = dataVerification(email_str, password_str);

        if (authenticated == true){
            authRef.signInWithEmailAndPassword(email_str,password_str).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("hasLoggedIn",true);
                        editor.commit();
                        if (currentUser == null){
                            currentUser = authRef.getCurrentUser();
                        }
                        intent = new Intent(Login.this,Profile.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(Login.this, "e-mail or password are wrong!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void toSignUpAct(View view) {
        intent = new Intent(Login.this,SignUp.class);
        startActivity(intent);
    }

    public void forgotPassword(View view) {
        intent = new Intent(Login.this,ResetPassword.class);
        startActivity(intent);
    }

    public Boolean dataVerification(String email, String password) {
        int errorExist = 0;
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_etL.setError("ERROR! Check the email.");
            errorExist++;
        }
        if (password.length()<6){
            password_etL.setError("ERROR! Password too short.");
            errorExist++;
        }

        if(errorExist > 0){
            return false;
        }
        return true;
    }
}