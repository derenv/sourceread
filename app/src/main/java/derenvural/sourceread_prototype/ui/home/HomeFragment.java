package derenvural.sourceread_prototype.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import derenvural.sourceread_prototype.data.cards.articles.Article;
import derenvural.sourceread_prototype.data.cards.articles.ArticleAdapter;
import derenvural.sourceread_prototype.data.cards.articles.filterType;
import derenvural.sourceread_prototype.data.functions.Function;
import derenvural.sourceread_prototype.data.functions.searchBar.SearchBar;
import derenvural.sourceread_prototype.data.functions.searchBar.SearchBarAdapter;
import derenvural.sourceread_prototype.ui.article.ArticleActivity;

public class HomeFragment extends Fragment {
    // View-Model
    private HomeViewModel homeViewModel;
    // Article list
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private filterType filter;
    // Search bar
    private RecyclerView searchCardView;
    private RecyclerView.Adapter mSearchAdapter;
    // Fragment text
    private TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Create activity reference for observers and recycler views
        MainActivity main = (MainActivity) getActivity();

        // Activate recycler views
        activate_search_bar(root, main);
        activate_article_list(root, main);

        // Link message text to view-model data
        textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // Request articles
        update();

        return root;
    }

    private void activate_search_bar(View root, final MainActivity main){
        // Find search bar
        searchCardView = root.findViewById(R.id.search_bar_view);

        // Use a linear layout manager
        RecyclerView.LayoutManager searchLayoutManager = new LinearLayoutManager(getActivity());
        searchCardView.setLayoutManager(searchLayoutManager);

        // Create search bar function for adapter
        SearchBar searchBar = new SearchBar(getString(R.string.search_bar_title), getString(R.string.prompt_search));
        ArrayList<Function> functions = new ArrayList<Function>();
        functions.add(searchBar);

        // Create search text listener
        // Check for change in input text
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s == null || s.toString().equals("") || s.toString().equals(getString(R.string.prompt_search))){
                    // Clear search results
                    homeViewModel.setSearchResults(null);
                }else {
                    // Create search list
                    ArrayList<Article> whitelist = new ArrayList<Article>();
                    for (Article article : homeViewModel.getCards().getValue()) {
                        // Check if title contains search string
                        if (article.getTitle().toLowerCase().contains(s.toString().toLowerCase())) {
                            whitelist.add(article);
                        }
                    }

                    // TODO: Sort by relevance?
                    //

                    homeViewModel.setSearchResults(whitelist);
                }
            }
        };

        // Specify an adapter
        mSearchAdapter = new SearchBarAdapter(main, functions, afterTextChangedListener);
        searchCardView.setAdapter(mSearchAdapter);
    }

    private void activate_article_list(View root, final MainActivity main) {
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

        // Link cards to view-model data
        homeViewModel.getSearchResults().observe(getViewLifecycleOwner(), new Observer<ArrayList<Article>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Article> searchList) {
                if(searchList == null) {
                    // Get normal cards
                    ArrayList<Article> updatedList = homeViewModel.getCards().getValue();

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
                }else if(searchList.size() != 0) {
                    // Reset adapter
                    mAdapter = new ArticleAdapter(main, searchList, listener, null);
                    recyclerView.setAdapter(mAdapter);
                }else{
                    // Reset adapter
                    mAdapter = new ArticleAdapter(main, searchList, listener, null);
                    recyclerView.setAdapter(mAdapter);

                    // If list still empty, display appropriate text and hide loading bar
                    if (mAdapter.getItemCount() == 0) {
                        homeViewModel.setText(getString(R.string.search_placeholder));
                    } else {
                        // Set text blank
                        homeViewModel.setText("");
                    }
                }
            }
        });
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