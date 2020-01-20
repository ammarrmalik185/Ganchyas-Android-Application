package app.ganchyas.NonActivityClasses;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * @author Paradox;
 */

public class ForumPack {

    private String sender;
    private String date;
    private String subject;
    private String mainText;
    private long dislikeCount;
    private long likeCount;
    private long commentCount;
    private ArrayList<CommentPack> commentPacks;
    private boolean disLiked;
    private boolean liked;
    private String forumId;
    private String fileUpload;
    private String senderPic;
    private String senderNo;
    private String senderDate;
    private String senderSection;
    private String type;

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

        DataSnapshot priofilePic = senderSnap.child("profile picture");

        if(priofilePic.exists())
            senderPic = priofilePic.getValue().toString();

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

    public String getForumId() {
        return forumId;
    }

    public String getCommentCount() {
        return Long.toString(commentCount);
    }

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

    public ArrayList<CommentPack> getCommentPacks() {
        return commentPacks;
    }

    public String getSender() {
        return sender;
    }

    public String getDate() {
        return date;
    }

    public String getSenderPic() {
        return senderPic;
    }

    public boolean hasSenderPic(){
        return senderPic != null;
    }

    public String getSubject() {
        return subject;
    }

    public String getSenderNo() {
        return senderNo;
    }

    public String getSenderDate() {
        return senderDate;
    }

    public String getSenderSection() {
        return senderSection;
    }

    public String getMainText() {
        return mainText;
    }

    public String getDislikeCount() {
        return Long.toString(dislikeCount);
    }

    public String getLikeCount() {
        return Long.toString(likeCount);
    }

    public boolean isDisLiked() {
        return disLiked;
    }

    public boolean isLiked() {
        return liked;
    }

    public boolean hasFile(){
        return !(fileUpload == null);
    }

    public String getFileUpload() {
        return fileUpload;
    }

    public String getType() {
        return type;
    }

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

    public int pressDisLikeButton() {
        if (!liked && !disLiked) {
            DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
            myDb.child("forumData").child(forumId).child("disLikers")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
            dislikeCount += 1;
            disLiked = true;
            return 1;

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
            return 2;

        } else {
            pressLikeButton();
            pressDisLikeButton();
            return 3;
        }
    }


}


