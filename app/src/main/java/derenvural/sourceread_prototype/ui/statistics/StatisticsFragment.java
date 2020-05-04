package derenvural.sourceread_prototype.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.functions.graph.Graph;
import derenvural.sourceread_prototype.data.functions.graph.GraphAdapter;

public class StatisticsFragment extends Fragment {
    // Context
    private SourceReadActivity currentActivity;
    // View-Model
    private StatisticsViewModel statisticsViewModel;
    // Graph
    private RecyclerView statisticCardView;
    private RecyclerView.Adapter mStatisticAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                ViewModelProviders.of(this).get(StatisticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        currentActivity = (SourceReadActivity) getActivity();


        // Find graph view
        statisticCardView = root.findViewById(R.id.overall_statistics_view);

        // Use a linear layout manager
        RecyclerView.LayoutManager searchLayoutManager = new LinearLayoutManager(getActivity());
        statisticCardView.setLayoutManager(searchLayoutManager);

        // Add line divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(statisticCardView.getContext(),
                ((LinearLayoutManager) searchLayoutManager).getOrientation());
        statisticCardView.addItemDecoration(dividerItemDecoration);

        // Create search bar function for adapter
        Graph graph = new Graph(getText(R.string.overall_from_card_title), getText(R.string.overall_from_card_text), null);
        Graph centrality = new Graph(getText(R.string.overall_quality_card_title), getText(R.string.overall_quality_card_text), null);
        ArrayList<Graph> functions = new ArrayList<Graph>();
        functions.add(graph);
        functions.add(centrality);

        // Specify an adapter
        mStatisticAdapter = new GraphAdapter(currentActivity, functions);
        statisticCardView.setAdapter(mStatisticAdapter);

        return root;
    }
}