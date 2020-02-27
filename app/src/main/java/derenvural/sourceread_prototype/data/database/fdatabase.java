package derenvural.sourceread_prototype.data.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class fdatabase {
    // Objects
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /*
    * Constructor
    * */
    public fdatabase(){
        // Objects
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /*
     * Request all user data
     * */
    public void request_user_data(final LoggedInUser user){
        DocumentReference user_request = get_current_user_request();
        user_request.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Get list of articles
                    DocumentSnapshot document = task.getResult();
                    user.setVeracity((String) document.get("veracity"));
                    user.setAppIDs((ArrayList<String>) document.get("apps"));
                    user.setArticleIDs((ArrayList<String>) document.get("articles"));
                } else {
                    // Log error
                    Log.e("DB", "get failed: ", task.getException());
                }
            }
        });
    }
    /*
     * Request all app data
     * */
    public void request_app_data(final LoggedInUser user){
        final ArrayList<HashMap<String, Object>> apps = new ArrayList<HashMap<String, Object>>();
        for(final String app : user.getAppIDs().getValue()) {
            DocumentReference app_request = get_app_request(app);
            app_request.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Get list of articles
                        DocumentSnapshot document = task.getResult();

                        // Populate hash map
                        HashMap<String, Object> this_app = new HashMap<String, Object>();
                        this_app.put("name", app);
                        this_app.put("description", document.get("description").toString());
                        this_app.put("key", document.get("key").toString());
                        this_app.put("requests", document.get("requests"));

                        // Add to list
                        apps.add(this_app);
                        user.setApps(apps);
                    } else {
                        // Log error
                        Log.e("DB", "get failed: ", task.getException());
                    }
                }
            });
        }
    }
    /*
     * Request all app data
     * */
    public void request_article_data(final LoggedInUser user){
        final ArrayList<HashMap<String, String>> articles = new ArrayList<HashMap<String, String>>();
        for(final String article : user.getArticleIDs().getValue()) {
            DocumentReference article_request = get_article_request(article);
            article_request.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Get list of articles
                        DocumentSnapshot document = task.getResult();

                        // Populate hash map
                        HashMap<String, String> this_article = new HashMap<String, String>();
                        this_article.put("id", article);
                        this_article.put("title", document.get("title").toString());
                        this_article.put("app", document.get("app").toString());
                        this_article.put("author", document.get("author").toString());
                        this_article.put("author_veracity", document.get("author_veracity").toString());
                        this_article.put("publication", document.get("publication").toString());
                        this_article.put("publication_veracity", document.get("publication_veracity").toString());
                        this_article.put("veracity", document.get("veracity").toString());

                        // Add to list
                        articles.add(this_article);
                        user.setArticles(articles);
                    } else {
                        // Log error
                        Log.e("DB", "get failed: ", task.getException());
                    }
                }
            });
        }
    }

    /*
     * Methods for forming valid DocumentReference requests
     * */
    public DocumentReference get_current_user_request(){
        if(mAuth.getUid() != null) {
             return db.collection("users").document(mAuth.getUid());
        }else{
            return null;
        }
    }
    public DocumentReference get_article_request(@NonNull String article_id){
        if(mAuth.getUid() != null) {
            return db.collection("articles").document(article_id);
        }else{
            return null;
        }
    }
    public DocumentReference get_app_request(@NonNull String app_id){
        if(mAuth.getUid() != null) {
            return db.collection("apis").document(app_id);
        }else{
            return null;
        }
    }
}
