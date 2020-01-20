package app.ganchyas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A Fragment that displays a map. On the map you can enable your live location and can see other user's
 * live location (if they ave enabled id)
 * @author Paradox
 */

public class LocationFragment extends Fragment {

    /**
     * A button that can be used to toggle your current location.
     */
    private FloatingActionButton floatingActionButton;
    /**
     * Stores whether the user has turned on the location or not
     */
    private boolean locationOn;

    /**
     * Empty Constructor
     */
    public LocationFragment() {
        // Required empty public constructor
    }

    /**
     * Generates the UI of the Fragment
     * @param inflater An Inflater object to inflate the layout
     * @param container A parent view where the UI will be placed
     * @param savedInstanceState An older instance of the fragment (if any)
     * @return The inflated UI of the Fragment Instance
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        locationOn = true;
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationOn) {
                    floatingActionButton.setImageResource(R.drawable.action_location_off_24dp);
                    locationOn = false;
                }
                else {
                    floatingActionButton.setImageResource(R.drawable.action_location_on_24dp);
                    locationOn = true;
                }
            }
        });
        return view;
    }

}
