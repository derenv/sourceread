package derenvural.sourceread_prototype.ui.article;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import derenvural.sourceread_prototype.MainActivity;
import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.analyse.analyser;
import derenvural.sourceread_prototype.data.cards.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.login.LoggedInUser;
import derenvural.sourceread_prototype.ui.home.menuStyle;
import derenvural.sourceread_prototype.ui.login.LoginActivity;

public class ArticleActivity extends AppCompatActivity {
    // Android
    private ArticleViewModel articleViewModel;
    private AppBarConfiguration mAppBarConfiguration;
    private ProgressBar progressBar;
    private DrawerLayout drawer;
    private Menu mainMenu;
    // Services
    private FirebaseAuth mAuth;
    private fdatabase db;
    // User
    private LoggedInUser user;
    // Article data
    private Article article;
    // Variables
    private boolean interfaceEnabled;
    private menuStyle menustyle;

    public LoggedInUser getUser() { return user; }
    public fdatabase getDatabase() { return db; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        // Find viewmodel
        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);


        menustyle = menuStyle.VISIBLE;

        // Navigation Drawer
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_article, R.id.nav_home,
                R.id.nav_about, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Progress bar
        progressBar = findViewById(R.id.loading_article);
        progressBar.setVisibility(View.INVISIBLE);

        // Add custom navigation listener to catch different activities
        final Activity this_activity = this;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        main_redirect();
                        break;
                    case R.id.nav_article:
                        // Change to correct menu
                        menustyle = menuStyle.VISIBLE;

                        // Replace current fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_article);
                        break;
                    case R.id.nav_about:
                        // Change to correct menu
                        menustyle = menuStyle.INVISIBLE;

                        // Replace current fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_about);
                        break;
                    case R.id.nav_settings:
                        // Change to correct menu
                        menustyle = menuStyle.INVISIBLE;

                        // Replace current fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_settings);
                        break;
                }

                //close navigation drawer
                invalidateOptionsMenu();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuth.getUid() == null){
            login_redirect();
        } else {
            // Create database object
            db = new fdatabase();

            // Get article & user from serial
            loadBundle(getIntent());
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

    /*
     * Redirect to login activity
     */
    private void login_redirect(){
        // Create intent
        Intent new_activity = new Intent(this, LoginActivity.class);

        // Add any flags to intent
        new_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Start next activity and end this one
        startActivity(new_activity);
        finish();
    }

    /*
     * Redirect to main activity
     */
    public void main_redirect(){
        // Create intent
        Intent main_activity = new Intent(this, MainActivity.class);

        // Create bundle with serialised object
        LoggedInUser user_data = getUser();
        Bundle bundle = new Bundle();
        user_data.saveInstanceState(bundle);

        // Add title, bundle and any flags to intent
        main_activity.putExtra("activity","article");
        main_activity.putExtras(bundle);
        main_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Start next activity and end this one
        startActivity(main_activity);
        finish();
    }

    public void loadBundle(Intent intent){
        // Fetch the bundle & check if it has extras
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String previous_activity = intent.getStringExtra("activity");
            if(previous_activity.equals("main")){
                // Fetch serialised article
                article = new Article();
                article.loadInstanceState(extras);
                articleViewModel.setArticle(article);

                // Fetch serialised user
                user = new LoggedInUser(mAuth.getCurrentUser());
                user.loadInstanceState(extras);
                articleViewModel.setUser(user);
            }else{
                logout();
                login_redirect();
            }
        }else{
            main_redirect();
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
            user = null;

            // Start login activity
            Intent new_activity = new Intent(this, LoginActivity.class);
            new_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(new_activity);
        }else {
            Toast.makeText(getApplicationContext(), "Log out failed!", Toast.LENGTH_SHORT).show();
        }
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
            getMenuInflater().inflate(R.menu.article, mainMenu);

            return true;
        }

        return false;
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(interfaceEnabled){
            switch (item.getItemId()) {
                case R.id.action_analyse_article:
                    Toast.makeText(this, "Analysing article...", Toast.LENGTH_SHORT).show();

                    // Disable menu button
                    item.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    // Get articles and create async task
                    analyser at = new analyser(article, db);
                    at.fetch_article(this, new Observer<Boolean>() {
                        // Called when "fetch_article" has a response
                        @Override
                        public void onChanged(Boolean done) {
                            if (done) {
                                // Re-enable menu button & disable progress bar
                                item.setEnabled(true);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                    return true;
                case R.id.action_logout_user:
                    Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
                    logout();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }else{
            return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
