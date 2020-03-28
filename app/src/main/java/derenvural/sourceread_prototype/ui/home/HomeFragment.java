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
import derenvural.sourceread_prototype.data.cards.Article;
import derenvural.sourceread_prototype.data.cards.ArticleAdapter;
import derenvural.sourceread_prototype.ui.article.ArticleActivity;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // find view to be linked
        recyclerView = root.findViewById(R.id.card_view);

        // use a linear layout manager
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

        // specify an adapter
        mAdapter = new ArticleAdapter(getActivity(), homeViewModel.getCards().getValue(), listener);
        recyclerView.setAdapter(mAdapter);

        // link message text to view-model data
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // link cards to view-model data
        homeViewModel.getCards().observe(getViewLifecycleOwner(), new Observer<ArrayList<Article>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Article> updatedList) {
                // Reset adapter
                mAdapter = new ArticleAdapter(getActivity(), updatedList, listener);
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
                MainActivity main = (MainActivity) getActivity();

                // create article redirect intent
                Intent article_activity = new Intent(main, ArticleActivity.class);

                // Create bundle with serialised object
                Bundle bundle = new Bundle();
                article.saveInstanceState(bundle);
                main.getUser().saveInstanceState(bundle);

                // Add title & bundle to intent
                article_activity.putExtra("activity", "main");
                article_activity.putExtras(bundle);
                article_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Start article activity and close main activity
                main.startActivity(article_activity);
                main.finish();
            }
        }
    }

    /*
     * Fetch articles
     * */
    public void update(){
        if(getActivity().getClass() == MainActivity.class) {
            // Request articles
            MainActivity main = (MainActivity) getActivity();

            // Add observer to articles
            homeViewModel.check_articles(main, main.getUser());
        }else if(getActivity().getClass() == ArticleActivity.class){
            // Request articles
            ArticleActivity articleActivity = (ArticleActivity) getActivity();

            // Add observer to articles
            homeViewModel.check_articles(articleActivity, articleActivity.getUser());
        }
    }
}