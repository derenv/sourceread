package derenvural.sourceread_prototype.ui.home;

import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import derenvural.sourceread_prototype.MainActivity;
import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.cards.Article;
import derenvural.sourceread_prototype.data.cards.ArticleAdapter;
import derenvural.sourceread_prototype.data.cards.filterType;
import derenvural.sourceread_prototype.ui.article.ArticleActivity;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private filterType filter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Find view to be linked
        recyclerView = root.findViewById(R.id.card_view);

        // Use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // Specify card listener
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View vx) {
                // Find article card clicked
                TextView current = vx.findViewById(R.id.card_title);

                // Start article activity with article object
                startArticleActivity(current.getText().toString());
            }
        };

        // Specify filter type
        final MainActivity main = (MainActivity) getActivity();
        filter = main.getFilter().getValue();
        main.getFilter().observe(getViewLifecycleOwner(), new Observer<filterType>() {
            @Override
            public void onChanged(@Nullable filterType sortFilter) {
                // Get filter
                filter = sortFilter;
                
                // Reset adapter
                mAdapter = new ArticleAdapter(main, homeViewModel.getCards().getValue(), listener, filter);
                recyclerView.setAdapter(mAdapter);
            }
        });

        // Specify an adapter
        mAdapter = new ArticleAdapter(main, homeViewModel.getCards().getValue(), listener, filter);
        recyclerView.setAdapter(mAdapter);

        // Link message text to view-model data
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // Link cards to view-model data
        homeViewModel.getCards().observe(getViewLifecycleOwner(), new Observer<ArrayList<Article>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Article> updatedList) {
                // Reset adapter
                mAdapter = new ArticleAdapter(main, updatedList, listener, filter);
                recyclerView.setAdapter(mAdapter);

                // If list still empty, display appropriate text and hide loading bar
                if(mAdapter.getItemCount() == 0) {
                    homeViewModel.setText(getString(R.string.home_placeholder));
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

    private void startArticleActivity(String title){
        // Get article for passing
        for(Article article: homeViewModel.getCards().getValue()) {
            if(article.getTitle().equals(title)){
                // Fetch user data
                SourceReadActivity currentActivity = (SourceReadActivity) getActivity();

                // create article redirect intent
                Intent article_activity = new Intent(currentActivity, ArticleActivity.class);

                // Create bundle with serialised object
                Bundle bundle = new Bundle();
                article.saveInstanceState(bundle);
                currentActivity.getUser().saveInstanceState(bundle);

                // Add title & bundle to intent
                article_activity.putExtra("activity", "main");
                article_activity.putExtras(bundle);
                article_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Start article activity and close main activity
                currentActivity.startActivity(article_activity);
                currentActivity.finish();
            }
        }
    }

    /*
     * Fetch articles
     * */
    public void update(){
        // Request articles
        SourceReadActivity currentActivity = (SourceReadActivity) getActivity();

        // Add observer to articles
        homeViewModel.check_articles(currentActivity, currentActivity.getUser());
    }
}