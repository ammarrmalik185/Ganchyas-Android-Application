package app.ganchyas.NonActivityClasses;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.ganchyas.ForumDisplayActivity;
import app.ganchyas.R;

/**
 * Generates UI of a list of forums dynamically
 * @author Paradox
 */
public class ForumListAdapter extends ArrayAdapter<ForumPack> {

    /**
     * A dialog used to show likes, dislikes, comments and user info
     */
    private Dialog dialog;
    /**
     * A value listener  for the database
     */
    private ValueEventListener listener;
    /**
     * Dynamically generates the UI of comments
     */
    private CommentsAdapter commentsAdapter;
    /**
     * On click listener for the forum to open it in ForumDisplayActivity
     */
    private View.OnClickListener goToDedicatedViewListner;

    /**
     * Calls the super constructor with design_forum_text.xml as resource id
     * @param context The instance of the activity running
     * @param objects A list of Forums that need to be displayed
     */
    public ForumListAdapter(@NonNull Context context, @NonNull List<ForumPack> objects) {
        super(context, R.layout.design_forum_text, objects);
    }

    /**
     * Inflates the UI of a single list entity
     * @param position The position of the entity in the list to be inflated
     * @param convertView Inflated view of the previous list entity (if any)
     * @param parent The parent View that the view needs to be inflated into
     * @return The inflated UI of the list entity at the position given
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        switch (getItem(position).getType()) {
            case "text":
            case "image":
                return inflateTextForum(position, parent);
            case "video":
                return inflateVideoForum(position, parent);
            case "file":
                return inflateFileForum(position, parent);
        }
        return inflateTextForum(position, parent);

    }

    /**
     * Updates the list with the new entries
     */
    private void update() {
        this.notifyDataSetChanged();
    }

    /**
     * Called when the like button is pressed on a forum
     * @param position Position of the forum in the array that was liked
     */
    private void likeButtonPressed(int position) {
        ForumPack currentForum = getItem(position);
        assert currentForum != null;
        currentForum.pressLikeButton();
        update();
    }

    /**
     * Called when the dislike button is pressed on a forum
     * @param position Position of the forum in the array that was disliked
     */
    private void disLikeButtonPressed(int position) {
        ForumPack currentForum = getItem(position);
        assert currentForum != null;
        currentForum.pressDisLikeButton();
        update();

    }

    /**
     * Shows a list of likes for the forum
     * @param position Position of the forum in the array that needs to display its likes
     */
    private void viewLikersAction(int position) {
        final DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        ForumPack currentForum = getItem(position);
        assert currentForum != null;
        final String forumId = currentForum.getForumId();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> likersArray = new ArrayList<>();

                if (dataSnapshot.child("forumData").child(forumId).child("likers").exists()) {
                    DataSnapshot likers = dataSnapshot.child("forumData").child(forumId).child("likers");
                    DataSnapshot users = dataSnapshot.child("userdata");
                    for (DataSnapshot likerSnap : likers.getChildren()) {
                        if ((boolean)likerSnap.getValue())
                        likersArray.add(users.child(likerSnap.getKey()).child("name").getValue().toString());
                    }
                }
                myDb.removeEventListener(listener);
                dialog.setContentView(R.layout.dialog_title_and_list);
                ListView listUsers = dialog.findViewById(R.id.listUsers);
                listUsers.setAdapter(new LikersDislikersAdapter(getContext(), likersArray));
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
     * Shows a list of dislikes for the forum
     * @param position Position of the forum in the array that needs to display its dislikes
     */
    private void viewDislikersAction(int position) {
        final DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        ForumPack currentForum = getItem(position);
        assert currentForum != null;
        final String forumId = currentForum.getForumId();
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
                listUsers.setAdapter(new LikersDislikersAdapter(getContext(), dislikersArray));
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
     * Opens the comment dialog that allows the user to post a comment
     * @param position Position of the forum in the array
     */
    private void commentButtonPressed(final int position) {
        final DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        dialog.setContentView(R.layout.dialog_comments);
        dialog.show();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ForumPack fp = getItem(position);
                fp.refreshComments(dataSnapshot);
                ArrayList<CommentPack> commentPacks = fp.getCommentPacks();
                ListView listUsers = dialog.findViewById(R.id.listUsers);
                Collections.reverse(commentPacks);
                commentsAdapter = new CommentsAdapter(getContext(), commentPacks);
                listUsers.setAdapter(commentsAdapter);
                TextView header = dialog.findViewById(R.id.header);
                Button submitButton = dialog.findViewById(R.id.submitButton);
                final EditText commentField = dialog.findViewById(R.id.commentField);

                listUsers.setSelection(listUsers.getCount()-1);

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment = commentField.getText().toString();
                        if (!comment.equals("")) {
                            pushComment(comment, position);
                            CommonMethods.toastMessage(getContext(), "Posted Successfully");
                            commentButtonPressed(position);
                        } else {
                            CommonMethods.toastMessage(getContext(), "please enter a comment");
                        }
                    }
                });
                header.setText("Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myDb.addValueEventListener(listener);

    }

