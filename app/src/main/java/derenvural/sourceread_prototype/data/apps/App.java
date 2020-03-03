package derenvural.sourceread_prototype.data.apps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

import derenvural.sourceread_prototype.data.DataHolder;

public class App extends DataHolder {
    private String name;
    private String description;
    private String key;
    private String access_token;
    private HashMap<String, String> requests;

    // extended functionality eg onclick, links, disclaimer
    public App(@Nullable String newImage, @NonNull String newTitle, @Nullable String newText){
        super(newImage, newTitle, newText);
    }

    // SET
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setKey(String key) { this.key = key; }
    public void setAccess_token(String access_token) { this.access_token = access_token; }
    public void setRequests(HashMap<String, String> requests) { this.requests = requests; }

    // GET
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getKey() { return key; }
    public String getAccess_token() { return access_token; }
    public HashMap<String, String> getRequests() { return requests; }
}
