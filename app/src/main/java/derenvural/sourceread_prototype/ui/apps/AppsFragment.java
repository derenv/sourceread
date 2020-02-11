package derenvural.sourceread_prototype.ui.apps;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.data.cards.Card;
import derenvural.sourceread_prototype.data.cards.CardAdapter;
import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.database.fdatabase;

public class AppsFragment extends Fragment {

    private AppsViewModel appsViewModel;
    private fdatabase db;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // get view model
        appsViewModel = ViewModelProviders.of(this).get(AppsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_apps, container, false);

        // find view to be linked
        recyclerView = root.findViewById(R.id.card_view);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new CardAdapter(getActivity(), appsViewModel.getCards().getValue());
        recyclerView.setAdapter(mAdapter);

        // link message text to view-model data
        final TextView textView = root.findViewById(R.id.text_apps);
        appsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // link cards to view-model data
        appsViewModel.getCards().observe(getViewLifecycleOwner(), new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Card> updatedList) {
                // Reset adapter
                mAdapter = new CardAdapter(getActivity(), appsViewModel.getCards().getValue());
                recyclerView.setAdapter(mAdapter);

                // If list still empty, display appropriate text
                if(mAdapter.getItemCount() <= 0) {
                    appsViewModel.setText("This is where you add article saver apps!");
                } else {
                    appsViewModel.setText("");
                }
            }
        });

        // Check if current user has > 0 apps
        db = new fdatabase();
        check_apps();

        return root;
    }

    // Check how many apps connected & verify quantity
    private void check_apps() {
        // Make apps request
        db.request_user_apps();

        // Add observer for db response
        db.get_user_apps().observe(getViewLifecycleOwner(), new Observer<HashMap<String,String>>() {
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
                    db.get_current_apps().observe(getViewLifecycleOwner(), new Observer<ArrayList<HashMap<String, Object>>>() {
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
                                    Card new_card = new Card(0, app_names[i].toString(), app_data.get(i).get("key").toString());
                                    cards.add(new_card);
                                }

                                // Add 'add app' card to list
                                cards.add(new Card(0, "Add new App", "(click me!)"));
                                appsViewModel.setCards(cards);
                            }
                        }
                    });
                }else{

                    // Add 'add app' card to list
                    final ArrayList<Card> cards = new ArrayList<Card>();
                    cards.add(new Card(0, "Add new App", "(click me!)"));
                    appsViewModel.setCards(cards);
                }
                // TODO: add separate click listener for add apps? or check title in original?
            }
        });
    }
}