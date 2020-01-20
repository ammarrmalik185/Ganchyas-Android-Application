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
 * @author Paradox;
 */

public class ProfileActivity extends AppCompatActivity {

    DatabaseReference myDb;
    ProgressDialog dialog;
    FirebaseUser user;
    StorageReference mStorageRef;
    Uri imageUri;
    TextView nameField, phoneField, dateField, sectionField, emailField;
    Dialog previewDialog;
    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = findViewById(R.id.ProfileActivityPic);
        previewDialog = new Dialog(ProfileActivity.this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(ProfileActivity.this);
        nameField = findViewById(R.id.nameField);
        phoneField = findViewById(R.id.phoneField);
        dateField = findViewById(R.id.dateField);
        sectionField = findViewById(R.id.sectionField);
        emailField = findViewById(R.id.emailField);

        user = FirebaseAuth.getInstance().getCurrentUser();
        previewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        emailField.setText(user.getEmail());

        myDb = FirebaseDatabase.getInstance().getReference();
        myDb.child("userdata").child(user.getUid()).
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
                        toastMessage("Unable to retrieve data");
                    }
                });

    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void editProfileAction(View view) {
        Intent intent = new Intent(ProfileActivity.this, EditInfoActivity.class);
        startActivity(intent);
    }

    public void changePasswordAction(View view) {
        Intent intent = new Intent(ProfileActivity.this, PasswordChangeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data!=null && data.getData() != null) {
                imageUri = data.getData();
                previewDialog.setContentView(R.layout.dialog_profile_pic);
                ImageView profilePicPreview = previewDialog.findViewById(R.id.profilePicPreview);
                Picasso.with(ProfileActivity.this).load(imageUri).resize(500,500).centerCrop().into(profilePicPreview);
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

    public void changeProfilePicture(View view){
        checkFilePermissions();
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("image/*");
        startActivityForResult(fileIntent, 1);
    }

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

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void uploadFile() {

        final StorageReference imageRef = mStorageRef.child("user profile images/" + user.getUid() + "." + getFileExtension(imageUri));
        imageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                     myDb.child("userdata").child(user.getUid()).child("profile picture").setValue(task.getResult().toString());
                     dialog.dismiss();
                 }
            }
        );

    }
}
