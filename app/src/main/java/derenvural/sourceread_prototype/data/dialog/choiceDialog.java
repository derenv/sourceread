package derenvural.sourceread_prototype.data.dialog;

import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;

public class choiceDialog extends SourceReadDialog {
    // Dialog state
    private CharSequence[] choices;
    // Listeners
    private DialogInterface.OnClickListener listener;

    public choiceDialog(@NonNull SourceReadActivity activity,
                        @NonNull CharSequence[] choices,
                        @Nullable DialogInterface.OnClickListener positive,
                        @Nullable DialogInterface.OnClickListener negative,
                        @Nullable Integer titleID,
                        @Nullable Integer confirmID,
                        @Nullable Integer cancelID,
                        @NonNull DialogInterface.OnClickListener listener){
        // Cancel response
        if(negative == null){
            if(cancelID == null){
                setCancellable(false);
            }else{
                setCancellable(true);
                setCancelID(cancelID);

                // Stub 'cancel dialog' method
                setNegative(getDefault_negative());
            }
        }else{
            setCancelID(cancelID);
            setCancellable(true);
            setNegative(negative);
        }

        // Accept response
        if(positive == null){
            // Stub 'cancel dialog' method
            setPositive(getDefault_negative());
        }else{
            setPositive(positive);
        }

        // Messages
        if(confirmID == null){
            setConfirmID(R.string.user_sure);
        }else{
            setConfirmID(confirmID);
        }
        if(titleID == null){
            setTitleID(R.string.dialog_default_title);
        }else{
            setTitleID(titleID);
        }

        this.choices = choices;
        this.listener = listener;

        // Create Box
        setAlertDialog(createDialog(activity));
    }

    // Dialogue
    private AlertDialog createDialog(@NonNull SourceReadActivity activity) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Set message and listeners
        builder.setTitle(getTitleID())
                .setCancelable(getCancelable())
                .setItems(choices, listener);

        // Check if cancel button needed
        if(getCancelable()){
            builder.setNeutralButton(getCancelID(), getNegative());
        }

        return builder.create();
    }
}
