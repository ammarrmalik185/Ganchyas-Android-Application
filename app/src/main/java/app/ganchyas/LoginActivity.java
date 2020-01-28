package app.ganchyas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import app.ganchyas.NonActivityClasses.CommonMethods;

/**
 * Login page to authenticate with firebase authentication. Skipped if already logged in.
 * @author Paradox
 */

public class LoginActivity extends AppCompatActivity {

    /**
     * Stores the firebase authentication instance
     */
    private FirebaseAuth firebaseAuth;
    /**
     * Stores the reference of the root node of the Database
     */
    private DatabaseReference completeDatabaseReference;
    /**
     * A dialog that appears when login button is clicked.
     */
    private ProgressDialog dialog;
    /**
     * Contains the password entered by the user
     */
    private String pass;
    /**
     * Reference to the edit text field in the UI that the user enters email into
     */
    private EditText emailEditText;
    /**
     * Reference to the edit text field in the UI that the user enters password into
     */
    private EditText passwordEditText;
    /**
     * A reference to the check box on the UI that if clicked saves the login info to a secure local file
     */
    private CheckBox rememberBox;
    /**
     * Contains the email that the user has entered
     */
    private String email;
    /**
     * A value event listener for the database reference
     */
    private ValueEventListener listener;

    /**
     * Inflates the UI using activity_login.xml.
     * @param savedInstanceState Previous instance of the UI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        if  (firebaseAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Logging in ... please wait");

        firebaseAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailField);
        passwordEditText = findViewById(R.id.passwordField);
        rememberBox = findViewById(R.id.rememberCheck);

        File file2 = new File(getFilesDir().getAbsolutePath() + "/login data.txt");
        try {
            if (file2.exists()) {
                Scanner fileReader = new Scanner(file2);
                String savedEmail = fileReader.nextLine();
                emailEditText.setText(savedEmail);
                String savedPassword = fileReader.nextLine();
                passwordEditText.setText(savedPassword);
                rememberBox.setChecked(true);
            } else {
                rememberBox.setChecked(false);
            }
        } catch (FileNotFoundException e) {
            rememberBox.setChecked(false);
        }


        completeDatabaseReference = FirebaseDatabase.getInstance().getReference();


    }

    /**
     * Overwritten to make sure that after login the user cannot go the MainActivity.java
     */
    public void onBackPressed() {
    }

    /**
     * Invoked when the login button is pressed
     * @param view The button object that was pressed
     */
    public void submitButtonAction(View view) {
        dialog.show();
        email = emailEditText.getText().toString();
        pass = passwordEditText.getText().toString();

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (rememberBox.isChecked()) {
                    File file = new File(getFilesDir().getAbsolutePath() + "/login data.txt");
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            CommonMethods.toastMessage(LoginActivity.this, "Error creating file to remember password");
                        }
                    }
                    try {
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(firebaseAuth.getCurrentUser().getEmail() + "\n" + pass);
                        fileWriter.close();
                    } catch (IOException e) {
                        CommonMethods.toastMessage(LoginActivity.this, "Unable to save login info to file");
                    }
                }

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (!dataSnapshot.child("userdata").child(user.getUid()).exists()) {
                    Intent intent = new Intent(LoginActivity.this, AddInfoActivity.class);
                    intent.putExtra("pass", pass);
                    intent.putExtra("id", email);
                    
                    CommonMethods.toastMessage(LoginActivity.this, "Welcome new user with email: " + user.getEmail());
                    dialog.dismiss();
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    CommonMethods.toastMessage(LoginActivity.this, "logged in as " + user.getEmail());
                    dialog.dismiss();
                    startActivity(intent);
                }

                completeDatabaseReference.removeEventListener(listener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                CommonMethods.toastMessage(LoginActivity.this, "Database access denied (could be an internet issue)");

            }
        };

        if (!email.equals("") && !pass.equals("")) {
            firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                completeDatabaseReference.addValueEventListener(listener);
                            } else {
                                Exception e = task.getException();
                                String exception = e.getMessage();

                                dialog.dismiss();
                                if (exception.equals("An internal error has occurred. [ 7: ]"))
                                    CommonMethods.toastMessage(LoginActivity.this, "Internet Problem");
                                else if (exception.equals("The password is invalid or the user does not have a password."))
                                    CommonMethods.toastMessage(LoginActivity.this, "Email or Password Incorrect");
                                else
                                    CommonMethods.toastMessage(LoginActivity.this, "Unexpected Error");

                            }
                        }
                    });
        } else {
            CommonMethods.toastMessage(LoginActivity.this, "You didn't fill in all the fields.");
        }

    }
}
