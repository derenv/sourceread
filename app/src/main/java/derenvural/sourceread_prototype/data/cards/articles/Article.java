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
    private String publication;
    private String publication_veracity;
    // Article Information
    private String word_count;
    private String veracity;
    private String text;
    private String excerpt;
    //TODO: excerpt (for card)

    /*
     * Create object from database document
     * */
    public Article(DocumentSnapshot document) {
        //FIXME: add excerpt as 3rd param
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
        //setPublication(document.get("publication").toString());
        // Article Information
        if(document.get("word_count") == null) {
            setWord_count("");
        }else{
            setWord_count(document.get("word_count").toString());
        }
        if(document.get("veracity") == null){
            setVeracity("");
        }else{
            setVeracity(document.get("veracity").toString());
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
        super(null, article.getString("resolved_title"),article.getString("excerpt"));
        // TODO: Add excerpt as field of object

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
        setVeracity("");
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
        setDatabase_id((String) bundle.getSerializable("did"));
        setTitle((String) bundle.getSerializable("title"));
        setApp((String) bundle.getSerializable("app"));
        // External Information
        setAuthors((ArrayList) bundle.getSerializable("authors"));
        //setPublication(bundle.getSerializable("publication"));
        //setPublication_veracity(bundle.getSerializable"publication_veracity"));
        // Article Information
        setWord_count((String) bundle.getSerializable("word_count"));
        setVeracity((String) bundle.getSerializable("veracity"));
        setText((String) bundle.getSerializable("text"));
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
    public String getDatabase_id() { return database_id; }
    public ArrayList<HashMap<String, String>> getAuthors() { return authors; }
    public String getWord_count() { return word_count; }
    public String getVeracity() { return veracity; }
    public String getApp() { return app; }
    public String getPublication() { return publication; }
    public String getPublication_veracity() { return publication_veracity; }
    public String getText() { return text; }

    // Sets
    public void setResolved_url(String resolved_url) { this.resolved_url = resolved_url; }
    public void setResolved_id(String resolved_id) { this.resolved_id = resolved_id; }
    public void setDatabase_id(String database_id) { this.database_id = database_id; }
    public void setAuthors(ArrayList<HashMap<String, String>> authors) { this.authors = authors; }
    public void setWord_count(String word_count) { this.word_count = word_count; }
    public void setVeracity(String veracity) { this.veracity = veracity; }
    public void setApp(String app) { this.app = app; }
    public void setPublication(String publication) { this.publication = publication; }
    public void setPublication_veracity(String publication_veracity) { this.publication_veracity = publication_veracity; }
    public void setText(String text) { this.text = text; }
}
