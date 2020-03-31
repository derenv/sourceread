package derenvural.sourceread_prototype.data.login;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import derenvural.sourceread_prototype.MainActivity;
import derenvural.sourceread_prototype.data.asyncTasks.deleteArticleAsyncTask;
import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.cards.Article;
import derenvural.sourceread_prototype.data.asyncTasks.importArticlesAsyncTask;
import derenvural.sourceread_prototype.data.asyncTasks.userAccessAsyncTask;
import derenvural.sourceread_prototype.data.asyncTasks.userPopulateAsyncTask;
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

    //Population Methods
    public void populate(MainActivity main, fdatabase db, httpHandler httph) {
        // Create async task
        userPopulateAsyncTask task = new userPopulateAsyncTask((Context) main, this, db, httph);

        // execute async task
        task.execute();

        // Check for task finish
        task.getDone().observe(main, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    Log.d("TASK", "user data population task done!");
                }
            }
        });
    }

    public void access_tokens(final MainActivity main, httpHandler httph, String app_name){
        // Create async task
        userAccessAsyncTask task = new userAccessAsyncTask(this, httph, app_name);

        // execute async task
        task.execute();

        // Check for task finish
        task.getDone().observe(main, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    Log.d("TASK", "access tokens task done!");

                    // Reactivate the UI
                    main.activate_interface();
                }
            }
        });
    }

    public void importArticles(final MainActivity main, httpHandler httph, fdatabase db, App app){
        // Create async task
        importArticlesAsyncTask task = new importArticlesAsyncTask(main, this, httph, db, app);

        // execute async task
        task.execute();

        // Check for task finish
        task.getDone().observe(main, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    Log.d("TASK", "articles import task done!");

                    // Reactivate the UI
                    main.activate_interface();
                }
            }
        });
    }

    public void deleteArticle(final ArticleActivity aa, fdatabase db, final Article article){
        // Create async task
        deleteArticleAsyncTask task = new deleteArticleAsyncTask(this, db, article);

        // execute async task
        task.execute();

        // Check for task finish
        task.getDone().observe(aa, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    Log.d("TASK", "article '"+article.getTitle()+"' deletion task done!");

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
