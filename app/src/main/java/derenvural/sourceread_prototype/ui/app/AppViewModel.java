package derenvural.sourceread_prototype.ui.app;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.data.cards.Card;
import derenvural.sourceread_prototype.data.database.fdatabase;

public class AppViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<Card>> mCards;

    public AppViewModel() {
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
    public void addCard(Card card) {
        ArrayList<Card> new_cards = mCards.getValue();
        new_cards.add(card);
        mCards.setValue(new_cards);
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<ArrayList<Card>> getCards() { return mCards; }


    // Check how many apps connected & verify quantity
    public void check_apps(final LifecycleOwner context) {
        // Make apps request
        final fdatabase db = new fdatabase();
        db.request_user_apps();

        // Add observer for db response
        db.get_user_apps().observe(context, new Observer<HashMap<String,String>>() {
            @Override
            public void onChanged(@Nullable HashMap<String,String> apps) {
                // Check for invalid data
                if (apps == null) {
                    return;
                }
                if (apps.size() > 0) {
                    // Get list of apps
                    Log.d("DB", "# Saved Apps: " + apps.size());

                    // Request all connected apps
                    final Object[] app_names = apps.keySet().toArray();
                    db.request_app_data(app_names);
                    db.get_current_apps().observe(context, new Observer<ArrayList<HashMap<String, Object>>>() {
                        @Override
                        public void onChanged(@Nullable ArrayList<HashMap<String, Object>> app_data) {
                            // Check for invalid data
                            if (app_data == null) {
                                return;
                            }
                            if (app_data.size() > 0) {
                                // Format list cards & add to view-model
                                final ArrayList<Card> cards = new ArrayList<Card>();
                                for (int i = 0; i < app_data.size(); i++) {
                                    // Show title & other fields of app in list
                                    // TODO: other fields
                                    //app_data.get(i).get("key").toString()
                                    Card new_card = new Card(0, app_names[i].toString(), "");
                                    cards.add(new_card);
                                }

                                // Add 'add app' card to list
                                cards.add(new Card(0, "Add new App", "(click me!)"));
                                setCards(cards);
                            }
                        }
                    });
                }else{

                    // Add 'add app' card to list
                    final ArrayList<Card> cards = new ArrayList<Card>();
                    cards.add(new Card(0, "Add new App", "(click me!)"));
                    setCards(cards);
                }
            }
        });
    }
}