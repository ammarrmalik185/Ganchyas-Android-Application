package app.ganchyas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import app.ganchyas.NonActivityClasses.CommonMethods;
import app.ganchyas.NonActivityClasses.NavViewPagerAdapter;

/**
 * Container that can cycle through the fragments. Contains the bottom navigation bar as well as a navigation drawer.
 * called "MainActivity" because all other application functions can be accessed through it
 * @author Paradox
 */

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    /**
     * Controls the fragments to be displayed
     */
    ViewPager viewPager;
    /**
     * Reference to the bottom navigation to switch between the fragments
     */
    BottomNavigationView bottomNavigationView;
    /**
     * Reference to the textView that shows the user's name on the drawer
     */
    TextView nameView;
    /**
     * Reference to the textView that shows the user's email on the drawer
     */
    TextView emailView;
    /**
     * Reference to the imageView that shows the user's profile picture on the drawer
     */
    ImageView profilePic;
    /**
     * Reference to the drawer on the main activity
     */
    private DrawerLayout drawerLayout;
    /**
     * Listener for the bottom navigation to cycle through the fragments
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_forums:
                            viewPager.setCurrentItem(0);
                            break;
                        case R.id.nav_message:
                            viewPager.setCurrentItem(1);
                            break;
                        case R.id.nav_location:
                            viewPager.setCurrentItem(2);
                            break;
                        case R.id.nav_notifications:
                            viewPager.setCurrentItem(3);
                            break;
                    }
                    return true; }};

    /**
     * View pager listener to cycle through fragments when the user swipes
     */
    private ViewPager.OnPageChangeListener page_listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0)
                bottomNavigationView.setSelectedItemId(R.id.nav_forums);
            else if (position == 1)
                bottomNavigationView.setSelectedItemId(R.id.nav_message);
            else if (position == 2)
                bottomNavigationView.setSelectedItemId(R.id.nav_location);
            else if (position == 3)
                bottomNavigationView.setSelectedItemId(R.id.nav_notifications);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * Inflates the UI using activity_main.xml.
     * @param savedInstanceState Previous instance of the UI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseMessaging.getInstance().subscribeToTopic("newForum");
        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getInstance().getUid());

        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.fragment_display);
        bottomNavigationView = findViewById(R.id.nav_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        FragmentPagerAdapter fragmentPagerAdapter = new NavViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setCurrentItem(0);
        bottomNavigationView.setSelectedItemId(R.id.nav_forums);
        viewPager.addOnPageChangeListener(page_listener);

        String s = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference myDb = FirebaseDatabase.getInstance().getReference();
        myDb.child("deviceTokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(s);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.drawer_view);
        nameView = navigationView.getHeaderView(0).findViewById(R.id.nameView);
        emailView = navigationView.getHeaderView(0).findViewById(R.id.emailView);
        profilePic = navigationView.getHeaderView(0).findViewById(R.id.profilePic);

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot ref = dataSnapshot.child("userdata").child(FirebaseAuth.getInstance().
                        getCurrentUser().getUid());
                nameView.setText(ref.child("name").getValue().toString());
                emailView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                if (ref.child("profile picture").exists())
                    Picasso.with(MainActivity.this)
                            .load(ref.child("profile picture").getValue().toString())
                            .resizeDimen(R.dimen.icon_1_size, R.dimen.icon_1_size)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .centerCrop().into(profilePic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.account:
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.settings:
                        Intent intent3 = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.patches:
                        Intent intent4 = new Intent(MainActivity.this, PatchesActivity.class);
                        startActivity(intent4);
                        break;
                }
                return true;
            }
        });

    }

    /**
     * Overwriting on back pressed to include drawer and double back press to exit
     */
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                android.os.Process.killProcess(android.os.Process.myPid());
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            CommonMethods.toastMessage(MainActivity.this, "Press back again to exit");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
        }

}

