package app.ganchyas;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.ganchyas.NonActivityClasses.CommonMethods;

/**
 * @author Paradox;
 */

public class PasswordChangeActivity extends AppCompatActivity {


    private static final String TAG = "AddInfoActivity";
    boolean authorized;
    private DatabaseReference myDb;
    private FirebaseUser user;
    private EditText currentPassField;
    private EditText passField1;
    private EditText passField2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        myDb = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        currentPassField = findViewById(R.id.oldPassField);
        passField1 = findViewById(R.id.passField1);
        passField2 = findViewById(R.id.passField2);

    }

    public void submitButtonAction(View view) {
        String currentPass = currentPassField.getText().toString();
        String pass1 = passField1.getText().toString();
        String pass2 = passField2.getText().toString();
        if (pass1.length() < 8) {
            toastMessage("Password should be more than 8 characters");
        } else if (!pass1.equals(pass2)) {
            toastMessage("Passwords do not match");
        } else {
            saveData(pass1, currentPass);
        }
    }

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
                                toastMessage("Password change failed");
                                authorized = false;
                            } else {
                                toastMessage("Password changed successfully");
                                authorized = true;
                                myDb.child("userdata").child(user.getUid()).child("password").setValue(pass1);
                            }
                        }
                    });

                } else {
                    toastMessage("Password Incorrect");
                    authorized = false;
                }
            }
        });
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
