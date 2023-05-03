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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.theschedule_finalproject.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUp extends AppCompatActivity {
    //הכרזה על רכיבי תצוגה, משתנים וכדומה
    EditText name_etS, email_etS, password_etS, passwordConfirm_etS;
    Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //התאמה בין רכיב תצוגה למשתנה
        name_etS = (EditText) findViewById(R.id.name_etS);
        email_etS = (EditText) findViewById(R.id.email_etS);
        password_etS = (EditText) findViewById(R.id.password_etS);
        passwordConfirm_etS = (EditText) findViewById(R.id.passwordConfirm_etS);

    }


    public void create_account(View view) {
        //המרת פרטים מרכיבי תצוגה לString
        String name_str  = name_etS.getText().toString();
        String email_str  = email_etS.getText().toString();
        String password_str  = password_etS.getText().toString();
        String confirmPassword_str  = passwordConfirm_etS.getText().toString();
        String profilePic = "Null";

        //שליחה לפעולת בדיקה ואישור נתונים
        Boolean authenticated = dataVerification(name_str, email_str,password_str,confirmPassword_str);

        //אם הנתונים מאושרים לבצע הרשמה והוספה למערך הנתונים
        if (authenticated){
            //יצית משתמש חדש ויצירת מאזין לפעולה
            authRef.createUserWithEmailAndPassword(email_str,password_str).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                @Override
                //אם הפעולה הושלמה בהצלחה
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //אם המשימה צלחה
                    if (task.isSuccessful()){
                        //יצירת עצם User בfirebase realtime database
                        currentUser = authRef.getCurrentUser();
                        User user = new User(currentUser.getUid(),name_str,profilePic);
                        usersRef.child(currentUser.getUid()).setValue(user);

                        //שינוי ערך בוליאני userHasLoggedIn של SharedPrefrance
                        SharedPreferences sharedPreferences = getSharedPreferences(Splash.PREFS_NAME,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Splash.userHasLoggedIn,true);
                        editor.commit();

                        //מעבר למסך הבית
                        intent = new Intent(SignUp.this,DailyScheduleView.class);
                        startActivity(intent);
                    }
                    //אם הרשמת משתמש חדש לא צלחה
                    else {
                        AlertDialog.Builder adb;
                        adb = new AlertDialog.Builder(SignUp.this);
                        adb.setTitle("Registration Error");
                        //אם משתמש כבר קיים הצג הודעה מתאימה
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            adb.setMessage("User with this e-mail is already exist!");
                        }
                        //אם תקלה אחרת הצג הודעה מתאימה
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

    //בדיקה האם הפרטים שנכנסו נכונים
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

        if(errorExist > 0){
            return false;
        }
        return true;
    }

    //מעבר למסך התחברות
    public void toLogInAct(View view) {
        intent = new Intent(SignUp.this,Login.class);
        startActivity(intent);
    }
}