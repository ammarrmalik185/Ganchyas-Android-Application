package app.ganchyas.NonActivityClasses;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import app.ganchyas.LocationFragment;
import app.ganchyas.ForumFragment;
import app.ganchyas.MessagingFragment;
import app.ganchyas.NotificationsFragment;

/**
 * @author Paradox;
 */

public class NavViewPagerAdapter extends FragmentPagerAdapter {

    private ForumFragment forumFragment;
    private MessagingFragment messagingFragment;
    private NotificationsFragment notificationsFragment;
    private LocationFragment locationFragment;

    public NavViewPagerAdapter(FragmentManager fm) {
        super(fm);
        forumFragment = new ForumFragment();
        messagingFragment = new MessagingFragment();
        notificationsFragment = new NotificationsFragment();
        locationFragment = new LocationFragment();

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return forumFragment;
        else if (position == 1)
            return messagingFragment;
        else if (position == 2)
            return locationFragment;
        else if (position == 3)
            return notificationsFragment;

        return forumFragment;
    }

    @Override
    public int getCount() {
        return 4;
    }


}

