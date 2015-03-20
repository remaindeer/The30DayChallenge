package nl.tue.the30daychallenge.notification;

import android.content.Context;
import android.content.Intent;

import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.data.LocalConnector;

/**
 * Created by kevin on 3/19/15.
 */
public class AlarmReceiverReminder extends AlarmReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean challengeExists = false;
        for (LocalChallenge challenge: LocalConnector.getChallenges()) {
            if (!challenge.isAlreadyCheckedToday() && !challenge.isFailed()) {
                // unchecked active challenge found
                challengeExists = true;
            }
        }
        super.onReceive(context, intent);
    }
}
