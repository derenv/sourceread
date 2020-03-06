package derenvural.sourceread_prototype.ui.apps;

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
import java.util.HashMap;

import derenvural.sourceread_prototype.data.cards.Card;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

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
    public void setCards(ArrayList<Card> cards) {
        mCards.setValue(cards);
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<ArrayList<Card>> getCards() { return mCards; }

    // Check how many articles connected & verify quantity
    public void check_apps(LifecycleOwner context, LoggedInUser user, final ProgressBar progressBar) {
        // Populate apps
        // Get current apps
        if(user.getApps().getValue() != null && user.getApps().getValue().size() != 0) {
            final ArrayList<Card> cards = new ArrayList<Card>();
            for (HashMap<String, Object> app : user.getApps().getValue()) {
                Card new_card = new Card(null, app.get("name").toString(), app.get("description").toString());
                cards.add(new_card);
            }
            // Add 'add app' card to list
            cards.add(new Card(null, "Add new App", "(click me!)"));
            setCards(cards);
        }

        // Add observer for future
        user.getApps().observe(context, new Observer<ArrayList<HashMap<String, Object>>>() {
            @Override
            public void onChanged(@Nullable ArrayList<HashMap<String, Object>> apps) {
                // Update UI
                progressBar.setVisibility(View.GONE);

                //check apps returned
                if (apps == null) {
                    return;
                }
                if (apps.size() > 0) {
                    Log.d("DB", "# Saved Apps: " + apps.size());

                    // Fetch data to be displayed
                    final ArrayList<Card> cards = new ArrayList<Card>();
                    for (HashMap<String, Object> app : apps) {
                        Card new_card = new Card(null, app.get("name").toString(), app.get("description").toString());
                        cards.add(new_card);
                    }

                    // Add 'add app' card to list
                    cards.add(new Card(null, "Add new App", "(click me!)"));
                    setCards(cards);
                }else{
                    // Add 'add app' card to list
                    final ArrayList<Card> cards = new ArrayList<Card>();
                    cards.add(new Card(null, "Add new App", "(click me!)"));
                    setCards(cards);
                }
            }
        });
    }
}