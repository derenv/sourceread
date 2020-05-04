package derenvural.sourceread_prototype.ui.article;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.cards.articles.Article;
import derenvural.sourceread_prototype.data.dialog.helpDialog;
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
        setMenuStyle(menuStyle.OUTER);

        // Set help dialog content
        setHelp(R.string.help_article);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Navigation Drawer
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_article, R.id.nav_home, R.id.nav_analysis,
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
                        // End current activity with non-deleting return value
                        end_activity();
                        break;
                    case R.id.nav_article:
                        // Change to correct menu
                        setMenuStyle(menuStyle.OUTER);

                        // Set help dialog content
                        setHelp(R.string.help_article);

                        // Replace current fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_article);
                        break;
                    case R.id.nav_analysis:
                        // Change to correct menu
                        setMenuStyle(menuStyle.OUTER);

                        // Set help dialog content
                        setHelp(R.string.help_analysis);

                        // Replace current fragment
                        Navigation.findNavController(this_activity,R.id.nav_host_fragment).navigate(R.id.nav_analysis);

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
            }else{
                logout();
                login_redirect();
            }
        }else{
            end_activity();
        }
    }

    // End current activity with deleting return value
    public void delete_article(){
        // Create intent
        Intent returnIntent = new Intent();

        // Create article bundle
        Bundle bundle = new Bundle();
        article.saveInstanceState(bundle);

        // Add extras & bundles
        returnIntent.putExtra("activity", "article");
        returnIntent.putExtra("result", articleResult.DELETE);
        returnIntent.putExtras(bundle);
        returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Return result to 'MainActivity'
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    // End current activity with non-deleting return value
    public void end_activity(){
        // Create intent
        Intent returnIntent = new Intent();

        // Add extras & bundles
        returnIntent.putExtra("activity", "article");
        returnIntent.putExtra("result", articleResult.NORMAL);
        returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Return result to 'MainActivity'
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    // Menu
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(getInterfaceEnabled()){
            switch (item.getItemId()) {
                case R.id.action_help:
                    // Show help dialog
                    helpDialog helpDialog = new helpDialog(this,
                            null,
                            null,
                            R.string.dialog_help_title,
                            R.string.user_ok,
                            null,
                            getHelp());
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
