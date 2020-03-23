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

/* Scrapes content from webpages
 * user-agent list:
 * http://www.useragentstring.com/pages/useragentstring.php?name=Firefox
 * */
public class scraper extends AsyncTask<Void, Void, Void> {
    // Query data
    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private final static String REFERRER = "http://www.google.com";
    private final static int TIMEOUT = 24000;

    // Data
    private MutableLiveData<Boolean> done = new MutableLiveData<Boolean>();
    private MutableLiveData<Article> article = new MutableLiveData<Article>();

    public scraper(Article article){
        setDone(false);
        setArticle(article);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Article article = getArticle().getValue();

        // Test output
        Log.d("JSOUP", "==START==");
        for (String text : article.getText()){
            Log.d("JSOUP", text);
        }
        Log.d("JSOUP", "==END==");

        // Analyse article
        Log.d("JSOUP", "analysing now..");
        article.analyse();

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

            // Get paragraph elements
            Elements paragraphElements = response
                    .parse()
                    .body()
                    .getElementsByTag("p");

            // Get paragraphs of article
            ArrayList<String> paragraphs = new ArrayList<String>();
            for (Element paragraph : paragraphElements) {
                // TODO: remove image descriptions & credits
                // TODO: remove publication descriptions & credits
                // TODO: remove author descriptions & credits
                paragraphs.add(paragraph.text());
            }

            // Add text to article & add to list of fetched articles
            article.setText(paragraphs);
        } catch (IOException error) {
            Log.e("JSOUP error", "unknown error scraping web-page: " + error.getMessage());
            error.printStackTrace();
            article.setText(new ArrayList<String>());
        }

        // Set live data with articles (or empty list for failure)
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
