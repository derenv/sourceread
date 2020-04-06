package derenvural.sourceread_prototype.data.dialog;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import derenvural.sourceread_prototype.R;

public class SourceReadDialog {
    private AlertDialog alertDialog;
    private DialogInterface.OnClickListener negative;
    private DialogInterface.OnClickListener positive;
    private Integer messageID;
    private Integer confirmID;
    private Integer cancelID;

    public SourceReadDialog(@NonNull Activity activity,
                            @Nullable DialogInterface.OnClickListener negative,
                            @Nullable DialogInterface.OnClickListener positive,
                            @Nullable Integer confirmID,
                            @Nullable Integer cancelID,
                            @Nullable Integer messageID){
        // Cancel response
        if(negative == null){
            this.negative = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // cancel
                    dialog.dismiss();
                }
            };
        }else{
            this.negative = negative;
        }

        // Accept response
        if(positive == null){
            this.negative = new DialogInterface.OnClickListener() {
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
        if(cancelID == null){
            this.cancelID = R.string.user_cancel;
        }else{
            this.cancelID = cancelID;
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
    private AlertDialog createDialog(@NonNull Activity activity) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Set message and listeners
        builder.setMessage(this.messageID)
                .setPositiveButton(R.string.user_sure, this.positive)
                .setNegativeButton(R.string.user_cancel, this.negative);

        return builder.create();
    }
}
