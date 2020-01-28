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
import android.widget.Toast;
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
 * @author Paradox;
 */

public class ForumListAdapter extends ArrayAdapter<ForumPack> {

    private Dialog mDialog;
    private ValueEventListener listner;
    private CommentsAdapter adapter;
    private View.OnClickListener listener3;
    private VideoView videoView;

    public ForumListAdapter(@NonNull Context context, @NonNull List<ForumPack> objects) {
        super(context, R.layout.design_forum_text, objects);
    }

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

    private void update() {
        this.notifyDataSetChanged();
    }

    private void likeButtonPressed(int position) {
        ForumPack currentForum = getItem(position);
        assert currentForum != null;
        currentForum.pressLikeButton();
        update();
    }

    private void disLikeButtonPressed(int position) {
        ForumPack currentForum = getItem(position);
        assert currentForum != null;
        currentForum.pressDisLikeButton();
        update();

    }

    private void viewLikersAction(int position) {
        final DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        ForumPack currentForum = getItem(position);
        assert currentForum != null;
        final String forumId = currentForum.getForumId();
        listner = new ValueEventListener() {
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
                myDb.removeEventListener(listner);
                mDialog.setContentView(R.layout.dialog_title_and_list);
                ListView listUsers = mDialog.findViewById(R.id.listUsers);
                listUsers.setAdapter(new LikersDislikersAdapter(getContext(), likersArray));
                TextView header = mDialog.findViewById(R.id.header);
                header.setText("Likes");

                mDialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myDb.addValueEventListener(listner);
    }

    private void viewDislikersAction(int position) {
        final DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        ForumPack currentForum = getItem(position);
        assert currentForum != null;
        final String forumId = currentForum.getForumId();
        listner = new ValueEventListener() {
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
                myDb.removeEventListener(listner);
                mDialog.setContentView(R.layout.dialog_title_and_list);
                ListView listUsers = mDialog.findViewById(R.id.listUsers);
                listUsers.setAdapter(new LikersDislikersAdapter(getContext(), dislikersArray));
                TextView header = mDialog.findViewById(R.id.header);
                header.setText("Dislikes");
                mDialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myDb.addValueEventListener(listner);
    }

    private void commentButtonPressed(final int position) {
        final DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        mDialog.setContentView(R.layout.dialog_comments);
        mDialog.show();
        listner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ForumPack fp = getItem(position);
                fp.refreshComments(dataSnapshot);
                ArrayList<CommentPack> commentPacks = fp.getCommentPacks();
                ListView listUsers = mDialog.findViewById(R.id.listUsers);
                Collections.reverse(commentPacks);
                adapter = new CommentsAdapter(getContext(), commentPacks);
                listUsers.setAdapter(adapter);
                TextView header = mDialog.findViewById(R.id.header);
                Button submitButton = mDialog.findViewById(R.id.submitButton);
                final EditText commentField = mDialog.findViewById(R.id.commentField);

                listUsers.setSelection(listUsers.getCount()-1);

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment = commentField.getText().toString();
                        if (!comment.equals("")) {
                            pushComment(comment, position);
                            toastMessage("Posted Successfully");
                            commentButtonPressed(position);
                        } else {
                            toastMessage("please enter a comment");
                        }
                    }
                });
                header.setText("Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myDb.addValueEventListener(listner);

    }

    private void toastMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void pushComment(String comment, int position) {
        ForumPack forumPack = getItem(position);
        adapter.pushComment(comment, forumPack);
    }

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

        forumImage.setOnClickListener(listener3);
        return forumView;
    }

    private View inflateVideoForum(final int position, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View forumView = inflater.inflate(R.layout.design_forum_video, parent, false);
        commonInflate(position, forumView);

        View videoStart = forumView.findViewById(R.id.videoStart);

        videoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setContentView(R.layout.dialog_view_forum_video);
                VideoView videoView = mDialog.findViewById(R.id.forumVideo);
                videoView.setVideoURI(Uri.parse(getItem(position).getFileUpload()));

                mDialog.show();
                videoView.start();
            }
        });


//        final MediaController mediaController = new MediaController(getContext());
//        videoView.setMediaController(mediaController);
//        mediaController.setAnchorView(videoView);
//
//        mediaController.hide()

        return forumView;
    }

    private View inflateFileForum(int position, ViewGroup parent) {
        return inflateTextForum(position, parent);
    }

    private void commonInflate(final int position, @Nullable View forumView){

        mDialog = new Dialog(getContext());
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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

        View.OnClickListener commentViewListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentButtonPressed(position);
            }
        };

        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setContentView(R.layout.dialog_view_profile);
                ImageView profileViewPic = mDialog.findViewById(R.id.profileViewPic);
                TextView profileViewPhoneField = mDialog.findViewById(R.id.profileViewPhoneField);
                TextView profileViewDateField = mDialog.findViewById(R.id.profileViewDateField);
                TextView profileViewNameField = mDialog.findViewById(R.id.profileViewNameField);
                TextView profileViewSectionField = mDialog.findViewById(R.id.profileViewSectionField);

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

                mDialog.show();
            }
        };

        profilePicture.setOnClickListener(listener2);
        sender.setOnClickListener(listener2);

        listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ForumDisplayActivity.class);
                i.putExtra("forumId", getItem(position).getForumId());
                getContext().startActivity(i);
            }
        };

        subject.setOnClickListener(listener3);
        mainText.setOnClickListener(listener3);


        commentCountDisplay.setOnClickListener(commentViewListner);
        forumView.findViewById(R.id.commentButton).setOnClickListener(commentViewListner);

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
