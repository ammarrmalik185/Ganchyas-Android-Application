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
 * Dynamically generates the UI of a list of likes or dislikes
 * @author Paradox
 */
public class LikersDislikersAdapter extends ArrayAdapter<String> {

    /**
     * Calls the super constructor with design_users_list_simple.xml as resource
     * @param context Reference to the activity that is running
     * @param objects An array of strings that contain the the likers or dislikers
     */
    public LikersDislikersAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, R.layout.design_users_list_simple, objects);
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
