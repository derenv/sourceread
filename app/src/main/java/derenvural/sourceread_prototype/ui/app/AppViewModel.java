package derenvural.sourceread_prototype.ui.app;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AppViewModel extends ViewModel {
    private MutableLiveData<String> mImage;
    private MutableLiveData<String> mName;
    private MutableLiveData<String> mDescription;
    private MutableLiveData<Long> mTimestamp;

    public AppViewModel() {
        mImage = new MutableLiveData<>();
        mImage.setValue("");

        mName = new MutableLiveData<>();
        mName.setValue("");

        mDescription = new MutableLiveData<>();
        mDescription.setValue("");

        mTimestamp = new MutableLiveData<>();
        mTimestamp.setValue(0l);
    }

    // SET
    public void setName(String s) { mName.setValue(s); }
    public void setDescription(String s) {
        mDescription.setValue(s);
    }
    public void setTimestamp(Long s) { mTimestamp.setValue(s); }
    public void setImage(String s) {
        mImage.setValue(s);
    }

    // GET
    public LiveData<String> getName() {
        return mName;
    }
    public LiveData<String> getDescription() {
        return mDescription;
    }
    public LiveData<Long> getTimestamp() {
        return mTimestamp;
    }
    public LiveData<String> getImage() {
        return mImage;
    }
}