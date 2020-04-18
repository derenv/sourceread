package derenvural.sourceread_prototype.data.asyncTasks;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.cards.apps.App;

public class populateAppsAsyncTask extends sourcereadAsyncTask<HashMap<String, Long>, ArrayList<App>> {
    // Activity
    private final WeakReference<SourceReadActivity> context;

    public populateAppsAsyncTask(SourceReadActivity context){
        super();

        // Activity
        this.context = new WeakReference<SourceReadActivity>(context);
    }

    @Override
    protected ArrayList<App> doInBackground(HashMap<String, Long>... params){
        // Initiate results list
        final ArrayList<App> result_apps = new ArrayList<App>();

        // Create empty app objects with name & timestamp
        ArrayList<App> apps = new ArrayList<App>();
        for(String app_name: params[0].keySet()){
            apps.add(new App(app_name, params[0].get(app_name)));
        }

        // Iterate through each app
        final Iterator<App> iterator = apps.iterator();
        if (apps.size() > 0) {
            while(iterator.hasNext()){
                final App app = iterator.next();

                // Request app data
                context.get().getDatabase().request_app_data(app.getTitle(), new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Get app
                            DocumentSnapshot document = task.getResult();

                            // Create app object
                            app.CreateApp(document);

                            // Ask for request token for authentication
                            request_token(app, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Cut token out of html response
                                    Log.d("API", "Request Response recieved");

                                    try {
                                        // Add new request token
                                        app.setRequestToken(response.getString("code"));

                                        // Add to list
                                        result_apps.add(app);

                                        // End task
                                        if(!iterator.hasNext()) {
                                            postData(result_apps);
                                            postDone(true);
                                        }
                                    }catch(JSONException error){
                                        Log.e("JSON error", "error reading JSON: " + error.getMessage());
                                    }
                                }
                            });
                        } else {
                            // Log error
                            Log.e("DB", "read failed: ", task.getException());
                            postData(result_apps);
                            postDone(true);
                        }
                    }
                });
            }
        }else{
            postData(result_apps);
            postDone(true);
        }
        return result_apps;
    }

    public void request_token(App app, Response.Listener<JSONObject> responseListener){
        // Get request token request URL for current app
        HashMap<String, String> app_requests = app.getRequests();
        String url = app_requests.get("request");

        // Cut out redirect URL from url
        String[] fullUrl = url.split("\\?");
        url = fullUrl[0];
        String redirect_uri = fullUrl[1];

        // Fetch app key
        String app_key = app.getKey();

        // Add JSON parameters
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("consumer_key",app_key);
        parameters.put("redirect_uri",redirect_uri);

        // Make https POST request
        context.get().getHttpHandler().make_volley_request_post(url, parameters,
                responseListener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API error", "api get failed: " + error.getMessage());
                    }
                }
        );
    }
}
