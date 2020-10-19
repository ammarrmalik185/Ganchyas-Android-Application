package app.ganchyas;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays a map, On the map you can enable your live location and can see other user's current location
 * live location (if they ave enabled id)
 * @author Paradox
 */

public class LocationFragment extends Fragment  implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "AIzaSyCn4KOeRTcuXEawQDFcp4IL9kXMveS3-5Q";
    /**
     * A button that can be used to toggle your current location.
     */
    private FloatingActionButton floatingActionButton;
    /**
     * Stores whether the user has turned on the location or not
     */
    private boolean locationOn;

    private MapView mMapView;

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
        mMapView = view.findViewById(R.id.mapView);
        initGoogleMap(savedInstanceState);
        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        mMapView.onCreate(null);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        checkLocationPermissions();
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void checkLocationPermissions() {

        int permissionCheck = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        }
        if (permissionCheck != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }

        }
    }
}
