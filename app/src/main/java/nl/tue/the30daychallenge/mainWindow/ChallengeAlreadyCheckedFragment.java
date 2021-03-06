package nl.tue.the30daychallenge.mainWindow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import nl.tue.the30daychallenge.data.LocalChallenge;

/**
 * Created by Kagan on 28-Mar-15.
 */
public class ChallengeAlreadyCheckedFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You have already checked this challenge for today.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}