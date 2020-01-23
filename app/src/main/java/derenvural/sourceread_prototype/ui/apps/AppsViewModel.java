package derenvural.sourceread_prototype.ui.apps;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import derenvural.sourceread_prototype.data.cards.Card;

public class AppsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private MutableLiveData<ArrayList<Card>> mCards;

    public AppsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");

        mCards = new MutableLiveData<ArrayList<Card>>();
        mCards.setValue(new ArrayList<Card>());
    }

    public void setText(String s) {
        mText.setValue(s);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setCards(ArrayList<Card> cards) {
        mCards.setValue(cards);
    }

    public void addCard(int si, String st, String sd) {
        ArrayList<Card> NewCards = mCards.getValue();
        NewCards.add(new Card(si,st,sd));
        mCards.setValue(NewCards);
    }

    public LiveData<ArrayList<Card>> getCards() { return mCards; }
}