package derenvural.sourceread_prototype.data.functions.graph;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.functions.FunctionAdapter;

public class GraphAdapter extends FunctionAdapter<Graph> {
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class graphViewHolder extends FunctionViewHolder {
        // data items
        public TextView vText;
        public ImageView vImage;

        public graphViewHolder(View v) {
            super(v);
            // Create view references
            vTitle = v.findViewById(R.id.graph_title);
            vText = v.findViewById(R.id.graph_text);
            vImage = v.findViewById(R.id.graph_svg_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GraphAdapter(SourceReadActivity context, ArrayList<Graph> newCards) {
        super(context, newCards);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GraphAdapter.graphViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.graph, parent, false);
        return new graphViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FunctionViewHolder holder, int position) {
        // Load title
        holder.vTitle.setText(mDataHolders.get(position).getTitle());

        // Convert to current view-holder
        graphViewHolder viewHolder = (graphViewHolder) holder;

        // Load graph image
        if(viewHolder.vImage != null && mDataHolders.get(position).getImage() != null) {
            viewHolder.vImage.setImageResource(mDataHolders.get(position).getImage());
        }

        // Load text
        if(viewHolder.vText != null && mDataHolders.get(position).getText() != null) {
            viewHolder.vText.setText(mDataHolders.get(position).getText());
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataHolders.size();
    }

}
