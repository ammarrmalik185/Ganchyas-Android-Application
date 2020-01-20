package app.ganchyas.NonActivityClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.ganchyas.MessengerActivity;
import app.ganchyas.R;

/**
 * Dynamically generates UI of Conversations that the user is a part of (already started)
 * @author Paradox
 */
public class ConversationListAdapter extends ArrayAdapter<ConversationPack> {

    /**
     * Constructor that calls super constructor with the given arguments and design_user_list.xml as
     * the default UI resource
     * @param context The Activity that is running
     * @param objects An array of CommentPack that need to be displayed
     */
    public ConversationListAdapter(@NonNull Context context, @NonNull List<ConversationPack> objects) {
        super(context, R.layout.design_user_list, objects);
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
        View userView;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            userView = inflater.inflate(R.layout.design_user_list, parent, false);
        } else {
            userView = convertView;
        }

        ConversationPack conversationPack = getItem(position);

        TextView nameView = userView.findViewById(R.id.senderNameUserList);

        nameView.setText(conversationPack.getOtherUser().getName());

        ImageView profilePicture = userView.findViewById(R.id.profilePicUserList);

        if (conversationPack.getOtherUser().hasImage())
            Picasso.with(getContext())
                    .load(conversationPack.getOtherUser().getImageUri())
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(profilePicture);
        else
            Picasso.with(getContext()).load(R.mipmap.ic_launcher_round)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(profilePicture);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessengerActivity.class);
                intent.putExtra("userId", getItem(position).getOtherUser().getUserId());
                intent.putExtra("conversationId", getItem(position).getConversationId());
                intent.putExtra("newConversation", false);
                getContext().startActivity(intent);
            }
        };

        profilePicture.setOnClickListener(listener);
        nameView.setOnClickListener(listener);

        return userView;
    }

}
