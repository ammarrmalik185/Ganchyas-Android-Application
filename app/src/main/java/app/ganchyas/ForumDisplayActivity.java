package app.ganchyas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import app.ganchyas.NonActivityClasses.CommentPack;
import app.ganchyas.NonActivityClasses.CommentsAdapter;
import app.ganchyas.NonActivityClasses.CommonMethods;
import app.ganchyas.NonActivityClasses.ForumPack;
import app.ganchyas.NonActivityClasses.LikersDislikersAdapter;

/**
 * Shows a forum in a dedicated Activity. Opens when a forum is clicked or when a notification is clicked.
 * @author Paradox
 */

public class ForumDisplayActivity extends AppCompatActivity {

    /**
     * A dialog to display comments, likers or dislikers
     */
    Dialog dialog;
    /**
     * A value event listener for the entire database (made global to be accessible thought inner classes)
     */
    ValueEventListener listener;
    /**
     * An Array adapter to show the list of comments on the dialog
     */
    CommentsAdapter adapter;
    /**
     * Contains the object of the current forum
     */
    ForumPack displayForum;
    /**
     * contains the Unique identifier of the forum
     */
    String forumId;
    /**
     * Stores the reference of the root node of the Database
     */
    DatabaseReference completeDatabaseReference;
    /**
     * Contains a view object that starts the video when clicked
     */
    View videoStart;

    /**
     * Empty Constructor
     */
    public ForumDisplayActivity() {
    }

    /**
     * Overriding onCreate to Inflate custom UI using activity_forum_display.xml
     * @param savedInstanceState contains the old state of this UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        completeDatabaseReference = FirebaseDatabase.getInstance().getReference();
        forumId = getIntent().getStringExtra("forumId");

        completeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot forumSnap = dataSnapshot.child("forumData")
                        .child(forumId);
                displayForum = new ForumPack(dataSnapshot, forumSnap);
                switch (displayForum.getType()){
                    case "text":
                    case "image":
                        setContentView(R.layout.activity_forum_display);
                        typeInflateImage();
                        break;
                    case "video":
                        setContentView(R.layout.activity_forum_display_video);
                        typeInflateVideo();
                        break;
                    case "file":
                        setContentView(R.layout.activity_forum_display);
                    default:
                        setContentView(R.layout.activity_forum_display);
                }
                initialize();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    /**
     * Invoked when the dislikeCount is clicked. Shows the dialog with a list of dislikers
     */
    private void viewDislikersAction() {
        final DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> dislikersArray = new ArrayList<>();

                if (dataSnapshot.child("forumData").child(forumId).child("disLikers").exists()) {
                    DataSnapshot likers = dataSnapshot.child("forumData").child(forumId).child("disLikers");
                    DataSnapshot users = dataSnapshot.child("userdata");
                    for (DataSnapshot dislikerSnap : likers.getChildren()) {
                        if ((boolean)dislikerSnap.getValue())
                            dislikersArray.add(users.child(dislikerSnap.getKey()).child("name").getValue().toString());
                    }
                }
                myDb.removeEventListener(listener);
                dialog.setContentView(R.layout.dialog_title_and_list);
                ListView listUsers = dialog.findViewById(R.id.listUsers);
                listUsers.setAdapter(new LikersDislikersAdapter(ForumDisplayActivity.this, dislikersArray));
                TextView header = dialog.findViewById(R.id.header);
                header.setText("Dislikes");
                dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myDb.addValueEventListener(listener);
    }

    /**
     * Invoked when the likeCount is clicked. Shows the dialog with a list of likers
     */
    private void viewLikersAction() {
        final DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> likersArray = new ArrayList<>();

                if (dataSnapshot.child("forumData").child(forumId).child("likers").exists()) {
                    DataSnapshot likers = dataSnapshot.child("forumData").child(forumId).child("likers");
                    DataSnapshot users = dataSnapshot.child("userdata");
                    for (DataSnapshot likerSnap : likers.getChildren()) {
                        if ((boolean)likerSnap.getValue())
                            likersArray.add(users.child(likerSnap.getKey().toString()).child("name").getValue().toString());
                    }
                }
                myDb.removeEventListener(listener);
                dialog.setContentView(R.layout.dialog_title_and_list);
                ListView listUsers = dialog.findViewById(R.id.listUsers);
                listUsers.setAdapter(new LikersDislikersAdapter(ForumDisplayActivity.this, likersArray));
                TextView header = dialog.findViewById(R.id.header);
                header.setText("Likes");

                dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myDb.addValueEventListener(listener);
    }

