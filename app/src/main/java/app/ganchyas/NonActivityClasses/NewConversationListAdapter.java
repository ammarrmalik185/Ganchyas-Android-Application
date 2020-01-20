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

import java.util.ArrayList;

import app.ganchyas.MessengerActivity;
import app.ganchyas.R;

/**
 * Dynamically generated the UI of the conversations that the user has using UserDataPacks
 * @author Paradox;
 */

public class NewConversationListAdapter extends ArrayAdapter<UserDataPack> {

    public NewConversationListAdapter(@NonNull Context context, ArrayList<UserDataPack> users) {
        super(context, R.layout.design_user_list , users);
    }

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

        UserDataPack userData = getItem(position);

        TextView nameView = userView.findViewById(R.id.senderNameUserList);

        nameView.setText(userData.getName());

        ImageView profilePicture = userView.findViewById(R.id.profilePicUserList);

        if (userData.hasImage())
            Picasso.with(getContext())
                    .load(userData.getImageUri())
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
                intent.putExtra("userId", getItem(position).getUserId());
                intent.putExtra("conversationId", "null");
                intent.putExtra("newConversation", true);
                getContext().startActivity(intent);
            }
        };

        profilePicture.setOnClickListener(listener);
        nameView.setOnClickListener(listener);

        return userView;
    }
}
