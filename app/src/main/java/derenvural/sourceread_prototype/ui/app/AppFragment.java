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

import java.util.ArrayList;

import derenvural.sourceread_prototype.MainActivity;
import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.login.LoggedInUser;
import derenvural.sourceread_prototype.ui.apps.redirectType;

public class AppFragment extends Fragment {
    private AppViewModel appViewModel;
    private Button connectButton;
    private Button importButton;
    private Button deleteButton;
    private TextView timestampView;
    private MainActivity main;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Get view model & root element of fragment
        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        View root = inflater.inflate(R.layout.fragment_app, container, false);

        main = (MainActivity) getActivity();

        // Find buttons
        connectButton = root.findViewById(R.id.button_connect);
        importButton = root.findViewById(R.id.button_import);
        deleteButton = root.findViewById(R.id.button_delete);

        // link name text to view-model data
        final TextView nameView = root.findViewById(R.id.text_app_name);
        final TextView descriptionView = root.findViewById(R.id.text_app_desc);
        timestampView = root.findViewById(R.id.text_app_time);
        final ImageView imageView = root.findViewById(R.id.image_app);
        appViewModel.getApp().observe(getViewLifecycleOwner(), new Observer<App>() {
            @Override
            public void onChanged(@Nullable App s) {
                nameView.setText("App: "+s.getTitle());
                descriptionView.setText(s.getText());
                timestampView.setText("Last import: "+s.getTimestamp().toString());

                // STUB
                imageView.setImageResource(R.drawable.ic_card_placeholder2);
            }
        });

        // Create callback
        main.setAppCallback(false);

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
        final App app = new App(appBundle);
        appViewModel.setApp(app);

        // Link to current state of app
        final LoggedInUser user = main.getUser();
        user.getApps().observe(main, new Observer<ArrayList<App>>() {
            @Override
            public void onChanged(ArrayList<App> apps) {
                for (App this_app : apps) {
                    if (this_app.getTitle().equals(app.getTitle())) {
                        // Assign values
                        appViewModel.setApp(this_app);
                        break;
                    }
                }
            }
        });

        // Get type (view or add)
        redirectType type = (redirectType) appBundle.getSerializable("type");
        if(type == redirectType.VIEW) {
            // TODO: cleanup timestamp format for display
            //

            activateButtons(main, user, app);
        }else if(type == redirectType.ADD) {
            // disable timestamp field
            timestampView.setVisibility(View.INVISIBLE);

            // show 'connect' button
            connectButton.setText("Connect");
            connectButton.setVisibility(View.VISIBLE);

            // add 'connect' onclick event
            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Attempt to login to pocket
                    Toast.makeText(getActivity(), "Attempting to connect app..", Toast.LENGTH_SHORT).show();

                    // Set callback on back button click
                    main.setAppCallback(true);

                    // Change buttons
                    activateButtons(main, user, app);

                    //remove app from user
                    user.connectApp(main, main.getDatabase(), main.getHttpHandler(), app);
                    main.setUser(user);
                }
            });
        }
    }

    public void activateButtons(final MainActivity main, final LoggedInUser user, final App app){
        // Show 'import', 'delete' & 'disconnect' buttons
        importButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        connectButton.setText("Disconnect");
        connectButton.setVisibility(View.VISIBLE);

        // Show timestamp
        timestampView.setVisibility(View.VISIBLE);

        // Add 'import' onclick event
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch main activity for tool objects
                MainActivity main = (MainActivity) getActivity();

                // Attempt to import all articles from app
                Toast.makeText(main, "Importing all articles from "+appViewModel.getApp().getValue().getTitle()+"..", Toast.LENGTH_SHORT).show();
                main.deactivate_interface();
                user.importArticles(main, main.getHttpHandler(), main.getDatabase(), appViewModel.getApp().getValue());
                main.setUser(user);
            }
        });
        // Add 'delete' onclick event
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch main activity for tool objects
                MainActivity main = (MainActivity) getActivity();

                // Attempt to delete all articles imported from app
                Toast.makeText(getActivity(), "Deleting all articles imported from "+appViewModel.getApp().getValue().getTitle()+"..", Toast.LENGTH_SHORT).show();
                main.deactivate_interface();
                user.deleteAllArticles(main, main.getDatabase(), appViewModel.getApp().getValue().getTitle());
                main.setUser(user);
            }
        });
        // Add 'disconnect' onclick event
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Attempt to remove app
                Toast.makeText(getActivity(), "Disconnecting "+appViewModel.getApp().getValue().getTitle()+"..", Toast.LENGTH_SHORT).show();

                // Set callback on back button click
                main.setAppCallback(false);

                // Disconnect app
                user.disconnectApp(main, main.getDatabase(), app);
                main.setUser(user);
            }
        });
    }
}