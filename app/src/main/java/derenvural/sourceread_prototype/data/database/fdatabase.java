package derenvural.sourceread_prototype.data.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class fdatabase {

    private MutableLiveData<ArrayList<String>> articles = new MutableLiveData<ArrayList<String>>();
    private MutableLiveData<ArrayList<HashMap<String,Object>>> current_articles = new MutableLiveData<>();
    private MutableLiveData<HashMap<String,String>> apps = new MutableLiveData<HashMap<String,String>>();
    private MutableLiveData<String> current_app_key = new MutableLiveData("");
    private MutableLiveData<ArrayList<HashMap<String,Object>>> current_apps = new MutableLiveData<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public fdatabase(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        articles.setValue(new ArrayList<String>());
        current_articles.setValue(new ArrayList<HashMap<String,Object>>());
        apps.setValue(new HashMap<String,String>());
        current_apps.setValue(new ArrayList<HashMap<String,Object>>());
    }

    public void request_user_apps(){
        DocumentReference user_request = get_current_user_request();
        user_request.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Get list of articles
                    DocumentSnapshot document = task.getResult();
                    apps.setValue((HashMap<String,String>) document.get("keys"));
                } else {
                    // Log error
                    Log.d("DB", "get failed: ", task.getException());
                }
            }
        });
    }

    public void request_user_articles(){
        DocumentReference user_request = get_current_user_request();
        user_request.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Get list of articles
                    DocumentSnapshot document = task.getResult();
                    articles.setValue((ArrayList<String>) document.get("articles"));
                } else {
                    Log.d("DB", "get failed: ", task.getException());
                }
            }
        });
    }

    public void request_article_data(@NonNull ArrayList<String> articles){
        // Fetch article data for each
        for(String id : articles) {
            DocumentReference article_request = get_article_request(id);
            article_request.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Get list of articles
                        DocumentSnapshot document = task.getResult();
                        ArrayList<HashMap<String, Object>> new_list = current_articles.getValue();
                        new_list.add((HashMap<String, Object>) document.getData());
                        current_articles.setValue(new_list);
                    } else {
                        Log.d("DB", "get failed: ", task.getException());
                    }
                }
            });
        }
    }

    public void request_app_data(@NonNull Object[] apps){
        // Fetch article data for each
        for(Object id : apps) {
            DocumentReference app_request = get_app_request(id.toString());
            app_request.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Get list of apps
                        DocumentSnapshot document = task.getResult();
                        ArrayList<HashMap<String, Object>> new_list = current_apps.getValue();
                        new_list.add((HashMap<String, Object>) document.getData());
                        current_apps.setValue(new_list);
                    } else {
                        Log.d("DB", "get failed: ", task.getException());
                    }
                }
            });
        }
    }

    public void request_app_key(@NonNull Object[] apps){
        // Fetch article data for each
        for(Object id : apps) {
            DocumentReference app_request = get_app_request(id.toString());
            app_request.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Get list of apps
                        DocumentSnapshot document = task.getResult();
                        final HashMap<String, Object> app = (HashMap<String, Object>) document.getData();
                        current_app_key.setValue(app.get("key").toString());
                    } else {
                        Log.d("DB", "get failed: ", task.getException());
                    }
                }
            });
        }
    }

    public void request_app_card(@NonNull Object[] apps){
        // Fetch article data for each
        for(Object id : apps) {
            DocumentReference app_request = get_app_request(id.toString());
            app_request.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Get list of apps
                        //DocumentSnapshot document = task.getResult();
                        //ArrayList<HashMap<String, Object>> new_list = current_apps.getValue();
                        //new_list.add((HashMap<String, Object>) document.getData());
                        //current_articles.setValue(new_list);
                    } else {
                        Log.d("DB", "get failed: ", task.getException());
                    }
                }
            });
        }
    }

    public LiveData<ArrayList<HashMap<String,Object>>> get_current_apps(){
        if(mAuth.getUid() != null) {
            return current_apps;
        }else{
            return null;
        }
    }
    public LiveData<ArrayList<HashMap<String,Object>>> get_current_articles(){
        if(mAuth.getUid() != null) {
            return current_articles;
        }else{
            return null;
        }
    }
    public LiveData<ArrayList<String>> get_user_articles(){
        if(mAuth.getUid() != null) {
            return articles;
        }else{
            return null;
        }
    }
    public LiveData<HashMap<String,String>> get_user_apps(){
        if(mAuth.getUid() != null) {
            return apps;
        }else{
            return null;
        }
    }
    public LiveData<String> get_user_app_key(){
        if(mAuth.getUid() != null) {
            return current_app_key;
        }else{
            return null;
        }
    }



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
