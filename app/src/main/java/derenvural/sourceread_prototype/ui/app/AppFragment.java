package derenvural.sourceread_prototype.ui.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.cards.App;

public class AppFragment extends Fragment {
    private AppViewModel appViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // get view model
        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        View root = inflater.inflate(R.layout.fragment_app, container, false);

        // link name text to view-model data
        final TextView nameView = root.findViewById(R.id.text_app_name);
        appViewModel.getName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                nameView.setText("App: "+s);
            }
        });

        // link description text to view-model data
        final TextView descriptionView = root.findViewById(R.id.text_app_desc);
        appViewModel.getDescription().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                descriptionView.setText(s);
            }
        });

        // link timestamp value to view-model data
        final TextView timestampView = root.findViewById(R.id.text_app_time);
        appViewModel.getTimestamp().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long s) {
                timestampView.setText("Last import: "+s.toString());
            }
        });

        // link image to view-model data
        final ImageView imageView = root.findViewById(R.id.image_app);
        appViewModel.getName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                imageView.setImageResource(R.drawable.ic_card_placeholder2);
            }
        });

        // Add app to view-model
        update();

        return root;
    }

    /*
     * Fetch app from bundle and set data
     * */
    private void update(){
        // Fetch bundle from arguments
        Bundle appBundle = getArguments();
        App app = new App(appBundle);

        // Assign values
        appViewModel.setName(app.getTitle());
        appViewModel.setDescription(app.getText());
        appViewModel.setTimestamp(app.getTimestamp());
        appViewModel.setImage(app.getImage());
    }
}