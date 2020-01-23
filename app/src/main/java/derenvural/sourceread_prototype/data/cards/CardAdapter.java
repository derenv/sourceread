package derenvural.sourceread_prototype.data.cards;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import java.util.ArrayList;

import derenvural.sourceread_prototype.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private ArrayList<Card> mDataHolders;

    Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        // data items
        public ImageView vImage;
        public TextView vTitle;
        public TextView vText;
        public CardViewHolder(View v) {
            super(v);
            vImage = v.findViewById(R.id.card_image);
            vTitle = v.findViewById(R.id.card_title);
            vText = v.findViewById(R.id.card_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardAdapter(Context context, ArrayList<Card> newCards) {
        this.context = context;

        this.mDataHolders = newCards;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
        return new CardViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.vImage.setImageResource(mDataHolders.get(position).getImage());
        holder.vTitle.setText(mDataHolders.get(position).getTitle());
        holder.vText.setText(mDataHolders.get(position).getText());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataHolders.size();
    }

}
