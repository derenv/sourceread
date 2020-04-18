package derenvural.sourceread_prototype.ui.apps;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import derenvural.sourceread_prototype.data.cards.apps.App;

public class AppChoiceViewModel extends ViewModel {
    private MutableLiveData<ArrayList<App>> mCards;
    private MutableLiveData<String> mText;

    public AppChoiceViewModel() {
        mCards = new MutableLiveData<ArrayList<App>>(new ArrayList<App>());
        mText = new MutableLiveData<String>("");
    }

    // SET
    public void setCards(ArrayList<App> cards) {
        mCards.setValue(cards);
    }
    public void setText(String text) {
        mText.setValue(text);
    }

    // GET
    public LiveData<ArrayList<App>> getCards() { return mCards; }
    public LiveData<String> getText() { return mText; }
}
