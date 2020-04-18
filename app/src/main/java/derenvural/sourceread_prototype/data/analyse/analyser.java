package derenvural.sourceread_prototype.data.analyse;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import derenvural.sourceread_prototype.data.cards.articles.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.asyncTasks.scraperAsyncTask;

public class analyser {
    private scraperAsyncTask task;

    public analyser(fdatabase db){
        // Create async task
        task = new scraperAsyncTask(db);
    }

    public void fetch_article(LifecycleOwner owner, Article article, Observer observer){
        // execute async task
        task.execute(article);

        // Check for task finish
        task.getDone().observe(owner, observer);
    }

    /* TODO: create various analyses to run
     * which are passed to scraper for post task completion
    **/
}
