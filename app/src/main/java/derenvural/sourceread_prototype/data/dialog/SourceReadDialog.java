package derenvural.sourceread_prototype.data.dialog;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;

public class SourceReadDialog {
    private AlertDialog alertDialog;
    private DialogInterface.OnClickListener negative;
    private DialogInterface.OnClickListener positive;
    private Integer messageID;
    private Integer confirmID;
    private Integer cancelID;
    private boolean cancellable;

    public SourceReadDialog(@NonNull SourceReadActivity activity,
                            @Nullable DialogInterface.OnClickListener negative,
                            @Nullable DialogInterface.OnClickListener positive,
                            @Nullable Integer confirmID,
                            @Nullable Integer cancelID,
                            @Nullable Integer messageID){
        // Cancel response
        if(negative == null){
            if(cancelID == null){
                cancellable = false;
            }else{
                cancellable = true;
                this.cancelID = cancelID;

                this.negative = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel
                        dialog.dismiss();
                    }
                };
            }
        }else{
            this.negative = negative;
        }

        // Accept response
        if(positive == null){
            this.positive = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // cancel
                    dialog.dismiss();
                }
            };
        }else{
            this.positive = positive;
        }

        // Messages
        if(confirmID == null){
            this.confirmID = R.string.user_sure;
        }else{
            this.confirmID = confirmID;
        }
        if(messageID == null){
            this.messageID = R.string.dialog_placeholder;
        }else{
            this.messageID = messageID;
        }

        // Create Box
        alertDialog = createDialog(activity);
    }

    public void show(){
        alertDialog.show();
    }

    // Dialogue
    private AlertDialog createDialog(@NonNull SourceReadActivity activity) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Set message and listeners
        builder.setMessage(this.messageID)
                .setCancelable(cancellable)
                .setPositiveButton(this.confirmID, this.positive);

        // Check if cancel button needed
        if(cancellable){
            builder.setNegativeButton(this.cancelID, this.negative);
        }

        return builder.create();
    }
}
