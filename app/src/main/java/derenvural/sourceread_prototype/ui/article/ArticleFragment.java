package derenvural.sourceread_prototype.ui.article;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.R;

public class ArticleFragment extends Fragment {
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_article, container, false);

        ArticleViewModel articleViewModel = ViewModelProviders.of(getActivity()).get(ArticleViewModel.class);

        // link URL text to view-model data
        final TextView urlView = root.findViewById(R.id.text_article_url);
        articleViewModel.getUrl().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                urlView.setText("URL - "+s);
                Log.d("article", "url set");
            }
        });

        // link author text to view-model data
        final TextView authorView = root.findViewById(R.id.text_article_authors);
        articleViewModel.getAuthors().observe(getViewLifecycleOwner(), new Observer<ArrayList<HashMap<String,String>>>() {
            @Override
            public void onChanged(@Nullable ArrayList<HashMap<String,String>> s) {
                String q = "Authors:\n'"+s.get(0).get("name")+"'";
                for(HashMap<String,String> t: s.subList(1,s.size())){
                    q = q + "\n'" + t.get("name")+"'";
                }
                authorView.setText(q);
                Log.d("article", "authors set");
            }
        });

        // link message text to view-model data
        final TextView veracityView = root.findViewById(R.id.text_article_veracity);
        articleViewModel.getVeracity().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                veracityView.setText("Veracity - "+s);
                Log.d("article", "veracity set");
            }
        });

        // Progress bar
        ProgressBar progressBar = root.findViewById(R.id.loading_article);
        progressBar.setVisibility(View.INVISIBLE);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // remove progress bar once loaded
        //progressBar.setVisibility(View.VISIBLE);
    }

}
