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

import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.cards.Article;
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

                    // Create empty app objects with name & timestamp
                    ArrayList<App> apps = new ArrayList<App>();
                    for(String app_name: found_apps.keySet()){
                        apps.add(new App(app_name, Long.parseLong(found_apps.get(app_name).toString())));
                    }

                    // Add analysis & article ID's to user
                    user.setArticleIDs(found_articles);
                    user.setVeracity((String) document.get("veracity"));

                    // Request app data
                    if (apps.size() > 0) {
                        for(final App app : apps) {
                            db.request_app_data(app.getTitle(), new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        // Get app
                                        DocumentSnapshot document = task.getResult();

                                        // Create app object
                                        app.CreateApp(document);

                                        // Add to list
                                        user.addApp(app);

                                        // Ask for request token for authentication
                                        request_token(app, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                // Cut token out of html response
                                                Log.d("API", "Request Response recieved");

                                                try {
                                                    // Add new request token
                                                    app.setRequestToken(response.getString("code"));

                                                    // End task
                                                    postData(user);
                                                    postDone(true);

                                                    // Open app in browser for authentication Creates callback
                                                    open_app(app, response.getString("code"));
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

    public void request_token(App app, Response.Listener<JSONObject> responseListener){
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

    public void open_app(App app, String token){
        // Get login URL
        HashMap<String, String> requests = app.getRequests();
        String app_login_url = requests.get("login");

        // Insert request token
        String url = app_login_url.replaceAll("REPLACEME", token);

        // Fetch non-null context
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
