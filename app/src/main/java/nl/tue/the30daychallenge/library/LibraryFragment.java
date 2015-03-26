package nl.tue.the30daychallenge.library;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.RemoteChallenge;
import nl.tue.the30daychallenge.data.RemoteConnector;
import nl.tue.the30daychallenge.details.DetailsActivity;
import nl.tue.the30daychallenge.exception.NoServerConnectionException;

public class LibraryFragment extends Fragment implements SensorListener {

    // Declaring Your View and Variables

    private FragmentActivity myContext;
    private LibraryFragment me;
    private static final int SHAKE_THRESHOLD = 4000;
    // For shake motion detection.
    public static SensorManager sensorMgr;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Editors picks", "Categories"};
    int Numboftabs = 2;
    private FragmentActivity myContext;
    private long lastUpdate = -1;
    private float x, y, z;
    private float last_x, last_y, last_z;
    private State currentState = State.OVERVIEW;

    public LibraryFragment() {
    }

    ;

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }

    private enum State {
        RANDOM, OVERVIEW
    };
    private State currentState = State.OVERVIEW;

    public LibraryFragment() {
        me = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentState = State.OVERVIEW;
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.activity_library, container, false);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(myContext.getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) v.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        // start motion detection
        boolean accelSupported = sensorMgr.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);
        Log.d("Shaker", "Sensor support: " + accelSupported);

        if (!accelSupported) {
            // on accelerometer on this device
            sensorMgr.unregisterListener(this,
                    SensorManager.SENSOR_ACCELEROMETER);
        }

        return v;
    }

    public void onSensorChanged(int sensor, float[] values) {
        if (currentState != State.RANDOM && sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = values[SensorManager.DATA_X];
                y = values[SensorManager.DATA_Y];
                z = values[SensorManager.DATA_Z];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)
                        / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    // @todo implement details screen for a random challenge
                    Log.d("Shaker", "Shaken!");
                    new AsyncTask<String, Boolean, String>() {

                        // test code
                        @Override
                        protected String doInBackground(String... params) {
                            try {
                                List<RemoteChallenge> challenges = RemoteConnector.getChallenges(new RemoteConnector.SortFilter(RemoteConnector.SortField.RANDOM));
                                Log.d("Shaker", challenges.get(0).toString());
                                DetailsActivity.setChallenge(challenges.get(0));
                                me.startActivity(new Intent(getActivity(),DetailsActivity.class));
                            } catch (NoServerConnectionException e) {
                                e.printStackTrace();
                            }
                            return "";
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    currentState = State.RANDOM;
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_library, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        theTextArea.setTextColor(Color.WHITE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getActivity(), ChallengesActivity.class);
                Bundle b = new Bundle();
                b.putString("query", query);
                intent.putExtras(b);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

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
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);

    }

    private enum State {
        RANDOM, OVERVIEW
    }
}