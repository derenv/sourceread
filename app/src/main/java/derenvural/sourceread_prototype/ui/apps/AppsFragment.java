package derenvural.sourceread_prototype.ui.apps;

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
import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.cards.AppAdapter;
import derenvural.sourceread_prototype.R;

public class AppsFragment extends Fragment {

    private AppsViewModel appsViewModel;
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
        mAdapter = new AppAdapter(getActivity(), appsViewModel.getCards().getValue());
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
        appsViewModel.getCards().observe(getViewLifecycleOwner(), new Observer<ArrayList<App>>() {
            @Override
            public void onChanged(@Nullable ArrayList<App> updatedList) {
                // Reset adapter
                mAdapter = new AppAdapter(getActivity(), appsViewModel.getCards().getValue());
                recyclerView.setAdapter(mAdapter);

                // If list still empty, display appropriate text
                if(mAdapter.getItemCount() <= 0) {
                    appsViewModel.setText(getString(R.string.apps_placeholder));
                } else {
                    appsViewModel.setText("");
                }
            }
        });

        // Request apps
        update();

        return root;
    }

    /*
     * Fetch saved apps
     * */
    public void update(){
        // Request articles
        MainActivity main = (MainActivity) getActivity();

        // Add observer to articles
        appsViewModel.check_apps(main, main.getUser());
    }
}