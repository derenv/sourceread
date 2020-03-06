package derenvural.sourceread_prototype.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import derenvural.sourceread_prototype.MainActivity;
import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.cards.Card;
import derenvural.sourceread_prototype.data.cards.CardAdapter;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // find view to be linked
        recyclerView = root.findViewById(R.id.card_view);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new CardAdapter(getActivity(), homeViewModel.getCards().getValue());
        recyclerView.setAdapter(mAdapter);

        // Progress bar
        progressBar = root.findViewById(R.id.loading_home);

        // link message text to view-model data
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // link cards to view-model data
        homeViewModel.getCards().observe(getViewLifecycleOwner(), new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Card> updatedList) {
                // Reset adapter
                mAdapter = new CardAdapter(getActivity(), homeViewModel.getCards().getValue());
                recyclerView.setAdapter(mAdapter);

                // If list still empty, display appropriate text and hide loading bar
                if(mAdapter.getItemCount() == 0) {
                    homeViewModel.setText("This is where imported articles appear!");
                } else {
                    // Set text blank
                    homeViewModel.setText("");
                }
            }
        });

        // Request articles
        update();

        return root;
    }

    /*
     * Fetch api tokens, auth tokens, and user token
     * */
    public void update(){
        // Request articles
        MainActivity main = (MainActivity) getActivity();

        // Add observer to articles
        progressBar.setVisibility(View.VISIBLE);
        homeViewModel.check_articles(main, main.getUser(), progressBar);
    }
}