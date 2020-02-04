package derenvural.sourceread_prototype.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AboutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the about app fragment, where all information and disclaimers will be displayed.\nA list should appear: contact us, terms of service and privacy policy");
    }

    public LiveData<String> getText() {
        return mText;
    }
}