    /**
     * Invoked when the comment button or the comment count is clicked
     */
    private void commentButtonPressed() {

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                displayForum.refreshComments(dataSnapshot);
                ArrayList<CommentPack> commentPacks = displayForum.getCommentPacks();
                dialog.setContentView(R.layout.dialog_comments);
                ListView listUsers = dialog.findViewById(R.id.listUsers);
                Collections.reverse(commentPacks);
                adapter = new CommentsAdapter(ForumDisplayActivity.this, commentPacks);
                listUsers.setAdapter(adapter);
                TextView header = dialog.findViewById(R.id.header);
                Button submitButton = dialog.findViewById(R.id.submitButton);
                final EditText commentField = dialog.findViewById(R.id.commentField);

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment = commentField.getText().toString();
                        if (!comment.equals("")) {
                            adapter.pushComment(comment, displayForum);
                            CommonMethods.toastMessage(ForumDisplayActivity.this, "Posted Successfully");
                        } else {
                            CommonMethods.toastMessage(ForumDisplayActivity.this, "please enter a comment");
                        }
                    }
                });
                listUsers.setSelection(listUsers.getCount()-1);
                completeDatabaseReference.removeEventListener(listener);
                header.setText("Comments");
                dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        completeDatabaseReference.addValueEventListener(listener);

    }

    /**
     * Inflates the main view of the layout
     */
    private void initialize(){

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView sender = findViewById(R.id.senderName);
        TextView date = findViewById(R.id.date);
        TextView subject = findViewById(R.id.subject);
        TextView mainText = findViewById(R.id.mainText);
        TextView likeCountDisplay = findViewById(R.id.likeCountDisplay);
        TextView disLikeCountDisplay = findViewById(R.id.disLikeCountDisplay);
        TextView commentCountDisplay = findViewById(R.id.commentCountDisplay);
        ImageView profilePicture = findViewById(R.id.profilePicForum);


        if (displayForum.hasSenderPic())
            Picasso.with(this).load(displayForum.getSenderPic())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .resizeDimen(R.dimen.icon_2_size,R.dimen.icon_2_size)
                    .centerCrop()
                    .into(profilePicture);
        else
            Picasso.with(this).load(R.mipmap.ic_launcher_round)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(profilePicture);

        sender.setText(displayForum.getSender());
        date.setText(displayForum.getDate());
        subject.setText(displayForum.getSubject());
        mainText.setText(displayForum.getMainText());
        likeCountDisplay.setText(displayForum.getLikeCount());
        disLikeCountDisplay.setText(displayForum.getDislikeCount());
        commentCountDisplay.setText(displayForum.getCommentCount());



        Button likeButton = findViewById(R.id.likeButton);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayForum.pressLikeButton();
            }
        });



        Button disLikeButton = findViewById(R.id.dislikeButton);

        disLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayForum.pressDisLikeButton();
            }
        });

        likeCountDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewLikersAction();
            }
        });

        disLikeCountDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDislikersAction();
            }
        });

        View.OnClickListener commentViewListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentButtonPressed();
            }
        };
        
        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.dialog_view_profile);
                ImageView profileViewPic = dialog.findViewById(R.id.profileViewPic);
                TextView profileViewPhoneField = dialog.findViewById(R.id.profileViewPhoneField);
                TextView profileViewDateField = dialog.findViewById(R.id.profileViewDateField);
                TextView profileViewNameField = dialog.findViewById(R.id.profileViewNameField);
                TextView profileViewSectionField = dialog.findViewById(R.id.profileViewSectionField);

                profileViewNameField.setText(displayForum.getSender());
                profileViewPhoneField.setText("Phone no: " + displayForum.getSenderNo());
                profileViewDateField.setText("Birth Date: " + displayForum.getSenderDate());
                profileViewSectionField.setText("Section: " + displayForum.getSenderSection());

                if (displayForum.hasSenderPic())
                    Picasso.with(ForumDisplayActivity.this).load(displayForum.getSenderPic())
                            .placeholder(R.mipmap.ic_launcher_round)
                            .resizeDimen(R.dimen.icon_2_size,R.dimen.icon_2_size)
                            .centerCrop()
                            .into(profileViewPic);
                else
                    Picasso.with(ForumDisplayActivity.this).load(R.mipmap.ic_launcher_round)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .resizeDimen(R.dimen.icon_2_size,R.dimen.icon_2_size)
                            .centerCrop()
                            .into(profileViewPic);

                dialog.show();
            }
        };

        profilePicture.setOnClickListener(listener2);
        sender.setOnClickListener(listener2);

        commentCountDisplay.setOnClickListener(commentViewListner);
        findViewById(R.id.commentButton).setOnClickListener(commentViewListner);

        if (displayForum.isLiked())
            likeButton.setText("Unlike");
        else
            likeButton.setText("Like");
        if (displayForum.isDisLiked())
            disLikeButton.setText("Undislike");
        else
            disLikeButton.setText("Dislike");

        findViewById(R.id.commentButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentButtonPressed();
            }
        });
    }

    /**
     * Inflates the layout if the type of the forum is Image or text. uses activity_forum_display.xml
     */
    private void typeInflateImage(){

        ImageView forumImage = findViewById(R.id.forumImage);
        if (displayForum.hasFile())
        {
            Picasso.with(ForumDisplayActivity.this)
                    .load(displayForum.getFileUpload())
                    .placeholder(R.drawable.placeholder_forum_pic)
                    .resizeDimen(R.dimen.forum_image_size_width, R.dimen.forum_image_size_height)
                    .centerCrop()
                    .into(forumImage);
        }
        else {
            Picasso.with(ForumDisplayActivity.this).load("none").into(forumImage);
        }

        forumImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.dialog_view_forum_image);
                ImageView imageView = dialog.findViewById(R.id.forumImageView);
                Picasso.with(ForumDisplayActivity.this)
                        .load(displayForum.getFileUpload())
                        .placeholder(R.drawable.placeholder_forum_pic)
                        .into(imageView);
                dialog.show();
            }
        });
    }

    /**
     * Inflates the layout if the type of the forum is video. uses activity_forum_display_video.xml
     */
    private void typeInflateVideo(){
        videoStart = findViewById(R.id.videoStart);
        videoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.dialog_view_forum_video);
                VideoView videoView = dialog.findViewById(R.id.forumVideo);
                videoView.setVideoURI(Uri.parse(displayForum.getFileUpload()));

//                MediaController mediaController = new MediaController(ForumDisplayActivity.this);
//                videoView.setMediaController(mediaController);
//                mediaController.setAnchorView(videoView);

                dialog.show();
                videoView.start();
            }
        });


    }

}
