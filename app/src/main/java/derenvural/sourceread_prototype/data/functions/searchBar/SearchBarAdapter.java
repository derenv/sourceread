package derenvural.sourceread_prototype.data.functions.searchBar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.functions.Function;
import derenvural.sourceread_prototype.data.functions.FunctionAdapter;

public class SearchBarAdapter extends FunctionAdapter<Function> {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class searchbarViewHolder extends FunctionViewHolder {
        // data items
        public TextView vTitle;
        public EditText vInput;
        public searchbarViewHolder(View v) {
            super(v);
            // Create view references
            vTitle = v.findViewById(R.id.search_bar_title);
            vInput = v.findViewById(R.id.search_bar_text);

            // Add click listener for fragment transition
            //v.setOnClickListener(listener);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchBarAdapter(SourceReadActivity context, ArrayList<Function> newCards/*, View.OnClickListener listener*/) {
        super(context, newCards);
        //SearchBarAdapter.listener = listener;
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
        // Load default icon
        //holder.vImage.setImageResource(R.drawable.ic_card_placeholder1);
        holder.vTitle.setText(mDataHolders.get(position).getTitle());
        holder.vInput.setText(mDataHolders.get(position).getText());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataHolders.size();
    }

}
