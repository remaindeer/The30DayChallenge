package nl.tue.the30daychallenge.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.data.LocalConnector;

/**
 * Created by kevin on 3/19/15.
 */
public class AlarmReceiverChallengeFailed extends AlarmReceiver {

    private int numFailedChallenges = 0;

    @Override
    public NotificationCompat.Builder createNotification(Context context) {
        NotificationCompat.Builder builder = super.createNotification(context);
        if (numFailedChallenges == 1) {
            builder.setContentText("One of the challenges failed!");
        } else if (numFailedChallenges > 1) {
            builder.setContentText("Multiple challenges failed!");
        }
        return builder;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        loadDatabase(context);
        for (LocalChallenge challenge: LocalConnector.getChallenges()) {
            if (challenge.isFailedYesterday()) numFailedChallenges++;
        }
        if (numFailedChallenges > 0) {
            this.notificationBuilder = createNotification(context);
            super.onReceive(context, intent);
        }
    }

}
