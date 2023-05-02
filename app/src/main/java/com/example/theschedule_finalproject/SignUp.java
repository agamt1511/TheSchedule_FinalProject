package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.usersRef;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theschedule_finalproject.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class SignUp extends AppCompatActivity {
    EditText name_etS, email_etS, password_etS, passwordConfirm_etS;
    Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name_etS = (EditText) findViewById(R.id.name_etS);
        email_etS = (EditText) findViewById(R.id.email_etS);
        password_etS = (EditText) findViewById(R.id.password_etS);
        passwordConfirm_etS = (EditText) findViewById(R.id.passwordConfirm_etS);

    }


    public void create_account(View view) {
        String name_str  = name_etS.getText().toString();
        String email_str  = email_etS.getText().toString();
        String password_str  = password_etS.getText().toString();
        String confirmPassword_str  = passwordConfirm_etS.getText().toString();
        String profilePic = "null";

        Boolean authenticated = dataVerification(name_str, email_str,password_str,confirmPassword_str);

        if (authenticated == true){
            authRef.createUserWithEmailAndPassword(email_str,password_str).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        currentUser = authRef.getCurrentUser();
                        User user = new User(currentUser.getUid(),name_str,profilePic);
//                        if (currentUser == null){
//                            currentUser = authRef.getCurrentUser();
//                        }
                        usersRef.child(currentUser.getUid()).setValue(user);

                            SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("hasLoggedIn",true);
                            editor.commit();


                        intent = new Intent(SignUp.this,Profile.class);
                        startActivity(intent);
                    }
                    else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException)
                            Toast.makeText(SignUp.this, "User with e-mail already exist!", Toast.LENGTH_SHORT).show();
                        else {
                            Toast.makeText(SignUp.this, "User create failed.", Toast.LENGTH_LONG).show();
                        }
                    }
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