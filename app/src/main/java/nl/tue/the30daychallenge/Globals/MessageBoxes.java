package nl.tue.the30daychallenge.Globals;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by tane on 3/24/15.
 */
public class MessageBoxes {
    public static void ShowOkMessageBox(String title, String message, Activity parent) {
        final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(parent);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}
