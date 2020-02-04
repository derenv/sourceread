package derenvural.sourceread_prototype.data.model;

import com.google.firebase.auth.FirebaseUser;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String[] articles;
    private String[] apps;
    private String veracity;
    private String email;

    public LoggedInUser(FirebaseUser user) {
        // Populate object from database
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String[] getArticles() {
        return articles;
    }

    public String[] getApps() {
        return apps;
    }

    public String[] getArticleVeracity(String id) {
        // Get ratings from database
        String[] ratings = {"","",""};
        return ratings;
    }

    public String getOverallVeracity() { return veracity; }

    public String getEmail() {
        return email;
    }
}
