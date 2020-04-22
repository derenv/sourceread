package derenvural.sourceread_prototype.data.functions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;

public class FunctionAdapter<T> extends RecyclerView.Adapter<FunctionAdapter.FunctionViewHolder> {
    protected ArrayList<T> mDataHolders;
    protected static Context context;
    protected static View.OnClickListener listener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class FunctionViewHolder extends RecyclerView.ViewHolder {
        // data items
        public TextView vTitle;
        public EditText vInput;
        public FunctionViewHolder(View v) {
            super(v);
            // Create view references
            vTitle = v.findViewById(R.id.search_bar_title);
            vInput = v.findViewById(R.id.search_bar_text);

            // Add click listener for fragment transition
            //v.setOnClickListener(listener);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FunctionAdapter(SourceReadActivity context, ArrayList<T> newCards/*, View.OnClickListener listener*/) {
        this.context = context;
        this.mDataHolders = newCards;
        //FunctionAdapter.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FunctionAdapter.FunctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.search_bar, parent, false);
        return new FunctionViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FunctionViewHolder holder, int position) {
        // Load default icon
        //holder.vImage.setImageResource(R.drawable.ic_card_placeholder1);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataHolders.size();
    }

}
