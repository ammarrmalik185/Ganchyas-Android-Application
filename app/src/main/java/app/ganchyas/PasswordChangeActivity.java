package app.ganchyas;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.ganchyas.NonActivityClasses.CommonMethods;

/**
 * Allows the user to change their password
 * @author Paradox
 */

public class PasswordChangeActivity extends AppCompatActivity {

    /**
     * Saves whether the user is authorized or not
     */
    boolean authorized;
    /**
     * Stores the reference of the root node of the Database
     */
    private DatabaseReference completeDatabaseReference;
    /**
     * Stores the currently logged in user
     */
    private FirebaseUser user;
    /**
     * Reference to the editText field that the user enter current password into
     */
    private EditText currentPassField;
    /**
     * Reference to the editText field that the user enter new password into
     */
    private EditText passField1;
    /**
     * Reference to the editText field that the user enter new password into(re-type new password)
     */
    private EditText passField2;

    /**
     * Overriding onCreate to Inflate custom UI using activity_password_change.xml
     * @param savedInstanceState contains the old state of this UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        completeDatabaseReference = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        currentPassField = findViewById(R.id.oldPassField);
        passField1 = findViewById(R.id.passField1);
        passField2 = findViewById(R.id.passField2);

    }

    /**
     * Invoked when the submit button is pressed (checks for new password validity)
     * @param view Contains the reference of the button pressed
     */
    public void submitButtonAction(View view) {
        String currentPass = currentPassField.getText().toString();
        String pass1 = passField1.getText().toString();
        String pass2 = passField2.getText().toString();
        if (pass1.length() < 8) {
            CommonMethods.toastMessage(PasswordChangeActivity.this, "Password should be more than 8 characters");
        } else if (!pass1.equals(pass2)) {
            CommonMethods.toastMessage(PasswordChangeActivity.this, "Passwords do not match");
        } else {
            saveData(pass1, currentPass);
        }
    }

    /**
     * Changes the password of the user using firebase authentication
     * @param pass1 old password (for re-login)
     * @param oldPass new password entered by the user
     */
    private void saveData(final String pass1, final String oldPass) {
        String regId = user.getEmail();
        AuthCredential initialAuth = EmailAuthProvider.getCredential(regId, oldPass);
        user.reauthenticate(initialAuth).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(pass1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                CommonMethods.toastMessage(PasswordChangeActivity.this, "Password change failed");
                                authorized = false;
                            } else {
                                CommonMethods.toastMessage(PasswordChangeActivity.this, "Password changed successfully");
                                authorized = true;
                                completeDatabaseReference.child("userdata").child(user.getUid()).child("password").setValue(pass1);
                            }
                        }
                    });

                } else {
                    CommonMethods.toastMessage(PasswordChangeActivity.this, "Password Incorrect");
                    authorized = false;
                }
            }
        });
    }


}
