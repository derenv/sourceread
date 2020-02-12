package derenvural.sourceread_prototype.ui.apps;

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


    // Check how many apps connected & verify quantity
    public void check_apps(final FragmentActivity context) {
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
                    db.request_app_card(app_names,context);
                    db.get_current_cards().observe(context, new Observer<ArrayList<Card>>() {
                        @Override
                        public void onChanged(@Nullable ArrayList<Card> app_cards) {
                            // Check for invalid data
                            if (app_cards == null) {
                                return;
                            }
                            if (app_cards.size() > 0) {
                                // Remove old 'add app' card
                                ArrayList<Card> new_app_cards = (ArrayList<Card>) app_cards.clone();
                                for(Card app: new_app_cards){
                                    if(app.getTitle().equals("Add new App")){
                                        new_app_cards.remove(app);
                                    }
                                }
                                // Add 'add app' card to list
                                new_app_cards.add(new Card(null, "Add new App", "(click me!)"));
                                setCards(new_app_cards);
                            }
                        }
                    });
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