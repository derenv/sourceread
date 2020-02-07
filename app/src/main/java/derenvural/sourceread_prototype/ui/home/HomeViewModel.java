package derenvural.sourceread_prototype.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import derenvural.sourceread_prototype.data.cards.Card;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<Card>> mCards;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");

        mCards = new MutableLiveData<ArrayList<Card>>();
        mCards.setValue(new ArrayList<Card>());
    }

    public void setText(String s) {
        mText.setValue(s);
    }
    public void setCards(ArrayList<Card> cards) {
        mCards.setValue(cards);
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<ArrayList<Card>> getCards() { return mCards; }
}