package derenvural.sourceread_prototype;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private fdatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This should begin article analysis", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Navigation Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(null == mAuth.getCurrentUser()){
            // Redirect to login page
            Intent new_activity = new Intent(this, LoginActivity.class);
            new_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(new_activity);
            finish();
        }else{
            // Create database object
            db = new fdatabase();

            // Check if any apps connected
            check_apps();
        }
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_apps:
                Toast.makeText(this, "Refreshing apps..", Toast.LENGTH_SHORT).show();
                // TODO: REFRESH APPS
                return true;
            case R.id.action_refresh_articles:
                Toast.makeText(this, "Refreshing articles..", Toast.LENGTH_SHORT).show();
                // TODO: REFRESH ARTICLES
                return true;
            case R.id.action_logout_user:
                Toast.makeText(this, "Logging out..", Toast.LENGTH_SHORT).show();
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Check how many apps connected & verify each
    private void check_apps() {
        // Make apps request
        db.request_user_apps();

        // Add observer for db response
        db.get_user_apps().observe(this, new Observer<HashMap<String,String>>() {
            @Override
            public void onChanged(@Nullable HashMap<String,String> apps) {
                // Get list of apps
                Log.d("DB", "# Saved Apps: " + apps.size());

                // Check for invalid data
                if (apps == null) {
                    return;
                }
                if (apps.size() == 0) {
                    // Prompt to add apps
                    //
                }else if (apps.size() > 0) {
                    // Validate each app key
                    for (Map.Entry<String,String> entry : apps.entrySet()) {
                        // Get app & key
                        String app = entry.getKey();
                        String api_key = entry.getValue();
                        Log.d("DB", "app: " + app + ", API-key: " + api_key);

                        // TODO: Validate key
                    }
                }
            }
        });
    }

    private void logout(){
        // Sign out Firebase user
        mAuth.signOut();

        // Check for successful sign out
        if(null == mAuth.getCurrentUser()){
            Toast.makeText(getApplicationContext(), "Successful Log out", Toast.LENGTH_SHORT).show();

            // Remove objects
            db = null;

            // Start login activity
            Intent new_activity = new Intent(this, LoginActivity.class);
            new_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(new_activity);
        }else {
            Toast.makeText(getApplicationContext(), "Log out failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
