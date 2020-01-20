package app.ganchyas.NonActivityClasses;

import com.google.firebase.database.DataSnapshot;

/**
 * Stores the data of a single comment
 * @author Paradox
 */
public class CommentPack {

    /**
     * Contains the sender's Display name
     */
    private String sender;
    /**
     * Contains the Main Body of the comment
     */
    private String text;
    /**
     * Contains the date when the comment was published
     */
    private String date;

    /**
     * Takes snapshots and extracts values from it.
     * @param context Snapshot of the entire dataBase
     * @param commentSnap Snapshot of the current comment
     * @throws NullPointerException if the commentSnap does not have the required data.
     */
    CommentPack(DataSnapshot context, DataSnapshot commentSnap) {

        text = commentSnap.child("text").getValue().toString();
        sender = context.child("userdata").child(commentSnap.child("sender").getValue().toString()).child("name").getValue().toString();
        date = commentSnap.child("date").getValue().toString();
    }

    /**
     * Getter Method for date
     * @return  The date the comment was published
     */
    public String getDate() {
        return date;
    }

    /**
     * Getter Method for Sender
     * @return  The the display name of the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Getter Method for Main body of the comment
     * @return  The the text that was in the comment
     */
    public String getText() {
        return text;
    }
}
