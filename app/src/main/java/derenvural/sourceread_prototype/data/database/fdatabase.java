package derenvural.sourceread_prototype.data.database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Map;

import derenvural.sourceread_prototype.data.cards.Article;

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

    // WRITE METHODS
    /*
     * Write (add a new) entry to user document field in database
     * */
    public void add_user_field(String field, Object new_data, OnCompleteListener end){
        // Get reference to user document in 'users' collection
        DocumentReference user_request = get_current_user_request();

        // Execute database update with onCompleteListener
        user_request.update(field, new_data).addOnCompleteListener(end);
    }
    /*
     * Write (overwrite) user document field in database
     * */
    public void update_user_field(String field, Object new_data, OnCompleteListener end) {
        // Get reference to user document in 'users' collection
        DocumentReference user_request = get_current_user_request();

        // Execute database update with onCompleteListener
        user_request.update(field, new_data).addOnCompleteListener(end);
    }
    /*
     * Write (overwrite) article document field in database
     * */
    public void update_article_field(final Article article, final String field, OnCompleteListener end) {
        // Create a Map of the object
        Map<String, Object> docData = article.map_data();

        // Get reference to article document in 'articles' collection
        DocumentReference article_request = get_article_request(article.getDatabase_id());

        // Execute database update with onCompleteListener
        article_request.update(field, docData.get(field)).addOnCompleteListener(end);
    }
    /*
     * Write (new initial) article documents to database collection
     * */
    public void write_new_articles(ArrayList<Article> articles, OnCompleteListener end) {
        // Create batch db write
        WriteBatch batch = db.batch();
        for(Article article : articles) {
            // Create a Map of the object
            Map<String, Object> docData = article.map_data();

            // Get reference to 'articles' collection and add new article
            DocumentReference article_request = get_articles_request().document();
            batch.set(article_request,docData);
        }

        // Commit the batch
        batch.commit().addOnCompleteListener(end);
    }
    /*
     * Write (new initial) article document to database collection
     * */
    public void write_new_article(Article article, OnCompleteListener end) {
        // Create a Map of the object
        Map<String, Object> docData = article.map_data();

        // Get reference to 'articles' collection
        CollectionReference articles_request = get_articles_request();

        // Execute database write with onCompleteListener
        articles_request.add(docData).addOnCompleteListener(end);
    }
    /*
     * Delete a user document from database
     * */
    public void delete_user(OnCompleteListener end) {
        // Get reference to article document in 'articles' collection
        DocumentReference user_request = get_current_user_request();

        // Execute database delete with onCompleteListener
        user_request.delete().addOnCompleteListener(end);
    }



    // READ METHODS
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
     * Request all article data
     * */
    public void request_article_data(String article, OnCompleteListener end){
        // Get reference to article document in 'articles' collection
        DocumentReference article_request = get_article_request(article);

        // Execute database read with onCompleteListener
        article_request.get().addOnCompleteListener(end);
    }
    /*
     * Request all app data
     * */
    public void request_apps(OnCompleteListener end){
        // Get reference to app document in 'apps' collection
        CollectionReference apps_request = get_apps_request();

        // Execute database read with onCompleteListener
        apps_request.whereEqualTo("validapp", true)
        .get().addOnCompleteListener(end);
    }
    /*
     * Check document in database exists
     * */
    public void request_articles(OnCompleteListener end){
        // Get reference to 'articles' collection
        CollectionReference articles_request = get_articles_request();

        // Execute database query with onCompleteListener
        articles_request.get().addOnCompleteListener(end);
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
    public CollectionReference get_articles_request(){
        if(mAuth.getUid() != null) {
            return db.collection("articles");
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
    public CollectionReference get_apps_request(){
        if(mAuth.getUid() != null) {
            return db.collection("apis");
        }else{
            return null;
        }
    }
}
