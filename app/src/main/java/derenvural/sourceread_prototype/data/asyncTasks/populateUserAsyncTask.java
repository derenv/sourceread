package derenvural.sourceread_prototype.data.asyncTasks;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class populateUserAsyncTask extends sourcereadAsyncTask<LoggedInUser, LoggedInUser> {
    // Tools
    private fdatabase db;
    private httpHandler httph;
    // Activity
    private final WeakReference<LifecycleOwner> context;
    private boolean triggered;

    public populateUserAsyncTask(LifecycleOwner context, fdatabase db, httpHandler httph){
        super();
        // Activity
        this.context = new WeakReference<>(context);

        // Tools
        this.db = db;
        this.httph = httph;

        this.triggered = false;
    }

    @Override
    protected LoggedInUser doInBackground(LoggedInUser... params){

        // Fetch user
        final LoggedInUser user = params[0];

        // Request user data
        db.request_user_data(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> userTask) {
                if (userTask.isSuccessful()) {
                    // Get list of data
                    DocumentSnapshot document = userTask.getResult();
                    final HashMap<String, Long> found_apps = (HashMap<String, Long>) document.get("apps");
                    HashMap<String, String> found_articles = (HashMap<String, String>) document.get("articles");

                    // Add analysis & article ID's to user
                    user.setVeracity((String) document.get("veracity"));


                    // Articles
                    if(found_articles.size() > 0) {
                        // Create async task
                        final populateArticlesAsyncTask articleTask = new populateArticlesAsyncTask(db);

                        // execute async task
                        articleTask.execute(found_articles);

                        // Check for task finish
                        articleTask.getDone().observe(context.get(), new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean done) {
                                if (done && !triggered) {

                                    triggered = true;
                                    // Get articles data
                                    user.setArticles(articleTask.getData().getValue());

                                    // Apps
                                    if(found_apps.size() > 0) {
                                        // Create async task
                                        final populateAppsAsyncTask appTask = new populateAppsAsyncTask(db, httph);

                                        // execute async task
                                        appTask.execute(found_apps);

                                        // Check for task finish
                                        appTask.getDone().observe(context.get(), new Observer<Boolean>() {
                                            @Override
                                            public void onChanged(Boolean done) {
                                                if (done) {
                                                    // Get apps data
                                                    user.setApps(appTask.getData().getValue());

                                                    // Remove all observers
                                                    appTask.getDone().removeObservers(context.get());
                                                    articleTask.getDone().removeObservers(context.get());

                                                    // End task
                                                    postData(user);
                                                    postDone(true);
                                                }
                                            }
                                        });
                                    }else {
                                        // End task
                                        postData(user);
                                        postDone(true);
                                    }
                                }
                            }
                        });
                    } else {
                        // End task
                        postData(user);
                        postDone(true);
                    }
                } else {
                    // Log error
                    Log.e("DB", "read failed: ", userTask.getException());
                }
            }
        });
        return user;
    }
}
