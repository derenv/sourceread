package derenvural.sourceread_prototype.data.analyse;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.android.volley.Response;
import com.android.volley.VolleyError;

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

    public analyser(SourceReadActivity owner, fdatabase db) {
        // Create async task
        task = new scraperAsyncTask(db);

        // Set activity
        context = new WeakReference<SourceReadActivity>(owner);
    }

    public void fetch_article(final Article article, Observer<Boolean> observer) {
        // Inform user of progress
        Toast.makeText(context.get(), "Scraping article content..", Toast.LENGTH_SHORT).show();

        // execute async task
        task.execute(article);

        // Check for task finish
        task.getDone().observe(context.get(), observer);

        task.getDone().observe(context.get(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    convertToJSONAIF(article);
                    //analyse(jsonAIF);
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
    private void convertToJSONAIF(Article article) {
        // Get URL
        final String url = "https://ws.arg.tech/m/turninator";

        // Get JSON-AIF
        String data = article.getText();
        String[] sentences = data.split("\\.");
        HashMap[] nodes = new HashMap[sentences.length];
        HashMap[] locutions = new HashMap[sentences.length];
        String[] edges = new String[(sentences.length * 2) - 2];
        for (int i = 0; i < sentences.length; i++) {
            HashMap<String, Object> locution = new HashMap<String, Object>();
            HashMap<String, Object> info = new HashMap<String, Object>();

            locution.put("text", sentences[i]);
            locution.put("type", "L");
            locution.put("nodeID", i);
            locution.put("timestamp", i);

            nodes[i] = locution;

            info.put("personID", 0);
            info.put("nodeID", i);
            info.put("timestamp", i);

            locutions[i] = info;
            /*
            if(i == 0){
                // 1 edge, latter
                edges[i] = i - 1;
            }else if (i == (sentences.length - 1)){
                // 1 edge, former
            }else{
                // 2 edges
            }*/
        }

        // Add JSON parameters
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("nodes", nodes);
        parameters.put("edges", edges);
        parameters.put("text", data);
        parameters.put("locutions", locutions);

        // Inform user of progress
        Toast.makeText(context.get(), "Analysing..", Toast.LENGTH_SHORT).show();

        // Make https POST request
        context.get().getHttpHandler().make_volley_request_post(url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Cut token out of html response
                        Log.d("ARG-TECH", "Webservice POST Request Response received");

                        //try {
                        //
                        Iterator<String> q = response.keys();
                        Toast.makeText(context.get(), "Analysis complete..", Toast.LENGTH_SHORT).show();
                        //} catch (JSONException error) {
                        //    Log.e("JSON error", "error reading JSON: " + error.getMessage());
                        //}
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
    private void analyse(Article article) {
        // Get URL
        final String url = "https://ws.arg.tech/m/inference_identifier";

        // Get JSON-AIF
        String data = article.getText();
        String[] sentences = data.split("\\.");

        // Add JSON parameters
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("test", sentences[0]);

        // Inform user of progress
        Toast.makeText(context.get(), "Analysing..", Toast.LENGTH_SHORT).show();

        // Make https POST request
        context.get().getHttpHandler().make_volley_request_post(url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Cut token out of html response
                        Log.d("ARG-TECH", "Webservice POST Request Response received");

                        //try {
                        //
                        Iterator<String> q = response.keys();
                        Toast.makeText(context.get(), "Analysis complete..", Toast.LENGTH_SHORT).show();
                        //} catch (JSONException error) {
                        //    Log.e("JSON error", "error reading JSON: " + error.getMessage());
                        //}
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
}