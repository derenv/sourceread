package derenvural.sourceread_prototype.ui.apps;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class AppsViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<App>> mCards;

    public AppsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");

        mCards = new MutableLiveData<ArrayList<App>>();
        mCards.setValue(new ArrayList<App>());
    }

    public void setText(String s) {
        mText.setValue(s);
    }
    public void setCards(ArrayList<App> cards) {
        mCards.setValue(cards);
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<ArrayList<App>> getCards() { return mCards; }

    // Check how many apps connected & verify quantity
    public void check_apps(LifecycleOwner context, LoggedInUser user) {
        // Populate apps
        // Get current apps
        if(user.getApps().getValue() != null && user.getApps().getValue().size() != 0) {
            setCards(user.getApps().getValue());
        }

        // Add observer for future
        user.getApps().observe(context, new Observer<ArrayList<App>>() {
            @Override
            public void onChanged(@Nullable ArrayList<App> apps) {
                //check apps returned
                if (apps == null) {
                    return;
                }
                if (apps.size() > 0) {
                    Log.d("DB", "# Saved Apps: " + apps.size());
                    ArrayList<App> cards = new ArrayList<App>();
                    cards.addAll(apps);
                    App add_card = new App("Add new App",0l);
                    add_card.setText("(Click me!)");
                    cards.add(add_card);
                    setCards(cards);
                }else{
                    // Add 'add app' card to list
                    ArrayList<App> cards = new ArrayList<App>();
                    App add_card = new App("Add new App",0l);
                    add_card.setText("(Click me!)");
                    cards.add(add_card);
                    setCards(cards);
                }
            }
        });
    }
}