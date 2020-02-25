package derenvural.sourceread_prototype.data.http;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class httpHandler {
    private RequestQueue queue;

    /*
    * Constructor
    * */
    public httpHandler(@NonNull final Context context){
        queue = Volley.newRequestQueue(context);
    }

    public void browser_open(Context context, String url){
        Log.d("HTTP","reached request");
        // Open url passed in browser
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    public void make_volley_request(@NonNull final  String url, @NonNull final Response.Listener<String> response, @NonNull final Response.ErrorListener error){
        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response, error);

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
