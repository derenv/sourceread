package derenvural.sourceread_prototype.data.asyncTasks;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import derenvural.sourceread_prototype.data.cards.articles.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;

/* Scrapes content from webpages
 * user-agent list:
 * http://www.useragentstring.com/pages/useragentstring.php?name=Firefox
 * */
public class scraperAsyncTask extends sourcereadAsyncTask<Article, Article> {
    // Query data
    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private final static String REFERRER = "http://www.google.com";
    private final static int TIMEOUT = 24000;
    // Database & user
    private fdatabase db;

    public scraperAsyncTask(fdatabase db){
        super();

        this.db = db;
    }

    @Override
    protected void onPostExecute(final Article article) {
        super.onPostExecute(article);

        if(article != null && !article.getText().equals("")) {
            // Save text to database
            db.update_article_field(article, "text", new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("DB", "updated article - '"+article.getDatabase_id()+"' field - 'text'");
                    }else{
                        // Log error
                        Log.e("DB", "update failed: ", task.getException());
                    }
                }
            });

            // Test output
            Log.d("JSOUP", "==START==");
            Log.d("JSOUP", article.getText());
            Log.d("JSOUP", "==END==");

            // Analyse article
            Log.d("JSOUP", "analysing now..");
            article.analyse();

            // Save analysis to database
            db.update_article_field(article, "veracity", new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> veracityTask) {
                    if (veracityTask.isSuccessful()) {
                        Log.d("DB", "updated article - '"+article.getDatabase_id()+"' field - 'veracity'");
                    }else{
                        // Log error
                        Log.e("DB", "update failed: ", veracityTask.getException());
                    }
                }
            });
        }

        // End task
        Log.d("JSOUP", "done!");
        postData(article);
        postDone(true);
    }

    private Connection.Response getResponse(String url){
        try {
            return Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent(USER_AGENT)
                    .referrer(REFERRER)
                    .timeout(TIMEOUT)
                    .followRedirects(true)
                    .execute();
        } catch (IOException error) {
            Log.e("JSOUP", "error scraping web-page: " + error.getMessage());
            error.printStackTrace();
        } catch (StackOverflowError | OutOfMemoryError error) {
            // catch memory errors from badly written pages
            Log.e("JSOUP", "error scraping web-page: " + error.getMessage());
            error.printStackTrace();
        }

        return null;
    }

    @Override
    protected Article doInBackground(Article... params){
        // Fetch data
        Article article = params[0];

        try {
            // Attempt scrape
            Connection.Response response = getResponse(article.getResolved_url());
            if(response == null){
                Log.e("JSOUP", "error scraping web-page code");
            }else if(response.statusCode() != 200) {
                Log.e("JSOUP", "error response scraping web-page code: " + response.statusCode());
                Log.e("JSOUP", "error response scraping web-page message: " + response.statusMessage());
            }

            // Fetch paragraph elements of page
            Elements paragraphElements = response
                    .parse()
                    .body()
                    .getElementsByTag("p");

            // Extract paragraphs from paragraph elements
            String text = "";
            for (Element paragraph : paragraphElements) {
                // TODO: remove image descriptions & credits
                // TODO: remove publication descriptions & credits
                // TODO: remove author descriptions & credits
                text += paragraph.text();
            }

            // replace extraneous spaces
            text.replace("  "," ");
            text.substring(0,text.length() -1);

            // Add text to article
            article.setText(text);
        } catch (IOException error) {
            Log.e("JSOUP", "unknown error scraping web-page: " + error.getMessage());
            error.printStackTrace();
            article.setText("");
        }

        // Set live data with article (with found text on success OR empty text on failure)
        return article;
    }
}
