package derenvural.sourceread_prototype.ui.apps;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;
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

import derenvural.sourceread_prototype.data.cards.Card;
import derenvural.sourceread_prototype.data.cards.CardAdapter;
import derenvural.sourceread_prototype.R;

public class AppsFragment extends Fragment {

    private AppsViewModel appsViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // get view model
        appsViewModel = ViewModelProviders.of(this).get(AppsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_apps, container, false);

        // find view to be linked
        recyclerView = root.findViewById(R.id.card_view);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new CardAdapter(getActivity(), appsViewModel.getCards().getValue());
        recyclerView.setAdapter(mAdapter);

        // link & update message text
        final TextView textView = root.findViewById(R.id.text_apps);
        appsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // link & update cards
        appsViewModel.getCards().observe(this, new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(final ArrayList<Card> updatedList) {
                //textView.setCards(s);
                mAdapter = new CardAdapter(getActivity(), appsViewModel.getCards().getValue());
                recyclerView.setAdapter(mAdapter);

                // if list still empty, display appropriate text
                if(mAdapter.getItemCount() <= 0) {
                    appsViewModel.setText("This is where you add article saver apps!");
                } else {
                    appsViewModel.setText("All apps successfully added!");
                }
            }
        });

        // add test value
        ArrayList<Card> newCards = new ArrayList<Card>();
        newCards.add(new Card(R.drawable.ic_card_placeholder1, "Test Title 1", "this is testing text, test test test test! test test"));
        newCards.add(new Card(R.drawable.ic_card_placeholder1, "TEST Title 2", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor  ex ea commodo consequat. Duis aute irure dolor"));
        newCards.add(new Card(R.drawable.ic_card_placeholder1, "TEST TITLE 3", "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip"));
        newCards.add(new Card(R.drawable.ic_card_placeholder1, "Test Title 4", "in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat"));
        newCards.add(new Card(R.drawable.ic_card_placeholder1, "TEST TITLE 5", "non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
        newCards.add(new Card(R.drawable.ic_card_placeholder1, "TEST TITLE 6", "test test test test test"));
        newCards.add(new Card(R.drawable.ic_card_placeholder1, "Test Title 7", "iufn ijufrngb viun eiuon wreoiun wrjiun wfdejiunmn wefjiu ewkiju nswjhud iudn iunn diw oiisd junfr yhks unsdkjih iundsc dsunjs idsjuncki iujhsoic cx"));
        appsViewModel.setCards(newCards);

        return root;
    }
}