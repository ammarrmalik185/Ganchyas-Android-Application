package app.ganchyas.NonActivityClasses;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

/**
 * @author Paradox;
 */

public class MessagePack {

    private String senderId;
    private String message;
    private String time;
    private String date;

    public MessagePack(DataSnapshot messageSnap){
        message = messageSnap.child("messageText").getValue().toString();
        senderId = messageSnap.child("sender").getValue().toString();
        time = messageSnap.child("time").getValue().toString();
        date = messageSnap.child("date").getValue().toString();
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public boolean sent(){
        return senderId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

}
