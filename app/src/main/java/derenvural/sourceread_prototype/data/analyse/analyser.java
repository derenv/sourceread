package derenvural.sourceread_prototype.data.analyse;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import derenvural.sourceread_prototype.data.article.Article;
import derenvural.sourceread_prototype.data.scraper.scraper;

public class analyser {
    private scraper scraper_task;

    public analyser(Article article){
         scraper_task = new scraper(article);
    }

    public void fetch_article(LifecycleOwner context, Observer observer){
        // execute async task
        scraper_task.execute();

        // Check for task finish
        scraper_task.getDone().observe(context, observer);
    }

    /* TODO: create various analyses to run
     * which are passed to scraper for post task completion
    **/
}