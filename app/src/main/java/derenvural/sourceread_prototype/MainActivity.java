package derenvural.sourceread_prototype;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Toast;

import derenvural.sourceread_prototype.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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
                R.id.nav_settings, R.id.nav_about, R.id.nav_policy)
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
            //user already logged in
            System.out.println("USER ALREADY LOGGED IN");

            // Connect to database
            db = FirebaseFirestore.getInstance();

            // Check if any apps connected
            check_apps(db);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Check how many apps connected & verify each
    //1.1.2 if attached apps == 0
    //  1.1.2.1 prompt add apps
    //1.1.3 if attached apps > 0
    //  1.1.3.1 for each attached app
    //      1.1.3.1.1 request key from database
    //      1.1.3.1.2 validate key
    //      1.1.3.1.3 if key invalid
    //          1.1.3.1.3.1 new login THEN 1.1.3.1.4.1
    //      1.1.3.1.4 if key valid
    //          1.1.3.1.4.1 check if new articles
    //          1.1.3.1.4.2 if new articles
    //              1.1.3.1.4.2.1 analyse prompt THEN 1.1.3.1.4.3.1
    //          1.1.3.1.4.3 if no new articles
    //              1.1.3.1.4.3.1 show current articles
    private void check_apps(FirebaseFirestore db) {
        //
    }

    private void sign_out(){
        // Sign out firebase user
        mAuth.signOut();

        // Check for successful signout
        if(null == mAuth.getCurrentUser()){
            Toast.makeText(getApplicationContext(), "Successful Sign out", Toast.LENGTH_SHORT).show();
        }

        // Start login activity
        Intent new_activity = new Intent(this, LoginActivity.class);
        new_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(new_activity);
    }
}
