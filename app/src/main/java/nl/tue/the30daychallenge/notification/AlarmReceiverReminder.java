package nl.tue.the30daychallenge.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by kevin on 3/19/15.
 */
public class AlarmReceiverReminder extends AlarmReceiver {

    @Override
    public NotificationCompat.Builder createNotification(Context context) {
        NotificationCompat.Builder builder = super.createNotification(context);
        builder
                .setContentTitle("COOL1")
                .setContentText("AWESOME")
        ;
        return builder;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean challengeExists = false;
        /*new LocalConnector(context);
        for (LocalChallenge challenge: new LocalConnector(context).getChallenges()) {
            if (!challenge.isAlreadyCheckedToday() && !challenge.isFailed()) {
                // unchecked active challenge found
                challengeExists = true;
            }
        }*/
        //if (challengeExists) {
        this.notificationBuilder = createNotification(context);
        super.onReceive(context, intent);
        //}
    }

}