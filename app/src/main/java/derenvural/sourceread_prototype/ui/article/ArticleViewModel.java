package derenvural.sourceread_prototype.ui.article;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class ArticleViewModel extends ViewModel {
    private MutableLiveData<String> mTitle;
    private MutableLiveData<String> mUrl;
    private MutableLiveData<ArrayList<HashMap<String,String>>> mAuthors;
    private MutableLiveData<String> mVeracity;

    public ArticleViewModel(){
        mTitle = new MutableLiveData<String>();
        setTitle("");
        mUrl = new MutableLiveData<String>();
        setUrl("");
        mAuthors = new MutableLiveData<ArrayList<HashMap<String,String>>>();
        setAuthors(new ArrayList<HashMap<String,String>>());
        mVeracity = new MutableLiveData<String>();
        setVeracity("");
    }

    // SET
    public void setTitle(String s) {
        mTitle.setValue(s);
    }
    public void setUrl(String s) {
        mUrl.setValue(s);
    }
    public void setAuthors(ArrayList<HashMap<String,String>> s) {
        mAuthors.setValue(s);
    }
    public void setVeracity(String s) {
        mVeracity.setValue(s);
    }

    // GET
    public LiveData<String> getTitle() {
        return mTitle;
    }
    public LiveData<String> getUrl() {
        return mUrl;
    }
    public LiveData<ArrayList<HashMap<String,String>>> getAuthors() {
        return mAuthors;
    }
    public LiveData<String> getVeracity() {
        return mVeracity;
    }
}
