package derenvural.sourceread_prototype.ui.analysis;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.cards.Card;
import derenvural.sourceread_prototype.data.cards.apps.App;
import derenvural.sourceread_prototype.data.functions.Function;
import derenvural.sourceread_prototype.data.functions.graph.Graph;
import derenvural.sourceread_prototype.data.functions.graph.GraphAdapter;
import derenvural.sourceread_prototype.ui.article.ArticleActivity;
import derenvural.sourceread_prototype.ui.article.ArticleViewModel;

public class AnalysisFragment extends Fragment {
    // Context
    private ArticleActivity aa;
    // View-Model
    private ArticleViewModel articleViewModel;
    // Buttons
    private Button analyseButton;
    // Graph
    private RecyclerView graphCardView;
    private RecyclerView.Adapter mGraphAdapter;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Get view model
        articleViewModel = ViewModelProviders.of(getActivity()).get(ArticleViewModel.class);
        aa = (ArticleActivity) getActivity();

        View root = inflater.inflate(R.layout.fragment_analysis, container, false);

        // Find button
        analyseButton = root.findViewById(R.id.button_analyse_article);


        // Find graph view
        graphCardView = root.findViewById(R.id.inferences_graph);

        // Use a linear layout manager
        RecyclerView.LayoutManager searchLayoutManager = new LinearLayoutManager(getActivity());
        graphCardView.setLayoutManager(searchLayoutManager);

        // Add line divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(graphCardView.getContext(),
                ((LinearLayoutManager) searchLayoutManager).getOrientation());
        graphCardView.addItemDecoration(dividerItemDecoration);

        // Create search bar function for adapter
        Graph graph = new Graph(getText(R.string.inference_graph_title), getText(R.string.inference_graph_text), R.drawable.ic_inference_graph);
        Graph centrality = new Graph(getText(R.string.centrality_card_title), getText(R.string.centrality_card_text), null);
        ArrayList<Graph> functions = new ArrayList<Graph>();
        functions.add(graph);
        functions.add(centrality);

        // Specify an adapter
        mGraphAdapter = new GraphAdapter(aa, functions);
        graphCardView.setAdapter(mGraphAdapter);

        // FIXME: change to analysis check
        if(articleViewModel.getArticle().getValue() != null &&
            articleViewModel.getArticle().getValue().getText() != null &&
            !articleViewModel.getArticle().getValue().getAif().equals("")){
            show_analysis(true);

            return root;
        }else{
            show_analysis(false);

            return root;
        }
    }

    private void show_analysis(boolean done){
        if(done) {
            // Show analysis views
            graphCardView.setVisibility(View.VISIBLE);

            // Hide button
            analyseButton.setVisibility(View.GONE);
        }else{
            // Hide analysis views
            graphCardView.setVisibility(View.INVISIBLE);

            // Show button for beginning analysis
            analyseButton.setVisibility(View.VISIBLE);

            // Create listener for button-click
            analyseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Inform user
                    Toast.makeText(aa, "Analysing article...", Toast.LENGTH_SHORT).show();

                    // Disable menu button
                    aa.deactivate_interface();

                    // Wait appropriate time
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // Show image
                            show_analysis(true);

                            // Re-enable menu button
                            aa.activate_interface();
                        }
                    }, 5000);
                }
            });
        }
    }
}
