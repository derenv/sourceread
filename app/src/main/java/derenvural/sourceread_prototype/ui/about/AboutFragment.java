package derenvural.sourceread_prototype.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import derenvural.sourceread_prototype.R;

public class AboutFragment extends Fragment {
    private AboutViewModel aboutViewModel;
    // List
    private ListView listView;
    // Buttons
    private Button backButton;
    // Views
    private ScrollView scrolledText;
    private TextView text;
    // Layout
    private LinearLayout ll;
    private LinearLayout.LayoutParams lp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initiate viewmodel
        aboutViewModel = ViewModelProviders.of(this).get(AboutViewModel.class);

        // Find list view
        View root = inflater.inflate(R.layout.fragment_about, container, false);
        listView = root.findViewById(R.id.list_view);

        // Find Layout and set new View params
        ll = root.findViewById(R.id.about_layout);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Add onItemClick listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Current item in list
                TextView item = (TextView) view;

                // Create new Views
                text = new TextView(getActivity());
                text.setVerticalScrollBarEnabled(true);
                backButton = new Button(getActivity());
                backButton.setText(getString(R.string.button_back));
                scrolledText = new ScrollView(getActivity());

                // Add back listener
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Reveal list
                        listView.setVisibility(View.VISIBLE);

                        // Remove button & text
                        scrolledText.setVisibility(View.GONE);
                        backButton.setVisibility(View.GONE);
                    }
                });

                if(item.getText().equals(getResources().getString(R.string.contact_us))){
                    // Open Contact Us
                    // Set text
                    aboutViewModel.setText(HtmlCompat.fromHtml(getString(R.string.contact_html), HtmlCompat.FROM_HTML_MODE_LEGACY));
                    text.setText(aboutViewModel.getText().getValue());
                    scrolledText.addView(text);

                    // Add items
                    ll.addView(backButton, lp);
                    ll.addView(scrolledText, lp);

                    // Hide list
                    listView.setVisibility(View.GONE);
                }else if(item.getText().equals(getResources().getString(R.string.privacy_policy))){
                    // Open Privacy Policy
                    // Set text
                    aboutViewModel.setText(HtmlCompat.fromHtml(getString(R.string.policy_html), HtmlCompat.FROM_HTML_MODE_LEGACY));
                    text.setText(aboutViewModel.getText().getValue());
                    scrolledText.addView(text);

                    // Add items
                    ll.addView(backButton, lp);
                    ll.addView(scrolledText, lp);

                    // Hide list
                    listView.setVisibility(View.GONE);
                }else if(item.getText().equals(getResources().getString(R.string.tos))){
                    // Open Terms of Service
                    // Set text
                    aboutViewModel.setText(HtmlCompat.fromHtml(getString(R.string.tos_html), HtmlCompat.FROM_HTML_MODE_LEGACY));
                    text.setText(aboutViewModel.getText().getValue());
                    scrolledText.addView(text);

                    // Add items
                    ll.addView(backButton, lp);
                    ll.addView(scrolledText, lp);

                    // Hide list
                    listView.setVisibility(View.GONE);
                }
            }
        });

        return root;
    }
}