package derenvural.sourceread_prototype.ui.app;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AppViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AppViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public void setText(String s) {
        mText.setValue(s);
    }

    public LiveData<String> getText() {
        return mText;
    }
}