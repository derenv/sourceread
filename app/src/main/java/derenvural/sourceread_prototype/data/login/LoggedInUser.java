package derenvural.sourceread_prototype.data.login;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.http.httpHandler;

public class LoggedInUser implements Parcelable {
    // Basic data
    private MutableLiveData<String> userId = new MutableLiveData<String>();
    private MutableLiveData<String> displayName = new MutableLiveData<String>();
    private MutableLiveData<String> email = new MutableLiveData<String>();
    // App/Article data
    private MutableLiveData<ArrayList<String>> appIDs = new MutableLiveData<ArrayList<String>>();
    private MutableLiveData<ArrayList<String>> articleIDs = new MutableLiveData<ArrayList<String>>();
    private MutableLiveData<ArrayList<HashMap<String, String>>> articles = new MutableLiveData<ArrayList<HashMap<String, String>>>();
    private MutableLiveData<ArrayList<HashMap<String, Object>>> apps = new MutableLiveData<ArrayList<HashMap<String, Object>>>();
    private MutableLiveData<HashMap<String, String>> appKeys = new MutableLiveData<HashMap<String, String>>();
    private MutableLiveData<HashMap<String, String>> requestTokens = new MutableLiveData<HashMap<String, String>>();
    private MutableLiveData<HashMap<String, String>> accessTokens = new MutableLiveData<HashMap<String, String>>();
    // Statistical data
    private MutableLiveData<String> veracity = new MutableLiveData<String>();

    // Standard constructor
    public LoggedInUser(FirebaseUser user) {
        // Populate object from database
        setUserId(user.getUid());
        setDisplayName(user.getDisplayName());
        setEmail(user.getEmail());
    }
    /* Alternate constructor
     * takes Parcel and returns populated object
     */
    private LoggedInUser(Parcel in) {
        setUserId(in.readString());
        setDisplayName(in.readString());
        setEmail(in.readString());

        setArticles(in.readArrayList(ArrayList.class.getClassLoader()));
        setArticleIDs(in.readArrayList(ArrayList.class.getClassLoader()));
        setApps(in.readArrayList(ArrayList.class.getClassLoader()));
        setAppIDs(in.readArrayList(ArrayList.class.getClassLoader()));

        setAppKeys(in.readHashMap(HashMap.class.getClassLoader()));
        setRequestTokens(in.readHashMap(HashMap.class.getClassLoader()));
        setAccessTokens(in.readHashMap(HashMap.class.getClassLoader()));

        setVeracity(in.readString());
    }

    // Parcelisation Methods
    @Override
    public int describeContents() {
        return 0;
    }
    // write objects data to Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getUserId().getValue());
        out.writeString(getDisplayName().getValue());
        out.writeString(getEmail().getValue());

        out.writeList(getArticles().getValue());
        out.writeList(getArticleIDs().getValue());
        out.writeList(getApps().getValue());
        out.writeList(getAppIDs().getValue());
        out.writeMap(getAppKeys().getValue());
        out.writeMap(getRequestTokens().getValue());
        out.writeMap(getAccessTokens().getValue());

