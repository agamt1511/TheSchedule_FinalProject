package com.example.theschedule_finalproject;

import static com.example.theschedule_finalproject.FBref.FBST;
import static com.example.theschedule_finalproject.FBref.authRef;
import static com.example.theschedule_finalproject.FBref.currentUser;
import static com.example.theschedule_finalproject.FBref.storageRef;
import static com.example.theschedule_finalproject.FBref.usersRef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author Agam Toledano
 * @version 1.0
 * @since 02 /01/2023
 * short description - Profile Screen.
 */
public class Profile extends AppCompatActivity {
    ImageView profilePic;
    EditText email_etP,
    name_etP;
    Intent intent;
    String user_uid;
    User user;
    int GALLERY_IMAGE = 3333;
    int CAMERA_IMAGE = 2222;
    StorageReference selectedImage_ref;
    AlertDialog.Builder adb;
    BroadcastReceiver broadcastReceiver;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /**
         * Internet connection test using BroadcastReceiver.
         * <p>
         */
        broadcastReceiver = new NetworkConnectionReceiver();
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        name_etP = (EditText) findViewById(R.id.name_etP);
        email_etP = (EditText) findViewById(R.id.email_etP);
        profilePic = (ImageView) findViewById (R.id.profilePic);

        showOriginalData();
    }

    /**
     * showOriginalData.
     * Short description - Displaying user information.
     * <p>
     */
    private void showOriginalData() {
        currentUser = authRef.getCurrentUser();
        user_uid = currentUser.getUid();
        email_etP.setText(currentUser.getEmail());
        Query query = usersRef.orderByChild("user_uid").equalTo(user_uid);
        progressDialog = ProgressDialog.show(this,"Loading Data", "loading ...",true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        user = dataSnapshot.getValue(User.class);
                        name_etP.setText(user.getUser_name());
                        if (!(user.getUser_image().matches("Null"))){
                            uploadProfileImage();
                        }
                        else {
                            progressDialog.dismiss();
                        }
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                adb = new AlertDialog.Builder(Profile.this);
                adb.setTitle("Error Occurred");
                adb.setMessage("There is a problem importing the data. Please try again later.");
                AlertDialog ad = adb.create();
                ad.show();
            }
        });
    }


    /**
     * uploadProfileImage.
     * Short description - Downloading a profile picture from Firebase and displaying it.
     * <p>
     */
    private void uploadProfileImage() {
        try {
            File profileImage = File.createTempFile("profileImage",".jpg");
            selectedImage_ref = FBST.getReference(user.getUser_image());
            selectedImage_ref.getFile(profileImage).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    Bitmap bitmap = BitmapFactory.decodeFile(profileImage.getAbsolutePath());
                    profilePic.setImageBitmap(bitmap);
                    progressDialog.dismiss();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * saveChanges.
     * Short description - Saving changes the user made to the profile.
     * <p>
     * @param view the view
     */
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
                    finish();
                }
            });
            AlertDialog ad = adb.create();
            ad.show();
        }
    }


    /**
     * Change profile image.
     * Short description - Change profile picture.
     * <p>
     * @param view the view
     */
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

    /**
     * onRequestPermissionsResult.
     * Short description - Checking permissions and opening a component by code.
     * <p>
     */
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

    /**
     * problemGettingPernissions.
     * Short description - Notification of not granting permissions.
     * <p>
     */
    private void problemGettingPernissions() {
        adb = new AlertDialog.Builder(Profile.this);
        adb.setTitle("Problem Getting Permissions");
        adb.setMessage("In order for us to use images from your gallery and camera, you must enable the permissions that the application requests.");
        AlertDialog ad = adb.create();
        ad.show();
    }

    /**
     * chooseFromGallery.
     * Short description - Opening a gallery and selecting an image.
     * <p>
     */
    private void chooseFromGallery() {
        Intent profilePic_GalleryIntent = new Intent();
        profilePic_GalleryIntent.setType("image/*");
        profilePic_GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(profilePic_GalleryIntent, "Gallery Image"), GALLERY_IMAGE);
    }

    /**
     * chooseFromCamera.
     * Short description - Receiving data for each intent - Opening the camera and taking a picture.
     * <p>
     */
    private void chooseFromCamera() {
        Intent profilePic_CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(profilePic_CameraIntent, CAMERA_IMAGE);
    }


    /**
     * onActivityResult.
     * Short description - Receiving data for each intent - storing and displaying data.
     * <p>
     */
    @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if ((resultCode == RESULT_OK)&&(data!=null)){
             String selectedImage_path = "Users/"+currentUser.getUid()+"/user_image"+".jpg";
             selectedImage_ref = storageRef.child(selectedImage_path);
             if (user.getUser_image().matches("Null")){
                 user.setUser_image(selectedImage_path);
                 usersRef.child(user_uid).setValue(user);
             }
             if (requestCode == GALLERY_IMAGE){
                 Uri selectedImageUri;
                 selectedImageUri = data.getData();
                 selectedImage_ref.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         profilePic.setImageURI(selectedImageUri);
                     }
                 });
             }
             else{
                 Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                 byte[] selectedImageBytes = baos.toByteArray();

                 UploadTask uploadTask = selectedImage_ref.putBytes(selectedImageBytes);
                 uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         profilePic.setImageBitmap(bitmap);
                     }
                 });
             }
         }
     }

    /**
     * Reset password.
     * Short description - go to ResetPassword screen.
     * <p>
     * @param view the view
     */
    public void resetPassword(View view) {
        intent = new Intent(Profile.this, ResetPassword.class);
        startActivity(intent);
    }

    /**
     * Logout.
     * Short description - Logout from this account.
     * <p>
     * @param view the view
     */
    public void logout(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(Splash.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Splash.userHasLoggedIn, false);
        editor.commit();
        authRef.signOut();
        intent = new Intent(Profile.this, SignUp.class);
        startActivity(intent);
    }

    /**
     * Data verification boolean.
     * Short description - Verification that the data entered is proper.
     * <p>
     * @param name  the name
     * @param email the email
     * @return the boolean
     */
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

    /**
     * Screen menu.
     * <p>
     * @param menu
     * @return super.onOptionsItemSelected(item)
     */
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
