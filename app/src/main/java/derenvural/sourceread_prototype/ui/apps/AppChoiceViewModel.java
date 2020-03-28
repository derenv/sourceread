package derenvural.sourceread_prototype.ui.apps;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import derenvural.sourceread_prototype.data.cards.App;

public class AppChoiceViewModel extends ViewModel {
    private MutableLiveData<ArrayList<App>> mCards;

    public AppChoiceViewModel() {
        mCards = new MutableLiveData<ArrayList<App>>();
        mCards.setValue(new ArrayList<App>());
    }

    // SET
    public void setCards(ArrayList<App> cards) {
        mCards.setValue(cards);
    }

    // GET
    public LiveData<ArrayList<App>> getCards() { return mCards; }
}
