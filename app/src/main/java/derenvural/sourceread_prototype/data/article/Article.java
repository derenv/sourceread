package derenvural.sourceread_prototype.data.article;

import android.os.Bundle;
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
    private ArrayList<String> text;

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
            setWord_count("");
        }
        if(document.get("veracity") == null){
            setVeracity("");
        }else{
            setVeracity(document.get("veracity").toString());
        }
        if(document.get("text") == null){
            setText(new ArrayList<String>());
        }else{
            setText((ArrayList<String>) document.get("text"));
        }
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
                setWord_count("");
            }
            setVeracity("");
            setText(new ArrayList<String>());
        }catch(JSONException error){
            Log.e("JSON error", "error reading JSON: " + error.getMessage());
        }
    }

    public Article() {

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
        docData.put("text", getText());

        return docData;
    }

    public void loadInstanceState(Bundle bundle) {
        // Fill in data
        // Identifiers
        setResolved_url((String) bundle.getSerializable("url"));
        setResolved_id((String) bundle.getSerializable("id"));
        setResolved_title((String) bundle.getSerializable("title"));
        setApp((String) bundle.getSerializable("app"));
        // External Information
        setAuthors((ArrayList) bundle.getSerializable("authors"));
        //setPublication(bundle.getSerializable("publication"));
        //setPublication_veracity(bundle.getSerializable"publication_veracity"));
        // Article Information
        setWord_count((String) bundle.getSerializable("word_count"));
        setVeracity((String) bundle.getSerializable("veracity"));
        setText((ArrayList) bundle.getSerializable("text"));
    }
    public void saveInstanceState(Bundle bundle) {
        // Fill in data
        // Identifiers
        bundle.putSerializable("url", getResolved_url());
        bundle.putSerializable("id", getResolved_id());
        bundle.putSerializable("title", getResolved_title());
        bundle.putSerializable("app", getApp());
        // External Information
        bundle.putSerializable("authors", getAuthors());
        //bundle.putSerializable("publication", getPublication());
        //bundle.putSerializable("publication_veracity", getPublication_veracity());
        // Article Information
        bundle.putSerializable("word_count", getWord_count());
        bundle.putSerializable("veracity", getVeracity());
        bundle.putSerializable("text", getText());
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
    public ArrayList<String> getText() { return text; }

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
    public void setText(ArrayList<String> text) { this.text = text; }
}
