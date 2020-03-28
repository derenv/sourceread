package derenvural.sourceread_prototype.data.cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import derenvural.sourceread_prototype.R;

public class ArticleAdapter extends CardAdapter<Article> {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ArticleViewHolder extends CardViewHolder {
        // data items
        public ImageView vImage;
        public TextView vTitle;
        public TextView vText;
        public ArticleViewHolder(View v) {
            super(v);
            // Create view references
            vImage = v.findViewById(R.id.card_image);
            vTitle = v.findViewById(R.id.card_title);
            vText = v.findViewById(R.id.card_text);

            // Add click listener for fragment transition
            v.setOnClickListener(listener);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ArticleAdapter(Context context, ArrayList<Article> newCards, View.OnClickListener listener) {
        super(context, newCards, listener);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ArticleAdapter.ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
        return new ArticleViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        // Load article icon
        holder.vImage.setImageResource(R.drawable.ic_card_placeholder1);

        // Load article card data
        holder.vTitle.setText(mDataHolders.get(position).getTitle());
        holder.vText.setText(mDataHolders.get(position).getResolved_url());
    }

}
