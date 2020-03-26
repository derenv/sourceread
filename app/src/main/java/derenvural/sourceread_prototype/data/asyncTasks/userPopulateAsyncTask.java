package derenvural.sourceread_prototype.data.asyncTasks;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.data.article.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.data.login.LoggedInUser;
import derenvural.sourceread_prototype.data.storage.storageSaver;

public class userPopulateAsyncTask extends sourcereadAsyncTask<LoggedInUser> {
    // Tools
    private fdatabase db;
    private httpHandler httph;
    // Activity
    private final WeakReference<Context> context;

    public userPopulateAsyncTask(Context context, LoggedInUser user, fdatabase db, httpHandler httph){
        super();
        // Activity
        this.context = new WeakReference<>(context);;

        // Data
        setData(user);

        // Tools
        this.db = db;
        this.httph = httph;
    }

    @Override
    protected Void doInBackground(Void... params){

        // Fetch user
        final LoggedInUser user = getData().getValue();

        // Request user data
        db.request_user_data(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Get list of data
                    DocumentSnapshot document = task.getResult();
                    HashMap<String, Object> found_apps = (HashMap<String, Object>) document.get("apps");
                    ArrayList<String> found_articles = (ArrayList<String>) document.get("articles");

                    // Add to user
                    user.setAppIDs(found_apps);
                    user.setArticleIDs(found_articles);
                    user.setVeracity((String) document.get("veracity"));

                    // Request app data
                    if (found_apps.size() > 0) {
                        for(final String app_name : found_apps.keySet()) {
                            db.request_app_data(app_name, new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        // Get list of articles
                                        DocumentSnapshot document = task.getResult();

                                        // Populate hash map
                                        final HashMap<String, Object> this_app = new HashMap<String, Object>();
                                        this_app.put("name", app_name);
                                        this_app.put("description", document.get("description").toString());
                                        this_app.put("key", document.get("key").toString());
                                        this_app.put("timestamp", user.getAppIDs().getValue().get(app_name));
                                        this_app.put("requests", document.get("requests"));

                                        // Add to list
                                        user.addApp(this_app);

                                        // Ask for request token for authentication
                                        request_token(this_app, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                // Cut token out of html response
                                                Log.d("API", "Request Response recieved");

                                                try {
                                                    // Create new hash-map
                                                    HashMap<String, String> new_request_token = new HashMap<String, String>();
                                                    new_request_token.put(this_app.get("name").toString(), response.getString("code"));

                                                    // Add new request token
                                                    user.addRequestToken(new_request_token);

                                                    // End task
                                                    postData(user);
                                                    postDone(true);

                                                    // Open app in browser for authentication Creates callback
                                                    open_app(this_app, response.getString("code"));
                                                }catch(JSONException error){
                                                    Log.e("JSON error", "error reading JSON: " + error.getMessage());
                                                }
                                            }
                                        });
                                    } else {
                                        // Log error
                                        Log.e("DB", "read failed: ", task.getException());
                                    }
                                }
                            });
                        }
                    }

                    // Request article data
                    if (found_articles.size() > 0) {
                        for(final String article : user.getArticleIDs().getValue()) {
                            db.request_article_data(article, new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        // Get list of articles
                                        DocumentSnapshot document = task.getResult();

                                        // Populate object
                                        Article this_article = new Article(document, article);

                                        // Add to list
                                        user.addArticle(this_article);
                                    } else {
                                        // Log error
                                        Log.e("DB", "read failed: ", task.getException());
                                    }
                                }
                            });
                        }
                    }
                } else {
                    // Log error
                    Log.e("DB", "read failed: ", task.getException());
                }
            }
        });
        return null;
    }

    public void request_token(HashMap<String, Object> app, Response.Listener<JSONObject> responseListener){
        // Get request token request URL for current app
        HashMap<String, Object> app_requests = (HashMap<String, Object>) app.get("requests");
        String url = app_requests.get("request").toString();

        // Cut out redirect URL from url
        String[] fullUrl = url.split("\\?");
        url = fullUrl[0];
        String redirect_uri = fullUrl[1];

        // Fetch app key
        String app_key = app.get("key").toString();

        // Add JSON parameters
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("consumer_key",app_key);
        parameters.put("redirect_uri",redirect_uri);

        // Make https POST request
        httph.make_volley_request(url, parameters,
                responseListener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API error", "api get failed: " + error.getMessage());
                    }
                }
        );
    }

    public void open_app(HashMap<String, Object> app, String token){
        // Get login URL
        HashMap<String, String> requests = (HashMap<String, String>) app.get("requests");
        String app_login_url = requests.get("login");
        Log.d("HTTP login url request", app_login_url);

        // Insert request token
        String url = app_login_url.replaceAll("REPLACEME", token);
        Log.d("HTTP login url request", url);

        if (context!=null) {
            Context main = context.get();

            // Store this object using local persistence
            if (storageSaver.write(main, getData().getValue().getUserId().getValue(), getData().getValue())) {
                // Redirect to browser for app login
                httph.browser_open(main, url);
            } else {
                Log.e("HTTP login url request", "SAVE FAILURE");
            }
        }
    }
}
