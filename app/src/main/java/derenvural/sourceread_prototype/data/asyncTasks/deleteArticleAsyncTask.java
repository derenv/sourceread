package derenvural.sourceread_prototype.data.asyncTasks;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.data.cards.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class deleteArticleAsyncTask extends sourcereadAsyncTask<LoggedInUser> {
    // Tools
    private fdatabase db;
    // Data
    private Article article;

    public deleteArticleAsyncTask(LoggedInUser user, fdatabase db, Article article){
        super();
        // Data
        setData(user);
        this.article = article;

        // Tools
        this.db = db;
    }

    @Override
    protected Void doInBackground(Void... params){
        // Fetch user
        final LoggedInUser user = getData().getValue();

        // Delete article from user object article list & create new list to be written to database
        HashMap<String,String> new_articles_ids = new HashMap<String,String>();
        ArrayList<Article> new_articles = new ArrayList<Article>();
        for(Article a: user.getArticles().getValue()){
            if(!a.getTitle().equals(article.getTitle())) {
                new_articles.add(a);
                new_articles_ids.put(a.getDatabase_id(), a.getApp());
            }
        }
        user.setArticles(new_articles);

        // Call database update method
        db.update_user_field("articles", new_articles_ids, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("DB","update done");

                    postData(user);
                    postDone(true);
                }else{
                    // Log error
                    Log.e("DB", "update failed: ", task.getException());
                }
            }
        });

        return null;
    }
}
