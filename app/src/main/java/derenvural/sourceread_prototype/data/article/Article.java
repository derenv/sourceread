package derenvural.sourceread_prototype.data.article;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Article {
    private String resolved_url;
    private String resolved_id;
    private String resolved_title;
    private JSONObject authors;
    private String word_count;

    public Article(JSONObject article){
        try{
            Log.d("API url", article.getString("resolved_url"));
            setResolved_url(article.getString("resolved_url"));
        }catch(JSONException error){
            Log.e("JSON error", "error reading JSON: " + error.getMessage());
        }
        try{
            Log.d("API id", article.getString("resolved_id"));
            setResolved_id(article.getString("resolved_id"));
        }catch(JSONException error){
            Log.e("JSON error", "error reading JSON: " + error.getMessage());
        }
        try{
            Log.d("API title", article.getString("resolved_title"));
            setResolved_title(article.getString("resolved_title"));
        }catch(JSONException error){
            Log.e("JSON error", "error reading JSON: " + error.getMessage());
        }
        try{
            Log.d("API authors", article.getString("authors"));
            setAuthors(article.getJSONObject("authors"));

            // TODO: parse authors into HashMap
        }catch(JSONException error){
            Log.e("JSON error", "error reading JSON: " + error.getMessage());
        }
        try{
            Log.d("API word count", article.getString("word_count"));
            setWord_count(article.getString("word_count"));
        }catch(JSONException error){
            Log.e("JSON error", "error reading JSON: " + error.getMessage());
        }
    }

    // Gets
    public String getResolved_url() {
        return resolved_url;
    }
    public String getResolved_id() {
        return resolved_id;
    }
    public String getResolved_title() {
        return resolved_title;
    }
    public JSONObject getAuthors() {
        return authors;
    }
    public String getWord_count() {
        return word_count;
    }

    // Sets
    public void setResolved_url(String resolved_url) {
        this.resolved_url = resolved_url;
    }
    public void setResolved_id(String resolved_id) {
        this.resolved_id = resolved_id;
    }
    public void setResolved_title(String resolved_title) {
        this.resolved_title = resolved_title;
    }
    public void setAuthors(JSONObject authors) {
        this.authors = authors;
    }
    public void setWord_count(String word_count) {
        this.word_count = word_count;
    }
}
