package app.ganchyas.NonActivityClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import app.ganchyas.ForumDisplayActivity;
import app.ganchyas.R;
/**
 * @author Paradox;
 */
public class NotificationsAdapter extends ArrayAdapter<Notification> {



    public NotificationsAdapter(@NonNull Context context, @NonNull List<Notification> objects) {
        super(context, R.layout.design_single_notification,objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View notificationView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            notificationView = inflater.inflate(R.layout.design_single_notification, parent, false);
        } else {
            notificationView = convertView;
        }

        TextView title = notificationView.findViewById(R.id.title_single_notification);
        TextView text = notificationView.findViewById(R.id.text_single_notification);
        TextView time = notificationView.findViewById(R.id.time_single_notification);
        TextView date = notificationView.findViewById(R.id.date_single_notification);
        LinearLayout singleNotification = notificationView.findViewById(R.id.singleNotification);


        title.setText(getItem(position).getTitle());
        text.setText(getItem(position).getText());
        time.setText(getItem(position).getTime());
        date.setText(getItem(position).getDate());
        singleNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ForumDisplayActivity.class);
                i.putExtra("forumId", getItem(position).getForumId());
                getContext().startActivity(i);
            }
        });
        return notificationView;
    }
}
