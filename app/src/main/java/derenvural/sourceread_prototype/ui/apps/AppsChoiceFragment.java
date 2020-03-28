package derenvural.sourceread_prototype.ui.apps;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import derenvural.sourceread_prototype.MainActivity;
import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.asyncTasks.importAppsAsyncTask;
import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.cards.AppAdapter;

public class AppsChoiceFragment extends Fragment {
    private AppChoiceViewModel appChoiceViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        appChoiceViewModel = ViewModelProviders.of(this).get(AppChoiceViewModel.class);
        View root = inflater.inflate(R.layout.fragment_appschoice, container, false);

        // find view to be linked
        recyclerView = root.findViewById(R.id.card_view);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // Specify card listener
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override public void onClick(View vx) {
                // Find article card clicked
                TextView current = vx.findViewById(R.id.card_title);

                // Start app activity with app object
                startAppActivity(current.getText().toString());
            }
        };

        // specify an adapter
        mAdapter = new AppAdapter(getActivity(), appChoiceViewModel.getCards().getValue(), listener);
        recyclerView.setAdapter(mAdapter);

        // link cards to view-model data
        appChoiceViewModel.getCards().observe(getViewLifecycleOwner(), new Observer<ArrayList<App>>() {
            @Override
            public void onChanged(@Nullable ArrayList<App> updatedList) {
                // Reset adapter
                mAdapter = new AppAdapter(getActivity(), updatedList, listener);
                recyclerView.setAdapter(mAdapter);
            }
        });

        // Request apps
        update();

        return root;
    }

    /*
     * Start process of adding chosen app
     * */
    private void startAppActivity(String title){
        // Get app for passing
        for (App app : appChoiceViewModel.getCards().getValue()) {
            if (app.getTitle().equals(title)) {
                // TODO: add selected app
                Toast.makeText(getContext(), "Adding '"+title+"' app..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * Fetch apps
     * */
    public void update() {
        // Deactivate the UI
        final MainActivity main = (MainActivity) getActivity();
        main.deactivate_interface();

        // Create async task
        final importAppsAsyncTask task = new importAppsAsyncTask(new ArrayList<App>(), main.getDatabase(), main.getUser());

        // execute async task
        task.execute();

        // Check for task finish
        task.getDone().observe(main, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean done) {
                if (done) {
                    Log.d("TASK", "apps fetched!");

                    // Get apps
                    appChoiceViewModel.setCards(task.getData().getValue());

                    // Reactivate the UI
                    main.activate_interface();
                }
            }
        });
    }
}
