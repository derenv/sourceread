package derenvural.sourceread_prototype;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;

import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.dialog.helpDialog;
import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.data.login.LoggedInUser;
import derenvural.sourceread_prototype.ui.home.menuStyle;
import derenvural.sourceread_prototype.ui.login.LoginActivity;

public abstract class SourceReadActivity extends AppCompatActivity {
    // Android
    protected AppBarConfiguration mAppBarConfiguration;
    protected ProgressBar progressBar;
    protected DrawerLayout drawer;
    // Services
    protected FirebaseAuth mAuth;
    private fdatabase db;
    private httpHandler httph;
    // User
    public LoggedInUser user;
    // Variables
    private boolean interfaceEnabled;
    private menuStyle menustyle;
    private Integer current_help;

    // GET
    public LoggedInUser getUser() { return user; }
    public httpHandler getHttpHandler() { return httph; }
    public fdatabase getDatabase() { return db; }
    public FirebaseAuth getAuth() { return mAuth; }
    public boolean getInterfaceEnabled() { return interfaceEnabled; }
    public menuStyle getMenuStyle() { return menustyle; }
    public Integer getHelp() { return current_help; }

    // SET
    public void setUser(LoggedInUser user) { this.user = user; }
    public void setHttpHandler(httpHandler httph) { this.httph = httph; }
    public void setDatabase(fdatabase db) { this.db = db; }
    public void setAuth(FirebaseAuth mAuth) { this.mAuth = mAuth; }
    public void setInterfaceEnabled(boolean interfaceEnabled) { this.interfaceEnabled = interfaceEnabled; }
    public void setMenuStyle(menuStyle menustyle) { this.menustyle = menustyle; }
    public void setHelp(Integer current_help) { this.current_help = current_help; }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tools
        setAuth(FirebaseAuth.getInstance());
        setDatabase(new fdatabase());
        setHttpHandler(new httpHandler(this));

        // Variables
        setInterfaceEnabled(true);
    }
    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getMenuStyle() == menuStyle.OUTER){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.outer, menu);

            return true;
        }else if(getMenuStyle() == menuStyle.MAIN){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);

            return true;
        }else if(getMenuStyle() == menuStyle.SETTINGS){
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
        setInterfaceEnabled(enabled);
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
        getAuth().signOut();

        // Check for successful sign out
        if(null == getAuth().getCurrentUser()){
            Toast.makeText(this, "Successful Log out", Toast.LENGTH_SHORT).show();

            // Remove objects
            setDatabase(null);
            setHttpHandler(null);
            setUser(null);

            // Start login activity
            login_redirect();
        }else {
            Toast.makeText(this, "Log out failed!", Toast.LENGTH_SHORT).show();
        }
    }
    public void reauthorize(@NonNull final FirebaseUser user, @NonNull final OnCompleteListener<Void> end){
        // Create dialog listeners
        DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Fetch credentials
                String email = "test2@test.com";
                String password = "Balls123";

                // Create credentials object
                AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                // Attempt to authenticate input credentials
                user.reauthenticate(credential).addOnCompleteListener(end);

                // End dialog
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel
                dialog.dismiss();
            }
        };
        // Get auth credentials from the user for re-authentication
        helpDialog dialogAccount = new helpDialog(this,
                negative, positive,
                R.string.dialog_default_title,
                null, R.string.user_cancel,
                R.string.dialog_delete_account);
        dialogAccount.show();
    }
    public void delete_account(@NonNull final FirebaseUser user){
        final String uid = user.getUid();

        // Attempt to delete account
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Attempt database delete
                    db.delete_user(uid, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("USER", "User account deletion successful!");

                                login_redirect();
                            }else{
                                Log.d("USER", "User account deletion unsuccessful!");
                            }
                        }
                    });
                } else {
                    // Check why delete failed
                    Exception e = task.getException();
                    if(e instanceof FirebaseException) {
                        Exception fe = task.getException();

                        // Handle exception
                        if (fe instanceof FirebaseAuthRecentLoginRequiredException) {
                            reauthorize(user, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Attempt firebase user delete
                                        delete_account(user);
                                    } else {
                                        Log.d("USER", "User account deletion unsuccessful!");
                                    }
                                }
                            });
                        } else if (fe instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.d("USER", "Invalid password!");
                        } else if (fe instanceof FirebaseAuthInvalidUserException) {
                            Log.d("USER", "Incorrect email address!");
                        } else {
                            Log.d("USER", "Error on delete - "+fe.getLocalizedMessage());
                        }
                    } else {
                        Log.d("USER", "Error on delete - "+e.getLocalizedMessage());
                    }
                }
            }
        });
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
