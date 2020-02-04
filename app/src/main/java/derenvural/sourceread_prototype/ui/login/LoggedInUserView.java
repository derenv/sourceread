package derenvural.sourceread_prototype.ui.login;

import derenvural.sourceread_prototype.data.model.LoggedInUser;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {

    private String userId;
    private String displayName;
    private String[] articles;
    private String[] apps;
    private String veracity;
    private String email;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(LoggedInUser user) {
        // Populate object from database
        this.userId = user.getUserId();
        this.displayName = user.getDisplayName();
        this.articles = user.getArticles();
        this.apps = user.getApps();
        this.veracity = user.getOverallVeracity();
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

    public String getEmail() { return email; }
}
