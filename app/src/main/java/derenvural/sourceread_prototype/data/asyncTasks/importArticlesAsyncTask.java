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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.cards.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class importArticlesAsyncTask extends sourcereadAsyncTask<LoggedInUser> {
    // Tools
    private fdatabase db;
    private httpHandler httph;
    // Data
    private App app;
    // Activity
    private final WeakReference<Context> context;

    public importArticlesAsyncTask(Context context, LoggedInUser user, httpHandler httph, fdatabase db, App app){
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

        // Request access token by http request to URL
        httph.make_volley_request(url, parameters,
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
                                        public void onComplete(@NonNull Task<QuerySnapshot> check_task) {
                                            if (check_task.isSuccessful()) {
                                                // Get list of currently stored articles
                                                List<DocumentSnapshot> documents = check_task.getResult().getDocuments();
                                                ArrayList<String> articleList = new ArrayList<String>();
                                                for(DocumentSnapshot document: documents){
                                                    articleList.add(document.getId());
                                                }

                                                // Check each article
                                                ArrayList<Article> articles = new ArrayList<Article>();
                                                for (Iterator<String> it = articles_json.keys(); it.hasNext(); ) {
                                                    try{
                                                        // Add article information to user object for display
                                                        final Article current_article = new Article(articles_json.getJSONObject(it.next()), app.getTitle());
                                                        articles.add(current_article);

                                                        // If an article already exists
                                                        boolean exists = false;
                                                        for(String id : articleList){
                                                            if(id.equals(current_article.getTitle())){
                                                                // Update user for existing article
                                                                Log.d("DB", "already saved article - "+id);

                                                                // Add to user object
                                                                current_article.setDatabase_id(id);
                                                                user.addArticle(current_article);

                                                                // Update user db entry
                                                                db.add_user_field("articles."+id, app.getTitle(), new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> old_task) {
                                                                        if (old_task.isSuccessful()) {
                                                                            Log.d("DB insert", "done");
                                                                        }else{
                                                                            // Log error
                                                                            Log.e("DB", "write failed: ", old_task.getException());
                                                                        }
                                                                    }
                                                                });
                                                                exists = true;
                                                                break;
                                                            }
                                                        }
                                                        if(!exists){
                                                            // Add new article to database
                                                            db.write_new_article(current_article, new OnCompleteListener<DocumentReference>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentReference> write_task) {
                                                                    if (write_task.isSuccessful()) {
                                                                        // Get database id
                                                                        String id = write_task.getResult().getId();
                                                                        Log.d("DB", "saved article - "+id);

                                                                        // Add to user object
                                                                        current_article.setDatabase_id(id);
                                                                        user.addArticle(current_article);

                                                                        // Update user db entry
                                                                        db.add_user_field("articles."+id, app.getTitle(), new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> new_task) {
                                                                                if (new_task.isSuccessful()) {
                                                                                    Log.d("DB insert", "done");
                                                                                }else{
                                                                                    // Log error
                                                                                    Log.e("DB", "write failed: ", new_task.getException());
                                                                                }
                                                                            }
                                                                        });
                                                                    }else{
                                                                        // Log error
                                                                        Log.e("DB", "write failed - ", write_task.getException());
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }catch(JSONException error){
                                                        Log.e("JSON error", "error reading JSON: " + error.getMessage());
                                                    }
                                                }

                                                // Create map object containing timestamp with correct format
                                                long request_stamp = Instant.now().getEpochSecond();
                                                Map<String, Object> new_stamp = new HashMap<>();
                                                new_stamp.put(app.getTitle(), request_stamp);

                                                // store timestamp
                                                app.setTimestamp(request_stamp);
                                                db.update_user_field("apps", new_stamp, new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> stamp_task) {
                                                        if (stamp_task.isSuccessful()) {
                                                            Log.d("DB update","done");
                                                        }else{
                                                            // Log error
                                                            Log.e("DB", "write failed: ", stamp_task.getException());
                                                        }
                                                    }
                                                });

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
                                                // Log error
                                                Log.e("DB", "read failed - ", check_task.getException());
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
