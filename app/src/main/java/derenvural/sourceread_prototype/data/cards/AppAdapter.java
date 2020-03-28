package derenvural.sourceread_prototype.data.cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import derenvural.sourceread_prototype.R;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {
    private static ArrayList<App> mDataHolders;
    private static Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class AppViewHolder extends RecyclerView.ViewHolder {
        // data items
        public ImageView vImage;
        public TextView vTitle;
        public TextView vText;
        public AppViewHolder(View v) {
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

                    // Start app activity with app object
                    //startAppActivity(current.getText().toString());
                }
            });
        }
        /*
        private void startAppActivity(String title){
            // Get app for passing
            for(App app: mDataHolders) {
                if(app.getTitle().equals(title)){
                    // create app redirect intent
                    Intent app_activity = new Intent(context, AppActivity.class);

                    // Fetch user data
                    MainActivity main = (MainActivity) context;

                    // Create bundle with serialised object
                    Bundle bundle = new Bundle();
                    app.saveInstanceState(bundle);
                    main.getUser().saveInstanceState(bundle);

                    // Add title & bundle to intent
                    app_activity.putExtra("activity", "main");
                    app_activity.putExtras(bundle);
                    app_activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Start app activity and close main activity
                    main.startActivity(app_activity);
                    main.finish();
                }
            }
        }*/
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AppAdapter(Context context, ArrayList<App> newCards) {
        this.context = context;

        this.mDataHolders = newCards;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AppAdapter.AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
        return new AppViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
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
