package derenvural.sourceread_prototype.ui.app;

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

import derenvural.sourceread_prototype.MainActivity;
import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.cards.Card;
import derenvural.sourceread_prototype.data.cards.CardAdapter;

public class AppFragment extends Fragment {

    private AppViewModel appViewModel;
    //private RecyclerView recyclerView;
    //private RecyclerView.Adapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // get view model
        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        View root = inflater.inflate(R.layout.fragment_apps, container, false);

        // find view to be linked
        //recyclerView = root.findViewById(R.id.card_view);

        // use a linear layout manager
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        //mAdapter = new CardAdapter(getActivity(), appViewModel.getCards().getValue());
        //recyclerView.setAdapter(mAdapter);

        // link message text to view-model data
        //final TextView textView = root.findViewById(R.id.text_apps);
        //appViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
        //    @Override
        //    public void onChanged(@Nullable String s) {
        //        textView.setText(s);
        //    }
        //});

        // link cards to view-model data
        /*appViewModel.getCards().observe(getViewLifecycleOwner(), new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Card> updatedList) {
                // Reset adapter
                mAdapter = new CardAdapter(getActivity(), appViewModel.getCards().getValue());
                recyclerView.setAdapter(mAdapter);

                // If list still empty, display appropriate text
                if(mAdapter.getItemCount() <= 0) {
                    appViewModel.setText("This is where you add article saver apps!");
                } else {
                    appViewModel.setText("");
                }
            }
        }); */

        // Request app
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
        //appViewModel.check_app(main, main.getUser());
    }
}