package app.ganchyas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import app.ganchyas.NonActivityClasses.CommonMethods;
import app.ganchyas.NonActivityClasses.PatchPack;
import app.ganchyas.NonActivityClasses.PatchesListAdapter;

/**
 * Shows the user a list of application patches that the user can download
 * @author ParadoX
 */
public class PatchesActivity extends AppCompatActivity {

    /**
     * Contains the reference of listView in xml that the list will be displayed on
     */
    ListView patchList;
    /**
     * Stores the reference of the root node of the Database
     */
    DatabaseReference completeDatabaseReference;
    /**
     * Contains a list of patches
     */
    ArrayList<PatchPack> patches;
    /**
     * An event listener for the entire database
     */
    ValueEventListener listener;

    /**
     * Overriding onCreate to Inflate custom UI using activity_password_change.xml
     * @param savedInstanceState contains the old state of this UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patches);

        patchList = findViewById(R.id.patchesList);
        completeDatabaseReference = FirebaseDatabase.getInstance().getReference();
        patches = new ArrayList<>();

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot singlePatch : dataSnapshot.getChildren()){
                    PatchPack patchPack = new PatchPack(singlePatch);
                    patches.add(patchPack);
                }
                Collections.reverse(patches);

                PatchesListAdapter adapter = new PatchesListAdapter(PatchesActivity.this, patches);
                patchList.setAdapter(adapter);

                completeDatabaseReference.child("patches").removeEventListener(listener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatchesActivity.this,"Unable to retrieve Patches Data"
                ,Toast.LENGTH_SHORT
                ).show();
            }
        };

        completeDatabaseReference.child("patches").addValueEventListener(listener);

    }
}
