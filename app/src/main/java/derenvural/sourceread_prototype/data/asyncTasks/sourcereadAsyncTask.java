package derenvural.sourceread_prototype.data.asyncTasks;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public abstract class sourcereadAsyncTask<type> extends AsyncTask<Void, Void, Void> {
    // Common task fields
    private MutableLiveData<Boolean> done;
    private MutableLiveData<type> data;

    public sourcereadAsyncTask(){
        // Set status of task
        done = new MutableLiveData<Boolean>();
        setDone(false);

        // Initialise data output in memory, must be set in extending classes constructor
        data = new MutableLiveData<type>();
        setData(null);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
    @Override
    protected Void doInBackground(Void... params) { return null; }

    // Get
    public LiveData<Boolean> getDone(){ return done; }
    public LiveData<type> getData(){ return data; }
    // Set
    public void setDone(Boolean done){ this.done.setValue(done); }
    public void setData(type o){ this.data.setValue(o); }
    // Post
    public void postDone(Boolean done){ this.done.postValue(done); }
    public void postData(type o){ this.data.postValue(o); }
}
