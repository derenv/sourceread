package derenvural.sourceread_prototype.ui.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.analyse.analyser;
import derenvural.sourceread_prototype.ui.article.ArticleActivity;
import derenvural.sourceread_prototype.ui.article.ArticleViewModel;

public class AnalysisFragment extends Fragment {
    // Context
    private ArticleActivity aa;
    // View-Model
    private ArticleViewModel articleViewModel;
    // Buttons
    private Button analyseButton;
    // Text Views
    private TextView analysisPlaceholder;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Get view model
        articleViewModel = ViewModelProviders.of(getActivity()).get(ArticleViewModel.class);
        aa = (ArticleActivity) getActivity();

        View root = inflater.inflate(R.layout.fragment_analysis, container, false);

        // Find text views
        analysisPlaceholder = root.findViewById(R.id.text_article_analysis);

        // Find button
        analyseButton = root.findViewById(R.id.button_analyse_article);

        // FIXME: change to analysis check
        if(articleViewModel.getArticle().getValue() != null &&
            articleViewModel.getArticle().getValue().getText() != null &&
            !articleViewModel.getArticle().getValue().getText().equals("")){
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
            analysisPlaceholder.setVisibility(View.VISIBLE);
            analysisPlaceholder.setText("DONE!");

            // Hide button
            analyseButton.setVisibility(View.GONE);
        }else{
            // Hide analysis views
            analysisPlaceholder.setVisibility(View.VISIBLE);

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

                    // Get articles and create async task
                    analyser at = new analyser(aa, aa.getDatabase());
                    at.fetch_article(articleViewModel.getArticle().getValue(), new Observer<Boolean>() {
                        // Called when "fetch_article" has a response
                        @Override
                        public void onChanged(Boolean done) {
                            if (done) {
                                // Activate interface
                                aa.activate_interface();

                                // Update fragment views
                                show_analysis(true);
                            }
                        }
                    });
                }
            });
        }
    }
}
