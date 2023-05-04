package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class Login extends AppCompatActivity {
    //הכרזה על רכיבי תצוגה, משתנים וכדומה
    EditText email_etL, password_etL;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //התאמה בין רכיב תצוגה למשתנה
        email_etL = (EditText) findViewById(R.id.email_etL);
        password_etL = (EditText) findViewById(R.id.password_etL);
    }

    public void log_in(View view) {
        //המרת פרטים מרכיבי תצוגה לString
        String email_str = email_etL.getText().toString();
        String password_str = password_etL.getText().toString();

        //שליחה לפעולת בדיקה ואישור נתונים
        Boolean authenticated = dataVerification(email_str, password_str);

        //אם הנתונים מאושרים לבצע כניסת משתמש
        if (authenticated){
            authRef.signInWithEmailAndPassword(email_str,password_str).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //אם המשימה צלחה
                    if (task.isSuccessful()){
                        //שינוי ערך בוליאני userHasLoggedIn של SharedPrefrance
                        SharedPreferences sharedPreferences = getSharedPreferences(Splash.PREFS_NAME,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Splash.userHasLoggedIn,true);
                        editor.commit();

                        //מעבר למסך הבית
                        intent = new Intent(Login.this,DailyScheduleView.class);
                        startActivity(intent);
                    }
                    //אם כניסת המשתמש לא צלחה
                    else {
                        AlertDialog.Builder adb;
                        adb = new AlertDialog.Builder(Login.this);
                        adb.setTitle("Login Error");
                        adb.setMessage("E-mail or Password are wrong!");
                        AlertDialog ad = adb.create();
                        ad.show();
                    }
                }
            });
        }
    }

    //מעבר למסך הרשמה
    public void toSignUpAct(View view) {
        intent = new Intent(Login.this,SignUp.class);
        startActivity(intent);
    }

    //מעבר למסך שכחתי סיסמה
    public void forgotPassword(View view) {
        intent = new Intent(Login.this,ResetPassword.class);
        startActivity(intent);
    }

    //בדיקת האם הפרטים שהוכנסו נכונים
    public Boolean dataVerification(String email, String password) {
        int errorExist = 0;
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_etL.setError("ERROR! Check the email.");
            errorExist++;
        }
        if (password.length()<6){
            password_etL.setError("ERROR! Enter at least 6 characters.");
            errorExist++;
        }

        if(errorExist > 0){
            return false;
        }
        return true;
    }
}