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
 * @author Paradox;
 */

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myDb;
    private ProgressDialog dialog;
    private String pass;
    private EditText mEmail, mPassword;
    private CheckBox rememberBox;
    private String email;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        if  (mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Logging in ... please wait");

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.emailField);
        mPassword = findViewById(R.id.passwordField);
        rememberBox = findViewById(R.id.rememberCheck);

        File file2 = new File(getFilesDir().getAbsolutePath() + "/login data.txt");
        try {
            if (file2.exists()) {
                Scanner fileReader = new Scanner(file2);
                String savedEmail = fileReader.nextLine();
                mEmail.setText(savedEmail);
                String savedPassword = fileReader.nextLine();
                mPassword.setText(savedPassword);
                rememberBox.setChecked(true);
            } else {
                rememberBox.setChecked(false);
            }
        } catch (FileNotFoundException e) {
            rememberBox.setChecked(false);
        }


        myDb = FirebaseDatabase.getInstance().getReference();


    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onBackPressed() {

    }

    public void submitButtonAction(View view) {
        dialog.show();
        email = mEmail.getText().toString();
        pass = mPassword.getText().toString();


        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (rememberBox.isChecked()) {
                    File file = new File(getFilesDir().getAbsolutePath() + "/login data.txt");
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            toastMessage("Error creating file to remember password");
                        }
                    }
                    try {
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(mAuth.getCurrentUser().getEmail() + "\n" + pass);
                        fileWriter.close();
                    } catch (IOException e) {
                        toastMessage("Unable to save login info to file");
                    }
                }

                FirebaseUser user = mAuth.getCurrentUser();

                if (!dataSnapshot.child("userdata").child(user.getUid()).exists()) {
                    Intent intent = new Intent(LoginActivity.this, AddInfoActivity.class);
                    intent.putExtra("pass", pass);
                    intent.putExtra("id", email);
                    toastMessage("Welcome new user with email: " + user.getEmail());
                    dialog.dismiss();
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    toastMessage("logged in as " + user.getEmail());
                    dialog.dismiss();
                    startActivity(intent);
                }

                myDb.removeEventListener(listener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toastMessage("Database access denied (could be an internet issue)");

            }
        };


        if (!email.equals("") && !pass.equals("")) {
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                myDb.addValueEventListener(listener);
                            } else {
                                Exception e = task.getException();
                                String exception = e.getMessage();

                                dialog.dismiss();
                                if (exception.equals("An internal error has occurred. [ 7: ]"))
                                    toastMessage("Internet Problem");
                                else if (exception.equals("The password is invalid or the user does not have a password."))
                                    toastMessage("Email or Password Incorrect");
                                else
                                    toastMessage("Unexpected Error");

                            }
                        }
                    });
        } else {
            toastMessage("You didn't fill in all the fields.");
        }

    }


}
