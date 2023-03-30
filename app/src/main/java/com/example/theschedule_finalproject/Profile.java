package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.usersRef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {
    EditText email_etP, name_etP;
    Intent intent;

    User user;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name_etP = (EditText) findViewById(R.id.name_etP);
        email_etP = (EditText) findViewById(R.id.email_etP);

       showOriginalData();
    }

    private void showOriginalData() {
        userID = currentUser.getUid();
        usersRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                name_etP.setText(user.getName());
                email_etP.setText(currentUser.getEmail());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    public void saveChanges(View view) {
        String name_str = name_etP.getText().toString();
        String email_str = email_etP.getText().toString();
        Boolean authenticated = dataVerification(name_str, email_str);

        if (authenticated == true){
            AlertDialog.Builder adb;
            adb = new AlertDialog.Builder(Profile.this);
            adb.setTitle("Save Changes");
            adb.setMessage("Are you sure you want to save the changes?");
            adb.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    currentUser.updateEmail(email_str);
                    user.setName(name_str);
                    usersRef.child(userID).setValue(user);
                }
            });
            adb.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
    }

    public void resetPassword(View view) {
        intent = new Intent(Profile.this,ResetPassword.class);
        startActivity(intent);
    }

    public void cancelChanges(View view) {
        finish();
    }

    public void logout(View view) {
        authRef.signOut();
        intent = new Intent(Profile.this,SignUp.class);
        startActivity(intent);
    }

    public Boolean dataVerification(String name, String email) {
        int errorExist = 0;
        if (name.length()<1){
            name_etP.setError("ERROR! The filed can't be blank.");
            errorExist++;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_etP.setError("ERROR! Check the email.");
            errorExist++;
        }

        if(errorExist > 0){
            return false;
        }
        return true;
    }
}