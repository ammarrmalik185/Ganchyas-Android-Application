package app.ganchyas;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import app.ganchyas.NonActivityClasses.CommonMethods;

/**
 * Shows the user their profile and allows them to edit it
 * @author Paradox
 */

public class ProfileActivity extends AppCompatActivity {

    /**
     * Stores the reference of the root node of the Database
     */
    DatabaseReference completeDatabaseReference;
    /**
     * Dialog that pops up when a file is being uploaded
     */
    ProgressDialog dialog;
    /**
     * Contains the currently logged in user
     */
    FirebaseUser user;
    /**
     * Contains the reference of the root directory of the cloud storage
     */
    StorageReference completeStorageReference;
    /**
     * The Uri of the user's current profile picture
     */
    Uri profilePictureUri;
    /**
     * Reference to the Text view that views the name of the user
     */
    TextView nameField;
    /**
     * Reference to the Text view that views the phone number of the user
     */
    TextView phoneField;
    /**
     * Reference to the Text view that views the date of birth of the user
     */
    TextView dateField;
    /**
     * Reference to the Text view that views the section of the user
     */
    TextView sectionField;
    /**
     * Reference to the Text view that views the email of the user
     */
    TextView emailField;
    /**
     * This dialog show the user what his new profile picture will look like after cropping it
     */
    Dialog previewDialog;
    /**
     * Reference to the Image view that views the profile picture of the user
     */
    ImageView profilePic;

    /**
     * Overriding onCreate to Inflate custom UI using activity_profile.xml
     * @param savedInstanceState contains the old state of this UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = findViewById(R.id.ProfileActivityPic);
        previewDialog = new Dialog(ProfileActivity.this);
        completeStorageReference = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(ProfileActivity.this);
        nameField = findViewById(R.id.nameField);
        phoneField = findViewById(R.id.phoneField);
        dateField = findViewById(R.id.dateField);
        sectionField = findViewById(R.id.sectionField);
        emailField = findViewById(R.id.emailField);

        user = FirebaseAuth.getInstance().getCurrentUser();
        previewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        emailField.setText(user.getEmail());

        completeDatabaseReference = FirebaseDatabase.getInstance().getReference();
        completeDatabaseReference.child("userdata").child(user.getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("profile picture").exists())
                        {
                            Picasso.with(ProfileActivity.this)
                                    .load(dataSnapshot.child("profile picture").getValue().toString())
                                    .placeholder(R.drawable.placeholder_profile)
                                    .resize(500,500).centerCrop()
                                    .into(profilePic);
                        }

                        nameField.setText(dataSnapshot.child("name").getValue().toString());
                        phoneField.setText("Phone no : " + dataSnapshot.child("phone no").getValue().toString());
                        dateField.setText("Birth Date : " + dataSnapshot.child("birth date").getValue().toString());
                        sectionField.setText("Section : " + dataSnapshot.child("section").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        CommonMethods.toastMessage(ProfileActivity.this, "Unable to retrieve data");
                    }
                });

    }

    /**
     * Invoked when the edit profile button is clicked. Takes the user to the EditProfileActivity.java page
     * @param view Contains the button that was pressed
     */
    public void editProfileAction(View view) {
        Intent intent = new Intent(ProfileActivity.this, EditInfoActivity.class);
        startActivity(intent);
    }

    /**
     * Invoked when the change password button is clicked. Takes the user to the PasswordChangeActivity.java page
     * @param view Contains the button that was pressed
     */
    public void changePasswordAction(View view) {
        Intent intent = new Intent(ProfileActivity.this, PasswordChangeActivity.class);
        startActivity(intent);
    }

    /**
     * Invoked when the change profile picture button is clicked. Takes the user to an image chooser to pick the new profile picture
     * @param view Contains the button that was pressed
     */
    public void changeProfilePicture(View view){
        checkFilePermissions();
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("image/*");
        startActivityForResult(fileIntent, 1);
    }

    /**
     * Invoked when the user has chosen an image as his new profile picture
     * @param requestCode The unique identifier of the image choose request
     * @param resultCode Contains the status of the request (completed successfully or not)
     * @param data The data that the request has returned
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data!=null && data.getData() != null) {
                profilePictureUri = data.getData();
                previewDialog.setContentView(R.layout.dialog_profile_pic);
                ImageView profilePicPreview = previewDialog.findViewById(R.id.profilePicPreview);
                Picasso.with(ProfileActivity.this).load(profilePictureUri).resize(500,500).centerCrop().into(profilePicPreview);
                Button confrmButon = previewDialog.findViewById(R.id.confirmButton);
                confrmButon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        previewDialog.dismiss();
                        dialog.setMessage("Uploading the file ....");
                        dialog.show();
                        uploadFile();
                    }
                });
                previewDialog.show();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * A function that checks if the application has file reading permissions (if it does'nt it requests fot the permission)
     */
    private void checkFilePermissions() {

        int permissionCheck = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck = ProfileActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck += ProfileActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (permissionCheck != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }

        }
    }

    /**
     * Can get the file extension from a given Uri
     * @param uri The Uri of the file
     * @return The extension of the file given via Uri
     */
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * Uploads the new profile picture to the storage and and updates the database
     */
    public void uploadFile() {

        final StorageReference imageRef = completeStorageReference.child("user profile images/" + user.getUid() + "." + getFileExtension(profilePictureUri));
        imageRef.putFile(profilePictureUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (!task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }

                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                 @Override
                 public void onComplete(@NonNull Task<Uri> task) {
                     completeDatabaseReference.child("userdata").child(user.getUid()).child("profile picture").setValue(task.getResult().toString());
                     dialog.dismiss();
                 }
            }
        );

    }
}
