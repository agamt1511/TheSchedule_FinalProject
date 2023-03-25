package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.usersRef;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;


public class SignUp extends AppCompatActivity {
    EditText name_et, email_et, password_et, passwordConfirm_et;
    TextView login;
    CheckBox connectView_cb;
    Boolean rememberMe = false;
    Boolean stayConnect;
    Users userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name_et = findViewById(R.id.name_et);
        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);
        passwordConfirm_et = findViewById(R.id.passwordConfirm_et);
        connectView_cb = findViewById(R.id.connectView_cb);

        //login = findViewById(R.id.login);
        connectView_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked() == true){
                    rememberMe = true;
                }
            }
        });
    }


    public void create_account(View view) {
        String name_str  = name_et.getText().toString();
        String email_str  = email_et.getText().toString();
        String password_str  = password_et.getText().toString();
        String confirmPassword_str  = passwordConfirm_et.getText().toString();
        String uid;

        Boolean authenticated = dataVerification(email_str,password_str,confirmPassword_str);

        if (authenticated == true) {
            authRef.createUserWithEmailAndPassword(email_str,password_str);
            Toast.makeText(this, "The Registration Process Was Completed Successfully", Toast.LENGTH_LONG).show();
            if (rememberMe == true){
                SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                SharedPreferences.Editor editor=settings.edit();
                editor.putBoolean("RememberMe",true);
                editor.commit();
                FirebaseUser user = authRef.getCurrentUser();
                uid = user.getUid();
                userDB = new Users(uid,name_str,email_str,true);
                usersRef.child(uid).setValue(userDB);
                Toast.makeText(SignUp.this, "Successful registration", Toast.LENGTH_SHORT).show();
                Intent si = new Intent(SignUp.this,MainActivity.class);
                si.putExtra("newuser",true);
                startActivity(si);
            }
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