package derenvural.sourceread_prototype.data.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.asyncTasks.deleteArticleAsyncTask;
import derenvural.sourceread_prototype.data.asyncTasks.populateAppsAsyncTask;
import derenvural.sourceread_prototype.data.asyncTasks.populateUserAsyncTask;
import derenvural.sourceread_prototype.data.asyncTasks.writeAppsAsyncTask;
import derenvural.sourceread_prototype.data.cards.apps.App;
import derenvural.sourceread_prototype.data.cards.articles.Article;
import derenvural.sourceread_prototype.data.asyncTasks.importArticlesAsyncTask;
import derenvural.sourceread_prototype.data.asyncTasks.userAccessAsyncTask;
import derenvural.sourceread_prototype.data.dialog.choiceDialog;
import derenvural.sourceread_prototype.data.dialog.helpDialog;
import derenvural.sourceread_prototype.data.storage.storageSaver;

public class LoggedInUser implements Serializable {
    // Basic data
    private MutableLiveData<String> userId = new MutableLiveData<String>();
    private MutableLiveData<String> displayName = new MutableLiveData<String>();
    private MutableLiveData<String> email = new MutableLiveData<String>();
    // App data
    private MutableLiveData<ArrayList<App>> apps = new MutableLiveData<ArrayList<App>>();
    // Article data
    private MutableLiveData<ArrayList<Article>> articles = new MutableLiveData<ArrayList<Article>>();
    // Statistical data
    private MutableLiveData<String> veracity = new MutableLiveData<String>();
    // Serialisation
    private static final long serialVersionUID = 1L;

    // Standard constructor
    public LoggedInUser(@NonNull FirebaseUser user) {
        // Populate object from database
        setUserId(user.getUid());
        setDisplayName(user.getDisplayName());
        setEmail(user.getEmail());
    }

    public void loadInstanceState(@NonNull Bundle outState) {
        // Basic data
        setUserId((String) outState.getSerializable("id"));
        setDisplayName((String) outState.getSerializable("displayName"));
        setEmail((String) outState.getSerializable("email"));
        // App data
        setApps((ArrayList) outState.getSerializable("apps"));
        // Article data
        setArticles((ArrayList) outState.getSerializable("articles"));
        // Statistical data
        setVeracity((String) outState.getSerializable("veracity"));
    }
    public void saveInstanceState(@NonNull Bundle bundle) {
        // Basic data
        bundle.putSerializable("id", getUserId().getValue());
        bundle.putSerializable("displayName", getDisplayName().getValue());
        bundle.putSerializable("email", getEmail().getValue());
        // App data
        bundle.putSerializable("apps", getApps().getValue());
        // Article data
        bundle.putSerializable("articles", getArticles().getValue());
        // Statistical data
        bundle.putSerializable("veracity", getVeracity().getValue());
    }

    public void writeObject(@NonNull ObjectOutputStream stream) throws IOException {
        // Basic data
        stream.writeObject(getUserId().getValue());
        stream.writeObject(getDisplayName().getValue());
        stream.writeObject(getEmail().getValue());
        // App data
        stream.writeObject(getApps().getValue());
        // Article data
        stream.writeObject(getArticles().getValue());
        // Statistical data
        stream.writeObject(getVeracity().getValue());
    }

    public void readObject(@NonNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
        // Basic data
        setUserId((String) stream.readObject());
        setDisplayName((String) stream.readObject());
        setEmail((String) stream.readObject());
        // App data
        setApps((ArrayList) stream.readObject());
        // Article data
        setArticles((ArrayList) stream.readObject());
        // Statistical data
        setVeracity((String) stream.readObject());
    }

    public void request_token(final SourceReadActivity currentActivity, App app,
                              Response.Listener<JSONObject> responseListener,
                              Response.ErrorListener failureListener){
        // Get request token request URL for current app
        HashMap<String, String> app_requests = app.getRequests();
        String url = app_requests.get("request");

        // Cut out redirect URL from url
        String[] fullUrl = url.split("\\?");
        url = fullUrl[0];
        String redirect_uri = fullUrl[1];

        // Fetch app key
        String app_key = app.getKey();

        // Add JSON parameters
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("consumer_key",app_key);
        parameters.put("redirect_uri",redirect_uri);

        // Make https POST request
        currentActivity.getHttpHandler().make_volley_request_post(url, parameters,
                responseListener,
                failureListener
        );
    }

