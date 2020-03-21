package derenvural.sourceread_prototype.ui.article;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import derenvural.sourceread_prototype.MainActivity;
import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.analyse.analyser;
import derenvural.sourceread_prototype.data.article.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.login.LoggedInUser;
import derenvural.sourceread_prototype.ui.login.LoginActivity;

public class ArticleActivity extends AppCompatActivity {
    // Android
    private AppBarConfiguration mAppBarConfiguration;
    // Services
    private FirebaseAuth mAuth;
    private fdatabase db;
    // User
    private LoggedInUser user;
    // Article data
    private ArticleViewModel articleViewModel;
    private Article article;

    public LoggedInUser getUser() { return user; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);

        /*
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_article, ArticleFragment.newInstance())
                    .commitNow();
        }
        */

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setTitle("Article View");

        // Navigation Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

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

    private void login_redirect(){
        // Redirect to login page
        Intent new_activity = new Intent(this, LoginActivity.class);
        new_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(new_activity);
        finish();
    }

    private void main_redirect(){
        // Redirect to login page
        Intent new_activity = new Intent(this, MainActivity.class);
        new_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(new_activity);
        finish();
    }

    public void loadBundle(Intent intent){
        // Fetch the bundle & check if it has extras
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String previous_activity = intent.getStringExtra("activity");

            // Fetch serialised article
            article = new Article();
            article.loadInstanceState(extras);
            loadArticle();

            // Fetch serialised user
            user = new LoggedInUser(mAuth.getCurrentUser());
            user.loadInstanceState(extras);
            loadUser();
        }else{
            main_redirect();
        }
    }

    public void loadArticle(){
        //TODO: add to viewmodel
        articleViewModel.setTitle(article.getResolved_title());
        articleViewModel.setUrl(article.getResolved_url());
        articleViewModel.setAuthors(article.getAuthors());
        articleViewModel.setVeracity(article.getVeracity());
    }

    public void loadUser(){
        //TODO: add to viewmodel
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.article, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_analyse_article:
                Toast.makeText(this, "Analysing article...", Toast.LENGTH_SHORT).show();

                // Disable menu button
                item.setEnabled(false);

                // Get articles and create async task
                analyser at = new analyser(article);
                at.fetch_article(this, new Observer<Boolean>() {
                    // Called when "request_app_data" has a response
                    @Override
                    public void onChanged(Boolean done) {
                        if (done) {
                            // Re-enable menu button
                            Log.d("JSOUP", "done!");
                            item.setEnabled(true);
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
