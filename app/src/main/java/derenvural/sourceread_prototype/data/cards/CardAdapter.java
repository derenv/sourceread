package derenvural.sourceread_prototype.data.cards;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.util.ArrayList;

import derenvural.sourceread_prototype.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private ArrayList<Card> mDataHolders;
    private Context context;

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
            // Create view references
            vImage = v.findViewById(R.id.card_image);
            vTitle = v.findViewById(R.id.card_title);
            vText = v.findViewById(R.id.card_text);

            // Add click listener for fragment transition
            v.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View vx) {
                // CardView item clicked
                TextView current = vx.findViewById(vTitle.getId());
                Toast.makeText(vx.getContext(), "Item in list: "+current.getText(), Toast.LENGTH_SHORT).show();

                // TODO: Transition to fragment for app OR article
                //
                }
            });
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
        if(mDataHolders.get(position).getTitle().equals("pocket")) {
            holder.vImage.setImageResource(R.drawable.ic_card_placeholder1);
        }else if(mDataHolders.get(position).getTitle().equals("instapaper")) {
            holder.vImage.setImageResource(R.drawable.ic_card_placeholder2);
        }else if(mDataHolders.get(position).getTitle().equals("evernote")) {
            holder.vImage.setImageResource(R.drawable.ic_card_placeholder1);
        }else if(mDataHolders.get(position).getTitle().equals("wallabag")) {
            holder.vImage.setImageResource(R.drawable.ic_card_placeholder2);
        }else if(mDataHolders.get(position).getTitle().equals("pinboard")) {
            holder.vImage.setImageResource(R.drawable.ic_card_placeholder1);
        }else{
            // Load article icon
            holder.vImage.setImageResource(R.drawable.ic_card_placeholder1);
        }
        holder.vTitle.setText(mDataHolders.get(position).getTitle());
        holder.vText.setText(mDataHolders.get(position).getText());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataHolders.size();
    }

}