        out.writeString(getOverallVeracity().getValue());
    }
    /*
     * regenerate your object
     * All Parcelables must have a CREATOR that implements these two methods
     */
    public static final Parcelable.Creator<LoggedInUser> CREATOR = new Parcelable.Creator<LoggedInUser>() {
        public LoggedInUser createFromParcel(Parcel in) {
            return new LoggedInUser(in);
        }

        public LoggedInUser[] newArray(int size) {
            return new LoggedInUser[size];
        }
    };

    //Population Methods
    public void populate(final Context context, final LifecycleOwner owner, final fdatabase db, final httpHandler httph) {
        final LoggedInUser dat = this;

        // Populate user data
        db.request_user_data(dat);

        // Populate apps (including app keys)
        getAppIDs().observe(owner, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> found_apps) {
                //
                if (found_apps == null) {
                    return;
                }
                if (found_apps.size() > 0) {
                    db.request_app_data(dat);
                }
            }
        });

        // Populate articles
        getArticleIDs().observe(owner, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> articles) {
                //
                if (articles == null) {
                    return;
                }
                if (articles.size() > 0) {
                    db.request_article_data(dat);
                    request_tokens(context, owner, httph);
                }
            }
        });
    }

    public void open_app(final Context context, final LifecycleOwner owner, final String app_name, final httpHandler httph){
        if(getApps().getValue() != null){
            if(getRequestTokens().getValue() != null){
                Log.d("HTTP", "NON-NULL");
                for(HashMap<String, Object> app : getApps().getValue()){
                    if (app.get("name").equals(app_name)) {
                        // Get login URL
                        HashMap<String, String> requests = (HashMap<String, String>) app.get("requests");
                        String app_login_url = requests.get("login");
                        Log.d("HTTP", app_login_url);

                        String request_token = getRequestTokens().getValue().get(app_name);
                        String url = app_login_url.replaceAll("REPLACEME", request_token);
                        Log.d("HTTP", url);

                        // Redirect to browser for app login
                        httph.browser_open(context, url);
                    }
                }
            }else{
                getRequestTokens().observe(owner, new Observer<HashMap<String, String>>() {
                    @Override
                    public void onChanged(@Nullable HashMap<String, String> found_tokens) {
                        if (found_tokens == null) {
                            return;
                        }
                        if (found_tokens.size() > 0) {
                            Log.d("HTTP", "non-null & non-empty tokens found");
                            for(HashMap<String, Object> app : getApps().getValue()){
                                if (app.get("name").equals(app_name)) {
                                    // Get login URL
                                    HashMap<String, String> requests = (HashMap<String, String>) app.get("requests");
                                    String app_login_url = requests.get("login");
                                    Log.d("HTTP", app_login_url);

                                    String request_token = found_tokens.get(app_name);
                                    String url = app_login_url.replaceAll("REPLACEME", request_token);
                                    Log.d("HTTP", url);

                                    // Redirect to browser for app login
                                    httph.browser_open(context, url);
                                }
                            }
                        }
                    }
                });
            }
        }else {
            // Fetch login URL from hash map
            getApps().observe(owner, new Observer<ArrayList<HashMap<String, Object>>>() {
                @Override
                public void onChanged(@Nullable final ArrayList<HashMap<String, Object>> found_apps) {
                    if (found_apps == null) {
                        return;
                    }
                    if (found_apps.size() > 0) {
                        if(getRequestTokens().getValue() != null){
                            Log.d("HTTP", "NON-NULL");
                            for(HashMap<String, Object> app : getApps().getValue()){
                                if (app.get("name").equals(app_name)) {
                                    // Get login URL
                                    HashMap<String, String> requests = (HashMap<String, String>) app.get("requests");
                                    String app_login_url = requests.get("login");
                                    Log.d("HTTP", app_login_url);

                                    String request_token = getRequestTokens().getValue().get(app_name);
                                    String url = app_login_url.replaceAll("REPLACEME", request_token);
                                    Log.d("HTTP", url);

                                    // Redirect to browser for app login
                                    httph.browser_open(context, url);
                                }
                            }
                        }else {
                            Log.d("HTTP", "non-null & non-empty apps found");
                            // Get & paste in request token
                            getRequestTokens().observe(owner, new Observer<HashMap<String, String>>() {
                                @Override
                                public void onChanged(@Nullable HashMap<String, String> found_tokens) {
                                    if (found_tokens == null) {
                                        return;
                                    }
                                    if (found_tokens.size() > 0) {
                                        Log.d("HTTP", "non-null & non-empty tokens found");
                                        for (HashMap<String, Object> app : found_apps) {
                                            if (app.get("name").equals(app_name)) {
                                                // Get login URL
                                                HashMap<String, String> requests = (HashMap<String, String>) app.get("requests");
                                                String app_login_url = requests.get("login");
                                                Log.d("HTTP", app_login_url);

                                                String request_token = found_tokens.get(app_name);
                                                String url = app_login_url.replaceAll("REPLACEME", request_token);
                                                Log.d("HTTP", url);

                                                // Redirect to browser for app login
                                                httph.browser_open(context, url);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void request_tokens(final Context context, final LifecycleOwner owner, final httpHandler httph){
        if(getApps().getValue() != null) {
            for (final HashMap<String, Object> app : getApps().getValue()) {
                HashMap<String, Object> app_requests = (HashMap<String, Object>) app.get("requests");
                String app_request_token_url = app_requests.get("request").toString();
                String app_key = app.get("key").toString();
                String url = app_request_token_url.replaceAll("REPLACEME", app_key);

                httph.make_volley_request(url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Cut token out of html response
                                Log.d("API", "Request Response is: " + response.substring(5));

                                // Add new request token
                                HashMap<String, String> new_request_tokens = getRequestTokens().getValue();
                                if (new_request_tokens == null) {
                                    new_request_tokens = new HashMap<String, String>();
                                }

                                // TODO: fix loop
                                // observer catches this request tokens
                                new_request_tokens.put(app.get("name").toString(), response.substring(5));
                                getRequestTokens().removeObservers(owner);
                                setRequestTokens(new_request_tokens);
                                Log.d("API request", getRequestTokens().getValue().toString());

                                //login
                                open_app(context, owner, app.get("name").toString(), httph);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("API", "api get failed: " + error.getMessage());
                            }
                        }
                );
            }
        }else{
            getApps().observe(owner, new Observer<ArrayList<HashMap<String, Object>>>() {
                @Override
                public void onChanged(@Nullable ArrayList<HashMap<String, Object>> found_apps) {
                    if (found_apps == null) {
                        return;
                    }
                    if (found_apps.size() > 0) {
                        for (final HashMap<String, Object> app : found_apps) {
                            HashMap<String, Object> app_requests = (HashMap<String, Object>) app.get("requests");
                            String app_request_token_url = app_requests.get("request").toString();
                            String app_key = app.get("key").toString();
                            String url = app_request_token_url.replaceAll("REPLACEME", app_key);

                            httph.make_volley_request(url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            // Cut token out of html response
                                            Log.d("API", "Request Response is: " + response.substring(5));

                                            // Add new request token
                                            HashMap<String, String> new_request_tokens = getRequestTokens().getValue();
                                            if (new_request_tokens == null) {
                                                new_request_tokens = new HashMap<String, String>();
                                            }
                                            new_request_tokens.put(app.get("name").toString(), response.substring(5));
                                            getRequestTokens().removeObservers(owner);
                                            setRequestTokens(new_request_tokens);
                                            Log.d("API request", getRequestTokens().getValue().toString());

                                            //login
                                            open_app(context, owner, app.get("name").toString(), httph);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("API", "api get failed: " + error.getMessage());
                                        }
                                    }
                            );
                        }
                    }
                }
            });
        }
    }

    public void access_tokens(final LifecycleOwner owner, final httpHandler httph){
        if(getApps().getValue() != null) {
            if (getRequestTokens().getValue() != null) {
                if (getAppKeys().getValue() != null) {
                    Log.d("HTTP", "NON-NULL");
                    for (HashMap<String, Object> app : getApps().getValue()) {
                        // Get login URL
                        HashMap<String, String> requests = (HashMap<String, String>) app.get("requests");
                        String access_token_url = requests.get("access");
                        Log.d("HTTP", access_token_url);

                        // Insert app key
                        String app_key = getAppKeys().getValue().get(app.get("name"));
                        String url = access_token_url.replaceAll("REPLACEME1", app_key);
                        Log.d("HTTP", url);

                        // Insert request token
                        String request_token = getRequestTokens().getValue().get(app.get("name"));
                        url = url.replaceAll("REPLACEME2", request_token);
                        Log.d("HTTP", url);

                        // Request access token
                        // Make http request using URL
                        httph.make_volley_request(url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Cut token out of html response
                                        Log.d("API", "Access Response is: "+ response.substring(5));

                                        // Add new request token
                                        HashMap<String, String> new_access_tokens = getAccessTokens().getValue();
                                        if(new_access_tokens == null){
                                            new_access_tokens = new HashMap<String, String>();
                                        }
                                        new_access_tokens.put("access_token",response.substring(5));
                                        setAccessTokens(new_access_tokens);
                                        Log.d("API access", getAccessTokens().getValue().toString());
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("API", "api get failed: " + error.getMessage());
                                    }
                                }
                        );
                    }
                }else{
                    getAppKeys().observe(owner, new Observer<HashMap<String, String>>() {
                        @Override
                        public void onChanged(@Nullable HashMap<String, String> found_keys) {
                            if (found_keys == null) {
                                return;
                            }
                            if (found_keys.size() > 0) {
                                Log.d("HTTP", "non-null & non-empty tokens found");
                                for (HashMap<String, Object> app : getApps().getValue()) {
                                    // Get login URL
                                    HashMap<String, String> requests = (HashMap<String, String>) app.get("requests");
                                    String app_login_url = requests.get("login");
                                    Log.d("HTTP", app_login_url);

                                    // Insert app key
                                    String app_key = found_keys.get(app.get("name"));
                                    String url = app_login_url.replaceAll("REPLACEME1", app_key);
                                    Log.d("HTTP", url);

                                    // Insert request token
                                    String request_token = getRequestTokens().getValue().get(app.get("name"));
                                    url = app_login_url.replaceAll("REPLACEME", request_token);
                                    Log.d("HTTP", url);

                                    // Request access token
                                    // Make http request using URL
                                    httph.make_volley_request(url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    // Cut token out of html response
                                                    Log.d("API", "Access Response is: "+ response.substring(5));

                                                    // Add new request token
                                                    HashMap<String, String> new_access_tokens = getAccessTokens().getValue();
                                                    if(new_access_tokens == null){
                                                        new_access_tokens = new HashMap<String, String>();
                                                    }
                                                    new_access_tokens.put("access_token",response.substring(5));
                                                    setAccessTokens(new_access_tokens);
                                                    Log.d("API access", getAccessTokens().getValue().toString());
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.d("API", "api get failed: " + error.getMessage());
                                                }
                                            }
                                    );
                                }
                            }
                        }
                    });
                }
            }else{
                getRequestTokens().observe(owner, new Observer<HashMap<String, String>>() {
                    @Override
                    public void onChanged(@Nullable final HashMap<String, String> found_tokens) {
                        if (found_tokens == null) {
                            return;
                        }
                        if (found_tokens.size() > 0) {
                            getAppKeys().observe(owner, new Observer<HashMap<String, String>>() {
                                @Override
                                public void onChanged(@Nullable HashMap<String, String> found_keys) {
                                    if (found_keys == null) {
                                        return;
                                    }
                                    if (found_keys.size() > 0) {
                                        Log.d("HTTP", "non-null & non-empty tokens found");
                                        for (HashMap<String, Object> app : getApps().getValue()) {
                                            // Get login URL
                                            HashMap<String, String> requests = (HashMap<String, String>) app.get("requests");
                                            String app_login_url = requests.get("login");
                                            Log.d("HTTP", app_login_url);

                                            // Insert app key
                                            String app_key = found_keys.get(app.get("name"));
                                            String url = app_login_url.replaceAll("REPLACEME1", app_key);
                                            Log.d("HTTP", url);

                                            // Insert request token
                                            String request_token = getRequestTokens().getValue().get(app.get("name"));
                                            url = app_login_url.replaceAll("REPLACEME", request_token);
                                            Log.d("HTTP", url);

                                            // Request access token
                                            // Make http request using URL
                                            httph.make_volley_request(url,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            // Cut token out of html response
                                                            Log.d("API", "Access Response is: "+ response.substring(5));

                                                            // Add new request token
                                                            HashMap<String, String> new_access_tokens = getAccessTokens().getValue();
                                                            if(new_access_tokens == null){
                                                                new_access_tokens = new HashMap<String, String>();
                                                            }
                                                            new_access_tokens.put("access_token",response.substring(5));
                                                            setAccessTokens(new_access_tokens);
                                                            Log.d("API access", getAccessTokens().getValue().toString());
                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Log.d("API", "api get failed: " + error.getMessage());
                                                        }
                                                    }
                                            );
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
        // Fetch access token request URL from hash map
        getApps().observe(owner, new Observer<ArrayList<HashMap<String, Object>>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<HashMap<String, Object>> found_apps) {
                if (found_apps == null) {
                    return;
                }
                if (found_apps.size() > 0) {
                    Log.d("HTTP", "non-null & non-empty apps found");
                    // Get & paste in request token
                    getRequestTokens().observe(owner, new Observer<HashMap<String, String>>() {
                        @Override
                        public void onChanged(@Nullable final HashMap<String, String> found_tokens) {
                            if (found_tokens == null) {
                                return;
                            }
                            if (found_tokens.size() > 0) {
                                getAppKeys().observe(owner, new Observer<HashMap<String, String>>() {
                                    @Override
                                    public void onChanged(@Nullable HashMap<String, String> found_keys) {
                                        if (found_keys == null) {
                                            return;
                                        }
                                        if (found_keys.size() > 0) {
                                            Log.d("HTTP", "non-null & non-empty tokens found");
                                            for (HashMap<String, Object> app : getApps().getValue()) {
                                                // Get login URL
                                                HashMap<String, String> requests = (HashMap<String, String>) app.get("requests");
                                                String app_login_url = requests.get("login");
                                                Log.d("HTTP", app_login_url);

                                                // Insert app key
                                                String app_key = found_keys.get(app.get("name"));
                                                String url = app_login_url.replaceAll("REPLACEME1", app_key);
                                                Log.d("HTTP", url);

                                                // Insert request token
                                                String request_token = getRequestTokens().getValue().get(app.get("name"));
                                                url = app_login_url.replaceAll("REPLACEME", request_token);
                                                Log.d("HTTP", url);

                                                // Request access token
                                                // Make http request using URL
                                                httph.make_volley_request(url,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                // Cut token out of html response
                                                                Log.d("API", "Access Response is: "+ response.substring(5));

                                                                // Add new request token
                                                                HashMap<String, String> new_access_tokens = getAccessTokens().getValue();
                                                                if(new_access_tokens == null){
                                                                    new_access_tokens = new HashMap<String, String>();
                                                                }
                                                                new_access_tokens.put("access_token",response.substring(5));
                                                                setAccessTokens(new_access_tokens);
                                                                Log.d("API access", getAccessTokens().getValue().toString());
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Log.d("API", "api get failed: " + error.getMessage());
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    // Sets
    public void setUserId(String userId) { this.userId.setValue(userId); }
    public void setDisplayName(String displayName) { this.displayName.setValue(displayName); }
    public void setEmail(String email) { this.email.setValue(email); }
    public void setArticleIDs(ArrayList<String> articles) { this.articleIDs.setValue(articles); }
    public void setArticles(ArrayList<HashMap<String, String>> articles) { this.articles.setValue(articles); }
    public void setApps(ArrayList<HashMap<String, Object>> apps) { this.apps.setValue(apps); }
    public void setVeracity(String veracity) { this.veracity.setValue(veracity); }
    public void setAppKeys(HashMap<String, String> appKeys) { this.appKeys.setValue(appKeys); }
    public void setRequestTokens(HashMap<String, String> requestTokens) { this.requestTokens.setValue(requestTokens); }
    public void setAccessTokens(HashMap<String, String> accessTokens) { this.accessTokens.setValue(accessTokens); }
    public void setAppIDs(ArrayList<String> appIDs) { this.appIDs.setValue(appIDs); }

    // Gets
    public LiveData<String> getUserId() { return userId; }
    public LiveData<String> getDisplayName() { return displayName; }
    public LiveData<ArrayList<String>> getArticleIDs() { return articleIDs; }
    public LiveData<ArrayList<HashMap<String, String>>> getArticles() { return articles; }
    public LiveData<ArrayList<HashMap<String, Object>>> getApps() { return apps; }
    public LiveData<String> getOverallVeracity() { return veracity; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<HashMap<String, String>> getAppKeys() { return appKeys; }
    public LiveData<HashMap<String, String>> getRequestTokens() { return requestTokens; }
    public LiveData<HashMap<String, String>> getAccessTokens() { return accessTokens; }
    public LiveData<ArrayList<String>> getAppIDs() { return appIDs; }


}
