package nl.tue.the30daychallenge.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by kevin on 3/19/15.
 */
public class AlarmReceiverChallengeFailed extends AlarmReceiver {

    @Override
    public NotificationCompat.Builder createNotification(Context context) {
        NotificationCompat.Builder builder = super.createNotification(context);
        builder
                .setContentTitle("30 day challenge")
                .setContentText("")
        ;
        return builder;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        loadDatabase(context);
        this.notificationBuilder = createNotification(context);
        super.onReceive(context, intent);
    }

}
