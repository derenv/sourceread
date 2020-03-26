package derenvural.sourceread_prototype.data.login;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import derenvural.sourceread_prototype.MainActivity;
import derenvural.sourceread_prototype.data.article.Article;
import derenvural.sourceread_prototype.data.asyncTasks.importArticlesAsyncTask;
import derenvural.sourceread_prototype.data.asyncTasks.userAccessAsyncTask;
import derenvural.sourceread_prototype.data.asyncTasks.userPopulateAsyncTask;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.http.httpHandler;

public class LoggedInUser implements Serializable {
    // Basic data
    private MutableLiveData<String> userId = new MutableLiveData<String>();
    private MutableLiveData<String> displayName = new MutableLiveData<String>();
    private MutableLiveData<String> email = new MutableLiveData<String>();
    // App data
    private MutableLiveData<ArrayList<HashMap<String, Object>>> apps = new MutableLiveData<ArrayList<HashMap<String, Object>>>();
    private MutableLiveData<HashMap<String, Object>> appIDs = new MutableLiveData<HashMap<String, Object>>();
    // Article data
    private MutableLiveData<ArrayList<Article>> articles = new MutableLiveData<ArrayList<Article>>();
    private MutableLiveData<ArrayList<String>> articleIDs = new MutableLiveData<ArrayList<String>>();
    // Tokens/Keys
    private MutableLiveData<HashMap<String, String>> requestTokens = new MutableLiveData<HashMap<String, String>>();
    private MutableLiveData<HashMap<String, String>> accessTokens = new MutableLiveData<HashMap<String, String>>();
    // Statistical data
    private MutableLiveData<String> veracity = new MutableLiveData<String>();
    // Serialisation
    private static final long serialVersionUID = 1L;

    // Standard constructor
    public LoggedInUser(FirebaseUser user) {
        // Populate object from database
        setUserId(user.getUid());
        setDisplayName(user.getDisplayName());
        setEmail(user.getEmail());
    }

    public void loadInstanceState(Bundle outState) {
        // Basic data
        setUserId((String) outState.getSerializable("id"));
        setDisplayName((String) outState.getSerializable("displayName"));
        setEmail((String) outState.getSerializable("email"));
        // App data
        setApps((ArrayList) outState.getSerializable("apps"));
        setAppIDs((HashMap) outState.getSerializable("appids"));
        // Article data
        setArticles((ArrayList) outState.getSerializable("articles"));
        setArticleIDs((ArrayList) outState.getSerializable("articleids"));
        // Tokens/Keys
        setRequestTokens((HashMap) outState.getSerializable("request"));
        setAccessTokens((HashMap) outState.getSerializable("access"));
        // Statistical data
        setVeracity((String) outState.getSerializable("veracity"));
    }
    public void saveInstanceState(Bundle bundle) {
        // Basic data
        bundle.putSerializable("id", getUserId().getValue());
        bundle.putSerializable("displayName", getDisplayName().getValue());
        bundle.putSerializable("email", getEmail().getValue());
        // App data
        bundle.putSerializable("apps", getApps().getValue());
        bundle.putSerializable("appids", getAppIDs().getValue());
        // Article data
        bundle.putSerializable("articles", getArticles().getValue());
        bundle.putSerializable("articleids", getArticleIDs().getValue());
        // Tokens/Keys
        bundle.putSerializable("request", getRequestTokens().getValue());
        bundle.putSerializable("access", getAccessTokens().getValue());
        // Statistical data
        bundle.putSerializable("veracity", getVeracity().getValue());
    }

    public void writeObject(java.io.ObjectOutputStream stream) throws IOException {
        // Basic data
        stream.writeObject(getUserId().getValue());
        stream.writeObject(getDisplayName().getValue());
        stream.writeObject(getEmail().getValue());
        // App data
        stream.writeObject(getApps().getValue());
        stream.writeObject(getAppIDs().getValue());
        // Article data
        stream.writeObject(getArticles().getValue());
        stream.writeObject(getArticleIDs().getValue());
        // Tokens/Keys
        stream.writeObject(getRequestTokens().getValue());
        stream.writeObject(getAccessTokens().getValue());
        // Statistical data
        stream.writeObject(getVeracity().getValue());
    }

