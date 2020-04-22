package derenvural.sourceread_prototype.data.asyncTasks;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import derenvural.sourceread_prototype.data.cards.articles.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class deleteArticleAsyncTask extends sourcereadAsyncTask<ArrayList<Article>, ArrayList<Article>> {
    // Tools
    private fdatabase db;
    // Data
    private LoggedInUser user;

    public deleteArticleAsyncTask(LoggedInUser user, fdatabase db){
        super();
        // Data
        setData(new ArrayList<Article>());
        this.user = user;

        // Tools
        this.db = db;
    }

    @Override
    protected ArrayList<Article> doInBackground(ArrayList<Article>... params){
        // Fetch parameters
        ArrayList<Article> blacklist = params[0];

        // Create new list to be written to database
        HashMap<String,String> new_articles_ids = new HashMap<String,String>();
        boolean validArticle;
        final ArrayList<Article> newlist = new ArrayList<Article>();
        for(Article white: Objects.requireNonNull(user.getArticles().getValue())){
            validArticle = true;
            for(Article black: blacklist) {
                if (black.getTitle().equals(white.getTitle())) {
                    validArticle = false;
                }
            }
            if(validArticle){
                newlist.add(white);
            }
        }
        for(Article article: newlist) {
            new_articles_ids.put(article.getDatabase_id(), article.getApp());
        }

        // Call database update method
        db.update_user_field("articles", new_articles_ids, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> articlesTask) {
                if (articlesTask.isSuccessful()) {
                    Log.d("DB","update done");

                    postData(newlist);
                    postDone(true);
                }else{
                    // Log error
                    Log.e("DB", "update failed: ", articlesTask.getException());
                }
            }
        });

        return newlist;
    }
}
