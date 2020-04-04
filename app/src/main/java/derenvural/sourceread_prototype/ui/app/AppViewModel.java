package derenvural.sourceread_prototype.ui.app;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDateTime;

import derenvural.sourceread_prototype.data.cards.App;

public class AppViewModel extends ViewModel {
    private MutableLiveData<App> mApp;
    private MutableLiveData<Integer> mArticlesNo;
    private MutableLiveData<LocalDateTime> mStamp;

    public AppViewModel() {
        mApp = new MutableLiveData<App>(null);
        mArticlesNo = new MutableLiveData<Integer>(0);
        mStamp = new MutableLiveData<LocalDateTime>(null);
    }

    // SET
    public void setApp(App s) { mApp.setValue(s); }
    public void setArticlesNo(Integer s) { mArticlesNo.setValue(s); }
    public void setStamp(LocalDateTime s) { mStamp.setValue(s); }

    // GET
    public LiveData<App> getApp() { return mApp; }
    public LiveData<Integer> getArticlesNo() {
        return mArticlesNo;
    }
    public LiveData<LocalDateTime> getStamp() {
        return mStamp;
    }
}