package derenvural.sourceread_prototype.data.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import derenvural.sourceread_prototype.data.article.Article;
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
     * Write an article id field to database
    public void write_article_id(final String new_id){
        // Make update attempt
        DocumentReference user_request = get_current_user_request();
        user_request.update("articles", FieldValue.arrayUnion(new_id)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("DB saved article ID", new_id);
                }else{
                    // Log error
                    Log.e("DB", "write failed: ", task.getException());
                }
            }
        });
    }
     * */
    /*
     * Write an article id field to database
     * */
    public void add_user_field(String field, Object new_data){
        // Make update attempt
        DocumentReference user_request = get_current_user_request();
        user_request.update(field, FieldValue.arrayUnion(new_data)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("DB insert", "done");
                }else{
                    // Log error
                    Log.e("DB", "write failed: ", task.getException());
                }
            }
        });
    }

    /*
     * Write a user field to database
     * */
    public void update_user_field(String field, Object new_data) {
        // Make update attempt
        DocumentReference user_request = get_current_user_request();
        user_request.update(field, new_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("DB update","done");
                }else{
                    // Log error
                    Log.e("DB", "write failed: ", task.getException());
                }
            }
        });
    }

    /*
     * Write an user veracity field to database
     * */
    public void write_user_veracity(LoggedInUser user){
        //
    }

    /*
     * Update an article document in database
     * */
    public void update_article_field(Article article, LoggedInUser user, final String field) {
        // Create a Map to fill
        Map<String, Object> docData = article.map_data();

        // Get ID of article
        for(int i=0;i<user.getArticles().getValue().size();i++){
            if(user.getArticles().getValue().get(i).getResolved_title().equals(article.getResolved_title())){
                final String id = user.getArticleIDs().getValue().get(i);

                // Make update attempt
                DocumentReference article_request = get_article_request(id);
                article_request.update(field, docData.get(field)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Get database id
                        Log.d("DB", "updated article - '"+id+"' field - '"+field+"'");
                    }
                });

                break;
            }
        }
    }

    /*
     * Write an article document to database
     * */
    public void write_new_article(final Article article, final LoggedInUser user) {
        // Create a Map to fill
        Map<String, Object> docData = article.map_data();

        // make write attempt
        db.collection("articles").add(docData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    // Get database id
                    String id = task.getResult().getId();
                    Log.d("DB", "saved article - "+id);

                    // Add to user object
                    user.addArticleID(id);
                    user.addArticle(article);

                    // Update user db entry
                    add_user_field("articles", id);
                    //write_article_id(id);
                }else{
                    // Log error
                    Log.e("DB", "write failed - ", task.getException());
                }
            }
        });
    }

    /*
     * Request all user data
     * */
    public void request_user_data(OnCompleteListener end){
        // Get reference to user document in 'users' collection
        DocumentReference user_request = get_current_user_request();

        // Execute database read with onCompleteListener
        user_request.get().addOnCompleteListener(end);
    }
    /*
     * Request all app data
     * */
    public void request_app_data(String app_name, OnCompleteListener end){
        // Get reference to app document in 'apps' collection
        DocumentReference app_request = get_app_request(app_name);

        // Execute database read with onCompleteListener
        app_request.get().addOnCompleteListener(end);
    }
    /*
     * Request all app data
     * */
    public void request_article_data(String article, OnCompleteListener end){
        DocumentReference article_request = get_article_request(article);
        article_request.get().addOnCompleteListener(end);
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
