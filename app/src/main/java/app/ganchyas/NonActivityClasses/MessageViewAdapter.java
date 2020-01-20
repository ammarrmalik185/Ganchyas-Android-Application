package app.ganchyas.NonActivityClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import app.ganchyas.R;

/**
 * @author Paradox;
 */

public class MessageViewAdapter extends ArrayAdapter<MessagePack> {

    public MessageViewAdapter(@NonNull Context context, @NonNull List<MessagePack> objects) {
        super(context, R.layout.design_message_sent, objects);
    }

    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View messageView;
        boolean sent = getItem(position).sent();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if(sent)
            messageView = inflater.inflate(R.layout.design_message_sent, parent, false);
        else
            messageView = inflater.inflate(R.layout.design_message_received, parent, false);


        TextView messageText = messageView.findViewById(R.id.messageText);
        TextView messageTime = messageView.findViewById(R.id.messageTime);
        messageText.setText(getItem(position).getMessage());
        messageTime.setText(getItem(position).getTime() + " on " + getItem(position).getDate());

        return messageView;
    }
}
