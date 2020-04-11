package derenvural.sourceread_prototype;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;

import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;

import java.util.HashMap;

import derenvural.sourceread_prototype.data.asyncTasks.populateUserAsyncTask;
import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.dialog.SourceReadDialog;
import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.data.login.LoggedInUser;
import derenvural.sourceread_prototype.data.storage.storageSaver;
import derenvural.sourceread_prototype.ui.home.menuStyle;

public class MainActivity extends SourceReadActivity {
    // Variables
    private boolean appCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set menu layout style
        setMenuStyle(menuStyle.MAIN);
        appCallback = false;

        // Set help dialog content
        setHelp(R.string.help_home);

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
                        setMenuStyle(menuStyle.MAIN);

                        // Set help dialog content
                        setHelp(R.string.help_home);

                        // Change fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_home);
                        break;
                    }
                    case R.id.nav_statistics: {
                        // Change to correct menu
                        setMenuStyle(menuStyle.OUTER);

                        // Set help dialog content
                        setHelp(R.string.help_statistics);

                        // Change fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_statistics);
                        break;
                    }
                    case R.id.nav_apps: {
                        // Change to correct menu
                        setMenuStyle(menuStyle.OUTER);

                        // Set help dialog content
                        setHelp(R.string.help_apps);

                        // Change fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_apps);
                        break;
                    }
                    case R.id.nav_about: {
                        // Change to correct menu
                        setMenuStyle(menuStyle.OUTER);

                        // Set help dialog content
                        setHelp(R.string.help_about);

                        // Change fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_about);
                        break;
                    }
                    case R.id.nav_settings: {
                        // Change to correct menu
                        setMenuStyle(menuStyle.SETTINGS);

                        // Set help dialog content
                        setHelp(R.string.help_settings);

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
            setHttpHandler(new httpHandler(this));

            // Create database object
            setDatabase(new fdatabase());

            // handle app links
            handleIntent(getIntent());
        }
    }

    private void handleIntent(Intent intent) {
        // Get intent data
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();

        // Check if intent comes from deep-link or activity
        if ((Intent.ACTION_MAIN.equals(appLinkAction) || Intent.ACTION_VIEW.equals(appLinkAction)) && appLinkData != null){
            // Fetch deep-link type
            String link_type = appLinkData.getLastPathSegment();

            // Whitelist deep-links
            if(link_type != null && (link_type.equals("successful_login") || link_type.equals("successful_auth"))){
                // Get app name
                String app_name = appLinkData.getPathSegments().get(appLinkData.getPathSegments().size() - 2);

                // Create blank user for populating
                setUser(new LoggedInUser(mAuth.getCurrentUser()));

                // Fetch user from local persistence
                if(storageSaver.read(this, mAuth.getCurrentUser().getUid(), user)){
                    // Request access tokens (interface reactivated on response)
                    user.access_tokens(this, getHttpHandler(), app_name);

                    // Return to correct page
                    if(link_type.equals("successful_login")) {
                        // Set help dialog text
                        setHelp(R.string.help_apps);

                        // Redirect to apps choice
                        fragment_redirect(R.id.nav_apps, new Bundle());
                    }
                }else{
                    // Show error
                    Toast.makeText(this, "Storage error, attempt login..",Toast.LENGTH_SHORT).show();

                    // Redirect to login due to persistent storage failure
                    login_redirect();
                }
            }else{
                // TODO: other deep links
            }
        }else{
            // No deep link!
            // Fetch the bundle & check if it has extras
            String previous_activity = intent.getStringExtra("activity");
            Bundle extras = intent.getExtras();
            setUser(new LoggedInUser(mAuth.getCurrentUser()));
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
        final populateUserAsyncTask task = new populateUserAsyncTask(this, getDatabase(), getHttpHandler());
        final SourceReadActivity currentActivity = this;

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
                            String app_login_url = requests.get("auth");

                            // Insert request token
                            String url = app_login_url.replaceAll("REPLACEME", app.getRequestToken());

                            // Store this object using local persistence
                            if (storageSaver.write(currentActivity, getUser().getUserId().getValue(), getUser())) {
                                // Redirect to browser for app login
                                getHttpHandler().browser_open(currentActivity, url);
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(getInterfaceEnabled()){
            switch (item.getItemId()) {
                case R.id.action_import_articles:
                    // TODO: replace with box asking link or apps
                    // TODO: on apps do as usual
                    // TODO: on link open dialog asking for link
                    if(getUser().getApps() != null && getUser().getApps().getValue() != null && getUser().getApps().getValue().size() > 0) {
                        // loading worm and "importing.."
                        deactivate_interface();

                        // Import articles from user accounts
                        for(App app: getUser().getApps().getValue()) {
                            // Fetch serialised user
                            user.importArticles(this, getHttpHandler(), getDatabase(), app);
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
                case R.id.action_help:
                    // Show help dialog
                    SourceReadDialog helpDialog = new SourceReadDialog(this, null, null, R.string.user_ok, null, getHelp());
                    helpDialog.show();

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
        if(fragmentID == R.id.nav_app){
            if(appCallback) {
                // Set help dialog text
                setHelp(R.string.help_apps);

                // Redirect to apps choice
                fragment_redirect(R.id.nav_apps, new Bundle());
                return true;
            }else{
                // Set help dialog text
                setHelp(R.string.help_apps_choice);

                // Redirect to previous fragment
                return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                        || super.onSupportNavigateUp();
            }
        }else if(fragmentID == R.id.nav_apps_choice){
            // Set help dialog text
            setHelp(R.string.help_apps);

            // Redirect to previous fragment
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        }else{
            // Redirect to previous fragment
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        }
    }
}
