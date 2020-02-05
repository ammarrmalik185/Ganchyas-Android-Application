package app.ganchyas;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import app.ganchyas.NonActivityClasses.CommonMethods;

/**
 * Allows the user to upload a forum with an image
 * @author Paradox
 */
public class WriteNewForumImageActivity extends AppCompatActivity {

    /**
     * Stores the reference of the root node of the Database
     */
    DatabaseReference completeDatabaseReference;
    /**
     * Stores the firebase authentication instance
     */
    FirebaseAuth firebaseAuth;
    /**
     * Contains the reference of all the forums in the database
     */
    DatabaseReference forumDataRoot;
    /**
     * Contains the Snapshot of the entire database
     */
    DataSnapshot completeDatabaseSnapshot;
    /**
     * Contains the reference of the root directory of the cloud storage
     */
    StorageReference completeStorageReference;
    /**
     * Contains the reference of the new forum in the database
     */
    DatabaseReference newForum;
    /**
     * Reference to the edit text on xml that the user enters subject of the forum into
     */
    EditText subject;
    /**
     * Reference to the edit text on xml that the user enters the main text of the forum into
     */
    EditText mainText;
    /**
     * Reference to the upload file button on the xml that lets the user choose a file
     */
    Button uploadFileButton;
    /**
     * Uri of the image needed to be uploaded
     */
    Uri imageUri;
    /**
     * Contains Unique identifier of the forum
     */
    String identifier;
    /**
     * Contains Subject of the forum
     */
    String subjectValue;
    /**
     * Contains Text of the forum
     */
    String mainTextValue;
    /**
     * Contains date of the forum
     */
    String date;
    /**
     * Shows when the file is being uploaded
     */
    ProgressDialog dialog;
    
    /**
     * Overriding onCreate to Inflate custom UI using activity_write_new_forum_image.xml
     * @param savedInstanceState contains the old state of this UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_new_forum_image);

        dialog = new ProgressDialog(WriteNewForumImageActivity.this);
        completeDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        forumDataRoot = completeDatabaseReference.child("forumData");
        completeStorageReference = FirebaseStorage.getInstance().getReference();
        uploadFileButton = findViewById(R.id.uploadFileButton);

        completeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                completeDatabaseSnapshot = dataSnapshot;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mainText = findViewById(R.id.mainText);
        subject = findViewById(R.id.subject);

    }

    /**
     * Invoked when the submit button is clicked
     * @param view Contains the button that was pressed
     */
    public void submitForumAction(View view) {
        subjectValue = subject.getText().toString();
        mainTextValue = mainText.getText().toString();

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        long format = Long.parseLong(s.format(new Date()));

        s = new SimpleDateFormat("dd/MM/yyyy");
        date = s.format(new Date());

        identifier = "forum_id_" + (50000000000000L - format);

        newForum = forumDataRoot.child(identifier);

        if (imageUri != null) {
            dialog.setMessage("Uploading the file ....");
            dialog.show();
            uploadFile();
        }

        else {
            CommonMethods.toastMessage(WriteNewForumImageActivity.this, "Image is required");
        }
    }

    /**
     * Invoked when the user has chosen a file for the forum
     * @param requestCode The unique identifier of the file choose request
     * @param resultCode Contains the status of the request (completed successfully or not)
     * @param data The data that the request has returned
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data!=null && data.getData() != null) {
                imageUri = data.getData();
                uploadFileButton.setText("Change File");
                uploadFileButton.setTextColor(Color.GREEN);

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
            permissionCheck = WriteNewForumImageActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck += WriteNewForumImageActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (permissionCheck != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }

        }
    }

    /**
     * Invoked when the upload file button is pressed
     * @param view The reference of the button pressed
     */
    public void uploadFileAction(View view) {
        checkFilePermissions();
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("image/*");
        startActivityForResult(fileIntent, 1);
    }

    /**
     * Uploads the file to the storage and and updates the database
     */
    public void uploadFile() {

        final StorageReference imageRef = completeStorageReference.child("forum images/" + identifier + "." + getFileExtension(imageUri));
        imageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(WriteNewForumImageActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    HashMap<String, String> values = new HashMap<>();
                    values.put("date", date);
                    values.put("sender", firebaseAuth.getCurrentUser().getUid());
                    values.put("mainText", mainTextValue);
                    values.put("subject", subjectValue);
                    values.put("fileUri", task.getResult().toString());
                    values.put("type", "image");
                    newForum.setValue(values);
                    FirebaseMessaging.getInstance().subscribeToTopic(identifier);
                    dialog.dismiss();
                    Intent intent = new Intent(WriteNewForumImageActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonMethods.toastMessage(WriteNewForumImageActivity.this, "uploading failed");
            }
        });

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

}
