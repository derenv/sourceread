package derenvural.sourceread_prototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.firebase.auth.FirebaseAuth;

import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.data.login.LoggedInUser;
import derenvural.sourceread_prototype.ui.apps.redirectType;
import derenvural.sourceread_prototype.ui.home.menuStyle;
import derenvural.sourceread_prototype.ui.login.LoginActivity;

public abstract class SourceReadActivity extends AppCompatActivity {
    // Android
    protected AppBarConfiguration mAppBarConfiguration;
    protected ProgressBar progressBar;
    protected DrawerLayout drawer;
    // Services
    protected FirebaseAuth mAuth;
    protected fdatabase db;
    protected httpHandler httph;
    // User
    protected LoggedInUser user;
    // Variables
    protected boolean interfaceEnabled;
    protected menuStyle menustyle;

    // GET
    public LoggedInUser getUser() { return user; }
    public httpHandler getHttpHandler() { return httph; }
    public fdatabase getDatabase() { return db; }

    // SET
    public void setUser(LoggedInUser user) { this.user = user; }
    public void setHttpHandler(httpHandler httph) { this.httph = httph; }
    public void setDatabase(fdatabase db) { this.db = db; }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(menustyle == menuStyle.OUTER){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.outer, menu);

            return true;
        }else if(menustyle == menuStyle.MAIN){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);

            return true;
        }else if(menustyle == menuStyle.ARTICLE){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.article, menu);

            return true;
        }else if(menustyle == menuStyle.SETTINGS){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.settings, menu);

            return true;
        }

        return false;
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

    // User Management
    public void logout(){
        // Sign out Firebase user
        mAuth.signOut();

        // Check for successful sign out
        if(null == mAuth.getCurrentUser()){
            Toast.makeText(this, "Successful Log out", Toast.LENGTH_SHORT).show();

            // Remove objects
            db = null;
            httph = null;
            user = null;

            // Start login activity
            login_redirect();
        }else {
            Toast.makeText(this, "Log out failed!", Toast.LENGTH_SHORT).show();
        }
    }

    // Redirects
    public void fragment_redirect(int destination, Bundle bundle){
        Navigation.findNavController(this,R.id.nav_host_fragment).navigate(destination, bundle);
        drawer.closeDrawers();
    }
    /*
     * Redirect to login activity
     */
    protected void login_redirect(){
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
    public void main_redirect(String source, Bundle userBundle){
        // Create intent
        Intent main_activity = new Intent(this, MainActivity.class);

        // Add title, bundle and any flags to intent
        main_activity.putExtra("activity", source);
        main_activity.putExtras(userBundle);
        main_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Start next activity and end this one
        startActivity(main_activity);
        finish();
    }
}
