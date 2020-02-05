package app.ganchyas.NonActivityClasses;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Contains the data of a single forum
 * @author Paradox
 */
public class ForumPack {

    /**
     * Name of the user that created the forum
     */
    private String sender;
    /**
     * When the forum was created
     */
    private String date;
    /**
     * The subject of the forum
     */
    private String subject;
    /**
     * Main text of the forum
     */
    private String mainText;
    /**
     * Count of the dislikes on the forum
     */
    private long dislikeCount;
    /**
     * Count of the likes on the forum
     */
    private long likeCount;
    /**
     * Count of the comments on the forum
     */
    private long commentCount;
    /**
     * Array of comments
     */
    private ArrayList<CommentPack> commentPacks;
    /**
     * Stores whether the forum is disliked by the current user or not
     */
    private boolean disLiked;
    /**
     * Stores whether the forum is liked by the current user or not
     */
    private boolean liked;
    /**
     * Unique id of the forum in the database
     */
    private String forumId;
    /**
     * Uri link of the file attached to the forum (if any)
     */
    private String fileUpload;
    /**
     * Profile picture of the sender (if any)
     */
    private String senderPic;
    /**
     * Sender's phone no
     */
    private String senderNo;
    /**
     * Sender's birth date
     */
    private String senderDate;
    /**
     * Sender's section
     */
    private String senderSection;
    /**
     * Type of the current forum (image/ text/ video or file)
     */
    private String type;

