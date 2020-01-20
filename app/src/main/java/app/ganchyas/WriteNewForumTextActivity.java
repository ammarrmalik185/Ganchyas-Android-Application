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
 * @author Paradox;
 */

public class WriteNewForumTextActivity extends AppCompatActivity {

    DatabaseReference myDb;
    FirebaseAuth mAuth;
    DatabaseReference forumDataRoot;
    DataSnapshot dsSnap;
    DatabaseReference newForum;
    EditText subject, mainText;
    String identifier, subjectValue, mainTextValue, date;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_new_forum_text);

        dialog = new ProgressDialog(WriteNewForumTextActivity.this);
        myDb = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        forumDataRoot = myDb.child("forumData");

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
        if (!subjectValue.equals("")) {

            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
            Long format = Long.parseLong(s.format(new Date()));

            s = new SimpleDateFormat("dd/MM/yyyy");
            date = s.format(new Date());

            identifier = "forum_id_" + (50000000000000L - format);

            newForum = forumDataRoot.child(identifier);

            HashMap<String, String> values = new HashMap<>();
            values.put("date", date);
            values.put("sender",mAuth.getCurrentUser().getUid());
            values.put("mainText", mainTextValue);
            values.put("subject", subjectValue);
            values.put("type", "text");
            newForum.setValue(values);
            FirebaseMessaging.getInstance().subscribeToTopic(identifier);
            Intent intent = new Intent(WriteNewForumTextActivity.this, MainActivity.class);
            startActivity(intent);

        } else {
            toastMessage("Subject is required");
        }


    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



}
