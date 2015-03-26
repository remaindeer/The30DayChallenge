package nl.tue.the30daychallenge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import java.util.prefs.PreferenceChangeEvent;

/**
 * Created by kevin on 3/24/15.
 */
public class SettingsActivity extends ActionBarActivity implements TimePicker.OnTimeChangedListener, CompoundButton.OnCheckedChangeListener {

    TimePicker midnightTime;
    TimePicker notificationTime;
    CheckBox notificationsEnabled;
    CheckBox vibrationsEnabled;
    CheckBox soundEnabled;

    public void saveSettings() {
        extractValues();
        Log.d("Settings", "Values extracted");
        SharedPreferences settings = getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("notificationsEnabled", Settings.notificationsEnabled);
        editor.putBoolean("vibrationsEnabled", Settings.vibrationsEnabled);
        editor.putBoolean("soundEnabled", Settings.soundEnabled);
        editor.putInt("midnightHours", Settings.midnightHours);
        editor.putInt("midnightMinutes", Settings.midnightMinutes);
        editor.putInt("reminderHours", Settings.reminderHours);
        editor.putInt("reminderMinutes", Settings.reminderMinutes);
        editor.commit();
        Log.d("Settings", "Settings stored: " + Settings.getString());
        updateNotificationsEnabledState();
        Settings.scheduleNotification(getApplicationContext());
    }

    public void extractValues() {
        Settings.notificationsEnabled = notificationsEnabled.isChecked();
        Settings.vibrationsEnabled = vibrationsEnabled.isChecked();
        Settings.soundEnabled = soundEnabled.isChecked();
        Settings.midnightHours = midnightTime.getCurrentHour();
        Settings.midnightMinutes = midnightTime.getCurrentMinute();
        Settings.reminderHours = notificationTime.getCurrentHour();
        Settings.reminderMinutes = notificationTime.getCurrentMinute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initialize the timers
        midnightTime = (TimePicker)findViewById(R.id.midnightPicker);
        notificationTime = (TimePicker)findViewById(R.id.notificationPicker);
        midnightTime.setIs24HourView(true);
        notificationTime.setIs24HourView(true);
        midnightTime.setCurrentHour(Settings.midnightHours);
        midnightTime.setCurrentMinute(Settings.midnightMinutes);
        notificationTime.setCurrentHour(Settings.reminderHours);
        notificationTime.setCurrentMinute(Settings.reminderMinutes);
        midnightTime.setOnTimeChangedListener(this);
        notificationTime.setOnTimeChangedListener(this);

        // initialize the checkboxes
        notificationsEnabled = (CheckBox)findViewById(R.id.notificationEnabled);
        vibrationsEnabled = (CheckBox)findViewById(R.id.vibrationsEnabled);
        soundEnabled = (CheckBox)findViewById(R.id.soundEnabled);
        notificationsEnabled.setChecked(Settings.notificationsEnabled);
        vibrationsEnabled.setChecked(Settings.vibrationsEnabled);
        soundEnabled.setChecked(Settings.soundEnabled);
        notificationsEnabled.setOnCheckedChangeListener(this);
        vibrationsEnabled.setOnCheckedChangeListener(this);
        soundEnabled.setOnCheckedChangeListener(this);

        // initialize
        updateNotificationsEnabledState();
    }

    public void updateNotificationsEnabledState() {
        Log.d("Settings", "Checkbox changed");
        if (notificationsEnabled.isChecked() == false) {
            vibrationsEnabled.setEnabled(false);
            soundEnabled.setEnabled(false);
            notificationTime.setEnabled(false);
        } else {
            vibrationsEnabled.setEnabled(true);
            soundEnabled.setEnabled(true);
            notificationTime.setEnabled(true);
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Remove the action bar's shadow
        getSupportActionBar().setElevation(0);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        saveSettings();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        saveSettings();
    }
}
