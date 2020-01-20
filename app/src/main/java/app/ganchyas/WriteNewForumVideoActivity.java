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
 * @author Paradox;
 */

public class WriteNewForumVideoActivity extends AppCompatActivity {

    DatabaseReference myDb;
    FirebaseAuth mAuth;
    DatabaseReference forumDataRoot;
    DataSnapshot dsSnap;
    StorageReference mStorageRef;
    DatabaseReference newForum;

    EditText subject;
    EditText mainText;
    Button uploadFileButton;

    Uri imageUri;
    String identifier, subjectValue, mainTextValue, date;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_new_forum_image);

        dialog = new ProgressDialog(WriteNewForumVideoActivity.this);
        myDb = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        forumDataRoot = myDb.child("forumData");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        uploadFileButton = findViewById(R.id.uploadFileButton);

        myDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dsSnap = dataSnapshot;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mainText = findViewById(R.id.mainText);
        subject = findViewById(R.id.subject);

    }

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
            toastMessage("Video is required");
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

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

    private void checkFilePermissions() {

        int permissionCheck = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck = WriteNewForumVideoActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck += WriteNewForumVideoActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (permissionCheck != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }

        }
    }

    public void uploadFileAction(View view) {
        checkFilePermissions();
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("video/*");
        startActivityForResult(fileIntent, 1);
    }

    public void uploadFile() {

        final StorageReference videoRef = mStorageRef.child("forum videos/" + identifier + "." + getFileExtension(imageUri));
        videoRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(WriteNewForumVideoActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
                // Continue with the task to get the download URL
                return videoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
             @Override
             public void onComplete(@NonNull Task<Uri> task) {
                 HashMap<String, String> values = new HashMap<>();
                 values.put("date", date);
                 values.put("sender",mAuth.getCurrentUser().getUid());
                 values.put("mainText", mainTextValue);
                 values.put("subject", subjectValue);
                 values.put("fileUri", task.getResult().toString());
                 values.put("type", "video");
                 newForum.setValue(values);
                 FirebaseMessaging.getInstance().subscribeToTopic(identifier);
                 dialog.dismiss();
                 Intent intent = new Intent(WriteNewForumVideoActivity.this, MainActivity.class);
                 startActivity(intent);
             }
         }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMessage("uploading failed");
            }
        });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}
