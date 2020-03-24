package derenvural.sourceread_prototype.data.scraper;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import derenvural.sourceread_prototype.data.article.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

/* Scrapes content from webpages
 * user-agent list:
 * http://www.useragentstring.com/pages/useragentstring.php?name=Firefox
 * */
public class scraper extends AsyncTask<Void, Void, Void> {
    // Query data
    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private final static String REFERRER = "http://www.google.com";
    private final static int TIMEOUT = 24000;
    // Database & user
    private fdatabase db;
    private LoggedInUser user;
    // Data
    private MutableLiveData<Boolean> done = new MutableLiveData<Boolean>();
    private MutableLiveData<Article> article = new MutableLiveData<Article>();

    public scraper(Article article, fdatabase db, LoggedInUser user){
        setDone(false);
        setArticle(article);
        this.db = db;
        this.user = user;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Save text to database
        Article article = getArticle().getValue();
        db.update_article_field(article, user, "text");

        // Test output
        Log.d("JSOUP", "==START==");
        Log.d("JSOUP", article.getText());
        Log.d("JSOUP", "==END==");

        // Analyse article
        Log.d("JSOUP", "analysing now..");
        article.analyse();

        // Save analysis to database
        db.update_article_field(article, user, "veracity");

        // End task
        postArticle(article);
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
            Log.e("JSOUP error", "error scraping web-page: " + error.getMessage());
            error.printStackTrace();
        } catch (StackOverflowError | OutOfMemoryError error) {
            // catch memory errors from badly written pages
            Log.e("JSOUP error", "error scraping web-page: " + error.getMessage());
            error.printStackTrace();
        }

        return null;
    }

    @Override
    protected Void doInBackground(Void... params){
        Article article = getArticle().getValue();

        try {
            // Attempt scrape
            Connection.Response response = getResponse(article.getResolved_url());
            if(response == null){
                Log.e("JSOUP error", "error scraping web-page code");
            }else if(response.statusCode() != 200) {
                Log.e("JSOUP error", "error response scraping web-page code: " + response.statusCode());
                Log.e("JSOUP error", "error response scraping web-page message: " + response.statusMessage());
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
            Log.e("JSOUP error", "unknown error scraping web-page: " + error.getMessage());
            error.printStackTrace();
            article.setText("");
        }

        // Set live data with article (with found text on success OR empty text on failure)
        postArticle(article);
        return null;
    }

    // Get
    public LiveData<Article> getArticle(){ return article; }
    public LiveData<Boolean> getDone(){ return done; }

    // Set
    public void setArticle(Article article){ this.article.setValue(article); }
    public void setDone(Boolean done){ this.done.setValue(done); }
    public void postArticle(Article article){ this.article.postValue(article); }
    public void postDone(Boolean done){ this.done.postValue(done); }
}
