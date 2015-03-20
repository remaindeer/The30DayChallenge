package nl.tue.the30daychallenge;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.sql.Timestamp;
import java.util.Calendar;

import nl.tue.the30daychallenge.notification.AlarmReceiverChallengeFailed;
import nl.tue.the30daychallenge.notification.AlarmReceiverReminder;

/**
 * Created by kevin on 3/20/15.
 */
public class Settings {

    // the midnight time
    public static int midnightHours = 16;
    public static int midnightMinutes = 23;

    // the reminder notification time
    public static int reminderHours = 16;
    public static int reminderMinutes = 24;

    public static void scheduleNotification(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long now = Calendar.getInstance().getTimeInMillis();
        Timestamp reminderstamp = new Timestamp(now);
        reminderstamp.setHours(reminderHours);
        reminderstamp.setMinutes(reminderMinutes);
        reminderstamp.setSeconds(0);
        long remindertime = reminderstamp.getTime();
        Timestamp midnightstamp = new Timestamp(now);
        midnightstamp.setHours(midnightHours);
        midnightstamp.setMinutes(midnightMinutes);
        midnightstamp.setSeconds(0);
        long midnighttime = midnightstamp.getTime();
        if (remindertime <= now) {
            // remindertime is in the past
            remindertime += 24 * 60 * 60 * 1000;
        }
        if (midnighttime <= now) {
            // midnighttime is in the past
            midnighttime += 24 * 60 * 60 * 1000;
        }

        Intent alarmIntent = new Intent(context, AlarmReceiverChallengeFailed.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.RTC_WAKEUP, midnighttime, pendingIntent);

        Intent alarmReminderIntent = new Intent(context, AlarmReceiverReminder.class);
        PendingIntent pendingReminderIntent = PendingIntent.getBroadcast(context, 0, alarmReminderIntent, 0);
        manager.cancel(pendingReminderIntent);
        manager.set(AlarmManager.RTC_WAKEUP, remindertime, pendingReminderIntent);
    }

}
