package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.usersActiveRef;
import static com.example.theschedule_finalproject.FBref.usersRef;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


public class SignUp extends AppCompatActivity {
    EditText name_etR, email_etR, password_etR, passwordConfirm_etR;
    TextView login_tvR;
    Intent si;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name_etR = (EditText) findViewById(R.id.name_etR);
        email_etR = (EditText) findViewById(R.id.email_etR);
        password_etR = (EditText) findViewById(R.id.password_etR);
        passwordConfirm_etR = (EditText) findViewById(R.id.passwordConfirm_etR);
        login_tvR = (TextView) findViewById(R.id.login_tvR);

        login_tvR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                si = new Intent(SignUp.this,Login.class);
                startActivity(si);
            }
        });
    }


    public void create_account(View view) {
        String name_str  = name_etR.getText().toString();
        String email_str  = email_etR.getText().toString();
        String password_str  = password_etR.getText().toString();
        String confirmPassword_str  = passwordConfirm_etR.getText().toString();

        Boolean authenticated = dataVerification(email_str,password_str,confirmPassword_str);

        if (authenticated == true) {
            authRef.createUserWithEmailAndPassword(email_str,password_str).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    User user = new User(name_str, email_str, password_str);
                    usersActiveRef.child(authRef.getCurrentUser().getUid()). setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SignUp.this, "The Registration Process Was Completed Successfully", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });

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