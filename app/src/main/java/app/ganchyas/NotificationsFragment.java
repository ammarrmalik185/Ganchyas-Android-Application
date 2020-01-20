package app.ganchyas;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.ganchyas.NonActivityClasses.FirebaseCallback;
import app.ganchyas.NonActivityClasses.Notification;
import app.ganchyas.NonActivityClasses.NotificationsAdapter;

/**
 * @author Paradox;
 */

public class NotificationsFragment extends Fragment {

    private ArrayAdapter notificationsAdapter;
    public View view;
    private DataSnapshot dsSnap;
    private DatabaseReference myDb;
    private ArrayList<Notification> notificationArray;
    private ValueEventListener listener;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        notificationArray = new ArrayList<>();
        myDb = FirebaseDatabase.getInstance().getReference();
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeToRefreshNotifications);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFragmentManager()
                        .beginTransaction()
                        .detach(NotificationsFragment.this)
                        .attach(NotificationsFragment.this)
                        .commit();

            }
        });
        return readData(new FirebaseCallback() {
            @Override
            public View onCallBack() {

                DataSnapshot notificationData = dsSnap.child("notificationData").child(FirebaseAuth
                .getInstance().getCurrentUser().getUid());

                for (DataSnapshot singleForum : notificationData.getChildren()) {
                    Notification notification = new Notification(singleForum);
                    notificationArray.add(notification);
                }

                notificationsAdapter = new NotificationsAdapter(getContext(), notificationArray);
                ListView listView = view.findViewById(R.id.notification_list);
                listView.setAdapter(notificationsAdapter);
                myDb.removeEventListener(listener);
                return view;
            }
        });
    }

    private View readData(final FirebaseCallback firebaseCallback) {

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dsSnap = dataSnapshot;
                view = firebaseCallback.onCallBack();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        myDb.addValueEventListener(listener);

        return view;
    }

}
