package derenvural.sourceread_prototype.data.asyncTasks;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.cards.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class importArticlesAsyncTask extends sourcereadAsyncTask<App, ArrayList<Article>> {
    // Tools
    private fdatabase db;
    private httpHandler httph;
    // Data
    private LoggedInUser user;
    // Activity
    private final WeakReference<Context> context;

    public importArticlesAsyncTask(Context context, LoggedInUser user, httpHandler httph, fdatabase db){
        super();
        // Activity
        this.context = new WeakReference<>(context);;

        // Data
        this.user = user;

        // Tools
        this.db = db;
        this.httph = httph;
    }

    @Override
    protected ArrayList<Article> doInBackground(App... params){
        // Fetch app
        final App app = params[0];

        // Get get URL
        String url = app.getRequests().get("articles");

        // Fetch access token
        String app_key = app.getKey();
        String access_token = app.getAccessToken();

        // Add JSON parameters
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("consumer_key", app_key);
        parameters.put("access_token", access_token);
        parameters.put("sort", "newest");
        parameters.put("detailType", "complete");
        parameters.put("contentType", "article");
        parameters.put("state", "all");

        // Fetch timestamp
        if(app.getTimestamp() != 0){
            // Catch for first time import
            parameters.put("since", Long.toString(app.getTimestamp()));
        }

        // List of articles
        final ArrayList<Article> articles = new ArrayList<Article>();

        // Request access token by http request to URL
        httph.make_volley_request_post(url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(!response.getString("status").equals("2")) {
                                // Get articles from JSON
                                final JSONObject articles_json = response.getJSONObject("list");
                                if (articles_json.length() != 0) {
                                    // Fetch list of old saved articles
                                    db.request_articles(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> checkTask) {
                                            if (checkTask.isSuccessful()) {
                                                // Get list of currently stored articles
                                                List<DocumentSnapshot> documents = checkTask.getResult().getDocuments();
                                                ArrayList<String> articleList = new ArrayList<String>();
                                                for(DocumentSnapshot document: documents){
                                                    articleList.add(document.getId());
                                                }
                                                ArrayList<Article> tobewritten = new ArrayList<Article>();
                                                final ArrayList<String> idstobewritten = new ArrayList<String>();

                                                // Check each article
                                                for (final Iterator<String> it = articles_json.keys(); it.hasNext(); ) {
                                                    try{
                                                        // Add article information to user object for display
                                                        final String current_id = it.next();
                                                        final Article current_article = new Article(articles_json.getJSONObject(current_id), app.getTitle());
                                                        articles.add(current_article);

                                                        // If an article already exists
                                                        boolean exists = false;
                                                        for(String id : articleList){
                                                            if(id.equals(current_article.getTitle())){
                                                                // Notify user
                                                                Log.d("DB", "already saved article - "+id);

                                                                // Add to idstobewritten list and end loop
                                                                idstobewritten.add(id);
                                                                exists = true;
                                                                break;
                                                            }
                                                        }
                                                        if(!exists){
                                                            // Notify user
                                                            Log.d("DB", "new article!");

                                                            // Add to be tobewritten list
                                                            tobewritten.add(current_article);
                                                        }
                                                    }catch(JSONException error){
                                                        Log.e("JSON", "error reading JSON: " + error.getMessage());
                                                    }
                                                }

                                                // Write all new articles to database
                                                final ArrayList<String> newIds = db.write_new_articles(tobewritten, new OnCompleteListener<CollectionReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<CollectionReference> writeManyTask) {
                                                        if (writeManyTask.isSuccessful()) {
                                                            Log.d("DB", "saved new articles!");
                                                        }else{
                                                            // Log error
                                                            Log.e("DB", "write failed: ", writeManyTask.getException());
                                                        }
                                                    }
                                                });

                                                // Add all article ID's
                                                idstobewritten.addAll(newIds);

                                                // Update user 'articles' field in database
                                                db.add_user_fields("articles.", idstobewritten, app.getTitle(), new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task userFieldTask) {
                                                        if (userFieldTask.isSuccessful()) {

                                                            // Notify user
                                                            Log.d("DB", "insert done");
                                                            Toast.makeText(context.get(), "Articles imported!", Toast.LENGTH_SHORT).show();

                                                            // End task
                                                            postData(articles);
                                                            postDone(true);
                                                        }else{
                                                            // Log error
                                                            Log.e("DB", "insert failed: ", userFieldTask.getException());
                                                        }
                                                    }
                                                });
                                            }else{
                                                // Log error
                                                Log.e("DB", "read failed - ", checkTask.getException());
                                                postData(articles);
                                                postDone(true);
                                            }
                                        }
                                    });
                                }
                            }else{
                                // Fetch non-null context
                                if (context!=null) {
                                    Context main = context.get();

                                    // Notify user if no new articles
                                    Toast.makeText(main, "No new articles to import..", Toast.LENGTH_SHORT).show();
                                }

                                // End task
                                postData(articles);
                                postDone(true);
                            }
                        }catch(JSONException error){
                            Log.e("JSON", "error reading JSON: " + error.getMessage());
                            postData(articles);
                            postDone(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API", "error - api get failed: " + error.getMessage());
                        postData(articles);
                        postDone(true);
                    }
                }
        );

        return articles;
    }
}
