package com.example.theschedule_finalproject;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static com.example.theschedule_finalproject.FBref.FBST;
import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.profilePic_ref;
import static com.example.theschedule_finalproject.FBref.storageRef;
import static com.example.theschedule_finalproject.FBref.usersRef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.theschedule_finalproject.Models.Assignment;
import com.example.theschedule_finalproject.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class Profile extends AppCompatActivity {
    //הכרזה על רכיבי תצוגה, משתנים וכדומה
    ImageView profilePic;
    EditText email_etP, name_etP;
    Intent intent;
    String user_uid;
    Query query;
    User user;
    int SelectImage = 3333;
    Uri selectedImageUri;
    StorageReference selectedImageUri_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //התאמה בין רכיב תצוגה למשתנה
        name_etP = (EditText) findViewById(R.id.name_etP);
        email_etP = (EditText) findViewById(R.id.email_etP);
        profilePic = (ImageView) findViewById (R.id.profilePic);

        // פעולת פתיחה - הצגת נתונים מקוריים
        showOriginalData();
    }

    //הצגת נתונים מקוריים ישר כשנכנסים למסך הפרופיל
    private void showOriginalData() {
        currentUser = authRef.getCurrentUser();
        user_uid = currentUser.getUid();
        query = usersRef.orderByChild("user_uid").equalTo(user_uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    email_etP.setText(currentUser.getEmail());
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        user = dataSnapshot.getValue(User.class);
                        name_etP.setText(user.getUser_name());
                        if (!(user.getUser_image().matches("Null"))){
                            uploadProfileImage();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    //העלאת קובץ תמונה לFirebase Storage
    private void uploadProfileImage() {
        try {
            File profileImage = File.createTempFile("profileImage",".jpg");
            selectedImageUri_ref = FBST.getReference(user.getUser_image());
            selectedImageUri_ref.getFile(profileImage).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    Bitmap bitmap = BitmapFactory.decodeFile(profileImage.getAbsolutePath());
                    profilePic.setImageBitmap(bitmap);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //שמירת נתונים חדשים שהמשתמש הכניס
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
                    if (!(currentUser.getEmail().matches(email_str))){
                        currentUser.updateEmail(email_str);
                    }
                    if (!(user.getUser_name().matches(name_str))){
                        user.setUser_name(name_str);
                        usersRef.child(user_uid).setValue(user);
                    }
                }
            });
            adb.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // אפשר לעשות finish כי המסך הקודם לא יכול להיות התחברות/ הרשמה ומסך זה הוא לא מסך הבית
                    finish();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
    }


    //שינוי תמונת פרופיל
    public void change_profileImage(View view) {
        AlertDialog.Builder adb;
        adb = new AlertDialog.Builder(Profile.this);
        adb.setTitle("Profile Picture");
        adb.setMessage("Choose from where you want to upload a your photo.");
        adb.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent profilePic_intent = new Intent();
                profilePic_intent.setType("image/*");
                profilePic_intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(profilePic_intent, "Select Image"), SelectImage );
            }
        });
        adb.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if ((resultCode == RESULT_OK)&&(data!=null)){
             if (requestCode == SelectImage){
                 selectedImageUri = data.getData();
                 String selectedImageUri_path = "Users/"+currentUser.getUid()+"/user_image"+".jpg";

                 user.setUser_image(selectedImageUri_path);
                 usersRef.child(user_uid).setValue(user);

                 selectedImageUri_ref = storageRef.child(selectedImageUri_path);
                 selectedImageUri_ref.putFile(selectedImageUri);
                 profilePic.setImageURI(selectedImageUri);
             }
         }
     }

    //מעבר למסך שכחתי סיסמה
    public void resetPassword(View view) {
        intent = new Intent(Profile.this, ResetPassword.class);
        startActivity(intent);
    }

    //התנתקות מהאפליקציה
    public void logout(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(Splash.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Splash.userHasLoggedIn, false);
        editor.commit();
        authRef.signOut();
        intent = new Intent(Profile.this, SignUp.class);
        startActivity(intent);
    }

    //בדיקה האם הפרטים שהוכנסו נכונים
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

    //תפריט מסכים
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
