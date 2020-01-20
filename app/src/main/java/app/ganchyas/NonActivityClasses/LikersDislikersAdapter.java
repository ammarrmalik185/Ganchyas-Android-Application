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

public class LikersDislikersAdapter extends ArrayAdapter<String> {

    public LikersDislikersAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, R.layout.design_forum_text, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            listView = inflater.inflate(R.layout.design_users_list_simple, parent, false);
        } else {
            listView = convertView;
        }
        TextView nameView = listView.findViewById(R.id.nameField);
        nameView.setText(getItem(position));

        return listView;
    }
}