    public void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        // Basic data
        setUserId((String) stream.readObject());
        setDisplayName((String) stream.readObject());
        setEmail((String) stream.readObject());
        // App data
        setApps((ArrayList) stream.readObject());
        setAppIDs((HashMap) stream.readObject());
        // Article data
        setArticles((ArrayList) stream.readObject());
        setArticleIDs((ArrayList) stream.readObject());
        // Tokens/Keys
        setRequestTokens((HashMap) stream.readObject());
        setAccessTokens((HashMap) stream.readObject());
        // Statistical data
        setVeracity((String) stream.readObject());
    }

    //Population Methods
    public void populate(MainActivity main, fdatabase db, httpHandler httph) {
        // Create async task
        userPopulateAsyncTask task = new userPopulateAsyncTask((Context) main, this, db, httph);

        // execute async task
        task.execute();

        // Check for task finish
        task.getDone().observe(main, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    Log.d("TASK", "user data population task done!");
                }
            }
        });
    }

    public void access_tokens(final MainActivity main, httpHandler httph, String app_name){
        // Create async task
        userAccessAsyncTask task = new userAccessAsyncTask(this, httph, app_name);

        // execute async task
        task.execute();

        // Check for task finish
        task.getDone().observe(main, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    Log.d("TASK", "access tokens task done!");

                    // Reactivate the UI
                    main.activate_interface();
                }
            }
        });
    }

    public void import_articles(final MainActivity main, httpHandler httph, final fdatabase db){
        for (final HashMap<String, Object> app : getApps().getValue()) {
            // Create async task
            importArticlesAsyncTask task = new importArticlesAsyncTask(main, this, httph, db, app);

            // execute async task
            task.execute();

            // Check for task finish
            task.getDone().observe(main, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean done) {
                    if (done) {
                        Log.d("TASK", "articles import task done!");

                        // Reactivate the UI
                        main.activate_interface();
                    }
                }
            });
        }
    }

    // Array and hashmap addition
    public void addAccessToken(String app_name, String new_access_token) {
        // Get previous request tokens
        HashMap<String, String> access_tokens = getAccessTokens().getValue();
        if (access_tokens == null) {
            access_tokens = new HashMap<String, String>();
        }

        // Add new request token
        access_tokens.put(app_name, new_access_token);
        setAccessTokens(access_tokens);
    }
    public void addRequestToken(HashMap<String, String> new_request_token) {
        // Get previous request tokens
        HashMap<String, String> request_tokens = getRequestTokens().getValue();
        if (request_tokens == null) {
            request_tokens = new HashMap<String, String>();
        }

        // Add new request token
        request_tokens.putAll(new_request_token);
        setRequestTokens(request_tokens);
    }
    public void addArticleID(String new_id) {
        // Get previous article ids
        ArrayList<String> old_ids = getArticleIDs().getValue();
        if(old_ids == null){
            old_ids = new ArrayList<String>();
        }

        // Add new article id
        old_ids.add(new_id);
        setArticleIDs(old_ids);
    }
    public void addArticle(Article new_article){
        // Get previous articles
        ArrayList<Article> old_articles = getArticles().getValue();
        if(old_articles == null){
            old_articles = new ArrayList<Article>();
        }

        // Add new article
        old_articles.add(new_article);
        setArticles(old_articles);
    }
    public void addApp(HashMap<String, Object> new_app){
        // Get previous apps
        ArrayList<HashMap<String, Object>> apps = getApps().getValue();
        if (apps == null) {
            apps = new ArrayList<HashMap<String, Object>>();
        }

        // Add new app
        apps.add(new_app);
        setApps(apps);
    }

    // Sets
    public void setUserId(String userId) { this.userId.setValue(userId); }
    public void setDisplayName(String displayName) { this.displayName.setValue(displayName); }
    public void setEmail(String email) { this.email.setValue(email); }
    public void setApps(ArrayList<HashMap<String, Object>> apps) { this.apps.setValue(apps); }
    public void setAppIDs(HashMap<String, Object> appIDs) { this.appIDs.setValue(appIDs); }
    public void setArticles(ArrayList<Article> articles) { this.articles.setValue(articles); }
    public void setArticleIDs(ArrayList<String> articles) { this.articleIDs.setValue(articles); }
    public void setRequestTokens(HashMap<String, String> requestTokens) { this.requestTokens.setValue(requestTokens); }
    public void setAccessTokens(HashMap<String, String> accessTokens) { this.accessTokens.setValue(accessTokens); }
    public void setVeracity(String veracity) { this.veracity.setValue(veracity); }

    // Gets
    public LiveData<String> getUserId() { return userId; }
    public LiveData<String> getDisplayName() { return displayName; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<ArrayList<HashMap<String, Object>>> getApps() { return apps; }
    public LiveData<HashMap<String, Object>> getAppIDs() { return appIDs; }
    public LiveData<ArrayList<Article>> getArticles() { return articles; }
    public LiveData<ArrayList<String>> getArticleIDs() { return articleIDs; }
    public LiveData<HashMap<String, String>> getRequestTokens() { return requestTokens; }
    public LiveData<HashMap<String, String>> getAccessTokens() { return accessTokens; }
    public LiveData<String> getVeracity() { return veracity; }
}
