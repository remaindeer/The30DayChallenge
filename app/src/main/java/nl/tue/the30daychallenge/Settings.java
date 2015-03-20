package nl.tue.the30daychallenge;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import nl.tue.the30daychallenge.notification.AlarmReceiverChallengeFailed;
import nl.tue.the30daychallenge.notification.AlarmReceiverReminder;

/**
 * Created by kevin on 3/20/15.
 */
public class Settings {

    public static void scheduleNotification(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, AlarmReceiverChallengeFailed.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 5000, pendingIntent);

        Intent alarmReminderIntent = new Intent(context, AlarmReceiverReminder.class);
        PendingIntent pendingReminderIntent = PendingIntent.getBroadcast(context, 0, alarmReminderIntent, 0);
        manager.cancel(pendingReminderIntent);
        manager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 7000, pendingReminderIntent);
    }

}
