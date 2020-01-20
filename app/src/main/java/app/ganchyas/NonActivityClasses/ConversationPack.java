package app.ganchyas.NonActivityClasses;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

/**
 * Container for a single Conversation Data (not messages, only meta data)
 * @author Paradox
 */
public class ConversationPack {

    /**
     * The data of the user that the conversation is with
     */
    private UserDataPack otherUser;
    /**
     * Uid of the conversation (same as Uid in database)
     */
    private String conversationId;

    /**
     * Automatically gets values from snapshot and assigns to local variables
     * @param mainSnap Snapshot of the entire database
     * @param conversationSnap Snapshot of the current conversation
     */
    public ConversationPack(DataSnapshot mainSnap, DataSnapshot conversationSnap) {

        String secondId;
        if (conversationSnap.child("user1").getValue().toString()
                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            secondId = conversationSnap.child("user2").getValue().toString();
        else
            secondId = conversationSnap.child("user1").getValue().toString();

        otherUser = new UserDataPack(mainSnap.child("userdata").child(secondId));
        conversationId = conversationSnap.getKey();

    }

    /**
     * Getter for otherUser Variable
     * @return The UserDataPack of the user that the conversation is with
     */
    UserDataPack getOtherUser() {
        return otherUser;
    }

    /**
     * Getter method for conversationId Variable
     * @return Uid of the conversation
     */
    String getConversationId() {
        return conversationId;
    }
}



