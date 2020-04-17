package derenvural.sourceread_prototype.ui.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.cards.App;
import derenvural.sourceread_prototype.data.dialog.helpDialog;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class SettingsFragment extends Fragment {
    // List
    private ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Find list view
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        listView = root.findViewById(R.id.list_view);

        // Add onItemClick listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Current item in list
                TextView item = (TextView) view;
                final SourceReadActivity currentActivity = (SourceReadActivity) getActivity();

                if(currentActivity != null) {
                    if (item.getText().equals(getResources().getString(R.string.delete_articles))) {
                        // Create dialog listeners
                        DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Attempt to delete ALL articles regardless of app
                                // Get user
                                LoggedInUser user = currentActivity.getUser();

                                if (user.getApps() != null && user.getApps().getValue() != null && user.getApps().getValue().size() > 0) {
                                    // Deactivate interface
                                    currentActivity.deactivate_interface();

                                    // Remove all articles
                                    for (App app : user.getApps().getValue()) {
                                        user.deleteAllArticles(currentActivity, app.getTitle());
                                    }

                                    // Set updated user
                                    currentActivity.setUser(user);

                                    // End dialog
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(currentActivity, "no apps to disconnect!", Toast.LENGTH_SHORT).show();
                                    Log.d("TASK", "no apps to disconnect!");
                                }
                            }
                        };
                        DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel
                                dialog.dismiss();
                            }
                        };

                        // Send dialog confirmation
                        helpDialog dialogArticles = new helpDialog(currentActivity,
                                negative, positive,
                                null, R.string.user_cancel,
                                R.string.dialog_delete_all_articles);
                        dialogArticles.show();
                    } else if (item.getText().equals(getResources().getString(R.string.disconnect_apps))) {
                        // Create dialog listeners
                        DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Attempt to disconnect ALL apps
                                // Get user
                                LoggedInUser user = currentActivity.getUser();

                                if (user.getApps() != null && user.getApps().getValue() != null && user.getApps().getValue().size() > 0) {
                                    // Deactivate interface
                                    currentActivity.deactivate_interface();

                                    // Remove all articles
                                    for (App app : user.getApps().getValue()) {
                                        user.disconnectApp(currentActivity, app, R.id.nav_settings);
                                    }

                                    // Set updated user
                                    currentActivity.setUser(user);

                                    // End dialog
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(currentActivity, "no apps to disconnect!", Toast.LENGTH_SHORT).show();
                                    Log.d("TASK", "no apps to disconnect!");
                                }
                            }
                        };
                        DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel
                                dialog.dismiss();
                            }
                        };

                        // Send dialog confirmation
                        helpDialog dialogApps = new helpDialog(currentActivity,
                                negative, positive,
                                null, R.string.user_cancel,
                                R.string.dialog_disconnect_all_apps);
                        dialogApps.show();
                    } else if (item.getText().equals(getResources().getString(R.string.action_logout_user))) {
                        // Create dialog listeners
                        DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Attempt to log out
                                currentActivity.logout();

                                // End dialog
                                dialog.dismiss();
                            }
                        };
                        DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel
                                dialog.dismiss();
                            }
                        };

                        // Send dialog confirmation
                        helpDialog dialogLogout = new helpDialog(currentActivity,
                                negative, positive,
                                null, R.string.user_cancel,
                                R.string.dialog_log_out);
                        dialogLogout.show();
                    } else if (item.getText().equals(getResources().getString(R.string.delete_account))) {
                        // Create dialog listeners
                        DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Attempt firebase user delete
                                currentActivity.delete_account(currentActivity.getAuth().getCurrentUser());

                                // End dialog
                                dialog.dismiss();
                            }
                        };
                        DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel
                                dialog.dismiss();
                            }
                        };

                        // Send dialog confirmation
                        helpDialog dialogAccount = new helpDialog(currentActivity,
                                negative, positive,
                                null, R.string.user_cancel,
                                R.string.dialog_delete_account);
                        dialogAccount.show();
                    }
                }
            }
        });

        return root;
    }
}