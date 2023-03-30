package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.usersRef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;


public class SignUp extends AppCompatActivity {
    EditText name_etS, email_etS, password_etS, passwordConfirm_etS;
    CheckBox rememberMe_cbS;
    Intent intent;
    Boolean rememberMe_boolS;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name_etS = (EditText) findViewById(R.id.name_etS);
        email_etS = (EditText) findViewById(R.id.email_etS);
        password_etS = (EditText) findViewById(R.id.password_etS);
        passwordConfirm_etS = (EditText) findViewById(R.id.passwordConfirm_etS);
        rememberMe_cbS = (CheckBox) findViewById(R.id.rememberMe_cbS);

        rememberMe_boolS = false;
        rememberMe_cbS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    rememberMe_boolS = true;
                }
                else{
                    rememberMe_boolS = false;
                }
            }
        });
    }


    public void create_account(View view) {
        String name_str  = name_etS.getText().toString();
        String email_str  = email_etS.getText().toString();
        String password_str  = password_etS.getText().toString();
        String confirmPassword_str  = passwordConfirm_etS.getText().toString();

        Boolean authenticated = dataVerification(name_str, email_str,password_str,confirmPassword_str);

        if (authenticated == true) {
            authRef.createUserWithEmailAndPassword(email_str,password_str).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    User user = new User(name_str);
                    usersRef.child(authRef.getCurrentUser().getUid()).setValue(user);

                    if (rememberMe_boolS == true){
                        SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("hasLoggedIn",true);
                        editor.commit();
                    }
                    currentUser = authRef.getCurrentUser();
                    intent = new Intent(SignUp.this,Profile.class);
                    startActivity(intent);
                }
            });
        }
    }

    public Boolean dataVerification(String name, String email, String password, String confirmPassword) {
        int errorExist = 0;
        if (name.length()<1){
            name_etS.setError("ERROR! The filed can't be blank.");
            errorExist++;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_etS.setError("ERROR! Check the email.");
            errorExist++;
        }
        if (password.length()<6){
            password_etS.setError("ERROR! Password too short.");
            errorExist++;
        }
        if (!password.equals(confirmPassword)) {
            passwordConfirm_etS.setError("ERROR! Passwords do not match");
            errorExist++;
        }

        if(errorExist > 0){
            return false;
        }
        return true;
    }

    public void toLogInAct(View view) {
        intent = new Intent(SignUp.this,Login.class);
        startActivity(intent);
    }
}