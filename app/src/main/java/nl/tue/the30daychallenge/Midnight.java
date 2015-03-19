package nl.tue.the30daychallenge;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kevin on 3/16/15.
 */
public class Midnight {

    int hours = 0;
    int minutes = 0;

    public Midnight(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public Midnight(Timestamp t) {
        this.hours = t.getHours();
        this.minutes = t.getMinutes();
    }

    public Timestamp getLastMidnight() {
        Date nowDate = Calendar.getInstance().getTime();
        Timestamp now = new Timestamp(nowDate.getTime());
        Timestamp midnight = new Timestamp(now.getTime());
        midnight.setHours(hours);
        midnight.setMinutes(minutes);
        midnight.setSeconds(0);
        if (midnight.getHours() > now.getHours() || (midnight.getHours() == now.getHours() && midnight.getMinutes() >= now.getMinutes())) {
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
