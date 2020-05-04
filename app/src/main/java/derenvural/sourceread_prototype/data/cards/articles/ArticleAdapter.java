package derenvural.sourceread_prototype.data.cards.articles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.cards.CardAdapter;
import derenvural.sourceread_prototype.data.cards.apps.App;

public class ArticleAdapter extends CardAdapter<Article> {
    private filterType filter;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ArticleViewHolder extends CardViewHolder {
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

    // Call super constructor then sort articles
    public ArticleAdapter(@NonNull final SourceReadActivity context,
                          @NonNull ArrayList<Article> newCards,
                          @NonNull View.OnClickListener listener,
                          @Nullable filterType new_filter) {
        super(context, newCards, listener);
        this.filter = new_filter;

        // Sort new cards
        Collections.sort(newCards, new Comparator<Article>() {
            @Override
            public int compare(Article lhs, Article rhs) {
                if(filter == null){
                    return 0;
                }else if(filter.equals(filterType.ALPHABET_AZ)) {
                    Collator myCollator = Collator.getInstance();
                    return myCollator.compare(lhs.getTitle(), rhs.getTitle());
                }else if(filter.equals(filterType.ALPHABET_ZA)) {
                    Collator myCollator = Collator.getInstance();
                    return -1 * myCollator.compare(lhs.getTitle(), rhs.getTitle());
                }else if(filter.equals(filterType.IMPORT_DT_LATEST)) {
                    // Add date of import check
                    long lhs_stamp = 0L;
                    long rhs_stamp = 0L;

                    // Get list of apps
                    ArrayList<App> apps = context.getUser().getApps().getValue();

                    if(apps != null && apps.size() > 0) {
                        for (App app : apps) {
                            if (app.getTimestamp() != null) {
                                if(app.getTitle().equals(lhs.getApp())) {
                                    lhs_stamp = app.getTimestamp();
                                }
                                if(app.getTitle().equals(rhs.getApp())) {
                                    rhs_stamp = app.getTimestamp();
                                }
                            }
                        }
                    }

                    if(lhs_stamp > rhs_stamp){
                        return 1;
                    }else if(lhs_stamp < rhs_stamp){
                        return -1;
                    }else{
                        return 0;
                    }
                }else if(filter.equals(filterType.IMPORT_DT_OLDEST)) {
                    // Add date of import check
                    long lhs_stamp = 0L;
                    long rhs_stamp = 0L;

                    // Get list of apps
                    ArrayList<App> apps = context.getUser().getApps().getValue();

                    if(apps != null && apps.size() > 0) {
                        for (App app : apps) {
                            if (app.getTimestamp() != null) {
                                if(app.getTitle().equals(lhs.getApp())) {
                                    lhs_stamp = app.getTimestamp();
                                }
                                if(app.getTitle().equals(rhs.getApp())) {
                                    rhs_stamp = app.getTimestamp();
                                }
                            }
                        }
                    }

                    if(lhs_stamp > rhs_stamp){
                        return -1;
                    }else if(lhs_stamp < rhs_stamp){
                        return 1;
                    }else{
                        return 0;
                    }
                }else if(filter.equals(filterType.VERACITY_HIGHEST)) {
                    // Check for unrated article
                    if(lhs.getAif() == null || lhs.getAif().equals("")){
                        if(rhs.getAif() == null || rhs.getAif().equals("")){
                            return 0;
                        }else{
                            return -1;
                        }
                    }else if(rhs.getAif() == null || rhs.getAif().equals("")){
                        return 1;
                    }else{
                        Collator myCollator = Collator.getInstance();
                        return myCollator.compare(Integer.parseInt(lhs.getAif()), Integer.parseInt(rhs.getAif()));
                    }
                }else if(filter.equals(filterType.VERACITY_LOWEST)) {
                    // Check for unrated article
                    if(lhs.getAif() == null || lhs.getAif().equals("")){
                        if(rhs.getAif() == null || rhs.getAif().equals("")){
                            return 0;
                        }else{
                            return 1;
                        }
                    }else if(rhs.getAif() == null || rhs.getAif().equals("")){
                        return -1;
                    }else{
                        Collator myCollator = Collator.getInstance();
                        return -1 * myCollator.compare(Integer.parseInt(lhs.getAif()), Integer.parseInt(rhs.getAif()));
                    }
                }else{
                    return 0;
                }
            }
        });

        // Replace with sorted list
        this.mDataHolders = newCards;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ArticleAdapter.ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.card, parent, false);
        return new ArticleViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        // Load article icon
        holder.vImage.setImageResource(R.drawable.ic_card_placeholder1);

        // Load article card data
        holder.vTitle.setText(mDataHolders.get(position).getTitle());
        holder.vText.setText(mDataHolders.get(position).getExcerpt());
    }

}
