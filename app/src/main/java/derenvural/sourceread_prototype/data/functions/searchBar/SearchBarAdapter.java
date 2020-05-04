package derenvural.sourceread_prototype.data.functions.searchBar;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.functions.FunctionAdapter;

public class SearchBarAdapter extends FunctionAdapter<SearchBar> {
    private static TextWatcher watcher;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class searchbarViewHolder extends FunctionViewHolder {
        // data items
        public EditText vInput;

        public searchbarViewHolder(View v) {
            super(v);
            // Create view references
            vTitle = v.findViewById(R.id.search_bar_title);
            vInput = v.findViewById(R.id.search_bar_text);

            // Add click listener for fragment transition
            vInput.addTextChangedListener(watcher);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchBarAdapter(SourceReadActivity context, ArrayList<SearchBar> newCards, TextWatcher watcher) {
        super(context, newCards);
        SearchBarAdapter.watcher = watcher;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SearchBarAdapter.searchbarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.search_bar, parent, false);
        return new searchbarViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FunctionViewHolder holder, int position) {
        // Load title
        holder.vTitle.setText(mDataHolders.get(position).getTitle());

        // Convert to current view-holder
        searchbarViewHolder viewHolder = (searchbarViewHolder) holder;

        // Load edittext prompt
        viewHolder.vInput.setText(mDataHolders.get(position).getText());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataHolders.size();
    }

}
