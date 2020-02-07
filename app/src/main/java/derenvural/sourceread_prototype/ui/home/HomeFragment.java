package derenvural.sourceread_prototype.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.cards.Card;
import derenvural.sourceread_prototype.data.cards.CardAdapter;
import derenvural.sourceread_prototype.data.database.fdatabase;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private fdatabase db;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // find view to be linked
        recyclerView = root.findViewById(R.id.card_view);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new CardAdapter(getActivity(), homeViewModel.getCards().getValue());
        recyclerView.setAdapter(mAdapter);

        // link & update cards
        homeViewModel.getCards().observe(getViewLifecycleOwner(), new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Card> updatedList) {
                // Reset adapter
                mAdapter = new CardAdapter(getActivity(), homeViewModel.getCards().getValue());
                recyclerView.setAdapter(mAdapter);

                // If list still empty, display appropriate text
                if(mAdapter.getItemCount() == 0) {
                    homeViewModel.setText("Import some articles...");
                } else {
                    // Set text blank
                    homeViewModel.setText("");
                }
            }
        });

        // Check if current user has > 0 articles
        db = new fdatabase();
        check_articles();

        // Text
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }

    // Check how many articles connected & verify quantity
    private void check_articles() {
        // Make apps request
        db.request_user_articles();

        // Add observer for db response
        db.get_user_articles().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
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
                    db.get_current_articles().observe(getViewLifecycleOwner(), new Observer<ArrayList<HashMap<String, Object>>>() {
                        @Override
                        public void onChanged(@Nullable ArrayList<HashMap<String, Object>> article_data) {
                            // Fetch data to be displayed
                            final ArrayList<Card> cards = new ArrayList<Card>();
                            for(HashMap<String, Object> article : article_data) {
                                Card new_card = new Card(0, (String) article.get("title"), (String) article.get("author"));
                                cards.add(new_card);
                            }
                            homeViewModel.setCards(cards);
                        }
                    });
                }
            }
        });
    }
}