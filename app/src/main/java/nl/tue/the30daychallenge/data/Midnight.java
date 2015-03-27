package nl.tue.the30daychallenge.data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nl.tue.the30daychallenge.Settings;

/**
 * Created by kevin on 3/16/15.
 */
public class Midnight {

    int hours = -1;
    int minutes = -1;

    public Midnight() {
        this.hours = Settings.midnightHours;
        this.minutes = Settings.midnightMinutes;
    }

    public Timestamp getLastMidnight() {
        Date nowDate = Calendar.getInstance().getTime();
        Timestamp now = new Timestamp(nowDate.getTime());
        Timestamp midnight = new Timestamp(now.getTime());
        midnight.setHours(hours);
        midnight.setMinutes(minutes);
        midnight.setSeconds(0);
        if (midnight.getTime() > now.getTime()) {
            // midnight is in the future!
            midnight.setTime(midnight.getTime() - 24 * 60 * 60 * 1000);
        }
        return midnight;
    }

    @Override
    public String toString() {
        return "Midnight{" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.getLastMidnight()) + '}';
    }
}
