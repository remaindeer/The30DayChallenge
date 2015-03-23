package nl.tue.the30daychallenge;

import android.app.Activity;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by kevin on 3/23/15.
 */
public class ShakeActivity extends Activity implements SensorListener {
    // For shake motion detection.
    private SensorManager sensorMgr;
    private long lastUpdate = -1;
    private float x, y, z;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 4000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Shaker", "Initialized");

        // start motion detection
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        boolean accelSupported = sensorMgr.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);
        Log.d("Shaker", "Sensor support: " + accelSupported);

        if (!accelSupported) {
            // on accelerometer on this device
            sensorMgr.unregisterListener(this,
                    SensorManager.SENSOR_ACCELEROMETER);
        }
    }

    protected void onPause() {
        if (sensorMgr != null) {
            sensorMgr.unregisterListener(this,
                    SensorManager.SENSOR_ACCELEROMETER);
            sensorMgr = null;
        }
        super.onPause();
    }

    public void onAccuracyChanged(int arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = values[SensorManager.DATA_X];
                y = values[SensorManager.DATA_Y];
                z = values[SensorManager.DATA_Z];

                float speed = Math.abs(x+y+z - last_x - last_y - last_z)
                        / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    // yes, this is a shake action! Do something about it!
                    // if device is shake with threshold then this condition become true..
                    // you can put your code here...
                    MainActivity.library.onShake();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }
}