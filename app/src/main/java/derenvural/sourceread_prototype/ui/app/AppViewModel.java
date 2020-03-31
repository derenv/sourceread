package derenvural.sourceread_prototype.ui.app;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import derenvural.sourceread_prototype.data.cards.App;

public class AppViewModel extends ViewModel {
    private MutableLiveData<App> mApp;

    public AppViewModel() {
        mApp = new MutableLiveData<>();
        mApp.setValue(null);
    }

    // SET
    public void setApp(App s) { mApp.setValue(s); }

    // GET
    public LiveData<App> getApp() {
        return mApp;
    }
}