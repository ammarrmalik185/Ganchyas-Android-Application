package app.ganchyas.ApplicationClasses;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 * Creates notification channels before the app starts
 * @author Paradox
 *
 */

public class ApplicationStart extends Application {

    /**
     *  Notification channel 1 id (for forum notifications)
     */
    public static final String CHANNEL_1_ID = "forumChannel";
    /**
     *  Notification channel 2 id (for comment notifications)
     */
    public static final String CHANNEL_2_ID = "commentChannel";
    /**
     *  Notification channel 3 id (for like/dislike notifications)
     */
    public static final String CHANNEL_3_ID = "likeDislikeChannel";
    /**
     *  Notification channel 4 id (for message notifications)
     */
    public static final String CHANNEL_4_ID = "messageChannel";

    /**
     * Empty constructor
     */
    public ApplicationStart() {
    }

    /**
     * Overriding onCreate to call createChannels method when the application is created with the given ids
     */
    @Override
    public void onCreate() {
        super.onCreate();
        createChannels();
    }

    /**
     * Creates the notification channels
     */
    private void createChannels() {
        // test for android version (channels only work in android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            // defining forums channel
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "New Forums",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Notifications for new forms");

            // defining Comments channel
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "New Comments",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("Notifications for new comments on your forums");

            // defining  Like/Dislike channel
            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "New Like/Dislike",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel3.setDescription("Notifications for new likes or dislikes on your forums");

            // defining Messages channel
            NotificationChannel channel4 = new NotificationChannel(
                    CHANNEL_4_ID,
                    "New Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel4.setDescription("Notifications for new messages");

            // creating channels
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);
            manager.createNotificationChannel(channel4);

        }
    }
}
