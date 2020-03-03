package derenvural.sourceread_prototype.ui.home;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import derenvural.sourceread_prototype.data.article.Article;
import derenvural.sourceread_prototype.data.cards.Card;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<Card>> mCards;

    public HomeViewModel() {
        mText = new MutableLiveData<String>();
        mText.setValue("");
        mCards = new MutableLiveData<ArrayList<Card>>();
        mCards.setValue(new ArrayList<Card>());
    }

    // SET
    public void setText(String s) {
        mText.setValue(s);
    }
    public void setCards(ArrayList<Card> cards) {
        mCards.setValue(cards);
    }

    // GET
    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<ArrayList<Card>> getCards() { return mCards; }

    // Check how many articles connected & verify quantity
    public void check_articles(LifecycleOwner context, LoggedInUser user) {
        // Populate articles
        // Get current apps
        if(user.getArticles().getValue() != null && user.getArticles().getValue().size() != 0) {
            final ArrayList<Card> cards = new ArrayList<Card>();
            for (Article article : user.getArticles().getValue()) {
                Card new_card = new Card(null, article.getResolved_title(), article.getResolved_url());
                cards.add(new_card);
            }
            setCards(cards);
        }
        // Add observer for future
        user.getArticles().observe(context, new Observer<ArrayList<Article>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Article> articles) {
                //
                if (articles == null) {
                    return;
                }
                if (articles.size() > 0) {
                    Log.d("DB", "# Saved Articles: " + articles.size());

                    // Fetch data to be displayed
                    final ArrayList<Card> cards = new ArrayList<Card>();
                    for (Article article : articles) {
                        Card new_card = new Card(null, article.getResolved_title(), article.getResolved_url());
                        cards.add(new_card);
                    }
                    setCards(cards);
                }
            }
        });
    }
}
