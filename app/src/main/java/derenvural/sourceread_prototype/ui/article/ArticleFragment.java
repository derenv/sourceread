package derenvural.sourceread_prototype.ui.article;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.data.cards.articles.Article;
import derenvural.sourceread_prototype.data.dialog.choiceDialog;
import derenvural.sourceread_prototype.data.dialog.helpDialog;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class ArticleFragment extends Fragment {
    private Button removeButton;
    private Button openButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Get view model & root element of fragment
        View root = inflater.inflate(R.layout.fragment_article, container, false);
        final ArticleViewModel articleViewModel = ViewModelProviders.of(getActivity()).get(ArticleViewModel.class);

        // Find buttons
        removeButton = root.findViewById(R.id.button_remove);
        removeButton.setText(R.string.button_delete);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArticleActivity aa = (ArticleActivity) getActivity();
                // Create dialog listeners
                DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Get user data
                        Article article = articleViewModel.getArticle().getValue();
                        //LoggedInUser user = articleViewModel.getUser().getValue();

                        // Attempt to delete article from database
                        aa.deactivate_interface();
                        Toast.makeText(aa, "Deleting '"+article.getTitle()+"'..", Toast.LENGTH_SHORT).show();

                        aa.delete_article();

                        //ArrayList<Article> articles = new ArrayList<Article>();
                        //articles.add(article);
                        //user.deleteArticle(aa, articles);

                        // End dialog
                        dialog.dismiss();
                    }
                };
                DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel
                        dialog.dismiss();
                    }
                };

                // Send dialog confirmation
                helpDialog dialogAccount = new helpDialog(aa,
                        negative, positive,
                        R.string.dialog_default_title,
                        null, R.string.user_cancel,
                        R.string.dialog_delete_article);
                dialogAccount.show();
            }
        });
        openButton = root.findViewById(R.id.button_open);
        openButton.setText(R.string.button_open);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArticleActivity aa = (ArticleActivity) getActivity();
                // Create dialog listeners
                DialogInterface.OnClickListener item_choice = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Find clicked option, specify sort type
                        switch(id){
                            case 0:
                                // Get URL of article
                                String url = articleViewModel.getArticle().getValue().getResolved_url();

                                // Redirect to browser for article URL
                                aa.getHttpHandler().browser_open(aa, url);

                                break;
                            case 1:
                                Toast.makeText(aa, "Feature not yet implemented..", Toast.LENGTH_SHORT).show();

                                break;
                        }
                    }
                };
                DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel
                        dialog.dismiss();
                    }
                };

                // Build dialog
                choiceDialog sortDialog = new choiceDialog(aa,
                        getResources().getStringArray(R.array.open_list),
                        null,
                        negative,
                        R.string.dialog_open_title,
                        null,
                        R.string.user_cancel,
                        item_choice);
                sortDialog.show();
            }
        });

        // link author text to view-model data
        final TextView titleView = root.findViewById(R.id.text_article_title);
        final TextView urlView = root.findViewById(R.id.text_article_url);
        final TextView authorView = root.findViewById(R.id.text_article_authors);
        final TextView wordcountView = root.findViewById(R.id.text_article_word_count);
        final TextView veracityView = root.findViewById(R.id.text_article_veracity);
        articleViewModel.getArticle().observe(getViewLifecycleOwner(), new Observer<Article>() {
            @Override
            public void onChanged(@Nullable Article s) {
                titleView.setText("Full Title - "+s.getTitle());
                urlView.setText("URL - "+s.getResolved_url());

                if(s.getAuthors() != null) {
                    String q = "Authors:\n'" + s.getAuthors().get(0).get("name") + "'";
                    for (HashMap<String, String> t : s.getAuthors().subList(1, s.getAuthors().size())) {
                        q = q + "\n'" + t.get("name") + "'";
                    }
                    authorView.setText(q);
                }

                // FIXME: if wordcount invalid
                wordcountView.setText("Word Count - "+s.getWord_count());

                // Analysis
                veracityView.setText("Veracity - "+s.getVeracity());
            }
        });

        return root;
    }
}
