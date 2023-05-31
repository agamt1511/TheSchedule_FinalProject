package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.usersRef;
import androidx.annotation.NonNull;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.theschedule_finalproject.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 13/12/2022
 * short description - SignUp Screen
 */

public class SignUp extends AppCompatActivity {
    EditText name_etS,
    email_etS,
    password_etS,
    passwordConfirm_etS;
    Intent intent;
    BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        /**
         * Internet connection test using BroadcastReceiver.
         * <p>
         */
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        name_etS = (EditText) findViewById(R.id.name_etS);
        email_etS = (EditText) findViewById(R.id.email_etS);
        password_etS = (EditText) findViewById(R.id.password_etS);
        passwordConfirm_etS = (EditText) findViewById(R.id.passwordConfirm_etS);
    }


    /**
     * Create Account
     * Short description - Create a new account.
     * <p>
     * @param view the view
     */
    public void create_account(View view) {
        String name_str  = name_etS.getText().toString();
        String email_str  = email_etS.getText().toString();
        String password_str  = password_etS.getText().toString();
        String confirmPassword_str  = passwordConfirm_etS.getText().toString();
        String profilePic = "Null";

        Boolean authenticated = dataVerification(name_str, email_str,password_str,confirmPassword_str);

        if (authenticated){
            final ProgressDialog progressDialog = ProgressDialog.show(this,"Create Account", "creates a user...",true);
            authRef.createUserWithEmailAndPassword(email_str,password_str).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()){
                        currentUser = authRef.getCurrentUser();
                        User user = new User(currentUser.getUid(),name_str,profilePic);
                        usersRef.child(currentUser.getUid()).setValue(user);

                        SharedPreferences sharedPreferences = getSharedPreferences(Splash.PREFS_NAME,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Splash.userHasLoggedIn,true);
                        editor.commit();

                        intent = new Intent(SignUp.this,DailyScheduleView.class);
                        startActivity(intent);
                    }
                    else {
                        AlertDialog.Builder adb;
                        adb = new AlertDialog.Builder(SignUp.this);
                        adb.setTitle("Registration Error");
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            adb.setMessage("User with this e-mail is already exist!");
                        }
                        else {
                            adb.setMessage("The system failed to create a new user. Please try again later.");
                        }
                        AlertDialog ad = adb.create();
                        ad.show();
                    }
                }

            });
        }
    }

    /**
     * Data verification boolean.
     * Short description - Verification that the data entered is proper.
     * <p>
     * @param name            the name
     * @param email           the email
     * @param password        the password
     * @param confirmPassword the confirm password
     * @return the boolean
     */
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
            password_etS.setError("ERROR! Enter at least 6 characters.");
            errorExist++;
        }
        if (!password.equals(confirmPassword)) {
            passwordConfirm_etS.setError("ERROR! Passwords do not match");
            errorExist++;
        }
        if (confirmPassword.length()==0){
            passwordConfirm_etS.setError("ERROR! The field cannot be left blank.");
            errorExist++;
        }

        if(errorExist > 0){
            return false;
        }
        return true;
    }

    /**
     * toLogInAct
     * Short description - Proceed to login screen.
     * <p>
     * @param view the view
     */
    public void toLogInAct(View view) {
        intent = new Intent(SignUp.this,Login.class);
        startActivity(intent);
    }
}