package derenvural.sourceread_prototype.ui;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class loading {
    //https://stackoverflow.com/questions/38160968/create-and-show-progressbar-programmatically
    //TODO: use for app loading
    public static void setProgressBar(Activity activity) {
        // Create progress bar
        ProgressBar progressBar = new ProgressBar(activity,null,android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        // Add to layout
        RelativeLayout layout = new RelativeLayout(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar,params);

        // Add layout to content
        activity.setContentView(layout);
    }
}
