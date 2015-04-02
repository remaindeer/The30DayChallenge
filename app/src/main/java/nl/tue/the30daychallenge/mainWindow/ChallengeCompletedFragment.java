package nl.tue.the30daychallenge.mainWindow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.exception.NoServerConnectionException;
import nl.tue.the30daychallenge.exception.RemoteChallengeNotFoundException;

/**
 * Created by Kagan on 02-Apr-15.
 */
public class ChallengeCompletedFragment extends DialogFragment {
    LocalChallenge challengeFragment;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You have already completed this challenge, do you want to reset it?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            challengeFragment.reset();
                        } catch (NoServerConnectionException e) {
                            Log.d("Check", e.toString());
                        } catch (RemoteChallengeNotFoundException e) {
                            Log.d("Check",e.toString());
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}