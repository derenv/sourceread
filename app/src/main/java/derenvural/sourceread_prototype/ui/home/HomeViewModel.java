package derenvural.sourceread_prototype.ui.home;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import derenvural.sourceread_prototype.data.cards.articles.Article;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<Article>> mCards;
    private MutableLiveData<ArrayList<Article>> mSearchResults;

    public HomeViewModel() {
        mText = new MutableLiveData<String>();
        mText.setValue("");
        mCards = new MutableLiveData<ArrayList<Article>>();
        mCards.setValue(new ArrayList<Article>());
        mSearchResults = new MutableLiveData<ArrayList<Article>>();
        mSearchResults.setValue(null);
    }

    // SET
    public void setText(String s) {
        mText.setValue(s);
    }
    public void setCards(ArrayList<Article> cards) {
        mCards.setValue(cards);
    }
    public void setSearchResults(ArrayList<Article> cards) {
        mSearchResults.setValue(cards);
    }

    // GET
    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<ArrayList<Article>> getCards() { return mCards; }
    public LiveData<ArrayList<Article>> getSearchResults() { return mSearchResults; }

    // Check how many articles connected & verify quantity
    public void check_articles(LifecycleOwner context, LoggedInUser user) {
        // Populate articles
        // Get current apps
        if(user.getArticles().getValue() != null && user.getArticles().getValue().size() != 0) {
            final ArrayList<Article> cards = new ArrayList<Article>();
            for (Article article : user.getArticles().getValue()) {
                cards.add(article);
            }
            setCards(cards);
        }
        // Add observer for future
        user.getArticles().observe(context, new Observer<ArrayList<Article>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Article> articles) {
                //check articles returned
                if (articles == null) {
                    return;
                }
                if (articles.size() > 0) {
                    setCards(articles);
                }
            }
        });
    }
}
