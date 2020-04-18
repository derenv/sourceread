package derenvural.sourceread_prototype.data.cards.apps;

import android.os.Bundle;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import derenvural.sourceread_prototype.data.cards.Card;

public class App extends Card {
    private String key;
    private String request_token;
    private String access_token;
    private Long timestamp;
    private HashMap<String, String> requests;

    // Constructors
    public App(String name, Long timestamp) {
        super(null, name,"");
        setTitle(name);
        setTimestamp(timestamp);
    }
    public App(Bundle bundle) {
        super(null, "","");
        loadInstanceState(bundle);
    }

    // Database constructors
    public void CreateApp(DocumentSnapshot document) {
        setTitle(document.getId());
        setText(document.get("description").toString());
        setKey(document.get("key").toString());
        setRequestToken("");
        setAccessToken("");
        setRequests((HashMap<String, String>) document.get("requests"));
    }
    public Map<String, Object> map_data(){
        // Create a Map to fill
        Map<String, Object> docData = new HashMap<>();

        // Fill in data
        docData.put("name", getTitle());
        docData.put("description", getText());
        docData.put("key", getKey());
        docData.put("request_token", getRequestToken());
        docData.put("access_token", getAccessToken());
        docData.put("timestamp", getTimestamp());
        docData.put("requests", getRequests());

        return docData;
    }

    // Serialization constructors
    public void loadInstanceState(Bundle bundle) {
        setTitle((String) bundle.getSerializable("name"));
        setText((String) bundle.getSerializable("description"));
        setKey((String) bundle.getSerializable("key"));
        setRequestToken((String) bundle.getSerializable("request_token"));
        setAccessToken((String) bundle.getSerializable("access_token"));
        setTimestamp((Long) bundle.getSerializable("timestamp"));
        setRequests((HashMap<String, String>) bundle.getSerializable("requests"));
    }
    public void saveInstanceState(Bundle bundle) {
        bundle.putSerializable("name", getTitle());
        bundle.putSerializable("description", getText());
        bundle.putSerializable("key", getKey());
        bundle.putSerializable("request_token", getRequestToken());
        bundle.putSerializable("access_token", getAccessToken());
        bundle.putSerializable("timestamp", getTimestamp());
        bundle.putSerializable("requests", getRequests());
    }

    // SET
    public void setKey(String key) { this.key = key; }
    public void setRequestToken(String request_token) { this.request_token = request_token; }
    public void setAccessToken(String access_token) { this.access_token = access_token; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public void setRequests(HashMap<String, String> requests) { this.requests = requests; }

    // GET
    public String getKey() { return key; }
    public String getRequestToken() { return request_token; }
    public String getAccessToken() { return access_token; }
    public Long getTimestamp() { return timestamp; }
    public HashMap<String, String> getRequests() { return requests; }
}
