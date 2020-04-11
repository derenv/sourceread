package derenvural.sourceread_prototype.ui.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import derenvural.sourceread_prototype.MainActivity;
import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.cards.Article;
import derenvural.sourceread_prototype.data.login.LoggedInUser;
import derenvural.sourceread_prototype.ui.apps.redirectType;

public class AppFragment extends Fragment {
    private AppViewModel appViewModel;
    // Buttons
    private Button connectButton;
    private Button importButton;
    private Button deleteButton;
    // Views
    private TextView timestampView;
    private TextView articlesnoView;
    // Context
    private SourceReadActivity currentActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Get view model & root element of fragment
        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        View root = inflater.inflate(R.layout.fragment_app, container, false);
        currentActivity = (MainActivity) getActivity();

        // Find buttons
        connectButton = root.findViewById(R.id.button_connect);
        importButton = root.findViewById(R.id.button_import);
        deleteButton = root.findViewById(R.id.button_delete);

        // link name text to view-model data
        final TextView nameView = root.findViewById(R.id.text_app_name);
        final TextView descriptionView = root.findViewById(R.id.text_app_desc);
        timestampView = root.findViewById(R.id.text_app_time);
        articlesnoView = root.findViewById(R.id.text_app_articles);
        final ImageView imageView = root.findViewById(R.id.image_app);
        appViewModel.getApp().observe(getViewLifecycleOwner(), new Observer<App>() {
            @Override
            public void onChanged(@Nullable App s) {
                // Calculcate new stamp
                if(appViewModel.getApp().getValue() == null || appViewModel.getApp().getValue().getTimestamp().equals(0L)) {
                    appViewModel.setStamp(null);
                }else{
                    Instant stamp = Instant.ofEpochSecond(appViewModel.getApp().getValue().getTimestamp());
                    appViewModel.setStamp(LocalDateTime.ofInstant(stamp, ZoneId.systemDefault()));
                }

                // Set text views
                nameView.setText("App: "+s.getTitle());
                descriptionView.setText(s.getText());

                // STUB
                imageView.setImageResource(R.drawable.ic_card_placeholder2);
            }
        });
        appViewModel.getArticlesNo().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer s) {
                if(s == null || s == 0){
                    String articleNoText = "Imported Articles: \nNone!";
                    articlesnoView.setText(articleNoText);
                }else {
                    String articleNoText = "Imported Articles: \n"+s.toString();
                    articlesnoView.setText(articleNoText);
                }
            }
        });
        appViewModel.getStamp().observe(getViewLifecycleOwner(), new Observer<LocalDateTime>() {
            @Override
            public void onChanged(@Nullable LocalDateTime s) {
                if(s == null || s.isEqual(LocalDateTime.ofInstant(Instant.ofEpochSecond(0L), ZoneId.systemDefault()))){
                    String stampText = "Last import: \nNever!";
                    timestampView.setText(stampText);
                }else {
                    String stampText = "Last import: \n" +
                            s.getYear() + "\t" + s.getMonth() + "\t" + s.getDayOfMonth() + "\n" +
                            s.getHour() + ":" + s.getMinute() + ":" + s.getSecond();
                    timestampView.setText(stampText);
                }
            }
        });

        // Create callback
        MainActivity main = (MainActivity) currentActivity;
        main.setAppCallback(false);
        currentActivity = main;

        // Add app to view-model
        update();

        return root;
    }

    /*
     * Fetch app from bundle and set data, then fetch view type from bundle
     * */
    private void update(){
        // Fetch app from bundle
        Bundle appBundle = getArguments();
        if(appBundle != null) {
            final App app = new App(appBundle);
            appViewModel.setApp(app);

            // Link to current state of app
            final LoggedInUser user = currentActivity.getUser();
            user.getApps().observe(currentActivity, new Observer<ArrayList<App>>() {
                @Override
                public void onChanged(ArrayList<App> apps) {
                    if(apps != null) {
                        for (App this_app : apps) {
                            if (this_app.getTitle().equals(app.getTitle())) {
                                // Assign values
                                appViewModel.setApp(this_app);
                                break;
                            }
                        }
                    }
                }
            });

            // Set callback on back button click
            final MainActivity main = (MainActivity) currentActivity;

            // Get type (view or add)
            redirectType type = (redirectType) appBundle.getSerializable("type");
            if (type == redirectType.VIEW) {
                main.setAppCallback(true);

                // Cleanup timestamp format for display
                if (appViewModel.getApp().getValue() == null || appViewModel.getApp().getValue().getTimestamp().equals(0l)) {
                    appViewModel.setStamp(null);
                } else {
                    Instant stamp = Instant.ofEpochSecond(appViewModel.getApp().getValue().getTimestamp());
                    appViewModel.setStamp(LocalDateTime.ofInstant(stamp, ZoneId.systemDefault()));
                }

                // Count # of articles in app
                if (user.getArticles().getValue() == null || user.getArticles().getValue().size() == 0) {
                    appViewModel.setArticlesNo(0);
                } else {
                    ArrayList<Article> toDisplay = new ArrayList<Article>();
                    for(Article countArticle : user.getArticles().getValue()) {
                        if(countArticle.getApp().equals(app.getTitle())){
                            toDisplay.add(countArticle);
                        }
                    }
                    appViewModel.setArticlesNo(toDisplay.size());
                }
                user.getArticles().observe(getViewLifecycleOwner(), new Observer<ArrayList<Article>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<Article> s) {
                        if (s == null || s.size() == 0) {
                            appViewModel.setArticlesNo(0);
                        } else {
                            ArrayList<Article> toDisplay = new ArrayList<Article>();
                            for(Article countArticle : s) {
                                if(countArticle.getApp().equals(app.getTitle())){
                                    toDisplay.add(countArticle);
                                }
                            }
                            appViewModel.setArticlesNo(toDisplay.size());
                        }
                    }
                });

                activateButtons(user, app);
            } else if (type == redirectType.ADD) {
                main.setAppCallback(false);

                // disable timestamp & number of articles fields
                timestampView.setVisibility(View.INVISIBLE);
                articlesnoView.setVisibility(View.INVISIBLE);

                // show 'connect' button
                connectButton.setText(R.string.action_connect_app);
                connectButton.setVisibility(View.VISIBLE);

                // add 'connect' onclick event
                connectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Attempt to login to pocket
                        Toast.makeText(currentActivity, "Attempting to connect app..", Toast.LENGTH_SHORT).show();

                        // Change buttons
                        activateButtons(user, app);

                        //remove app from user
                        user.connectApp(main, main.getDatabase(), main.getHttpHandler(), app);
                        main.setUser(user);

                        currentActivity = main;
                    }
                });
            }
        }
    }

    private void activateButtons(@NonNull final LoggedInUser user, @NonNull final App app){
        // Show 'import', 'delete' & 'disconnect' buttons
        importButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        connectButton.setText(R.string.action_disconnect_app);
        connectButton.setVisibility(View.VISIBLE);

        // Show timestamp & number of articles fields
        timestampView.setVisibility(View.VISIBLE);
        articlesnoView.setVisibility(View.VISIBLE);

        // Add 'import' onclick event
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Attempt to import all articles from app
                Toast.makeText(currentActivity, "Importing all articles from "+appViewModel.getApp().getValue().getTitle()+"..", Toast.LENGTH_SHORT).show();
                currentActivity.deactivate_interface();
                user.importArticles(currentActivity, currentActivity.getHttpHandler(), currentActivity.getDatabase(), appViewModel.getApp().getValue());
                currentActivity.setUser(user);
            }
        });
        // Add 'delete' onclick event
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Attempt to delete all articles imported from app
                Toast.makeText(currentActivity, "Deleting all articles imported from "+appViewModel.getApp().getValue().getTitle()+"..", Toast.LENGTH_SHORT).show();
                currentActivity.deactivate_interface();
                user.deleteAllArticles(currentActivity, currentActivity.getDatabase(), appViewModel.getApp().getValue().getTitle());
                currentActivity.setUser(user);
            }
        });
        // Add 'disconnect' onclick event
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Attempt to remove app
                Toast.makeText(currentActivity, "Disconnecting "+appViewModel.getApp().getValue().getTitle()+"..", Toast.LENGTH_SHORT).show();

                MainActivity main = (MainActivity) currentActivity;

                // Set callback on back button click
                main.setAppCallback(false);

                // Set help dialog text
                main.setHelp(R.string.help_apps);

                // Disconnect app
                user.disconnectApp(main, main.getDatabase(), app, R.id.nav_apps);
                main.setUser(user);

                currentActivity = main;
            }
        });
    }
}