    //Population Methods
    public void populate(final SourceReadActivity currentActivity) {
        // Create async task
        final populateUserAsyncTask task = new populateUserAsyncTask(currentActivity);

        // execute async task
        task.execute(this);

        // Check for task finish
        task.getDone().observe(currentActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    if(task.getData().getValue() != null) {
                        // Get user object result
                        LoggedInUser user1 = task.getData().getValue();
                        currentActivity.setUser(user1);

                        // Check for access tokens
                        user1.verify_apps(currentActivity);

                        // Only reached if network error
                        currentActivity.activate_interface();
                    }
                }
            }
        });
    }

    public void verify_apps(@NonNull final SourceReadActivity currentActivity){
        if(getApps() != null && getApps().getValue() != null && getApps().getValue().size() > 0) {
            for (App app : getApps().getValue()) {
                if((app.getAccessToken() == null || app.getAccessToken().equals("")) &&
                   (app.getRequestToken() != null)){
                    // Open app in browser for authentication Creates callback
                    // Get login URL
                    HashMap<String, String> requests = app.getRequests();
                    String app_login_url = requests.get("auth");

                    if(app_login_url != null && !app_login_url.equals("")) {
                        // Insert request token
                        String url = app_login_url.replaceAll("REPLACEME", app.getRequestToken());

                        // Store this object using local persistence
                        if (storageSaver.write(currentActivity, getUserId().getValue(), this)) {
                            // Redirect to browser for app login
                            currentActivity.getHttpHandler().browser_open(currentActivity, url);
                        } else {
                            Log.e("HTTP", "login url request failure");
                        }
                    }
                }
            }
        }else{
            currentActivity.activate_interface();
        }
    }

    public void access_tokens(@NonNull final SourceReadActivity currentActivity, @NonNull final String app_name){
        // Create async task
        final userAccessAsyncTask task = new userAccessAsyncTask(currentActivity.getHttpHandler(), app_name);

        // execute async task
        task.execute(this);

        // Check for task finish
        task.getDone().observe(currentActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    if(task.getData().getValue() == null){
                        // Create listeners
                        DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Remove request token
                                final ArrayList<App> new_apps = new ArrayList<App>();
                                for(App app : getApps().getValue()){
                                    if (app.getTitle().equals(app_name)) {
                                        // Remove token
                                        app.setRequestToken(null);
                                        new_apps.add(app);
                                    }else{
                                        // ignore other apps
                                        new_apps.add(app);
                                    }
                                }
                                setApps(new_apps);

                                // Next app
                                verify_apps(currentActivity);

                                // Reactivate the UI
                                currentActivity.activate_interface();

                                // cancel
                                dialog.dismiss();
                            }
                        };
                        DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // retry
                                verify_apps(currentActivity);
                            }
                        };

                        // Build dialog
                        helpDialog retryDialog = new helpDialog(currentActivity,
                                negative,
                                positive,
                                R.string.dialog_authenticate_app_title,
                                R.string.user_ok,
                                R.string.user_cancel,
                                R.string.dialog_authenticate_app);

                        // Display dialog box to choose sort method (default is A to Z)
                        retryDialog.show();
                    }else {
                        // Set new apps
                        setApps(task.getData().getValue());

                        // move onto next app
                        verify_apps(currentActivity);

                        // Reactivate the UI
                        currentActivity.activate_interface();
                    }
                }
            }
        });
    }

    public void disconnectApp(@NonNull final SourceReadActivity currentActivity, @NonNull final App app, @NonNull final Integer destination){
        // create list of all remaining apps in user object
        ArrayList<App> currentApps = getApps().getValue();
        ArrayList<App> newApps = new ArrayList<App>();
        for(App this_app : currentApps){
            // If matching app
            if(this_app.getTitle().equals(app.getTitle())){
                // If any articles exist
                if(getArticles().getValue() != null && getArticles().getValue().size() != 0) {
                    // Count articles from app
                    int amount = 0;
                    for (Article countArticle : getArticles().getValue()) {
                        if (countArticle.getApp().equals(app.getTitle())) {
                            amount++;
                        }
                    }

                    // If some articles from this app are still present
                    if (amount != 0) {
                        // Invalidate app but remain on list (so user can delete)
                        this_app.setRequestToken(null);
                        this_app.setAccessToken(null);
                        newApps.add(this_app);
                    }
                }
            }else{
                newApps.add(this_app);
            }
        }

        // Remove app from user object
        setApps(newApps);

        // Create async task
        writeAppsAsyncTask task = new writeAppsAsyncTask(currentActivity.getDatabase());

        // execute async task
        task.execute(newApps);

        // Check for task finish
        task.getDone().observe(currentActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    // Notify user
                    Toast.makeText(currentActivity, app.getTitle()+" disconnected!", Toast.LENGTH_LONG).show();

                    // Reactivate the UI
                    currentActivity.activate_interface();

                    // Redirect to list of apps page
                    currentActivity.fragment_redirect(destination, new Bundle());
                }
            }
        });
    }

    public void connectApp(@NonNull final SourceReadActivity currentActivity, @NonNull final App app){
        // create list of all remaining apps in user object
        ArrayList<App> currentApps = getApps().getValue();
        if(currentApps == null){
            currentApps = new ArrayList<App>();
        }
        currentApps.add(app);

        // Remove app from user object
        setApps(currentApps);

        // Create async task
        final writeAppsAsyncTask writeAppsTask = new writeAppsAsyncTask(currentActivity.getDatabase());
        final LoggedInUser user = this;

        // execute async task
        writeAppsTask.execute(currentApps);

        // Check for task finish
        writeAppsTask.getDone().observe(currentActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    //Create HashMap<String, Object> of apps
                    HashMap<String, Long> found_apps = writeAppsTask.getData().getValue();

                    // Get app data then ask for request token
                    final populateAppsAsyncTask appTask = new populateAppsAsyncTask(currentActivity);

                    // execute async task
                    appTask.execute(found_apps);

                    // Check for task finish
                    appTask.getDone().observe(currentActivity, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean done) {
                            if (done) {
                                // Get apps data
                                setApps(appTask.getData().getValue());
                                for(App appToOpen: getApps().getValue()){
                                    if(appToOpen.getTitle().equals(app.getTitle())){
                                        // Open app in browser for authentication Creates callback
                                        // Get login URL
                                        HashMap<String, String> requests = appToOpen.getRequests();
                                        String app_login_url = requests.get("login");

                                        // Insert request token
                                        String url = app_login_url.replaceAll("REPLACEME", appToOpen.getRequestToken());

                                        // Store this object using local persistence
                                        if (storageSaver.write(currentActivity, getUserId().getValue(), user)) {
                                            // Redirect to browser for app login
                                            currentActivity.getHttpHandler().browser_open(currentActivity, url);
                                        } else {
                                            Log.e("HTTP", "login url request failure");
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void importArticles(@NonNull final SourceReadActivity currentActivity, @NonNull final App app){
        if(app.getAccessToken() != null && !app.getAccessToken().equals("")) {
            // Create async task
            final importArticlesAsyncTask task = new importArticlesAsyncTask(currentActivity);

            // execute async task
            task.execute(app);

            // Check for task finish
            task.getDone().observe(currentActivity, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean done) {
                    if (done) {
                        // Retrieve data
                        setArticles(task.getData().getValue());

                        // Create map object containing timestamp with correct format
                        final long request_stamp = Instant.now().getEpochSecond();
                        HashMap<String, Object> new_stamp = new HashMap<String, Object>();
                        new_stamp.put(app.getTitle(), request_stamp);

                        // Store timestamp in database & user object
                        currentActivity.getDatabase().update_user_field("apps", new_stamp, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> stamp_task) {
                                if (stamp_task.isSuccessful()) {
                                    // Store timestamp in user object
                                    ArrayList<App> old_apps = getApps().getValue();
                                    ArrayList<App> new_apps = new ArrayList<App>();
                                    for(App this_app : old_apps) {
                                        if (this_app.getTitle().equals(app.getTitle())) {
                                            // store timestamp
                                            this_app.setTimestamp(request_stamp);
                                            new_apps.add(this_app);
                                        }else{
                                            new_apps.add(this_app);
                                        }
                                    }
                                    setApps(new_apps);

                                    // Notify user
                                    Toast.makeText(currentActivity, "All your articles from "+app.getTitle()+" imported!", Toast.LENGTH_SHORT).show();

                                    // Reactivate the UI
                                    currentActivity.activate_interface();
                                }else{
                                    // Log error
                                    Log.e("DB", "write failed: ", stamp_task.getException());
                                }
                            }
                        });
                    }
                }
            });

            // Notify user
            Toast.makeText(currentActivity, "Importing all articles from " + app.getTitle() + "..", Toast.LENGTH_SHORT).show();
        }else{
            // Reactivate the UI
            currentActivity.activate_interface();

            // Notify user
            Toast.makeText(currentActivity, "Cannot import articles from " + app.getTitle() + ", needs authenticated!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAllArticles(@NonNull final SourceReadActivity currentActivity, @NonNull final String app_name){
        // Build list of articles to remove
        if(getArticles() != null && getArticles().getValue() != null && getArticles().getValue().size() > 0) {
            ArrayList<Article> articles = new ArrayList<Article>();
            for(Article article : getArticles().getValue()){
                if(article.getApp().equals(app_name)) {
                    articles.add(article);
                }
            }
            if(articles.size() > 0) {

                // Create async task
                final deleteArticleAsyncTask task = new deleteArticleAsyncTask(this, currentActivity.getDatabase());

                // execute async task
                task.execute(articles);

                // Check for task finish
                task.getDone().observe(currentActivity, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean done) {
                        if (done) {
                            Log.d("TASK", "article deletion task done!");

                            // Retrieve data
                            setArticles(task.getData().getValue());

                            // If missing request & access tokens remove from apps list
                            for(App this_app : getApps().getValue()) {
                                if (this_app.getAccessToken() == null && this_app.getRequestToken() == null) {
                                    disconnectApp(currentActivity, this_app, R.id.nav_apps);
                                }
                            }

                            // Replace timestamp with invalid
                            ArrayList<App> old_apps = getApps().getValue();
                            ArrayList<App> new_apps = new ArrayList<App>();
                            for (App this_app : old_apps) {
                                if (this_app.getTitle().equals(app_name)) {
                                    // Store timestamp in user object
                                    this_app.setTimestamp(0L);
                                    new_apps.add(this_app);

                                    // Store timestamp in database
                                    Map<String, Object> new_stamp = new HashMap<>();
                                    new_stamp.put(app_name, 0L);
                                    currentActivity.getDatabase().update_user_field("apps", new_stamp, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> stampTask) {
                                            if (stampTask.isSuccessful()) {
                                                Log.d("DB", "update done");

                                                // Reactivate the UI
                                                Toast.makeText(currentActivity, "All articles imported from " + app_name + " deleted!", Toast.LENGTH_SHORT).show();
                                                currentActivity.activate_interface();
                                            } else {
                                                // Log error
                                                Log.e("DB", "update failed: ", stampTask.getException());
                                            }
                                        }
                                    });
                                } else {
                                    new_apps.add(this_app);
                                }
                            }
                            setApps(new_apps);
                        }
                    }
                });
            }else{
                Toast.makeText(currentActivity, "no articles from "+app_name+" to delete!", Toast.LENGTH_SHORT).show();
                Log.d("TASK", "no articles from "+app_name+" to delete!");
                currentActivity.activate_interface();
            }
        }else{
            Toast.makeText(currentActivity, "no articles from "+app_name+" to delete!", Toast.LENGTH_SHORT).show();
            Log.d("TASK", "no articles from "+app_name+" to delete!");
            currentActivity.activate_interface();
        }
    }

    public void deleteArticle(@NonNull final SourceReadActivity currentActivity, @NonNull final ArrayList<Article> articles){
        // Create async task
        final deleteArticleAsyncTask task = new deleteArticleAsyncTask(this, currentActivity.getDatabase());

        // execute async task
        task.execute(articles);

        final LoggedInUser user = this;

        // Check for task finish
        task.getDone().observe(currentActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    Log.d("TASK", "article deletion task done!");

                    // Retrieve data
                    user.setArticles(task.getData().getValue());

                    // Reactivate the UI
                    currentActivity.activate_interface();
                }
            }
        });
    }

    // SET
    private void setUserId(String userId) { this.userId.setValue(userId); }
    private void setDisplayName(String displayName) { this.displayName.setValue(displayName); }
    private void setEmail(String email) { this.email.setValue(email); }
    public void setApps(ArrayList<App> apps) { this.apps.setValue(apps); }
    public void setArticles(ArrayList<Article> articles) { this.articles.setValue(articles); }
    public void setVeracity(String veracity) { this.veracity.setValue(veracity); }

    // GET
    public LiveData<String> getUserId() { return userId; }
    public LiveData<String> getDisplayName() { return displayName; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<ArrayList<App>> getApps() { return apps; }
    public LiveData<ArrayList<Article>> getArticles() { return articles; }
    public LiveData<String> getVeracity() { return veracity; }
}
