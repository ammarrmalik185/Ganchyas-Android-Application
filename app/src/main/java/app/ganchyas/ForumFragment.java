package app.ganchyas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.ganchyas.NonActivityClasses.FirebaseCallback;
import app.ganchyas.NonActivityClasses.ForumListAdapter;
import app.ganchyas.NonActivityClasses.ForumPack;
import app.ganchyas.NonActivityClasses.ForumTypeChooser;

/**
 * Displays a list of forums.
 * @author Paradox
 */

public class ForumFragment extends Fragment {

    /**
     * Contains the UI of the current fragment Instance
     */
    public View view;
    /**
     * Stores the reference of the root node of the Database
     */
    private DatabaseReference completeDatabaseReference;
    /**
     * Contains the Snapshot of the entire database
     */
    private DataSnapshot completeDatabaseSnapshot;
    /**
     * Contains all the forums that need to be displayed in forum of ForumPack Objects
     */
    private ArrayList<ForumPack> forumArray;
    /**
     * A value event listener for the entire database (made global to be accessible thought inner classes)
     */
    private ValueEventListener listener;
    /**
     * Contains reference to the SwipeRefreshLayout to set a refresh Action
     */
    private SwipeRefreshLayout refreshContainer;
    /**
     * An Array Adapter to dynamically generate UI of the Forums
     */
    private ForumListAdapter adapter;

    /**
     * Empty Constructor
     */
    public ForumFragment() {
        // Required empty public constructor

    }

    /**
     * Called when the activity is created
     * @param savedInstanceState Contains the saved Instance of the Activity
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Generates the UI of the Fragment
     * @param inflater An Inflater object to inflate the layout
     * @param container A parent view where the UI will be placed
     * @param savedInstanceState An older instance of the fragment (if any)
     * @return The inflated UI of the Fragment Instance
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("X185", "forum fragment started");
        completeDatabaseReference = FirebaseDatabase.getInstance().getReference();
        forumArray = new ArrayList<>();

        view = inflater.inflate(R.layout.fragment_forum, container, false);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForumTypeChooser forumTypeChooser = new ForumTypeChooser();
                forumTypeChooser.show(getFragmentManager(),"typeChooser");
            }
        });

        return readData(new FirebaseCallback() {
            @Override
            public View onCallBack() {

                DataSnapshot forums = completeDatabaseSnapshot.child("forumData");

                for (DataSnapshot singleForum : forums.getChildren()) {
                    ForumPack formPack = new ForumPack(completeDatabaseSnapshot, singleForum);
                    forumArray.add(formPack);
                }

                // Inflate the layout for this fragment
                ListView forumList = view.findViewById(R.id.forumList);
                adapter = new ForumListAdapter(getActivity(), forumArray);
                refreshContainer = view.findViewById(R.id.swipeToRefresh);
                refreshContainer.setRefreshing(false);
                refreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getFragmentManager()
                                .beginTransaction()
                                .detach(ForumFragment.this)
                                .attach(ForumFragment.this)
                                .commit();
                    }
                });
                forumList.setAdapter(adapter);
                completeDatabaseReference.removeEventListener(listener);
                return view;
            }
        });

    }

    /**
     * A helping function to read the data of the database
     * @param firebaseCallback A child of FirebaseCallBack that has implemented the onCallBack() method.
     * @return An inflated view.
     */
    private View readData(final FirebaseCallback firebaseCallback) {

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                completeDatabaseSnapshot = dataSnapshot;
                view = firebaseCallback.onCallBack();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        completeDatabaseReference.addValueEventListener(listener);
        return view;
    }

}
