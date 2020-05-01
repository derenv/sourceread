package derenvural.sourceread_prototype.data.analyse;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;

import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.cards.articles.Article;
import derenvural.sourceread_prototype.data.database.fdatabase;
import derenvural.sourceread_prototype.data.asyncTasks.scraperAsyncTask;

public class analyser {
    private scraperAsyncTask task;
    private WeakReference<SourceReadActivity> context;
    private fdatabase db;
    private JSONObject jsonAIF;
    private MutableLiveData<Article> article;
    // Constants
    private static final String converterURL = "http://ws.arg.tech/m/turninator";
    private static final String analyserURL = "http://ws.arg.tech/m/inference_identifier";

    public analyser(SourceReadActivity owner, fdatabase db) {
        // Set database
        this.db = db;

        // Create async task
        task = new scraperAsyncTask(this.db);

        // Set activity
        context = new WeakReference<SourceReadActivity>(owner);

        // Set article
        article = new MutableLiveData<Article>(null);
    }

    public void fetch_article(final Article newArticle, Observer<Boolean> observer) {
        // Inform user of progress
        Toast.makeText(context.get(), "Scraping article content..", Toast.LENGTH_SHORT).show();

        // execute async task
        article.setValue(newArticle);
        task.execute(article.getValue());

        // Check for task finish
        task.getDone().observe(context.get(), observer);

        task.getDone().observe(context.get(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    // Set live data
                    article.setValue(task.getData().getValue());

                    if(article != null && article.getValue() != null) {
                        // Convert to JSON-AIF
                        convertToJSONAIF();
                    }
                }
            }
        });
    }

    /*
     * Make POST requests to ARG-tech webservices for analysis of sent JSON-AIF
     *
     * {"nodes":
     * [{"text":"We are losing our good jobs. Our jobs are fleeing the country.",
     * "type":"L",
     * "nodeID":0,
     * "timestamp":"00:00:00"}],
     * "edges":[],
     * "text":" text=Trump: We are losing our good jobs. Our jobs are fleeing the country..",
     * "locutions":[{"personID":"text=Trump:","nodeID":0,"timestamp":"00:00:00"}]}
     *
     *
     **/
    private void convertToJSONAIF() {
        // Get body text & clean
        String data = article.getValue().getText();

        // Inform user of progress
        Toast.makeText(context.get(), "Analysing..", Toast.LENGTH_SHORT).show();

        // Make https POST request
        context.get().getHttpHandler().argtech_request_post(converterURL, data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Cut token out of html response
                        Log.d("ARG-TECH", "Webservice POST Request Response received");

                        try {
                            // Attempt to create the JSON object response
                            jsonAIF = new JSONObject(response);

                            // Put response into article
                            Article newArticle = article.getValue();
                            newArticle.setAif(jsonAIF.toString());
                            article.setValue(newArticle);

                            // Notify user
                            Toast.makeText(context.get(), "Conversion to AIF complete..", Toast.LENGTH_SHORT).show();

                            // Attempt analysis on json-aif
                            analyse();
                        } catch (JSONException error) {
                            Log.e("JSON error", "error reading JSON: " + error.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: display some error in analysis page
                        Log.e("ARG-TECH error", "Webservice POST request failed: " + error.getMessage());
                    }
                }
        );
    }

    /*
     * Make POST requests to ARG-tech webservices for analysis of sent JSON-AIF
     **/
    private void analyse() {
        // Get JSON-AIF
        String data = article.getValue().getAif();

        // Inform user of progress
        Toast.makeText(context.get(), "Analysing..", Toast.LENGTH_SHORT).show();

        // Make https POST request
        context.get().getHttpHandler().argtech_request_post(analyserURL, data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Cut token out of html response
                        Log.d("ARG-TECH", "Webservice POST Request Response received");

                        try {
                            // Attempt to create the JSON object response
                            jsonAIF = new JSONObject(response);

                            // Put response into article
                            Article newArticle = article.getValue();
                            newArticle.setAif(jsonAIF.toString());
                            article.setValue(newArticle);

                            // Notify user
                            Toast.makeText(context.get(), "Argument Structure analysis complete..", Toast.LENGTH_SHORT).show();

                            // Update database
                            update_database();
                        } catch (JSONException error) {
                            Log.e("JSON error", "error reading JSON: " + error.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: display some error in analysis page
                        Log.e("ARG-TECH error", "Webservice POST request failed: " + error.getMessage());
                    }
                }
        );
    }


    private void update_database() {
        if(article != null && article.getValue() != null) {
            // Save analysis to database
            db.update_article_field(article.getValue(), "aif", new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> aifTask) {
                    if (aifTask.isSuccessful()) {
                        Toast.makeText(context.get(), "Article '" + article.getValue().getTitle() + "' analysis saved to database!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Log error
                        Log.e("DB", "update failed: ", aifTask.getException());
                    }
                }
            });
        }
    }
}