package derenvural.sourceread_prototype.data.cards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import derenvural.sourceread_prototype.MainActivity;
import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.article.Article;
import derenvural.sourceread_prototype.ui.article.ArticleActivity;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {
    private static ArrayList<Article> mDataHolders;
    private static Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
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
            v.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View vx) {
                    // Find article card clicked
                    TextView current = vx.findViewById(vTitle.getId());

                    // Start article activity with article object
                    startArticleActivity(current.getText().toString());
                }
            });
        }

        private void startArticleActivity(String title){
            // Get article for passing
            for(Article article: mDataHolders) {
                if(article.getResolved_title().equals(title)){
                    // create article redirect intent
                    Intent article_activity = new Intent(context, ArticleActivity.class);

                    // Fetch user data
                    MainActivity main = (MainActivity) context;

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
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ArticleAdapter(Context context, ArrayList<Article> newCards) {
        this.context = context;

        this.mDataHolders = newCards;
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
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        // Load article icon
        holder.vImage.setImageResource(R.drawable.ic_card_placeholder1);
        holder.vTitle.setText(mDataHolders.get(position).getResolved_title());
        holder.vText.setText(mDataHolders.get(position).getResolved_url());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataHolders.size();
    }

}
