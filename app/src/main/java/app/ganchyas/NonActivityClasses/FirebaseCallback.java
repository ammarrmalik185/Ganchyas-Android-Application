package app.ganchyas.NonActivityClasses;

import android.view.View;

/**
 * Callback interface to help get database from firebase inside a fragment
 * @author Paradox
 */
public interface FirebaseCallback {

    /**
     * Called when the database returns a snapshot
     * @return The view of the fragment
     */
    View onCallBack();

}
