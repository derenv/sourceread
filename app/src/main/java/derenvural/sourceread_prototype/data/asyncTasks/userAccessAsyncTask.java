package derenvural.sourceread_prototype.data.asyncTasks;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class userAccessAsyncTask extends sourcereadAsyncTask<LoggedInUser> {
    // Tools
    private httpHandler httph;
    // Data
    private String app_name;

    public userAccessAsyncTask(LoggedInUser user, httpHandler httph, String app_name){
        super();
        // Data
        setData(user);
        this.app_name = app_name;

        // Tools
        this.httph = httph;
    }

    @Override
    protected Void doInBackground(Void... params){
        // Fetch user
        final LoggedInUser user = getData().getValue();

        // Find app
        for (HashMap<String, Object> app : user.getApps().getValue()) {
            if(app.get("name").equals(app_name)) {
                // Get login URL
                String url = ((HashMap<String, String>) app.get("requests")).get("access");

                // Cut out redirect URL from url
                String[] fullUrl = url.split("\\?");
                url = fullUrl[0];
                String redirect_uri = fullUrl[1];

                // Fetch app key & request token
                String app_key = app.get("key").toString();
                String request_token = user.getRequestTokens().getValue().get(app_name);

                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("consumer_key",app_key);
                parameters.put("code",request_token);
                parameters.put("redirect_uri",redirect_uri);

                // Request access token by http request to URL
                httph.make_volley_request(url, parameters,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Cut token out of html response
                                Log.d("API", "Request Response recieved");

                                try{
                                    // Add new access token to map
                                    user.addAccessToken(app_name, response.getString("access_token"));

                                    // Set display nameset
                                    user.setDisplayName(response.getString("username"));

                                    // End task
                                    postData(user);
                                    postDone(true);
                                }catch(JSONException error){
                                    Log.e("JSON error", "error reading JSON: " + error.getMessage());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("API error", "api get failed: " + error.getMessage());
                            }
                        }
                );
            }
        }

        return null;
    }
}
