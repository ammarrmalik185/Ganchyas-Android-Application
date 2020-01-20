package app.ganchyas.CloudNotifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import app.ganchyas.ForumDisplayActivity;
import app.ganchyas.MessengerActivity;
import app.ganchyas.R;
import app.ganchyas.NonActivityClasses.Notification;

// importing notification channel ids
import static app.ganchyas.ApplicationClasses.ApplicationStart.CHANNEL_1_ID;
import static app.ganchyas.ApplicationClasses.ApplicationStart.CHANNEL_2_ID;
import static app.ganchyas.ApplicationClasses.ApplicationStart.CHANNEL_3_ID;
import static app.ganchyas.ApplicationClasses.ApplicationStart.CHANNEL_4_ID;

/**
 * Manages Notifications (Service)
 * @author ParadoX
 */
public class NotificationService extends FirebaseMessagingService {

    /**
     * Number of the current notification
     */
    int id = 0;

    /**
     * Empty Constructor
     */
    public NotificationService() {
    }

    /**
     * Is invoked when a new notification is recieved
     * @param remoteMessage A RemoteMessage Object that contains the data of the notification
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // checking if message is not empty
        if(remoteMessage.getData().size() > 0) {
            id ++;
            String type = remoteMessage.getData().get("type");
            String initiator = remoteMessage.getData().get("initiator");

            // if the current user has not initiated the notification
            if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(initiator)) {
                assert type != null;
                // if type is not message, push to db else ignore
                if (!type.equals("newMessage")) {
                    Notification notification = new Notification(remoteMessage);
                    notification.pushToDb();
                }

                // send the notification on the appropriate channel
                switch (type) {
                    case "newForum":
                        sendNotificationForum(remoteMessage);
                        break;
                    case "newComment":
                        sendNotificationComment(remoteMessage);
                        break;
                    case "newLike_Dislike":
                        sendNotificationLikeDislike(remoteMessage);
                        break;
                    case "newMessage":
                        sendNotificationMessage(remoteMessage);
                        break;
                    case "testNotification":
                        sendNotificationTest(remoteMessage);
                        break;
                }
            }
        }
    }

    /**
     * Is invoked when a token is assigned or refreshed for the device
     * @param token Contains the new Token
     */
    @Override
    public void onNewToken(String token) {
        // if a device is assigned a new id, it is uploaded to database
        DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        myDb.child("deviceTokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    /**
     * Is invoked by the onMessageReceived when the type of the notification is forum
     * @param remoteMessage contains the data of the notification
     */
    private void sendNotificationForum(RemoteMessage remoteMessage) {

        // getting the notification data
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("text");

        // creating an intent for the notification
        Intent intent = new Intent(this, ForumDisplayActivity.class);
        intent.putExtra("forumId", remoteMessage.getData().get("forumId"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        // building the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(""));

        // sending the notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Is invoked by the onMessageReceived when the type of the notification is comment
     * @param remoteMessage contains the data of the notification
     */
    private void sendNotificationComment(RemoteMessage remoteMessage) {

        // same as sendNotificationForum (with different intent values and build layout)

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("text");

        Intent intent = new Intent(this, ForumDisplayActivity.class);
        intent.putExtra("forumId", remoteMessage.getData().get("forumId"));

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(""));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Is invoked by the onMessageReceived when the type of the notification is Message
     * @param remoteMessage contains the data of the notification
     */
    private void sendNotificationMessage(RemoteMessage remoteMessage) {

        // same as sendNotificationForum (with different intent values and build layout)

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("text");
        String message = remoteMessage.getData().get("message");

        Intent intent = new Intent(this, MessengerActivity.class);
        intent.putExtra("userId", remoteMessage.getData().get("initiator"));
        intent.putExtra("conversationId", remoteMessage.getData().get("conversationId"));
        intent.putExtra("newConversation", false);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 3 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_4_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body + "\n" + message));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Is invoked by the onMessageReceived when the type of the notification is Like or Dislike
     * @param remoteMessage contains the data of the notification
     */
    private void sendNotificationLikeDislike(RemoteMessage remoteMessage) {

        // same as sendNotificationForum (with different intent values and build layout)

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("text");

        Intent intent = new Intent(this, ForumDisplayActivity.class);
        intent.putExtra("forumId", remoteMessage.getData().get("forumId"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 3 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(""));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Is invoked by the onMessageReceived when a test notification is sent
     * @param remoteMessage contains the data of the notification
     */
    private void sendNotificationTest(RemoteMessage remoteMessage) {

        // same as sendNotificationForum (with different intent values and build layout)

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("text");

        Intent intent = new Intent(this, ForumDisplayActivity.class);
        intent.putExtra("forumId", remoteMessage.getData().get("forumId"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 4, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(""))
                .setGroup("lol");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(id, notificationBuilder.build());
    }



}
