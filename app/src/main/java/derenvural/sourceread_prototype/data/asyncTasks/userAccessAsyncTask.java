package derenvural.sourceread_prototype.data.asyncTasks;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.http.httpHandler;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class userAccessAsyncTask extends sourcereadAsyncTask<LoggedInUser, ArrayList<App>> {
    // Tools
    private httpHandler httph;
    // Data
    private String app_name;

    public userAccessAsyncTask(httpHandler httph, String app_name){
        super();
        // Data
        this.app_name = app_name;

        // Tools
        this.httph = httph;
    }

    @Override
    protected ArrayList<App> doInBackground(LoggedInUser... params){
        // Fetch user
        final LoggedInUser user = params[0];
        final ArrayList<App> apps = user.getApps().getValue();
        final ArrayList<App> new_apps = new ArrayList<App>();

        // Find app
        for (final App app : apps) {
            if(app.getTitle().equals(app_name)) {
                // Get login URL
                String url = app.getRequests().get("access");

                // Cut out redirect URL from url
                String[] fullUrl = url.split("\\?");
                url = fullUrl[0];
                String redirect_uri = fullUrl[1];

                // Fetch app key & request token
                String app_key = app.getKey();
                String request_token = app.getRequestToken();

                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("consumer_key", app_key);
                parameters.put("code", request_token);
                parameters.put("redirect_uri", redirect_uri);

                // Request access token by http request to URL
                httph.make_volley_request(url, parameters,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Cut token out of html response
                                Log.d("API", "Request Response recieved");

                                try{
                                    // Add new access token to map
                                    app.setAccessToken(response.getString("access_token"));

                                    // Store app
                                    for(App this_app : apps) {
                                        if (this_app.getTitle().equals(app.getTitle())) {
                                            // store timestamp
                                            new_apps.add(app);
                                        }else{
                                            new_apps.add(this_app);
                                        }
                                    }

                                    // End task
                                    postData(new_apps);
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
                break;
            }
        }

        return new_apps;
    }
}
