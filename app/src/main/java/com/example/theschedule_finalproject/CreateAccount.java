package com.example.theschedule_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CreateAccount extends AppCompatActivity {
    EditText email_et, password_et, passwordConfirm_et;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);
        passwordConfirm_et = findViewById(R.id.passwordConfirm_et);
        login = findViewById(R.id.login);
    }

    public void create_account(View view) {
        String email_str  = email_et.getText().toString();
        String password_str  = password_et.getText().toString();
        String confirmPassword_str  = passwordConfirm_et.getText().toString();

    }
}