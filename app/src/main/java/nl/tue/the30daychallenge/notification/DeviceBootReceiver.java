package nl.tue.the30daychallenge.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nl.tue.the30daychallenge.Settings;

/**
 * Created by kevin on 3/20/15.
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Settings.scheduleNotification(context);
        }
    }

}
