package derenvural.sourceread_prototype;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.MenuItem;

import androidx.core.view.GravityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;

import java.util.ArrayList;

import derenvural.sourceread_prototype.data.cards.apps.App;
import derenvural.sourceread_prototype.data.cards.articles.Article;
import derenvural.sourceread_prototype.data.cards.articles.filterType;
import derenvural.sourceread_prototype.data.dialog.choiceDialog;
import derenvural.sourceread_prototype.data.dialog.helpDialog;
import derenvural.sourceread_prototype.data.login.LoggedInUser;
import derenvural.sourceread_prototype.data.storage.storageSaver;
import derenvural.sourceread_prototype.ui.article.articleResult;
import derenvural.sourceread_prototype.ui.home.menuStyle;

public class MainActivity extends SourceReadActivity {
    // Variables
    private boolean appCallback;
    private MutableLiveData<filterType> filter;

    public LiveData<filterType> getFilter(){return filter;}
    public void setFilter(filterType filter){this.filter.setValue(filter);}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set menu layout style
        setMenuStyle(menuStyle.MAIN);
        appCallback = false;

        // Set help dialog content
        setHelp(R.string.help_home);

        // Set default filter
        filter = new MutableLiveData<filterType>();
        setFilter(filterType.ALPHABET_AZ);

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

        // Check if user is signed in (non-null) and update UI accordingly.
        if(getAuth().getUid() == null){
            login_redirect();
        } else {
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
            String scheme = appLinkData.getScheme();
            String link_type = appLinkData.getLastPathSegment();

            // Whitelist deep-links
            if(link_type != null && (link_type.equals("successful_login") || link_type.equals("successful_auth"))){
                // Get app name
                String app_name = appLinkData.getPathSegments().get(appLinkData.getPathSegments().size() - 2);

                // Create blank user for populating
                setUser(new LoggedInUser(getAuth().getCurrentUser()));

                // Fetch user from local persistence
                if(storageSaver.read(this, getAuth().getCurrentUser().getUid(), user)){
                    // Request access tokens (interface reactivated on response)
                    user.access_tokens(this, app_name);

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
            }else if(link_type != null && (link_type.equals("password_reset") || link_type.equals("confirm_email_registration"))){
                // account deep-links
                if(link_type.equals("confirm_email_registration")) {
                    // TODO: log in user
                    setUser(new LoggedInUser(getAuth().getCurrentUser()));
                    user.populate(this);
                }else{
                    // TODO!!!
                }
            }else{
                //other deep-links?
            }
        }else{
            // No deep link!
            // Fetch the bundle & check if it has extras
            String previous_activity = intent.getStringExtra("activity");
            Bundle extras = intent.getExtras();
            setUser(new LoggedInUser(getAuth().getCurrentUser()));
            if (extras != null) {
                // Fetch serialised user
                user.loadInstanceState(extras);

                if(previous_activity.equals("login")) {
                    user.populate(this);
                }else if(previous_activity.equals("article")) {
                    // Reactivate interface & disable worm
                    activate_interface();
                }
            }else {
                // Attempt population
                user.populate(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check valid return value
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                articleResult result = (articleResult) data.getSerializableExtra("result");
                if(result == articleResult.DELETE){
                    // Fetch bundle from intent
                    Bundle extras = data.getExtras();

                    if(extras != null) {
                        // Fetch serialised article
                        Article article = new Article();
                        article.loadInstanceState(extras);

                        // Delete article from user
                        ArrayList<Article> articles = new ArrayList<Article>();
                        articles.add(article);
                        getUser().deleteArticle(this, articles);
                    }else{
                        login_redirect();
                    }
                }
            }else{
                login_redirect();
            }
        }
    }

    // Menu
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final SourceReadActivity main = this;
        if(getInterfaceEnabled()){
            switch (item.getItemId()) {
                case R.id.action_sort:
                    // Create listeners
                    DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // cancel
                            dialog.dismiss();
                        }
                    };
                    DialogInterface.OnClickListener item_choice = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Find clicked option, specify sort type
                            switch(id){
                                case 0:
                                    setFilter(filterType.ALPHABET_AZ);
                                    break;
                                case 1:
                                    setFilter(filterType.ALPHABET_ZA);
                                    break;
                                case 2:
                                    setFilter(filterType.IMPORT_DT_LATEST);
                                    break;
                                case 3:
                                    setFilter(filterType.IMPORT_DT_OLDEST);
                                    break;
                                case 4:
                                    setFilter(filterType.VERACITY_HIGHEST);
                                    break;
                                case 5:
                                    setFilter(filterType.VERACITY_LOWEST);
                                    break;
                            }
                        }
                    };

                    // Build dialog
                    choiceDialog sortDialog = new choiceDialog(this,
                            getResources().getStringArray(R.array.sorting_options),
                            null,
                            negative,
                            R.string.dialog_sort_title,
                            R.string.user_ok,
                            R.string.user_cancel,
                            item_choice);

                    // Display dialog box to choose sort method (default is A to Z)
                    sortDialog.show();

                    return true;
                case R.id.action_import_articles:
                    //Dialog asking link or apps

                    // Create listeners
                    DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // cancel
                            dialog.dismiss();
                        }
                    };
                    DialogInterface.OnClickListener which_button = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Find clicked option, specify sort type
                            switch(id){
                                case 0:
                                    //APP
                                    // On apps do as usual
                                    if(getUser().getApps() != null && getUser().getApps().getValue() != null && getUser().getApps().getValue().size() > 0) {
                                        // loading worm and "importing.."
                                        main.deactivate_interface();

                                        // Import articles from user accounts
                                        for(App app: getUser().getApps().getValue()) {
                                            user.importArticles(main, app);
                                        }
                                    }else{
                                        Toast.makeText(main, "No apps to import from..", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 1:
                                    //LINK
                                    // TODO: on link open dialog asking for link
                                    Toast.makeText(main, "Not yet implemented..", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    };

                    // Build dialog
                    choiceDialog importDialog = new choiceDialog(this,
                            getResources().getStringArray(R.array.import_options),
                            null,
                            cancel,
                            R.string.dialog_import_title,
                            R.string.user_ok,
                            R.string.user_cancel,
                            which_button);

                    // Display dialog box to choose sort method (default is A to Z)
                    importDialog.show();

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
