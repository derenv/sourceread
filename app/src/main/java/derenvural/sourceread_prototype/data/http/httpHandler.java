package derenvural.sourceread_prototype.data.http;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class httpHandler {
    private RequestQueue queue;

    /*
    * Constructor
    * */
    public httpHandler(@NonNull final Context context){
        queue = Volley.newRequestQueue(context);
    }

    /*
    * Open url passed in browser
    * */
    public void browser_open(Context context, String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    public void argtech_request_post(@NonNull String url,
                                     @NonNull final String body,
                                     @NonNull Response.Listener<String> response,
                                     @NonNull Response.ErrorListener error){
        // Construct string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response, error) {
            @Override
            public byte[] getBody() {
                return body.getBytes(StandardCharsets.UTF_8);
            }
        };

        // Add to Volley request queue
        queue.add(stringRequest);
    }

    public void make_volley_request_post(@NonNull String url,
                                         @Nullable HashMap<String, Object> parameters,
                                         @NonNull Response.Listener<JSONObject> response,
                                         @NonNull Response.ErrorListener error){
        // Create JSON object
        JSONObject jsonParameters;
        if(parameters != null) {
            jsonParameters = new JSONObject(parameters);
        }else{
            jsonParameters = new JSONObject();
        }

        // Create JSON request with custom headers
        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonParameters,
                response,
                error){
            /**
             * Passing some request headers
             * set request & response to JSON
             */
            @Override
            public String getBodyContentType()
            {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String>  params = new HashMap<String, String>();
                params.put("X-Accept", "application/json");
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(request_json);
    }
}
