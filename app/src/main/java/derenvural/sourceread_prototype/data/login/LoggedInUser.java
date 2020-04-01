package derenvural.sourceread_prototype.data.login;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import derenvural.sourceread_prototype.MainActivity;
import derenvural.sourceread_prototype.data.asyncTasks.deleteArticleAsyncTask;
import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.cards.Article;
import derenvural.sourceread_prototype.data.asyncTasks.importArticlesAsyncTask;
import derenvural.sourceread_prototype.data.asyncTasks.userAccessAsyncTask;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.ui.article.ArticleActivity;

public class LoggedInUser implements Serializable {
    // Basic data
    private MutableLiveData<String> userId = new MutableLiveData<String>();
    private MutableLiveData<String> displayName = new MutableLiveData<String>();
    private MutableLiveData<String> email = new MutableLiveData<String>();
    // App data
    private MutableLiveData<ArrayList<App>> apps = new MutableLiveData<ArrayList<App>>();
    // Article data
    private MutableLiveData<ArrayList<Article>> articles = new MutableLiveData<ArrayList<Article>>();
    // Statistical data
    private MutableLiveData<String> veracity = new MutableLiveData<String>();
    // Serialisation
    private static final long serialVersionUID = 1L;

    // Standard constructor
    public LoggedInUser(FirebaseUser user) {
        // Populate object from database
        setUserId(user.getUid());
        setDisplayName(user.getDisplayName());
        setEmail(user.getEmail());
    }

    public void loadInstanceState(Bundle outState) {
        // Basic data
        setUserId((String) outState.getSerializable("id"));
        setDisplayName((String) outState.getSerializable("displayName"));
        setEmail((String) outState.getSerializable("email"));
        // App data
        setApps((ArrayList) outState.getSerializable("apps"));
        // Article data
        setArticles((ArrayList) outState.getSerializable("articles"));
        // Statistical data
        setVeracity((String) outState.getSerializable("veracity"));
    }
    public void saveInstanceState(Bundle bundle) {
        // Basic data
        bundle.putSerializable("id", getUserId().getValue());
        bundle.putSerializable("displayName", getDisplayName().getValue());
        bundle.putSerializable("email", getEmail().getValue());
        // App data
        bundle.putSerializable("apps", getApps().getValue());
        // Article data
        bundle.putSerializable("articles", getArticles().getValue());
        // Statistical data
        bundle.putSerializable("veracity", getVeracity().getValue());
    }

    public void writeObject(ObjectOutputStream stream) throws IOException {
        // Basic data
        stream.writeObject(getUserId().getValue());
        stream.writeObject(getDisplayName().getValue());
        stream.writeObject(getEmail().getValue());
        // App data
        stream.writeObject(getApps().getValue());
        // Article data
        stream.writeObject(getArticles().getValue());
        // Statistical data
        stream.writeObject(getVeracity().getValue());
    }

