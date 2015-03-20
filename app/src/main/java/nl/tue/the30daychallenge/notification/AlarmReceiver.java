package nl.tue.the30daychallenge.notification;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import nl.tue.the30daychallenge.MainActivity;
import nl.tue.the30daychallenge.R;

/**
 * Created by kevin on 3/20/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    protected long[] vibratePattern = new long[]{0, 300, 50, 300, 50, 300};
    protected NotificationCompat.Builder notificationBuilder = null;
    public static int currentID = 1;

    public NotificationCompat.Builder createNotification(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.abc_cab_background_top_material)
                        .setVibrate(vibratePattern);
        return mBuilder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(AlarmReceiver.currentID++, notificationBuilder.build());
    }

}
