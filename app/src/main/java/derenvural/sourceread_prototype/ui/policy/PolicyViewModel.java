package derenvural.sourceread_prototype.ui.policy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PolicyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PolicyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the privacy policy fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}