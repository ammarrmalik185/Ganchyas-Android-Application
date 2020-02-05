package app.ganchyas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import app.ganchyas.NonActivityClasses.CommonMethods;

/**
 * Allow the user to post a forum with only text
 * @author Paradox
 */

public class WriteNewForumTextActivity extends AppCompatActivity {

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
     * Shows when the forum is being uploaded
     */
    ProgressDialog dialog;

    /**
     * Overriding onCreate to Inflate custom UI using activity_write_new_forum_text.xml
     * @param savedInstanceState contains the old state of this UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_new_forum_text);

        dialog = new ProgressDialog(WriteNewForumTextActivity.this);
        completeDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        forumDataRoot = completeDatabaseReference.child("forumData");

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
        if (!subjectValue.equals("")) {

            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
            Long format = Long.parseLong(s.format(new Date()));

            s = new SimpleDateFormat("dd/MM/yyyy");
            date = s.format(new Date());

            identifier = "forum_id_" + (50000000000000L - format);

            newForum = forumDataRoot.child(identifier);

            HashMap<String, String> values = new HashMap<>();
            values.put("date", date);
            values.put("sender", firebaseAuth.getCurrentUser().getUid());
            values.put("mainText", mainTextValue);
            values.put("subject", subjectValue);
            values.put("type", "text");
            newForum.setValue(values);
            FirebaseMessaging.getInstance().subscribeToTopic(identifier);
            Intent intent = new Intent(WriteNewForumTextActivity.this, MainActivity.class);
            startActivity(intent);

        } else {
            CommonMethods.toastMessage(WriteNewForumTextActivity.this, "Subject is required");
        }


    }

}
