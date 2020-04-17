package derenvural.sourceread_prototype.data.dialog;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public abstract class SourceReadDialog{
    // Object
    private AlertDialog alertDialog;
    // Dialog state
    private boolean cancellable;
    // Message ID's
    private Integer messageID;
    private Integer cancelID;
    private Integer confirmID;
    // Listeners
    private DialogInterface.OnClickListener positive;
    private DialogInterface.OnClickListener negative;
    private DialogInterface.OnClickListener default_negative = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            // cancel
            dialog.dismiss();
        }
    };

    // Show method
    public void show(){ getAlertDialog().show(); }

    // Listeners
    DialogInterface.OnClickListener getPositive() { return positive; }
    void setPositive(DialogInterface.OnClickListener positive) { this.positive = positive; }
    DialogInterface.OnClickListener getNegative() { return negative; }
    void setNegative(DialogInterface.OnClickListener negative) { this.negative = negative; }
    DialogInterface.OnClickListener getDefault_negative() { return default_negative; }

    // State
    boolean getCancelable() { return cancellable; }
    void setCancellable(boolean cancellable) { this.cancellable = cancellable; }

    // Message ID's
    Integer getConfirmID() { return confirmID; }
    void setConfirmID(Integer confirmID) { this.confirmID = confirmID; }
    Integer getCancelID() { return cancelID; }
    void setCancelID(Integer cancelID) { this.cancelID = cancelID; }
    Integer getMessageID() { return messageID; }
    void setMessageID(Integer messageID) { this.messageID = messageID; }

    AlertDialog getAlertDialog() { return alertDialog; }
    void setAlertDialog(AlertDialog alertDialog) { this.alertDialog = alertDialog; }
}
