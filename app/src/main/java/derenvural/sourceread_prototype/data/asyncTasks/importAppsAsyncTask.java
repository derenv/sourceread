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
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class importAppsAsyncTask extends sourcereadAsyncTask<ArrayList<App>> {
    // Tools
    private fdatabase db;
    // Data
    private LoggedInUser user;

    public importAppsAsyncTask(ArrayList<App> apps, fdatabase db, LoggedInUser user){
        super();
        // Data
        setData(apps);
        this.user = user;

        // Tools
        this.db = db;
    }

    @Override
    protected Void doInBackground(Void... params){

        // Fetch user
        final ArrayList<App> apps = getData().getValue();

        // Request all apps
        db.request_apps(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Get list of apps
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    ArrayList<App> apps = new ArrayList<App>();

                    // For each app found
                    for(DocumentSnapshot document: documents){
                        // Create base object
                        App new_app = new App(document.getId(), 0);

                        // If already added get timestamp
                        for(App app: user.getApps().getValue()){
                            if(app.getTitle().equals(document.getId())){
                                new_app = new App(app.getTitle(), app.getTimestamp());
                                break;
                            }
                        }

                        // Build rest of data from document and add to result
                        new_app.CreateApp(document);
                        apps.add(new_app);
                    }

                    // End task
                    postData(apps);
                    postDone(true);
                } else {
                    // Log error
                    Log.e("DB", "read failed: ", task.getException());
                }
            }
        });
        return null;
    }
}
