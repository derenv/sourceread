package derenvural.sourceread_prototype.data.analyse;

import android.util.Log;

import androidx.lifecycle.Observer;

import com.android.volley.Response;
import com.android.volley.VolleyError;

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

    public analyser(SourceReadActivity owner, fdatabase db){
        // Create async task
        task = new scraperAsyncTask(db);

        // Set activity
        context = new WeakReference<SourceReadActivity>(owner);
    }

    public void fetch_article(final Article article, Observer<Boolean> observer){
        // execute async task
        task.execute(article);

        // Check for task finish
        task.getDone().observe(context.get(), observer);

        task.getDone().observe(context.get(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    analyse(article);
                }
            }
        });
    }

    /*
     * Make POST requests to ARG-tech webservices for analysis of sent JSON-AIF
    **/
    private void analyse(Article article){
        // Get URL
        final String url = "https://ws.arg.tech/m/inference_identifier";

        // Get JSON-AIF
        String data = article.getText();
        String[] sentences = data.split("\\.");

        // Add JSON parameters
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("test",sentences[0]);

        // Make https POST request
        context.get().getHttpHandler().make_volley_request_post(url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Cut token out of html response
                        Log.d("API", "Request Response recieved");

                        //try {
                        //
                        Iterator<String> q = response.keys();
                        //} catch (JSONException error) {
                        //    Log.e("JSON error", "error reading JSON: " + error.getMessage());
                        //}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API error", "api get failed: " + error.getMessage());
                    }
                }
        );
    }
}
