package derenvural.sourceread_prototype.data.asyncTasks;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.database.fdatabase;

public class importAppsAsyncTask extends sourcereadAsyncTask<ArrayList<App>, ArrayList<App>> {
    // Tools
    private fdatabase db;
    // Data
    private boolean listeners_done;

    public importAppsAsyncTask(fdatabase db){
        super();
        // Data
        listeners_done = false;

        // Tools
        this.db = db;
    }

    @Override
    protected void onPostExecute(ArrayList<App> aVoid) {
        super.onPostExecute(aVoid);

        while (!listeners_done) {
            //
            Log.d("TASK", "AWAITING");
        }

        postData(getData().getValue());
        postDone(true);
    }

    @Override
    protected ArrayList<App> doInBackground(ArrayList<App>... params){

        // Fetch user
        final ArrayList<App> blacklist = getData().getValue();
        final ArrayList<App> apps = new ArrayList<App>();

        // Request all apps
        db.request_apps(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Get list of apps
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();

                    // For each app found
                    for(DocumentSnapshot document: documents){
                        // Create base object
                        App new_app = new App(document.getId(), 0l);

                        // Check not blacklisted (ie already connected)
                        boolean blacklisted = false;
                        for(App app: blacklist){
                            if(app.getTitle().equals(document.getId())){
                                blacklisted = true;
                                break;
                            }
                        }
                        if(!blacklisted){
                            new_app.CreateApp(document);
                            apps.add(new_app);
                        }
                    }

                    // End task
                    postData(apps);
                    listeners_done = true;
                } else {
                    // Log error
                    Log.e("DB", "read failed: ", task.getException());
                }
            }
        });
        return apps;
    }
}
