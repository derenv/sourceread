package derenvural.sourceread_prototype.data.asyncTasks;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import derenvural.sourceread_prototype.data.cards.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;

public class populateArticlesAsyncTask extends sourcereadAsyncTask<HashMap<String, String>, ArrayList<Article>> {
    // Tools
    private fdatabase db;

    public populateArticlesAsyncTask(fdatabase db){
        super();
        // Tools
        this.db = db;
    }

    @Override
    protected ArrayList<Article> doInBackground(HashMap<String, String>... params){
        final ArrayList<Article> articles = new ArrayList<Article>();

        // Request user data
        if (params[0].keySet().size() > 0) {
            final Iterator<String> iterator = params[0].keySet().iterator();
            while(iterator.hasNext()){
                final String article_id = iterator.next();
                db.request_article_data(article_id, new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Get list of articles
                            DocumentSnapshot document = task.getResult();

                            // Populate object
                            Article this_article = new Article(document);

                            // Add to list
                            articles.add(this_article);

                            // End task
                            if(!iterator.hasNext()) {
                                postData(articles);
                                postDone(true);
                            }
                        } else {
                            // Log error
                            Log.e("DB", "read failed: ", task.getException());
                        }
                    }
                });
            }
        }else{
            postData(articles);
            postDone(true);
        }
        return articles;
    }
}
