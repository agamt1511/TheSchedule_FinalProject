package com.example.theschedule_finalproject;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.profilePic_ref;
import static com.example.theschedule_finalproject.FBref.storageRef;
import static com.example.theschedule_finalproject.FBref.usersRef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.theschedule_finalproject.Models.Assignment;
import com.example.theschedule_finalproject.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class Profile extends AppCompatActivity {
    ImageView profilePic;
    EditText email_etP, name_etP;
    Intent intent;
    User user;
    Uri selectedImageUri;
    String userID, pathToImage;
    int SELECT_PICTURE = 333;
    int sourceType;
    File profilePic_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name_etP = (EditText) findViewById(R.id.name_etP);
        email_etP = (EditText) findViewById(R.id.email_etP);
        profilePic = (ImageView) findViewById (R.id.profilePic);

        showOriginalData();
    }

    private void showOriginalData() {
        currentUser = authRef.getCurrentUser();
        profilePic_ref = storageRef.child("Users").child(currentUser.getUid()).child("profileImage");
        userID = currentUser.getUid();
        usersRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                //name_etP.setText(user.getName());
                email_etP.setText(currentUser.getEmail());

                //if (!user.getImage().matches("null")){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void saveChanges(View view) {
        String name_str = name_etP.getText().toString();
        String email_str = email_etP.getText().toString();
        Boolean authenticated = dataVerification(name_str, email_str);

        if (authenticated == true) {
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
        intent = new Intent(Profile.this, ResetPassword.class);
        startActivity(intent);
    }

    public void cancelChanges(View view) {
        finish();
    }

    public void logout(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("hasLoggedIn", false);
        editor.commit();
        authRef.signOut();
        intent = new Intent(Profile.this, SignUp.class);
        startActivity(intent);
    }

    public Boolean dataVerification(String name, String email) {
        int errorExist = 0;
        if (name.length() < 1) {
            name_etP.setError("ERROR! The filed can't be blank.");
            errorExist++;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_etP.setError("ERROR! Check the email.");
            errorExist++;
        }

        if (errorExist > 0) {
            return false;
        }
        return true;
    }

    public void change_profileImage(View view) {
        AlertDialog.Builder adb;
        adb = new AlertDialog.Builder(Profile.this);
        adb.setTitle("Profile Picture");
        adb.setMessage("Choose from where you want to upload a your photo.");
        adb.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                gallery();
            }
        });
        adb.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                camera();

            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    private void camera() {
        Intent openCamera = new Intent();
        openCamera.setType("image/*");
        openCamera.setAction(ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCamera, 100);
    }

    public void gallery (){
        sourceType = 1;
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == SELECT_PICTURE) && (resultCode == RESULT_OK) && (null != data)) {
            if (sourceType == 1) {
                selectedImageUri = data.getData();
                user.setImage(String.valueOf(selectedImageUri));
                if (selectedImageUri != null) {
                    profilePic_ref.putFile(selectedImageUri);
                    pathToImage = profilePic_ref.toString();
                    user.setImage(String.valueOf(selectedImageUri));
                    usersRef.child(currentUser.getUid()).setValue(user);
                    profilePic.setImageURI(selectedImageUri);
                }
            }
            else {
                Uri photo = data.getData();
                profilePic.setImageURI(photo);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.dailySchedule) {
            intent = new Intent(this, DailyScheduleView.class);
            startActivity(intent);
        }
        else if (id == R.id.assignments) {
            intent = new Intent(this, AssignmentsView.class);
            startActivity(intent);
        }
        else if (id == R.id.notes) {
            intent = new Intent(this, NotesView.class);
            startActivity(intent);
        }
        else if (id == R.id.credits) {
            intent = new Intent(this, Credits.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }



}
