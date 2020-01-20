package app.ganchyas;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.ganchyas.NonActivityClasses.ConversationListAdapter;
import app.ganchyas.NonActivityClasses.ConversationPack;
import app.ganchyas.NonActivityClasses.UserDataPack;
import app.ganchyas.NonActivityClasses.NewConversationListAdapter;

/**
 * @author Paradox;
 */

public class MessagingFragment extends Fragment {

    private Dialog usersDialog;
    private DatabaseReference completeDatabase;
    private ValueEventListener valueListner;
    private NewConversationListAdapter adapter;
    private ListView conversations;

    public MessagingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        completeDatabase = FirebaseDatabase.getInstance().getReference();
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);
        usersDialog = new Dialog(getContext());
        usersDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        usersDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                completeDatabase.removeEventListener(valueListner);
            }
        });

        FloatingActionButton startNewConvo = view.findViewById(R.id.floatingActionButtonMessage);
        conversations = view.findViewById(R.id.listConversations);

        startNewConvo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewConvoPressed();
            }
        });

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ConversationPack> userDataPacks = new ArrayList<>();
                ArrayList<String> userIds = new ArrayList<>();
                DataSnapshot allMessages = dataSnapshot.child("messageData");
                if (allMessages.exists()) {
                    for (DataSnapshot singleMessage : allMessages.getChildren()) {
                        String secondId = "";
                        if (singleMessage.child("user1").getValue().toString()
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            secondId = singleMessage.child("user2").getValue().toString();


                        if (singleMessage.child("user2").getValue().toString()
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            secondId = singleMessage.child("user1").getValue().toString();

                        if (!secondId.equals("") && !userIds.contains(secondId)) {

                            ConversationPack userDataPack = new ConversationPack(dataSnapshot, singleMessage);
                            userIds.add(secondId);
                            userDataPacks.add(userDataPack);
                        }
                    }
                }
                ConversationListAdapter adapter2 = new ConversationListAdapter(getContext(), userDataPacks);
                conversations.setAdapter(adapter2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        completeDatabase.addValueEventListener(listener);


        return view;
    }

    private void startNewConvoPressed(){
        usersDialog.setContentView(R.layout.dialog_title_and_list);

        final ListView usersList = usersDialog.findViewById(R.id.listUsers);
        TextView header = usersDialog.findViewById(R.id.header);
        header.setText("Available Users");

        valueListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot usersSnap = dataSnapshot.child("userdata");
                ArrayList<UserDataPack> userData = new ArrayList<>();
                ArrayList<String> strings = new ArrayList<>();
                DataSnapshot allConversations = dataSnapshot.child("messageData");

                for (DataSnapshot singleConversation:allConversations.getChildren()){
                    if (singleConversation.child("user1").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        strings.add(singleConversation.child("user2").getValue().toString());
                    }else if (singleConversation.child("user2").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        strings.add(singleConversation.child("user1").getValue().toString());
                    }

                }

                for (DataSnapshot singleUserData: usersSnap.getChildren()){
                    if (!singleUserData.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        if(!strings.contains(singleUserData.getKey())) {
                            UserDataPack dataPack = new UserDataPack(singleUserData);
                            userData.add(dataPack);
                        }
                    }
                }

                adapter = new NewConversationListAdapter(getActivity(), userData);

                usersList.setAdapter(adapter);
                usersDialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        completeDatabase.addValueEventListener(valueListner);
    }

}
