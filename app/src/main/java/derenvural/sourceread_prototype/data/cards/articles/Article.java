package derenvural.sourceread_prototype.data.cards.articles;

import android.os.Bundle;

import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import derenvural.sourceread_prototype.data.cards.Card;

public class Article extends Card {
    // Identifiers
    private String resolved_url;
    private String resolved_id;
    private String database_id;
    private String app;
    // External Information
    private ArrayList<HashMap<String, String>> authors;
    // Article Information
    private String word_count;
    private String aif;
    private String text;
    private String excerpt;

    /*
     * Create object from database document
     * */
    public Article(DocumentSnapshot document) {
        super(null, document.get("title").toString(),document.get("title").toString());
        // Identifiers
        setResolved_url(document.get("url").toString());
        setResolved_id(document.get("appid").toString());
        setDatabase_id(document.getId());
        setTitle(document.get("title").toString());
        setApp(document.get("app").toString());
        // External Information
        if(document.get("authors") == null) {
            setAuthors(new ArrayList<HashMap<String, String>>());
        }else{
            setAuthors((ArrayList<HashMap<String, String>>) document.get("authors"));
        }
        // Article Information
        if(document.get("word_count") == null) {
            setWord_count("");
        }else{
            setWord_count(document.get("word_count").toString());
        }
        if(document.get("aif") == null){
            setAif("");
        }else{
            setAif(document.get("aif").toString());
        }
        if(document.get("excerpt") == null){
            setExcerpt("");
        }else{
            setExcerpt(document.get("excerpt").toString());
        }
        if(document.get("text") == null){
            setText("");
        }else{
            setText(document.get("text").toString());
        }

    }

    /*
     * Create object from JSON object
     * */
    public Article(JSONObject article, String app_name) throws JSONException{
        super(null, article.getString("resolved_title"),null);

        // ((Safe))
        setApp(app_name);

        // Identifiers
        setResolved_url(article.getString("resolved_url"));
        setResolved_id(article.getString("resolved_id"));
        setTitle(article.getString("resolved_title"));
        // External Information
        if(article.has("authors")) {
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
        if(article.has("word_count")) {
            setWord_count(article.getString("word_count"));
        }else{
            setWord_count("");
        }
        if(article.has("excerpt")){
            setExcerpt(article.getString("excerpt"));
        }else{
            setExcerpt("");
        }
        setAif("");
        setText("");
    }

    public Article() {
        super(null,"","");
    }

    public Map<String, Object> map_data(){
        // Create a Map to fill
        Map<String, Object> docData = new HashMap<>();

        // Fill in data
        // Identifiers
        docData.put("url", getResolved_url());
        docData.put("appid", getResolved_id());
        docData.put("title", getTitle());
        docData.put("app", getApp());
        // External Information
        docData.put("authors", getAuthors());
        // Article Information
        docData.put("word_count", getWord_count());
        docData.put("aif", getAif());
        docData.put("text", getText());
        docData.put("excerpt", getExcerpt());

        return docData;
    }

    public void loadInstanceState(Bundle bundle) {
        // Fill in data
        // Identifiers
        setResolved_url((String) bundle.getSerializable("url"));
        setResolved_id((String) bundle.getSerializable("id"));
        setDatabase_id((String) bundle.getSerializable("did"));
        setTitle((String) bundle.getSerializable("title"));
        setApp((String) bundle.getSerializable("app"));
        // External Information
        setAuthors((ArrayList) bundle.getSerializable("authors"));
        // Article Information
        setWord_count((String) bundle.getSerializable("word_count"));
        setAif((String) bundle.getSerializable("aif"));
        setText((String) bundle.getSerializable("text"));
        setExcerpt((String) bundle.getSerializable("excerpt"));
    }
    public void saveInstanceState(Bundle bundle) {
        // Fill in data
        // Identifiers
        bundle.putSerializable("url", getResolved_url());
        bundle.putSerializable("id", getResolved_id());
        bundle.putSerializable("did", getDatabase_id());
        bundle.putSerializable("title", getTitle());
        bundle.putSerializable("app", getApp());
        // External Information
        bundle.putSerializable("authors", getAuthors());
        // Article Information
        bundle.putSerializable("word_count", getWord_count());
        bundle.putSerializable("aif", getAif());
        bundle.putSerializable("text", getText());
        bundle.putSerializable("excerpt", getExcerpt());
    }

    // Gets
    public String getResolved_url() { return resolved_url; }
    public String getResolved_id() { return resolved_id; }
    public String getDatabase_id() { return database_id; }
    public ArrayList<HashMap<String, String>> getAuthors() { return authors; }
    public String getWord_count() { return word_count; }
    public String getAif() { return aif; }
    public String getApp() { return app; }
    public String getText() { return text; }
    public String getExcerpt() { return excerpt; }

    // Sets
    public void setResolved_url(String resolved_url) { this.resolved_url = resolved_url; }
    public void setResolved_id(String resolved_id) { this.resolved_id = resolved_id; }
    public void setDatabase_id(String database_id) { this.database_id = database_id; }
    public void setAuthors(ArrayList<HashMap<String, String>> authors) { this.authors = authors; }
    public void setWord_count(String word_count) { this.word_count = word_count; }
    public void setAif(String aif) { this.aif = aif; }
    public void setApp(String app) { this.app = app; }
    public void setText(String text) { this.text = text; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }
}
