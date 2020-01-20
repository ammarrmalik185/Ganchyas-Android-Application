package app.ganchyas.NonActivityClasses;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Paradox;
 */

public class Notification {

    private String type;
    private String initiator;
    private String title;
    private String text;
    private String time;
    private String date;
    private String forumId;
    private DatabaseReference myDb;

    public Notification(DataSnapshot notificaitonSnap) {

        myDb = FirebaseDatabase.getInstance().getReference();
        type = notificaitonSnap.child("type").getValue().toString();
        initiator = notificaitonSnap.child("initiator").getValue().toString();
        title = notificaitonSnap.child("title").getValue().toString();
        text = notificaitonSnap.child("text").getValue().toString();
        time = notificaitonSnap.child("time").getValue().toString();
        date = notificaitonSnap.child("date").getValue().toString();
        forumId = notificaitonSnap.child("forumId").getValue().toString();
    }

    public Notification(RemoteMessage remoteMessage) {

        myDb = FirebaseDatabase.getInstance().getReference();
        type = remoteMessage.getData().get("type");
        initiator = remoteMessage.getData().get("initiator");
        title = remoteMessage.getData().get("title");
        text = remoteMessage.getData().get("text");
        forumId = remoteMessage.getData().get("forumId");

        DateFormat s = new SimpleDateFormat("dd/MM/yyyy");
        date = s.format(new Date());
        s = new SimpleDateFormat("HH:mm");
        time = s.format(new Date());
    }

    public String getType() {
        return type;
    }

    public String getInitiator() {
        return initiator;
    }

    public String getForumId() {
        return forumId;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public void pushToDb(){


        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        Long format = Long.parseLong(s.format(new Date()));

        String identifier = "notification_id_" + (50000000000000L - format);

        DatabaseReference notificationRef = myDb.child("notificationData").child(FirebaseAuth
                .getInstance().getCurrentUser().getUid()).child(identifier);

        HashMap<String, String> notification = new HashMap<>();

        notification.put("type", type);
        notification.put("initiator", initiator);
        notification.put("title", title);
        notification.put("text", text);
        notification.put("time", time);
        notification.put("date", date);
        notification.put("forumId", forumId);

        notificationRef.setValue(notification);

    }
}
