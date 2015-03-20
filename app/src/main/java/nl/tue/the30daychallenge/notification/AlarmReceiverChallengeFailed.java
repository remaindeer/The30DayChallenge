package nl.tue.the30daychallenge.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by kevin on 3/19/15.
 */
public class AlarmReceiverChallengeFailed extends AlarmReceiver {

    private long[] vibratePattern = new long[]{0, 300, 50, 300, 50, 300};

    @Override
    public NotificationCompat.Builder createNotification(Context context) {
        NotificationCompat.Builder builder = super.createNotification(context);
        builder
                .setContentTitle("COOL")
                .setContentText("AWESOME")
        ;
        return builder;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.notificationBuilder = createNotification(context);
        super.onReceive(context, intent);
    }

}