    /**
     * Adds comment in the database
     * @param comment The text of the comment
     * @param position The position of the forum that the comment needs to be added to
     */
    private void pushComment(String comment, int position) {
        ForumPack forumPack = getItem(position);
        assert forumPack != null;
        commentsAdapter.pushComment(comment, forumPack);
    }

    /**
     * Inflates the forum as a Text/Image forum
     * @param position The position of the forum that needs to be inflated
     * @param parent The view that the forum needs to be attached to
     * @return The inflated view of the forum
     */
    private View inflateTextForum(final int position, @NonNull ViewGroup parent){


        LayoutInflater inflater = LayoutInflater.from(getContext());
        View forumView = inflater.inflate(R.layout.design_forum_text, parent, false);


        commonInflate(position, forumView);

        final ForumPack currentForum = getItem(position);

        ImageView forumImage = forumView.findViewById(R.id.forumImage);

        assert currentForum != null;


        if (currentForum.hasFile())
        {
            Picasso.with(getContext())
                    .load(currentForum.getFileUpload())
                    .placeholder(R.drawable.placeholder_forum_pic)
                    .resizeDimen(R.dimen.forum_image_size_width, R.dimen.forum_image_size_height)
                    .centerCrop()
                    .into(forumImage);
        }
        else {
            Picasso.with(getContext()).load("none").into(forumImage);
        }

        forumImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.dialog_view_forum_image);
                ImageView imageView = dialog.findViewById(R.id.forumImageView);
                Picasso.with(getContext())
                        .load(getItem(position).getFileUpload())
                        .placeholder(R.drawable.placeholder_forum_pic)
                        .into(imageView);
                dialog.show();
            }
        });

        return forumView;
    }

    /**
     * Inflates the forum as a Video forum
     * @param position The position of the forum that needs to be inflated
     * @param parent The view that the forum needs to be attached to
     * @return The inflated view of the forum
     */
    private View inflateVideoForum(final int position, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View forumView = inflater.inflate(R.layout.design_forum_video, parent, false);
        commonInflate(position, forumView);

        View videoStart = forumView.findViewById(R.id.videoStart);

        videoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.dialog_view_forum_video);
                VideoView videoView = dialog.findViewById(R.id.forumVideo);
                videoView.setVideoURI(Uri.parse(getItem(position).getFileUpload()));

                dialog.show();
                videoView.start();
            }
        });

        return forumView;
    }

    /**
     * Inflates the forum as a File forum
     * @param position The position of the forum that needs to be inflated
     * @param parent The view that the forum needs to be attached to
     * @return The inflated view of the forum
     */
    private View inflateFileForum(final int position, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View forumView = inflater.inflate(R.layout.design_forum_file, parent, false);
        commonInflate(position, forumView);

        View downloadFileButton = forumView.findViewById(R.id.forumDownloadFile);
        TextView fileNameView = forumView.findViewById(R.id.forumFileName);

        fileNameView.setText(getItem(position).getFileDisplayName());
        downloadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getItem(position).getFileUpload()));
                getContext().startActivity(browserIntent);
            }
        });

        return forumView;
    }

    /**
     * Inflates the general elements of the forum
     * @param position The position of the forum that needs to be inflated
     * @param forumView The view that the forum needs to be inflated into
     */
    private void commonInflate(final int position, @Nullable View forumView){

        dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final ForumPack currentForum = getItem(position);
        TextView sender = forumView.findViewById(R.id.senderName);
        TextView date = forumView.findViewById(R.id.date);
        TextView subject = forumView.findViewById(R.id.subject);
        TextView mainText = forumView.findViewById(R.id.mainText);
        TextView likeCountDisplay = forumView.findViewById(R.id.likeCountDisplay);
        TextView disLikeCountDisplay = forumView.findViewById(R.id.disLikeCountDisplay);
        TextView commentCountDisplay = forumView.findViewById(R.id.commentCountDisplay);
        ImageView profilePicture = forumView.findViewById(R.id.profilePicForum);


        if (currentForum.hasSenderPic())
            Picasso.with(getContext()).load(currentForum.getSenderPic())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .resizeDimen(R.dimen.icon_2_size,R.dimen.icon_2_size)
                    .centerCrop()
                    .into(profilePicture);
        else
            Picasso.with(getContext()).load(R.mipmap.ic_launcher_round)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(profilePicture);

        sender.setText(currentForum.getSender());
        date.setText(currentForum.getDate());
        subject.setText(currentForum.getSubject());
        mainText.setText(currentForum.getMainText());
        likeCountDisplay.setText(currentForum.getLikeCount());
        disLikeCountDisplay.setText(currentForum.getDislikeCount());
        commentCountDisplay.setText(currentForum.getCommentCount());
        Button likeButton = forumView.findViewById(R.id.likeButton);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeButtonPressed(position);
            }
        });

        Button disLikeButton = forumView.findViewById(R.id.dislikeButton);

        disLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disLikeButtonPressed(position);
            }
        });

        likeCountDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewLikersAction(position);
            }
        });

        disLikeCountDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDislikersAction(position);
            }
        });

        View.OnClickListener commentViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentButtonPressed(position);
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

                profileViewNameField.setText(currentForum.getSender());
                profileViewPhoneField.setText("Phone no: " + currentForum.getSenderNo());
                profileViewDateField.setText("Birth Date: " + currentForum.getSenderDate());
                profileViewSectionField.setText("Section: " + currentForum.getSenderSection());

                if (currentForum.hasSenderPic())
                    Picasso.with(getContext()).load(currentForum.getSenderPic())
                            .placeholder(R.mipmap.ic_launcher_round)
                            .resizeDimen(R.dimen.icon_2_size,R.dimen.icon_2_size)
                            .centerCrop()
                            .into(profileViewPic);
                else
                    Picasso.with(getContext()).load(R.mipmap.ic_launcher_round)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .resizeDimen(R.dimen.icon_2_size,R.dimen.icon_2_size)
                            .centerCrop()
                            .into(profileViewPic);

                dialog.show();
            }
        };

        profilePicture.setOnClickListener(listener2);
        sender.setOnClickListener(listener2);

        goToDedicatedViewListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ForumDisplayActivity.class);
                i.putExtra("forumId", getItem(position).getForumId());
                getContext().startActivity(i);
            }
        };

        subject.setOnClickListener(goToDedicatedViewListner);
        mainText.setOnClickListener(goToDedicatedViewListner);


        commentCountDisplay.setOnClickListener(commentViewListener);
        forumView.findViewById(R.id.commentButton).setOnClickListener(commentViewListener);

        if (currentForum.isLiked())
            likeButton.setText("Unlike");
        else
            likeButton.setText("Like");
        if (currentForum.isDisLiked())
            disLikeButton.setText("Undislike");
        else
            disLikeButton.setText("Dislike");

        forumView.findViewById(R.id.commentButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentButtonPressed(position);
            }
        });

    }



}
