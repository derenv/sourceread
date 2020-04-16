package derenvural.sourceread_prototype.ui.article;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.analyse.analyser;
import derenvural.sourceread_prototype.data.cards.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.dialog.SourceReadDialog;
import derenvural.sourceread_prototype.data.login.LoggedInUser;
import derenvural.sourceread_prototype.ui.home.menuStyle;

public class ArticleActivity extends SourceReadActivity {
    // Android
    private ArticleViewModel articleViewModel;
    // Article data
    private Article article;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        // Find viewmodel
        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);

        // Set menu layout style
        setMenuStyle(menuStyle.ARTICLE);

        // Set help dialog content
        setHelp(R.string.help_article);

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
                        // Create bundle with serialised object
                        Bundle bundle = new Bundle();
                        getUser().saveInstanceState(bundle);

                        // Set help dialog content
                        setHelp(R.string.help_home);

                        // Redirect to home page
                        main_redirect("article", bundle);
                        break;
                    case R.id.nav_article:
                        // Change to correct menu
                        setMenuStyle(menuStyle.ARTICLE);

                        // Set help dialog content
                        setHelp(R.string.help_article);

                        // Replace current fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_article);
                        break;
                    case R.id.nav_about:
                        // Change to correct menu
                        setMenuStyle(menuStyle.OUTER);

                        // Set help dialog content
                        setHelp(R.string.help_about);

                        // Replace current fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_about);
                        break;
                    case R.id.nav_settings:
                        // Change to correct menu
                        setMenuStyle(menuStyle.SETTINGS);

                        // Set help dialog content
                        setHelp(R.string.help_settings);

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

        // Check if user is signed in (non-null) and update UI accordingly.
        if(getAuth().getUid() == null){
            login_redirect();
        } else {
            // Get article & user from serial
            loadBundle(getIntent());
        }
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
                LoggedInUser user = new LoggedInUser(getAuth().getCurrentUser());
                user.loadInstanceState(extras);
                setUser(user);
                articleViewModel.setUser(user);
            }else{
                logout();
                login_redirect();
            }
        }else{
            // Create bundle with serialised object
            Bundle bundle = new Bundle();
            getUser().saveInstanceState(bundle);

            main_redirect("article", bundle);
        }
    }

    // Menu
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(getInterfaceEnabled()){
            switch (item.getItemId()) {
                case R.id.action_analyse_article:
                    Toast.makeText(this, "Analysing article...", Toast.LENGTH_SHORT).show();

                    // Disable menu button
                    item.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    // Get articles and create async task
                    analyser at = new analyser(getDatabase());
                    at.fetch_article(this, article, new Observer<Boolean>() {
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
