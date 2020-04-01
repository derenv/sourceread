package derenvural.sourceread_prototype.data.asyncTasks;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public abstract class sourcereadAsyncTask<paramtype, resulttype> extends AsyncTask<paramtype, Void, resulttype> {
    // Common task fields
    private MutableLiveData<Boolean> done;
    private MutableLiveData<resulttype> data;

    public sourcereadAsyncTask(){
        // Set status of task
        done = new MutableLiveData<Boolean>();
        setDone(false);

        // Initialise data output in memory, must be set in extending classes constructor
        data = new MutableLiveData<resulttype>();
        setData(null);
    }

    @Override
    protected void onPostExecute(resulttype aVoid) {
        super.onPostExecute(aVoid);
    }
    @Override
    protected resulttype doInBackground(paramtype... params) { return null; }

    // Get
    public LiveData<Boolean> getDone(){ return done; }
    public LiveData<resulttype> getData(){ return data; }
    // Set
    public void setDone(Boolean done){ this.done.setValue(done); }
    public void setData(resulttype o){ this.data.setValue(o); }
    // Post
    public void postDone(Boolean done){ this.done.postValue(done); }
    public void postData(resulttype o){ this.data.postValue(o); }
}