    /**
     * Gets the data from database snapshot and assigns values
     * @param mainSnap Snapshot of the entire database
     * @param forumSnap Snapshot of the current forum in the database
     */
    public ForumPack(DataSnapshot mainSnap, DataSnapshot forumSnap) {

        forumId = forumSnap.getKey();
        String senderID = forumSnap.child("sender").getValue().toString();
        DataSnapshot senderSnap = mainSnap.child("userdata").child(senderID);
        this.sender = senderSnap.child("name").getValue().toString();
        this.date = forumSnap.child("date").getValue().toString();
        this.subject = forumSnap.child("subject").getValue().toString();
        this.mainText = forumSnap.child("mainText").getValue().toString();
        this.senderNo = senderSnap.child("phone no").getValue().toString();
        this.senderDate = senderSnap.child("birth date").getValue().toString();
        this.senderSection = senderSnap.child("section").getValue().toString();
        this.type = "text";

        DataSnapshot profilePic = senderSnap.child("profile picture");

        if(profilePic.exists())
            senderPic = profilePic.getValue().toString();

        if (!forumSnap.child("likers").exists()) {
            likeCount = 0;
            liked = false;
        }
        else {
            likeCount =0;
            for (DataSnapshot liker:forumSnap.child("likers").getChildren()){
                if ((boolean)liker.getValue())
                    likeCount += 1;
            }
            liked = forumSnap.child("likers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()
            && (boolean)forumSnap.child("likers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue();
        }

        if (!forumSnap.child("disLikers").exists()) {
            dislikeCount = 0;
            disLiked = false;
        }
        else {
            dislikeCount =0;
            for (DataSnapshot disliker:forumSnap.child("disLikers").getChildren()){
                if ((boolean)disliker.getValue())
                    dislikeCount += 1;
            }
            disLiked = forumSnap.child("disLikers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()
                    && (boolean)forumSnap.child("disLikers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue();
        }

        commentPacks = new ArrayList<>();

        if (!forumSnap.child("comments").exists()) {
            commentCount = 0;
        }
        else {
            commentCount = forumSnap.child("comments").getChildrenCount();
            DataSnapshot commentsSnap = forumSnap.child("comments");
            for (DataSnapshot dataSnapshot : commentsSnap.getChildren()) {
                CommentPack commentPack = new CommentPack(mainSnap, dataSnapshot);
                commentPacks.add(commentPack);
            }
        }

        if(forumSnap.child("fileUri").exists()) {
            fileUpload = forumSnap.child("fileUri").getValue().toString();
            if (forumSnap.child("type").exists()){
                type = forumSnap.child("type").getValue().toString();
            }
        }
    }

    /**
     * Getter method
     * @return Forum id
     */
    public String getForumId() {
        return forumId;
    }

    /**
     * Getter method
     * @return Comment count
     */
    public String getCommentCount() {
        return Long.toString(commentCount);
    }

    /**
     * Refreshes the comments of a forum by using a new snapshot of the forum
     * @param dataSnapshot Snapshot of the complete database
     */
    public void refreshComments(DataSnapshot dataSnapshot) {
        DataSnapshot forumSnap = dataSnapshot.child("forumData").child(forumId);

        commentPacks = new ArrayList<>();

        if (!forumSnap.child("comments").exists()) {
            commentCount = 0;
        } else {
            commentCount = forumSnap.child("comments").getChildrenCount();
            DataSnapshot commentsSnap = forumSnap.child("comments");
            for (DataSnapshot commentSnap : commentsSnap.getChildren()) {
                CommentPack commentPack = new CommentPack(dataSnapshot, commentSnap);
                commentPacks.add(commentPack);
            }
        }
    }

    /**
     * Getter method
     * @return Array of comments
     */
    public ArrayList<CommentPack> getCommentPacks() {
        return commentPacks;
    }

    /**
     * Getter method
     * @return Sender id
     */
    public String getSender() {
        return sender;
    }

    /**
     * Getter method
     * @return Date
     */
    public String getDate() {
        return date;
    }

    /**
     * Getter method
     * @return Senders's profile picture
     */
    public String getSenderPic() {
        return senderPic;
    }

    /**
     * Check if the sender has a profile picture or not
     * @return True if the sender has a profile picture.
     */
    public boolean hasSenderPic(){
        return senderPic != null;
    }

    /**
     * Getter method
     * @return Forum subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Getter method
     * @return Sender phone no
     */
    public String getSenderNo() {
        return senderNo;
    }

    /**
     * Getter method
     * @return Sender birth date
     */
    public String getSenderDate() {
        return senderDate;
    }

    /**
     * Getter method
     * @return Sender section
     */
    public String getSenderSection() {
        return senderSection;
    }

    /**
     * Getter method
     * @return Forum text
     */
    public String getMainText() {
        return mainText;
    }

    /**
     * Getter method
     * @return Number of dislikes
     */
    public String getDislikeCount() {
        return Long.toString(dislikeCount);
    }

    /**
     * Getter method
     * @return Number of dislikes
     */
    public String getLikeCount() {
        return Long.toString(likeCount);
    }

    /**
     * Getter method
     * @return Whether or not the current user has disliked the forum or not
     */
    public boolean isDisLiked() {
        return disLiked;
    }

    /**
     * Getter method
     * @return Whether or not the current user has liked the forum or not
     */
    public boolean isLiked() {
        return liked;
    }

    /**
     * Checks if the forum has a file attached to it
     * @return Whether tha forum has a file or not
     */
    public boolean hasFile(){
        return !(fileUpload == null);
    }

    /**
     * Getter method
     * @return The uri of the file attached to the forum
     */
    public String getFileUpload() {
        return fileUpload;
    }

    /**
     * Getter method
     * @return The type of the forum
     */
    public String getType() {
        return type;
    }

    /**
     * Add like to the forum from the current user
     */
    public void pressLikeButton() {
        if (!liked && !disLiked) {
            DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
            myDb.child("forumData").child(forumId).child("likers")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
            likeCount += 1;
            liked = true;

        } else {
            if (liked) {
                DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();

                try {
                    myDb.child("forumData").child(forumId).child("likers")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(false);
                } catch (Exception e) {
                    Log.e("X185", e.toString());
                }
                likeCount -= 1;
                liked = false;

            } else {
                pressDisLikeButton();
                pressLikeButton();
            }
        }
    }

    /**
     * Add dislike to the forum from the current user
     */
    public void pressDisLikeButton() {
        if (!liked && !disLiked) {
            DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
            myDb.child("forumData").child(forumId).child("disLikers")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
            dislikeCount += 1;
            disLiked = true;

        } else if (disLiked) {

            DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();

            try {
                myDb.child("forumData").child(forumId).child("disLikers")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(false);
            } catch (Exception e) {
                Log.e("X185", e.toString());
            }

            dislikeCount -= 1;
            disLiked = false;

        } else {
            pressLikeButton();
            pressDisLikeButton();
        }
    }


}


