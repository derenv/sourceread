package derenvural.sourceread_prototype.data.article;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Article implements Serializable {
    // Identifiers
    private String resolved_url;
    private String resolved_id;
    private String resolved_title;
    private String app;
    // External Information
    private ArrayList<HashMap<String, String>> authors;
    private String publication;
    private String publication_veracity;
    // Article Information
    private String word_count;
    private String veracity;

    /*
     * Create object from database document
     * */
    public Article(DocumentSnapshot document, String api_id) {
        // Identifiers
        setResolved_url(document.get("url").toString());
        setResolved_id(api_id);
        setResolved_title(document.get("title").toString());
        setApp(document.get("app").toString());
        // External Information
        setAuthors((ArrayList<HashMap<String, String>>) document.get("authors"));
        //setPublication(document.get("publication").toString());
        // Article Information
        if(document.get("word_count") != null) {
            setWord_count(document.get("word_count").toString());
        }else{
            setWord_count("n/a");
        }
        setVeracity(document.get("veracity").toString());

    }

    /*
     * Create object from JSON object
     * */
    public Article(JSONObject article, String app_name){
        // ((Safe))
        setApp(app_name);

        try{
            // Identifiers
            Log.d("API url", article.getString("resolved_url"));
            setResolved_url(article.getString("resolved_url"));
            Log.d("API id", article.getString("resolved_id"));
            setResolved_id(article.getString("resolved_id"));
            Log.d("API title", article.getString("resolved_title"));
            setResolved_title(article.getString("resolved_title"));
            // External Information
            Log.d("API authors", article.getString("authors"));
            if(article.getString("authors") != null) {
                // Parse authors into HashMap
                JSONObject authors_json = article.getJSONObject("authors");
                ArrayList<HashMap<String, String>> authors = new ArrayList<HashMap<String, String>>();

                // Parse authors into HashMap
                for (Iterator<String> it = authors_json.keys(); it.hasNext();) {
                    // Get current author
                    JSONObject author_json = authors_json.getJSONObject(it.next());

                    // Create HashMap
                    HashMap<String, String> author = new HashMap<String, String>();
                    author.put("name", author_json.getString("name"));
                    author.put("url", author_json.getString("url"));

                    // Add to list
                    authors.add(author);
                }
                setAuthors(authors);
            }else{
                setAuthors(new ArrayList<HashMap<String, String>>());
            }
            // Article Information
            Log.d("API word count", article.getString("word_count"));
            if(article.getString("word_count") != null) {
                setWord_count(article.getString("word_count"));
            }else{
                setWord_count("n/a");
            }
        }catch(JSONException error){
            Log.e("JSON error", "error reading JSON: " + error.getMessage());
        }
    }

    public Map<String, Object> map_data(){
        // Create a Map to fill
        Map<String, Object> docData = new HashMap<>();

        // Fill in data
        // Identifiers
        docData.put("url", getResolved_url());
        docData.put("id", getResolved_id());
        docData.put("title", getResolved_title());
        docData.put("app", getApp());
        // External Information
        docData.put("authors", getAuthors());
        //docData.put("publication", getPublication());
        //docData.put("publication_veracity", getPublication_veracity());
        // Article Information
        docData.put("word_count", getWord_count());
        docData.put("veracity", getVeracity());

        return docData;
    }

    public void analyse(){
        // TODO: stub for analysis
        setVeracity("100");
    }

    // Gets
    public String getResolved_url() { return resolved_url; }
    public String getResolved_id() { return resolved_id; }
    public String getResolved_title() { return resolved_title; }
    public ArrayList<HashMap<String, String>> getAuthors() { return authors; }
    public String getWord_count() { return word_count; }
    public String getVeracity() { return veracity; }
    public String getApp() { return app; }
    public String getPublication() { return publication; }
    public String getPublication_veracity() { return publication_veracity; }

    // Sets
    public void setResolved_url(String resolved_url) { this.resolved_url = resolved_url; }
    public void setResolved_id(String resolved_id) { this.resolved_id = resolved_id; }
    public void setResolved_title(String resolved_title) { this.resolved_title = resolved_title; }
    public void setAuthors(ArrayList<HashMap<String, String>> authors) { this.authors = authors; }
    public void setWord_count(String word_count) { this.word_count = word_count; }
    public void setVeracity(String veracity) { this.veracity = veracity; }
    public void setApp(String app) { this.app = app; }
    public void setPublication(String publication) { this.publication = publication; }
    public void setPublication_veracity(String publication_veracity) { this.publication_veracity = publication_veracity; }

}
