package app.ganchyas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.ganchyas.NonActivityClasses.CommonMethods;

/**
 * Gets the data of the user and adds to the database. Only launches when the data of the user is not available in the database.
 * @author Paradox
 */

public class AddInfoActivity extends AppCompatActivity {

    /**
     * Stores whether the user is authorized or not (false if the password change fails)
     */
    boolean authorized;
    /**
     * Store the password of the user for re-login before password change(required by FireBase)
     */
    String regPass;
    /**
     * Stores the e-mail of the user for re-login before password change(required by FireBase)
     */
    String regEmail;
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
     * Reference to the EditText on xml that contains the new Password of the user
     */
    private EditText passField1;
    /**
     * Reference to the EditText on xml that contains the new Password of the user (re-enter)
     */
    private EditText passField2;
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
     * Empty Constructor
     */
    public AddInfoActivity() {
    }

    /**
     * Overriding onCreate to Inflate custom UI using activity_add_info.xml
     * @param savedInstanceState contains the old state of this UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        completeDatabaseReference = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();
        regPass = getIntent().getStringExtra("pass");
        regEmail = getIntent().getStringExtra("id");

        nameField = findViewById(R.id.nameField);
        passField1 = findViewById(R.id.passField1);
        passField2 = findViewById(R.id.passField2);
        phoneField = findViewById(R.id.phoneField);
        dateField = findViewById(R.id.dateField);
        sectionField = findViewById(R.id.sectionField);


    }

    /**
     * Invoked when the Submit Button is clicked. Uploads the info and takes the user to MainActivity
     * if the info is valid or displays a Toast message to show the error
     * @param view Contains the Button that was Pressed
     */
    public void submitButtonAction(View view) {
        String name = nameField.getText().toString();
        String pass1 = passField1.getText().toString();
        String pass2 = passField2.getText().toString();
        String date = dateField.getText().toString();
        String section = sectionField.getText().toString();
        String phone = phoneField.getText().toString();

        if (!(name.equals("") || pass1.equals("") || pass2.equals("") ||
                date.equals("") || section.equals("") || phone.equals(""))) {
            if (pass1.length() < 8) {
                CommonMethods.toastMessage(AddInfoActivity.this, "Password should be more than 8 characters");
            } else if (!pass1.equals(pass2)) {
                CommonMethods.toastMessage(AddInfoActivity.this, "Passwords do not match");
            } else {
                saveData(name, phone, date, section, pass1);
                if (authorized) {
                    Intent intent = new Intent(AddInfoActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        } else {
            CommonMethods.toastMessage(AddInfoActivity.this, "All Fields are required");
        }

    }

    /**
     * Invoked by the submitButtonAction when the info is valid to Upload data to the DataBase
     * @param name The Display Name that the user has entered
     * @param phone The Phone Number that the user has entered
     * @param date The Birth Date that the user has entered
     * @param section The Section that the user has entered
     * @param pass1 The New Password that the user has entered
     */
    private void saveData(String name, String phone, String date, String section, final String pass1) {
        CommonMethods.toastMessage(AddInfoActivity.this, "Adding Data ...");
        String id = user.getUid();
        try {

            DatabaseReference temp = completeDatabaseReference.child("userdata").child(id);
            temp.child("name").setValue(name);
            temp.child("phone no").setValue(phone);
            temp.child("birth date").setValue(date);
            temp.child("section").setValue(section);


            AuthCredential initialAuth = EmailAuthProvider.getCredential(regEmail, regPass);
            user.reauthenticate(initialAuth).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(pass1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    CommonMethods.toastMessage(AddInfoActivity.this, "Password change failed");
                                    authorized = false;
                                }
                            }
                        });

                    } else {
                        CommonMethods.toastMessage(AddInfoActivity.this, "Re auth Error");
                        authorized = false;
                    }
                }
            });
            authorized = true;
            temp.child("password").setValue(pass1);

        } catch (DatabaseException e) {
            CommonMethods.toastMessage(AddInfoActivity.this, "Database Error");
            authorized = false;
        }


    }

}
