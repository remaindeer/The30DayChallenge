package nl.tue.the30daychallenge.notification;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import nl.tue.the30daychallenge.MainActivity;
import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.Settings;
import nl.tue.the30daychallenge.data.LocalConnector;

/**
 * Created by kevin on 3/20/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    //protected long[] vibratePattern = new long[]{0, 300, 50, 300, 50, 300};
    protected long[] vibratePattern = new long[]{100,200,100,200,100,200,100,200,100,100,100,100,100,200,100,200,100,200,100,200,100,100,100,100,100,200,100,200,100,200,100,200,100,100,100,100,100,100,100,100,100,100,50,50,100,800};

    protected NotificationCompat.Builder notificationBuilder = null;
    public static int currentID = 1;

    public NotificationCompat.Builder createNotification(Context context) {
        Settings.loadSettings(context.getSharedPreferences("settings", 0));
        Log.d("Settings", "createNotification: " + Settings.getString());
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("30 day challenge")
                ;
        if (Settings.vibrationsEnabled) {
            mBuilder.setVibrate(vibratePattern);
        }
        if (Settings.soundEnabled) {
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        return mBuilder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        Settings.loadSettings(context.getSharedPreferences("settings", 0));
        Log.d("Settings", "onReceive: " + Settings.getString());
        if (Settings.notificationsEnabled) {
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

    public static void loadDatabase(Context context) {
        Settings.loadSettings(context.getSharedPreferences("settings", 0));
        LocalConnector.load(context);
    }

}
