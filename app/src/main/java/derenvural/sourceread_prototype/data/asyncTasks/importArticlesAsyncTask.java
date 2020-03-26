package derenvural.sourceread_prototype.data.asyncTasks;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import derenvural.sourceread_prototype.data.article.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class importArticlesAsyncTask extends sourcereadAsyncTask<LoggedInUser> {
    // Tools
    private fdatabase db;
    private httpHandler httph;
    // Data
    private HashMap<String, Object> app;
    // Activity
    private final WeakReference<Context> context;

    public importArticlesAsyncTask(Context context, LoggedInUser user, httpHandler httph, fdatabase db, HashMap<String, Object> app){
        super();
        // Activity
        this.context = new WeakReference<>(context);;

        // Data
        setData(user);
        this.app = app;

        // Tools
        this.db = db;
        this.httph = httph;
    }

    @Override
    protected Void doInBackground(Void... params){
        // Fetch user
        final LoggedInUser user = getData().getValue();

        // Get get URL
        HashMap<String, String> requests = (HashMap<String, String>) app.get("requests");
        String url = requests.get("articles");

        // Fetch access token
        String app_key = app.get("key").toString();
        String access_token = user.getAccessTokens().getValue().get(app.get("name"));

        // Add JSON parameters
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("consumer_key", app_key);
        parameters.put("access_token", access_token);
        parameters.put("sort", "newest");
        parameters.put("detailType", "complete");
        parameters.put("contentType", "article");
        parameters.put("state", "all");

        // Fetch timestamp
        for(String app_name: user.getAppIDs().getValue().keySet()){
            if(user.getAppIDs().getValue() != null && !user.getAppIDs().getValue().get(app_name).equals("")){
                long previous_request = Long.parseLong(user.getAppIDs().getValue().get(app_name).toString());

                // Catch for first time import
                if(previous_request != 0) {
                    parameters.put("since", Long.toString(previous_request));
                }
            }
        }

        // Request access token by http request to URL
        httph.make_volley_request(url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(!response.getString("status").equals("2")) {
                                // Get articles from JSON
                                JSONObject articles_json = response.getJSONObject("list");
                                if (articles_json.length() != 0) {
                                    ArrayList<Article> articles = new ArrayList<Article>();
                                    for (Iterator<String> it = articles_json.keys(); it.hasNext(); ) {
                                        // Add article information to user object for display
                                        Article current_article = new Article(articles_json.getJSONObject(it.next()), app.get("name").toString());
                                        articles.add(current_article);

                                        // Add new article to database
                                        db.write_new_article(current_article, user);
                                    }
                                }

                                // Create map object containing timestamp with correct format
                                long request_stamp = Instant.now().getEpochSecond();
                                Map<String, Object> new_stamp = new HashMap<>();
                                new_stamp.put(app.get("name").toString(), request_stamp);

                                // store timestamp
                                //String app_name, String field, Object new_data)
                                db.update_user_field("apps", new_stamp);

                                // Fetch non-null context
                                if (context!=null) {
                                    Context main = context.get();

                                    // Notify user
                                    Toast.makeText(main, "Articles imported!", Toast.LENGTH_SHORT).show();
                                }

                                // End task
                                postData(user);
                                postDone(true);
                            }else{
                                // Fetch non-null context
                                if (context!=null) {
                                    Context main = context.get();

                                    // Notify user if no new articles
                                    Toast.makeText(main, "No new articles to import..", Toast.LENGTH_SHORT).show();
                                }

                                // End task
                                postData(user);
                                postDone(true);
                            }
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

        return null;
    }
}
