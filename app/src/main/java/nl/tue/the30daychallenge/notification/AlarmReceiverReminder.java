package nl.tue.the30daychallenge.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.data.LocalConnector;

/**
 * Created by kevin on 3/19/15.
 */
public class AlarmReceiverReminder extends AlarmReceiver {

    private int numUncheckedActiveChallenges = 0;

    @Override
    public NotificationCompat.Builder createNotification(Context context) {
        NotificationCompat.Builder builder = super.createNotification(context);
        if (numUncheckedActiveChallenges == 1) {
            builder.setContentText("You have got one unchecked challenge!");
        } else if (numUncheckedActiveChallenges > 1) {
            builder.setContentText("You have got multiple unchecked challenges!");
        }
        return builder;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        loadDatabase(context);
        Log.d("Connector", "Reminder alarm called");
        for (LocalChallenge challenge: LocalConnector.getChallenges()) {
            Log.d("Connector", challenge.toString());
            if (!challenge.isAlreadyCheckedToday() && !challenge.isFailed()) {
                // unchecked active challenge found
                numUncheckedActiveChallenges++;
            }
        }
        if (numUncheckedActiveChallenges > 0) {
            this.notificationBuilder = createNotification(context);
            super.onReceive(context, intent);
        }
    }

}
