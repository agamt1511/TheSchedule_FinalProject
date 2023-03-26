package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class SignUp extends AppCompatActivity {
    EditText email_etR, password_etR, passwordConfirm_etR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email_etR = findViewById(R.id.email_etR);
        password_etR = findViewById(R.id.password_etR);
        passwordConfirm_etR = findViewById(R.id.passwordConfirm_etR);
    }


    public void create_account(View view) {
        String email_str  = email_etR.getText().toString();
        String password_str  = password_etR.getText().toString();
        String confirmPassword_str  = passwordConfirm_etR.getText().toString();

        Boolean authenticated = dataVerification(email_str,password_str,confirmPassword_str);

        if (authenticated == true) {
            authRef.createUserWithEmailAndPassword(email_str,password_str);
            Toast.makeText(this, "The Registration Process Was Completed Successfully", Toast.LENGTH_LONG).show();
        }
    }

    public Boolean dataVerification(String email, String password, String confirmPassword) {
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "ERROR! Check the email.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length()<6){
            Toast.makeText(this, "ERROR! Password too short.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.equals(confirmPassword)){
            Toast.makeText(this, "ERROR! Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}