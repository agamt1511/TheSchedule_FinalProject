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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
    String selectedImage_path;
    int GALLERY_IMAGE = 3333;
    int CAMERA_IMAGE = 2222;
    Uri selectedImageUri;
    StorageReference selectedImage_ref;
    AlertDialog.Builder adb;

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


    //הורדת קובץ תמונה מFirebase Storage
    private void uploadProfileImage() {
        try {
            File profileImage = File.createTempFile("profileImage",".jpg");
            selectedImage_ref = FBST.getReference(user.getUser_image());
            selectedImage_ref.getFile(profileImage).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
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

        if (authenticated) {
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
        adb = new AlertDialog.Builder(Profile.this);
        adb.setTitle("Profile Picture");
        adb.setMessage("Choose from where you want to upload a your photo.");
        adb.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_IMAGE);
                }
                else {
                    chooseFromGallery();
                }
            }
        });
        adb.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Profile.this, new String[] {Manifest.permission.CAMERA}, CAMERA_IMAGE);
                }
                else{
                    chooseFromCamera();
                }
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    //הרשאות לגלריה ומצלמה
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == GALLERY_IMAGE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                chooseFromGallery();
            }
            else {
                problemGettingPernissions();
            }
        }
        else{
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseFromCamera();
            }
            else {
                problemGettingPernissions();
            }
        }
    }

    //התראה על אי נתינת הרשאות
    private void problemGettingPernissions() {
        adb = new AlertDialog.Builder(Profile.this);
        adb.setTitle("Problem Getting Permissions");
        adb.setMessage("In order for us to use images from your gallery and camera, you must enable the permissions that the application requests.");
        AlertDialog ad = adb.create();
        ad.show();
    }

    //פתיחת גלריה ובחירת תמונה
    private void chooseFromGallery() {
        Intent profilePic_GalleryIntent = new Intent();
        profilePic_GalleryIntent.setType("image/*");
        profilePic_GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(profilePic_GalleryIntent, "Gallery Image"), GALLERY_IMAGE);
    }

    private void chooseFromCamera() {
        Intent profilePic_CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(profilePic_CameraIntent, CAMERA_IMAGE);
    }

    @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if ((resultCode == RESULT_OK)&&(data!=null)){
             selectedImage_path = "Users/"+currentUser.getUid()+"/user_image"+".jpg";
             selectedImage_ref = storageRef.child(selectedImage_path);
             if (user.getUser_image().matches("Null")){
                 user.setUser_image(selectedImage_path);
                 usersRef.child(user_uid).setValue(user);
             }
             //עבור תמונה מגלריה
             if (requestCode == GALLERY_IMAGE){
                 selectedImageUri = data.getData();
                 selectedImage_ref.putFile(selectedImageUri);
                 profilePic.setImageURI(selectedImageUri);
             }
             // עבור תמונה ממצלמה
             else{
                 Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                 byte[] selectedImageBytes = baos.toByteArray();

                 UploadTask uploadTask = selectedImage_ref.putBytes(selectedImageBytes);
                 uploadTask.addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception exception) {
                         Toast.makeText(Profile.this, "FFFFFF", Toast.LENGTH_SHORT).show();
                     }
                 }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         profilePic.setImageBitmap(bitmap);
                     }
                 });
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
