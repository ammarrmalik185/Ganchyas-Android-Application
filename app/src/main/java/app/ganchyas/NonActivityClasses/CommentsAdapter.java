package app.ganchyas.NonActivityClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.ganchyas.R;

/**
 * Adapter class that dynamically generated the UI of a list of comments.
 * @author Paradox
 */
public class CommentsAdapter extends ArrayAdapter<CommentPack> {

    /**
     * Constructor that calls super constructor with the given arguments and design_comments.xml as
     * the default UI resource
     * @param context The Activity that is running
     * @param objects An array of CommentPack that need to be displayed
     */
    public CommentsAdapter(@NonNull Context context, @NonNull List<CommentPack> objects) {
        super(context, R.layout.design_comments, objects);
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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View commentView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            commentView = inflater.inflate(R.layout.design_comments, parent, false);
        } else {
            commentView = convertView;
        }
        TextView nameField = commentView.findViewById(R.id.nameField);
        nameField.setText(getItem(position).getSender());
        TextView dateField = commentView.findViewById(R.id.dateField);
        dateField.setText(getItem(position).getDate());
        TextView commentField = commentView.findViewById(R.id.commentField);
        commentField.setText(getItem(position).getText());

        return commentView;
    }

    /**
     * Adds the comment to the database as well as local data
     * @param comment The text to be added as comment
     * @param forumPack The forum pack the comment needs to be added to
     */
    public void pushComment(String comment, ForumPack forumPack) {
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        Long format = Long.parseLong(s.format(new Date()));
        s = new SimpleDateFormat("dd/MM/yyyy");
        String date = s.format(new Date());
        String identifier = "comment id : " + (50000000000000L - format);
        String sender = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference commentDb = myDb.child("forumData").child(forumPack.getForumId()).child("comments").child(identifier);
        HashMap<String,String> map = new HashMap<>();
        map.put("sender", sender);
        map.put("date", date);
        map.put("text", comment);
        commentDb.setValue(map);

        notifyDataSetChanged();

    }

}
