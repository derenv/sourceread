package derenvural.sourceread_prototype.data.scraper;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/* Scrapes content from webpages
 * user-agent list:
 * http://www.useragentstring.com/pages/useragentstring.php?name=Firefox
 * */
public class scraper extends AsyncTask<String, Void, Void> {
    // Query data
    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private final static String REFERRER = "http://www.google.com";
    private final static int TIMEOUT = 24000;

    // Data
    private MutableLiveData<ArrayList<String>> document = new MutableLiveData<ArrayList<String>>();
    private MutableLiveData<Boolean> done = new MutableLiveData<Boolean>();

    public scraper(){
        set_result(new ArrayList<String>());
        set_done(false);
    }

    @Override
    protected Void doInBackground(String... url){
        try {
            // Attempt scrape
            Element body = Jsoup.connect(url[0])
                    .ignoreContentType(true)
                    .userAgent(USER_AGENT)
                    .referrer(REFERRER)
                    .timeout(TIMEOUT)
                    .followRedirects(true)
                    .get().body();

            // Get paragraphs of article
            Elements paragraphs = body.getElementsByTag("p");
            ArrayList<String> data = new ArrayList<String>();
            for(Element paragraph : paragraphs){
                // TODO: remove image descriptions & credits
                // TODO: remove publication descriptions & credits
                // TODO: remove author descriptions & credits
                data.add(paragraph.text());
            }

            // Set live data with paragraph text
            set_result(data);
            set_done(true);
        }catch(IOException error){
            Log.e("JSOUP error", "error scraping webpage: " + error.getMessage());
            set_result(new ArrayList<String>());
        }
        return null;
    }

    // Get
    public LiveData<ArrayList<String>> get_result(){ return document; }
    public LiveData<Boolean> get_done(){ return done; }

    // Set
    public void set_result(ArrayList<String> document){ this.document.postValue(document); }
    public void set_done(Boolean done){ this.done.postValue(done); }
}
