package app.ganchyas.NonActivityClasses;

import com.google.firebase.database.DataSnapshot;

/**
 * @author Paradox;
 */

public class UserDataPack {

    private String userId;
    private String name;
    private String imageUri;

    public UserDataPack(DataSnapshot userSnap) {

        this.userId = userSnap.getKey();
        this.name = userSnap.child("name").getValue().toString();
        if (userSnap.child("profile picture").exists())
        {
            imageUri = userSnap.child("profile picture").getValue().toString();
        }

    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getImageUri() {
            return imageUri;
    }

    public boolean hasImage(){
        return imageUri != null;
    }
}
