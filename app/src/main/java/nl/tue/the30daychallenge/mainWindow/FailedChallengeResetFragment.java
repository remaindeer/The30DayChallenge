package nl.tue.the30daychallenge.mainWindow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import nl.tue.the30daychallenge.data.LocalChallenge;

/**
 * Created by s130968 on 27-Mar-15.
 */
public class FailedChallengeResetFragment extends DialogFragment {
    LocalChallenge challengex;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("The challenge has failed, do you want to reset?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        challengex.reset();
                        Log.d("", "Yes was clicked" + challengex.toString());

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("", "No was clicked");
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}