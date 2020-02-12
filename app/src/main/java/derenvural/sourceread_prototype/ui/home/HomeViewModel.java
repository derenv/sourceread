package derenvural.sourceread_prototype.ui.home;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.data.cards.Card;
import derenvural.sourceread_prototype.data.database.fdatabase;

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

    // Check how many articles connected & verify quantity
    public void check_articles(final FragmentActivity context) {
        // Make apps request
        final fdatabase db = new fdatabase();
        db.request_user_articles();

        // Add observer for db response
        db.get_user_articles().observe(context, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> articles) {
                // Get list of apps
                Log.d("DB", "# Saved Articles: " + articles.size());

                // Check for invalid data
                if (articles == null) {
                    return;
                }
                if (articles.size() > 0) {
                    // Create and set card for each article
                    db.request_article_data(articles);
                    db.get_current_articles().observe(context, new Observer<ArrayList<HashMap<String, Object>>>() {
                        @Override
                        public void onChanged(@Nullable ArrayList<HashMap<String, Object>> article_data) {
                            // Fetch data to be displayed
                            final ArrayList<Card> cards = new ArrayList<Card>();
                            for(HashMap<String, Object> article : article_data) {
                                Card new_card = new Card(null, (String) article.get("title"), (String) article.get("author"));
                                cards.add(new_card);
                            }
                            setCards(cards);
                        }
                    });
                }
            }
        });
    }
}