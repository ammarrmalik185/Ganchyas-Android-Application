package app.ganchyas;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.ganchyas.NonActivityClasses.CommonMethods;

/**
 * Gets the data from the user and edits the database. Allows the user to edit their information.
 * @author Paradox
 */

public class EditInfoActivity extends AppCompatActivity {

    /**
     * Stores the reference of the root node of the Database
     */
    private DatabaseReference completeDatabaseReference;
    /**
     * Stores the reference of the currently logged user
     */
    private FirebaseUser user;
    /**
     * Reference to the EditText on xml that contains the Display Name of the user
     */
    private EditText nameField;
    /**
     * Reference to the EditText on xml that contains the phone number of the user
     */
    private EditText phoneField;
    /**
     * Reference to the EditText on xml that contains the date of birth of the user
     */
    private EditText dateField;
    /**
     * Reference to the EditText on xml that contains the section(e.g, BSE - 3A) of the user
     */
    private EditText sectionField;

    /**
     * Overriding onCreate to Inflate custom UI using activity_edit_info.xml
     * @param savedInstanceState contains the old state of this UI
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        completeDatabaseReference = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        nameField = findViewById(R.id.nameField);
        phoneField = findViewById(R.id.phoneField);
        dateField = findViewById(R.id.dateField);
        sectionField = findViewById(R.id.sectionField);
        DatabaseReference temp = completeDatabaseReference.child("userdata").child(user.getUid());
        temp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameField.setHint("Name : " + dataSnapshot.child("name").getValue().toString());
                phoneField.setHint("Phone no : " + dataSnapshot.child("phone no").getValue().toString());
                dateField.setHint("Birth Date : " + dataSnapshot.child("birth date").getValue().toString());
                sectionField.setHint("Section : " + dataSnapshot.child("section").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    /**
     * Invoked when the Submit Button is clicked. Uploads the non-null info and ignores empty Fields
     * @param view Contains the Button that was Pressed
     */
    public void submitButtonAction(View view) {
        String name = nameField.getText().toString();
        String date = dateField.getText().toString();
        String section = sectionField.getText().toString();
        String phone = phoneField.getText().toString();

        DatabaseReference temp = completeDatabaseReference.child("userdata").child(user.getUid());
        if (!name.equals("")) temp.child("name").setValue(name);
        if (!phone.equals("")) temp.child("phone no").setValue(phone);
        if (!date.equals("")) temp.child("birth date").setValue(date);
        if (!section.equals("")) temp.child("section").setValue(section);

        CommonMethods.toastMessage(EditInfoActivity.this, "Information Updated");
    }
}
