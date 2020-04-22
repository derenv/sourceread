package derenvural.sourceread_prototype.data.asyncTasks;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.data.cards.apps.App;
import derenvural.sourceread_prototype.data.database.fdatabase;

public class writeAppsAsyncTask extends sourcereadAsyncTask<ArrayList<App>, HashMap<String, Long>> {
    // Tools
    private fdatabase db;

    public writeAppsAsyncTask(fdatabase db){
        super();
        // Tools
        this.db = db;
    }

    @Override
    protected HashMap<String, Long> doInBackground(ArrayList<App>... params){
        // Fetch app
        final ArrayList<App> apps = params[0];

        // Create new apps object for database
        final HashMap<String, Long> appsMap = new HashMap<String, Long>();
        for(App app: apps){
            appsMap.put(app.getTitle(),app.getTimestamp());
        }

        // Write to database
        db.update_user_field("apps", appsMap, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> appsTask) {
                if (appsTask.isSuccessful()) {
                    Log.d("DB","update done");

                    postData(appsMap);
                    postDone(true);
                }else{
                    // Log error
                    Log.e("DB", "update failed: ", appsTask.getException());
                }
            }
        });

        return appsMap;
    }
}
