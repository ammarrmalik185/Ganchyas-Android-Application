package app.ganchyas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import app.ganchyas.NonActivityClasses.CommonMethods;
import app.ganchyas.NonActivityClasses.MessagePack;
import app.ganchyas.NonActivityClasses.MessageViewAdapter;

/**
 * Messenger that the users interact with to send and view messages
 * @author Paradox
 */

public class MessengerActivity extends AppCompatActivity {

    /**
     * The id of the user that the current user is conversing with
     */
    String userId;
    /**
     * The Uid assigned to the current conversation
     */
    String conversationId;
    /**
     * Stores whether or not the conversation is in the database or not
     */
    boolean isInitialized;
    /**
     * Stores the reference of the root node of the Database
     */
    DatabaseReference completeDatabaseReference;
    /**
     * Database reference of the current conversation
     */
    DatabaseReference conversationRef;
    /**
     * Contains the reference of the Text Field on the xml that shows the Name of the user
     */
    TextView nameView;
    /**
     * Contains the reference of the ImageView on the xml that shows the Profile picture of the user
     */
    ImageView profilePicture;
    /**
     * A value event listener that gets the information of the user
     */
    ValueEventListener userListener;
    /**
     * The main list of messages the the user sees
     */
    ListView conversationDisplay;
    /**
     * Field that the user type the message into
     */
    EditText messageView;

    /**
     * Overriding onCreate to Inflate custom UI using activity_messenger.xml
     * @param savedInstanceState contains the old state of this UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        messageView = findViewById(R.id.messageToSend);
        nameView = findViewById(R.id.messageReceiverName);
        profilePicture = findViewById(R.id.messageReceiverPicture);
        conversationDisplay = findViewById(R.id.messageView);

        userId = getIntent().getExtras().get("userId").toString();
        conversationId = getIntent().getExtras().get("conversationId").toString();
        isInitialized = !(boolean) getIntent().getExtras().get("newConversation");

        completeDatabaseReference = FirebaseDatabase.getInstance().getReference();
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                nameView.setText(
                        dataSnapshot.child("userdata").child(userId).child("name").getValue().toString()
                );
                if (dataSnapshot.child("userdata").child(userId).child("profile picture").exists())
                    Picasso.with(MessengerActivity.this)
                        .load(dataSnapshot.child("userdata").child(userId).child("profile picture").getValue().toString())
                        .placeholder(R.mipmap.ic_launcher_round)
                        .into(profilePicture);
                else
                    Picasso.with(MessengerActivity.this)
                            .load(R.mipmap.ic_launcher_round)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .into(profilePicture);

                completeDatabaseReference.removeEventListener(userListener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        completeDatabaseReference.addValueEventListener(userListener);

        initializeConversation();

        conversationRef = completeDatabaseReference.child("messageData").child(conversationId);

        conversationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot messages = dataSnapshot.child("messages");
                ArrayList<MessagePack> messageList = new ArrayList<>();

                if (messages.exists()){
                    for (DataSnapshot message: messages.getChildren()){
                        messageList.add(new MessagePack(message));
                    }
                }
                MessageViewAdapter messageViewAdapter = new MessageViewAdapter(MessengerActivity.this, messageList);
                conversationDisplay.setAdapter(messageViewAdapter);
                conversationDisplay.setSelection(conversationDisplay.getCount()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * Adds the data of the conversation to the database (if this is a new conversation)
     */
    public void initializeConversation(){

        if (!isInitialized)
        {

            SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
            long format = Long.parseLong(s.format(new Date()));
            conversationId = "conversation_id_" + (50000000000000L - format);

            DatabaseReference newConversation = completeDatabaseReference.child("messageData").child(conversationId);
            HashMap<String, String> data = new HashMap<>();
            data.put("user1",FirebaseAuth.getInstance().getCurrentUser().getUid());
            data.put("user2", userId);
            newConversation.setValue(data);
            isInitialized = true;
            FirebaseMessaging.getInstance().subscribeToTopic(conversationId);
        }
    }

    /**
     * Sends the entered message to the user
     * @param view Contains the submit button
     */
    public void sendButtonPressed(View view){

        String messageText = messageView.getText().toString();
        HashMap<String, String> messageData = new HashMap<>();

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        Long format = Long.parseLong(s.format(new Date()));

        s = new SimpleDateFormat("dd/MM/yyyy");
        String date = s.format(new Date());
        s = new SimpleDateFormat("HH:mm");
        String time = s.format(new Date());

        String identifier = "message_id_" + (format);

        messageData.put("messageText", messageText);
        messageData.put("time", time);
        messageData.put("date", date);
        messageData.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());

        conversationRef.child("messages").child(identifier).setValue(messageData);
        messageView.setText("");
    }
}
