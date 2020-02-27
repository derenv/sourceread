package derenvural.sourceread_prototype.data.login;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
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
import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.data.storage.storageSaver;

public class LoggedInUser implements Serializable {
    // Basic data
    private MutableLiveData<String> userId = new MutableLiveData<String>();
    private MutableLiveData<String> displayName = new MutableLiveData<String>();
    private MutableLiveData<String> email = new MutableLiveData<String>();
    // App data
    private MutableLiveData<ArrayList<HashMap<String, Object>>> apps = new MutableLiveData<ArrayList<HashMap<String, Object>>>();
    private MutableLiveData<ArrayList<String>> appIDs = new MutableLiveData<ArrayList<String>>();
    // Article data
    private MutableLiveData<ArrayList<HashMap<String, String>>> articles = new MutableLiveData<ArrayList<HashMap<String, String>>>();
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
        setAppIDs((ArrayList) outState.getSerializable("appids"));
        // Article data
        setArticles((ArrayList) outState.getSerializable("articles"));
        setArticleIDs((ArrayList) outState.getSerializable("articleids"));
        // Tokens/Keys
        setRequestTokens((HashMap) outState.getSerializable("request"));
        setAccessTokens((HashMap) outState.getSerializable("access"));
        // Statistical data
        setVeracity((String) outState.getSerializable("veracity"));
    }
    public void saveInstanceState(Bundle outState) {
        // Basic data
        outState.putSerializable("id", getUserId().getValue());
        outState.putSerializable("displayName", getDisplayName().getValue());
        outState.putSerializable("email", getEmail().getValue());
        // App data
        outState.putSerializable("apps", getApps().getValue());
        outState.putSerializable("appids", getAppIDs().getValue());
        // Article data
        outState.putSerializable("articles", getArticles().getValue());
        outState.putSerializable("articleids", getArticleIDs().getValue());
        // Tokens/Keys
        outState.putSerializable("request", getRequestTokens().getValue());
        outState.putSerializable("access", getAccessTokens().getValue());
        // Statistical data
        outState.putSerializable("veracity", getVeracity().getValue());
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
        setAppIDs((ArrayList) stream.readObject());
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
    public void populate(final Context context, final LifecycleOwner owner, final fdatabase db, final httpHandler httph) {
        final LoggedInUser dat = this;

        /* Populate user data
         * +veracity
         * +appIDs
         * +articleIDs
         */
        db.request_user_data(dat);

        /* Populate apps (including app keys)
         * +apps
         * ++name
         * ++description
         * ++key
         * ++requests
         */
        getAppIDs().observe(owner, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> found_apps) {
                // Called when "request_user_data" has a response
                if (found_apps == null) {
                    return;
                }
                if (found_apps.size() > 0) {
                    db.request_app_data(dat);
                }
            }
        });

        /* Populate articles
         * +articles
         * ++id
         * ++title
         * ++app
         * ++author
         * ++author_veracity
         * ++publication
         * ++publication_veracity
         * ++veracity
         */
        getArticleIDs().observe(owner, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> articles) {
                // Called when "request_user_data" has a response
                if (articles == null) {
                    return;
                }
                if (articles.size() > 0) {
                    db.request_article_data(dat);
                }
            }
        });

        /* Request request tokens for authentication
         * +requestTokens
         */
        getApps().observe(owner, new Observer<ArrayList<HashMap<String, Object>>>() {
            // Called when "request_app_data" has a response
            @Override
            public void onChanged(@Nullable final ArrayList<HashMap<String, Object>> found_apps) {
                if (found_apps == null) {
                    return;
                }
                if (found_apps.size() > 0) {
                    request_tokens(httph, found_apps);
                }
            }
        });

        /* Open app in browser for authentication
         * Creates callback
         */
        getRequestTokens().observe(owner, new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(@Nullable final HashMap<String, String> found_tokens) {
                if (found_tokens == null) {
                    return;
                }
                if (found_tokens.size() > 0) {
                    open_app(context, httph, found_tokens);
                }
            }
        });
    }

    public void open_app(Context context, httpHandler httph, HashMap<String, String> request_tokens){
        // Only apply to apps with a request token
        for(String app_name : request_tokens.keySet()){
            // Get app for current token
            for(HashMap<String, Object> app : getApps().getValue()) {
                // If current app matches
                if(app.get("name").equals(app_name)) {
                    // Get login URL
                    HashMap<String, String> requests = (HashMap<String, String>) app.get("requests");
                    String app_login_url = requests.get("login");
                    Log.d("HTTP login url request", app_login_url);

                    // Insert request token
                    String url = app_login_url.replaceAll("REPLACEME", request_tokens.get(app_name));
                    Log.d("HTTP login url request", url);

                    // Store this object using local persistence
                    if(storageSaver.write(context, getUserId().getValue(), this)) {
                        // Redirect to browser for app login
                        httph.browser_open(context, url);
                    }else{
                        // TODO: handle error
                    }
                }
            }
        }
    }

    private void request_tokens(httpHandler httph, ArrayList<HashMap<String, Object>> found_apps){
        // Only apply to populated apps
        for (final HashMap<String, Object> app : found_apps) {
            // Get request token request URL for current app
            HashMap<String, Object> app_requests = (HashMap<String, Object>) app.get("requests");
            String url = app_requests.get("request").toString();

            // Cut out redirect URL from url
            String[] fullUrl = url.split("\\?");
            url = fullUrl[0];
            String redirect_uri = fullUrl[1];

            // Fetch app key
            String app_key = app.get("key").toString();

            // Get app key for current app
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("consumer_key",app_key);
            parameters.put("redirect_uri",redirect_uri);

            // Make https POST request
            httph.make_volley_request(url, parameters,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Cut token out of html response
                            Log.d("API", "Request Response recieved");

                            try {
                                // Create new hash-map
                                HashMap<String, String> new_request_tokens = getRequestTokens().getValue();
                                if (new_request_tokens == null) {
                                    new_request_tokens = new HashMap<String, String>();
                                }
                                new_request_tokens.put(app.get("name").toString(), response.getString("code"));

                                // Add new request token
                                setRequestTokens(new_request_tokens);
                            }catch(JSONException error){
                                Log.e("JSON error", "error reading JSON: " + error.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("API error", "api get failed: " + error.getMessage());
                        }
                    }
            );
        }
    }

    public void request_access_tokens(LifecycleOwner owner, final httpHandler httph, final String app_name){
        if(getApps().getValue() != null){
            access_tokens(httph, getApps().getValue(), app_name);
        }else{
            if(getApps().hasObservers()){
                getApps().removeObservers(owner);
            }

            getApps().observe(owner, new Observer<ArrayList<HashMap<String, Object>>>() {
                // Called when "request_app_data" has a response
                @Override
                public void onChanged(@Nullable final ArrayList<HashMap<String, Object>> found_apps) {
                    if (found_apps == null) {
                        return;
                    }
                    if (found_apps.size() > 0) {
                        access_tokens(httph, found_apps, app_name);
                    }
                }
            });
        }
    }

    public void access_tokens(httpHandler httph, ArrayList<HashMap<String, Object>> found_apps, final String app_name){
        for (HashMap<String, Object> app : found_apps) {
            if(app.get("name").equals(app_name)) {
                // Get login URL
                HashMap<String, String> requests = (HashMap<String, String>) app.get("requests");
                String url = requests.get("access");

                // Cut out redirect URL from url
                String[] fullUrl = url.split("\\?");
                url = fullUrl[0];
                String redirect_uri = fullUrl[1];

                // Fetch app key & request token
                String app_key = app.get("key").toString();
                String request_token = getRequestTokens().getValue().get(app_name);

                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("consumer_key",app_key);
                parameters.put("code",request_token);
                parameters.put("redirect_uri",redirect_uri);

                // Request access token by http request to URL
                httph.make_volley_request(url, parameters,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Cut token out of html response
                                Log.d("API", "Request Response recieved");

                                try{
                                    // Create new hash-map
                                    HashMap<String, String> new_access_tokens = getAccessTokens().getValue();
                                    if (new_access_tokens == null) {
                                        new_access_tokens = new HashMap<String, String>();
                                    }
                                    new_access_tokens.put(app_name, response.getString("access_token"));
                                    setDisplayName(response.getString("username"));

                                    // Add new access token
                                    setAccessTokens(new_access_tokens);
                                }catch(JSONException error){
                                    Log.e("JSON error", "error reading JSON: " + error.getMessage());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("API error", "api get failed: " + error.getMessage());
                            }
                        }
                );
            }
        }
    }

    // Sets
    public void setUserId(String userId) { this.userId.setValue(userId); }
    public void setDisplayName(String displayName) { this.displayName.setValue(displayName); }
    public void setEmail(String email) { this.email.setValue(email); }
    public void setApps(ArrayList<HashMap<String, Object>> apps) { this.apps.setValue(apps); }
    public void setAppIDs(ArrayList<String> appIDs) { this.appIDs.setValue(appIDs); }
    public void setArticles(ArrayList<HashMap<String, String>> articles) { this.articles.setValue(articles); }
    public void setArticleIDs(ArrayList<String> articles) { this.articleIDs.setValue(articles); }
    public void setRequestTokens(HashMap<String, String> requestTokens) { this.requestTokens.setValue(requestTokens); }
    public void setAccessTokens(HashMap<String, String> accessTokens) { this.accessTokens.setValue(accessTokens); }
    public void setVeracity(String veracity) { this.veracity.setValue(veracity); }

    // Gets
    public LiveData<String> getUserId() { return userId; }
    public LiveData<String> getDisplayName() { return displayName; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<ArrayList<HashMap<String, Object>>> getApps() { return apps; }
    public LiveData<ArrayList<String>> getAppIDs() { return appIDs; }
    public LiveData<ArrayList<HashMap<String, String>>> getArticles() { return articles; }
    public LiveData<ArrayList<String>> getArticleIDs() { return articleIDs; }
    public LiveData<HashMap<String, String>> getRequestTokens() { return requestTokens; }
    public LiveData<HashMap<String, String>> getAccessTokens() { return accessTokens; }
    public LiveData<String> getVeracity() { return veracity; }
}