    public void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        // Basic data
        setUserId((String) stream.readObject());
        setDisplayName((String) stream.readObject());
        setEmail((String) stream.readObject());
        // App data
        setApps((ArrayList) stream.readObject());
        // Article data
        setArticles((ArrayList) stream.readObject());
        // Statistical data
        setVeracity((String) stream.readObject());
    }

    public void access_tokens(final MainActivity main, httpHandler httph, String app_name){
        // Create async task
        final userAccessAsyncTask task = new userAccessAsyncTask(httph, app_name);

        // execute async task
        task.execute(this);

        // Check for task finish
        task.getDone().observe(main, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    Log.d("TASK", "access tokens task done!");

                    // Set display nameset
                    setApps(task.getData().getValue());

                    // Reactivate the UI
                    main.activate_interface();
                }
            }
        });
    }

    public void importArticles(final MainActivity main, httpHandler httph, final fdatabase db, final App app){
        // Create async task
        final importArticlesAsyncTask task = new importArticlesAsyncTask(main, this, httph, db);

        // execute async task
        task.execute(app);

        // Check for task finish
        task.getDone().observe(main, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    // Retrieve data
                    setArticles(task.getData().getValue());

                    Log.d("TASK", "articles import task done!");

                    // Create map object containing timestamp with correct format
                    final long request_stamp = Instant.now().getEpochSecond();
                    HashMap<String, Object> new_stamp = new HashMap<String, Object>();
                    new_stamp.put(app.getTitle(), request_stamp);

                    // Store timestamp in database & user object
                    db.update_user_field("apps", new_stamp, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> stamp_task) {
                            if (stamp_task.isSuccessful()) {
                                // Store timestamp in user object
                                ArrayList<App> old_apps = getApps().getValue();
                                ArrayList<App> new_apps = new ArrayList<App>();
                                for(App this_app : old_apps) {
                                    if (this_app.getTitle().equals(app.getTitle())) {
                                        // store timestamp
                                        this_app.setTimestamp(request_stamp);
                                        new_apps.add(this_app);
                                    }else{
                                        new_apps.add(this_app);
                                    }
                                }
                                setApps(new_apps);

                                // Notify user
                                Log.d("DB","update done");
                                Toast.makeText(main, "All your articles from "+app.getTitle()+" imported!", Toast.LENGTH_SHORT).show();

                                // Reactivate the UI
                                main.activate_interface();
                            }else{
                                // Log error
                                Log.e("DB", "write failed: ", stamp_task.getException());
                            }
                        }
                    });
                }
            }
        });
    }

    public void deleteAllArticles(final MainActivity main, final fdatabase db, final String app_name){
        // Build list of articles to remove
        ArrayList<Article> articles = new ArrayList<Article>();
        for(Article article : getArticles().getValue()){
            if(article.getApp().equals(app_name)) {
                articles.add(article);
            }
        }

        // Create async task
        final deleteArticleAsyncTask task = new deleteArticleAsyncTask(this, db);

        // execute async task
        task.execute(articles);

        // Check for task finish
        task.getDone().observe(main, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    Log.d("TASK", "article deletion task done!");

                    // Retrieve data
                    setArticles(task.getData().getValue());

                    // Replace timestamp with invalid
                    ArrayList<App> old_apps = getApps().getValue();
                    ArrayList<App> new_apps = new ArrayList<App>();
                    for(App this_app : old_apps){
                        if(this_app.getTitle().equals(app_name)){
                            // Store timestamp in user object
                            this_app.setTimestamp(0l);
                            new_apps.add(this_app);

                            // Store timestamp in database
                            Map<String, Object> new_stamp = new HashMap<>();
                            new_stamp.put(app_name, 0l);
                            db.update_user_field("apps", new_stamp, new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> stamp_task) {
                                    if (stamp_task.isSuccessful()) {
                                        Log.d("DB","update done");

                                        // Reactivate the UI
                                        Toast.makeText(main, "All articles imported from "+app_name+" deleted!", Toast.LENGTH_SHORT).show();
                                        main.activate_interface();
                                    }else{
                                        // Log error
                                        Log.e("DB", "update failed: ", stamp_task.getException());
                                    }
                                }
                            });
                        }else{
                            new_apps.add(this_app);
                        }
                    }
                    setApps(new_apps);
                }
            }
        });
    }

    public void deleteArticle(final ArticleActivity aa, fdatabase db, final ArrayList<Article> articles){
        // Create async task
        final deleteArticleAsyncTask task = new deleteArticleAsyncTask(this, db);

        // execute async task
        task.execute(articles);

        // Check for task finish
        task.getDone().observe(aa, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    Log.d("TASK", "article deletion task done!");

                    // Retrieve data
                    setArticles(task.getData().getValue());

                    // Reactivate the UI & redirect to main (no article for fragment to display)
                    aa.activate_interface();
                    aa.main_redirect();
                }
            }
        });
    }

    // ArrayList item addition
    public void addArticle(Article new_article){
        // Get previous articles
        ArrayList<Article> old_articles = getArticles().getValue();
        if(old_articles == null){
            old_articles = new ArrayList<Article>();
        }

        // Add new article
        old_articles.add(new_article);
        setArticles(old_articles);
    }
    public void addApp(App new_app){
        // Get previous apps
        ArrayList<App> apps = getApps().getValue();
        if (apps == null) {
            apps = new ArrayList<App>();
        }

        // Add new app
        apps.add(new_app);
        setApps(apps);
    }

    // SET
    public void setUserId(String userId) { this.userId.setValue(userId); }
    public void setDisplayName(String displayName) { this.displayName.setValue(displayName); }
    public void setEmail(String email) { this.email.setValue(email); }
    public void setApps(ArrayList<App> apps) { this.apps.setValue(apps); }
    public void setArticles(ArrayList<Article> articles) { this.articles.setValue(articles); }
    public void setVeracity(String veracity) { this.veracity.setValue(veracity); }

    // GET
    public LiveData<String> getUserId() { return userId; }
    public LiveData<String> getDisplayName() { return displayName; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<ArrayList<App>> getApps() { return apps; }
    public LiveData<ArrayList<Article>> getArticles() { return articles; }
    public LiveData<String> getVeracity() { return veracity; }
}
