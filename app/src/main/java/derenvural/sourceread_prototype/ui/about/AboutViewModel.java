package derenvural.sourceread_prototype.ui.about;

import android.text.Spanned;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutViewModel extends ViewModel {
    private MutableLiveData<Spanned> mText;

    // Constructor
    public AboutViewModel() {
        mText = new MutableLiveData<Spanned>();
    }

    // Get
    public LiveData<Spanned> getText() {
        return mText;
    }

    // Set
    public void setText(Spanned new_text) {
        mText.setValue(new_text);
    }
}