package derenvural.sourceread_prototype.data.asyncTasks;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public abstract class sourcereadAsyncTask<P, R> extends AsyncTask<P, Void, R> {
    // Common task fields
    private MutableLiveData<Boolean> done;
    private MutableLiveData<R> data;

    public sourcereadAsyncTask(){
        // Set status of task
        done = new MutableLiveData<Boolean>();
        setDone(false);

        // Initialise data output in memory, must be set in extending classes constructor
        data = new MutableLiveData<R>();
        setData(null);
    }

    @Override
    protected void onPostExecute(R aVoid) {
        super.onPostExecute(aVoid);
    }
    @Override
    protected R doInBackground(P... params) { return null; }

    // Get
    public LiveData<Boolean> getDone(){ return done; }
    public LiveData<R> getData(){ return data; }
    // Set
    public void setDone(Boolean done){ this.done.setValue(done); }
    public void setData(R o){ this.data.setValue(o); }
    // Post
    public void postDone(Boolean done){ this.done.postValue(done); }
    public void postData(R o){ this.data.postValue(o); }
}
