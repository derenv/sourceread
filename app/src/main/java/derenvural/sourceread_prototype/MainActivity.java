package derenvural.sourceread_prototype;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;

import derenvural.sourceread_prototype.data.asyncTasks.populateUserAsyncTask;
import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.data.login.LoggedInUser;
import derenvural.sourceread_prototype.data.storage.storageSaver;
import derenvural.sourceread_prototype.ui.app.AppFragment;
import derenvural.sourceread_prototype.ui.apps.redirectType;
import derenvural.sourceread_prototype.ui.home.menuStyle;
import derenvural.sourceread_prototype.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    // Android
    private AppBarConfiguration mAppBarConfiguration;
    private ProgressBar progressBar;
    private DrawerLayout drawer;
    private Menu mainMenu;
    // Services
    private FirebaseAuth mAuth;
    private fdatabase db;
    private httpHandler httph;
    // User
    public LoggedInUser user;
    // Variables
    private boolean interfaceEnabled;
    private menuStyle menustyle;
    private boolean appCallback;

    public httpHandler getHttpHandler() { return httph; }
    public fdatabase getDatabase() { return db; }
    public LoggedInUser getUser() { return user; }
    public void setUser(LoggedInUser user) { this.user = user; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menustyle = menuStyle.VISIBLE;

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Progress Bar
        progressBar = findViewById(R.id.loading_main);

        // Navigation
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_statistics, R.id.nav_apps,
                R.id.nav_settings, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        final Activity this_activity = this;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home: {
                        // Change to correct menu
                        menustyle = menuStyle.VISIBLE;

                        // Change fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_home);
                        break;
                    }
                    case R.id.nav_statistics: {
                        // Change to correct menu
                        menustyle = menuStyle.INVISIBLE;

                        // Change fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_statistics);
                        break;
                    }
                    case R.id.nav_apps: {
                        // Change to correct menu
                        menustyle = menuStyle.INVISIBLE;

                        // Change fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_apps);
                        break;
                    }
                    case R.id.nav_about: {
                        // Change to correct menu
                        menustyle = menuStyle.INVISIBLE;

                        // Change fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_about);
                        break;
                    }
                    case R.id.nav_settings: {
                        // Change to correct menu
                        menustyle = menuStyle.INVISIBLE;

                        // Change fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_settings);
                        break;
                    }
                }

                //close navigation drawer
                invalidateOptionsMenu();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Disable interface
        deactivate_interface();

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuth.getUid() == null){
            login_redirect();
        } else {
            // Create http object
            httph = new httpHandler(this);

            // Create database object
            db = new fdatabase();

            // handle app links
            handleIntent(getIntent());
        }
    }

    // Interface methods
    private void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;

        drawer.setDrawerLockMode(lockMode);
        interfaceEnabled = enabled;
    }
    public void deactivate_interface(){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        setDrawerEnabled(false);
    }
    public void activate_interface(){
        progressBar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        setDrawerEnabled(true);
    }

    // Redirects
    private void login_redirect(){
        // Redirect to login page
        Intent new_activity = new Intent(this, LoginActivity.class);
        new_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(new_activity);
        finish();
    }
    public void app_fragment_redirect(@NonNull App app, @NonNull redirectType type){
        // Create bundle with app
        Bundle appBundle = new Bundle();
        app.saveInstanceState(appBundle);
        appBundle.putSerializable("type",type);

        // Navigate to app details fragment
        Navigation.findNavController(this,R.id.nav_host_fragment).navigate(R.id.nav_app, appBundle);
        drawer.closeDrawers();
    }
    public void apps_fragment_redirect(){
        // Navigate to app choice fragment
        Navigation.findNavController(this,R.id.nav_host_fragment).navigate(R.id.nav_apps);
        drawer.closeDrawers();
    }
    public void choice_fragment_redirect(){
        // Navigate to app choice fragment
        Navigation.findNavController(this,R.id.nav_host_fragment).navigate(R.id.nav_appschoice);
        drawer.closeDrawers();
    }

    private void handleIntent(Intent intent) {
        // Get intent data
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();

        // Check if intent comes from deep-link or activity
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null){
            // Fetch deep-link type
            String link_type = appLinkData.getLastPathSegment();
            Log.d("ACTIVITY","value - "+appLinkData.toString()+"   type - " + link_type);

            // Whitelist deep-links
            if(link_type.equals("successful_login")){
                // Get app name
                String app_name = appLinkData.getPathSegments().get(appLinkData.getPathSegments().size() - 2);
                Log.d("ACTIVITY","app - "+app_name);

                // Create blank user for populating
                user = new LoggedInUser(mAuth.getCurrentUser());

                // Fetch user from local persistence
                if(storageSaver.read(this, mAuth.getCurrentUser().getUid(), user)){
                    // Request access tokens (interface reactivated on response)
                    user.access_tokens(this, httph, app_name);
                }else{
                    // TODO: show error
                }
            }else{
                // TODO: other deep links
            }
        }else{
            Log.d("ACTIVITY","no deep-link");

            // Fetch the bundle & check if it has extras
            String previous_activity = intent.getStringExtra("activity");
            Bundle extras = intent.getExtras();
            user = new LoggedInUser(mAuth.getCurrentUser());
            if (extras != null) {
                // Fetch serialised user
                user.loadInstanceState(extras);

                if(previous_activity.equals("login")) {
                    populate(user);
                }else if(previous_activity.equals("article")) {
                    // Reactivate interface & disable worm
                    activate_interface();
                }
            }else {
                // Attempt population
                populate(user);
            }
        }
    }

    //Population Methods
    public void populate(final LoggedInUser user) {
        // Create async task
        final populateUserAsyncTask task = new populateUserAsyncTask(this, db, httph);
        final MainActivity main = this;

        // execute async task
        task.execute(user);

        // Check for task finish
        task.getDone().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    setUser(task.getData().getValue());

                    if(getUser().getApps() != null && getUser().getApps().getValue() != null && getUser().getApps().getValue().size() > 0) {
                        for (App app : getUser().getApps().getValue()) {

                            // Open app in browser for authentication Creates callback
                            // Get login URL
                            HashMap<String, String> requests = app.getRequests();
                            String app_login_url = requests.get("login");

                            // Insert request token
                            String url = app_login_url.replaceAll("REPLACEME", app.getRequestToken());

                            // Store this object using local persistence
                            if (storageSaver.write(main, getUser().getUserId().getValue(), getUser())) {
                                // Redirect to browser for app login
                                httph.browser_open(main, url);
                            } else {
                                Log.e("HTTP", "login url request failure");
                            }
                        }
                    }else{
                        activate_interface();
                    }
                    Log.d("TASK", "user data population task done!");
                }
            }
        });
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;
        if(menustyle == menuStyle.INVISIBLE){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.nonhome, mainMenu);

            return true;
        }else if(menustyle == menuStyle.VISIBLE){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, mainMenu);

            return true;
        }

        return false;
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(interfaceEnabled){
            switch (item.getItemId()) {
                case R.id.action_import_articles:
                    if(getUser().getApps() != null && getUser().getApps().getValue() != null && getUser().getApps().getValue().size() > 0) {
                        // loading worm and "importing.."
                        deactivate_interface();

                        // Import articles from user accounts
                        for(App app: user.getApps().getValue()) {
                            user.importArticles(this, httph, db, app);
                        }
                    }else{
                        Toast.makeText(this, "No apps to import from..", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                case R.id.action_refresh_apps:
                    if(getUser().getApps() != null && getUser().getApps().getValue() != null && getUser().getApps().getValue().size() > 0) {
                        // TODO: refresh apps using authenticate functions
                        Toast.makeText(this, "Refreshing apps..", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "No apps to refresh..", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                case R.id.action_logout_user:
                    Toast.makeText(this, "Logging out..", Toast.LENGTH_SHORT).show();
                    logout();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }else{
            return false;
        }
    }

    public void setAppCallback(boolean appCallback){this.appCallback = appCallback;}

    @Override
    public boolean onSupportNavigateUp() {
        // Fetch fragment
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        int fragmentID = navController.getCurrentDestination().getId();
        if(fragmentID == R.id.nav_app && appCallback){
            apps_fragment_redirect();
            return true;
        }else {
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        }
    }

    private void logout(){
        // Sign out Firebase user
        mAuth.signOut();

        // Check for successful sign out
        if(null == mAuth.getCurrentUser()){
            Toast.makeText(getApplicationContext(), "Successful Log out", Toast.LENGTH_SHORT).show();

            // Remove objects
            db = null;
            httph = null;
            user = null;

            // Start login activity
            Intent new_activity = new Intent(this, LoginActivity.class);
            new_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(new_activity);
        }else {
            Toast.makeText(getApplicationContext(), "Log out failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
