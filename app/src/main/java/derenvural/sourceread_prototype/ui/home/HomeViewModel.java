package derenvural.sourceread_prototype.ui.home;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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
    private MutableLiveData<ArrayList<Article>> mCards;

    public HomeViewModel() {
        mText = new MutableLiveData<String>();
        mText.setValue("");
        mCards = new MutableLiveData<ArrayList<Article>>();
        mCards.setValue(new ArrayList<Article>());
    }

    // SET
    public void setText(String s) {
        mText.setValue(s);
    }
    public void setCards(ArrayList<Article> cards) {
        mCards.setValue(cards);
    }

    // GET
    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<ArrayList<Article>> getCards() { return mCards; }

    // Check how many articles connected & verify quantity
    public void check_articles(LifecycleOwner context, LoggedInUser user, final ProgressBar progressBar) {
        // Populate articles
        // Get current apps
        if(user.getArticles().getValue() != null && user.getArticles().getValue().size() != 0) {
            final ArrayList<Article> cards = new ArrayList<Article>();
            for (Article article : user.getArticles().getValue()) {
                //Card new_card = new Card(null, article.getResolved_title(), article.getResolved_url());
                cards.add(article);
            }
            setCards(cards);
        }
        // Add observer for future
        user.getArticles().observe(context, new Observer<ArrayList<Article>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Article> articles) {
                // Update UI
                progressBar.setVisibility(View.GONE);

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
