package nl.tue.the30daychallenge;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;

import nl.tue.the30daychallenge.addChallenge.AddChallenge;
import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.data.LocalConnector;
import nl.tue.the30daychallenge.data.RemoteConnector;
import nl.tue.the30daychallenge.details.DetailsActivity;
import nl.tue.the30daychallenge.exception.NoServerConnectionException;
import nl.tue.the30daychallenge.exception.RemoteChallengeNotFoundException;
import nl.tue.the30daychallenge.library.LibraryFragment;
import nl.tue.the30daychallenge.mainWindow.MainFragment;


public class MainActivity extends ActionBarActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public static Share share = null;
    public static CallbackManager callbackManager;
    public static ShareDialog shareDialog;
    public static MainActivity me;

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        share.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final MainActivity me = this;
        super.onCreate(savedInstanceState);
        MainActivity.me = this;

        if (share == null) {
            share = new Share();
        }

        FacebookSdk.setApplicationId("366234573582332");
        FacebookSdk.setApplicationName("The30DayChallenge");
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        Settings.loadSettings(getSharedPreferences("settings", 0));

        LibraryFragment.sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, new MainFragment())
                .commit();

        mNavItems.add(new NavItem(getString(R.string.title_main), getString(R.string.description_main), R.drawable.ic_action_home));
        mNavItems.add(new NavItem(getString(R.string.title_library), getString(R.string.description_library), R.drawable.ic_action_explore));
        mNavItems.add(new NavItem(getString(R.string.title_settings), getString(R.string.description_settings), R.drawable.ic_action_settings));

        setTitle(mNavItems.get(0).mTitle);

        // DO NOT REMOVE OR MODIFY THIS LINE (NEVER EVER, REALLY)!!!!!!!!!11!!
        RemoteConnector.setCertificate(me.getResources().openRawResource(R.raw.certificate));
        LocalConnector.load(me.getApplicationContext());

        // @test
        createTestEnvironment();


        new AsyncTask<String, Boolean, String>() {

            // test code
            @Override
            protected String doInBackground(String... params) {

                Settings.scheduleNotification(me.getApplicationContext());

                new RemoteConnector(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
                new LocalConnector(me);
                try {
                    LocalChallenge.syncAll();
                } catch (NoServerConnectionException e) {
                    Log.d("Sync",e.toString());
                } catch (RemoteChallengeNotFoundException e) {
                    Log.d("Sync",e.toString());
                }
                /*try {
                    //RemoteConnector.setCertificate(me.getResources().openRawResource(R.raw.certificate));
                    //Log.d("Connector", RemoteConnector.getChallenges().toString());
                } catch (NoServerConnectionException e) {
                    e.printStackTrace();
                }*/
                return "";
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigation Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /*
             * Called when a particular item from the navigation drawer
             * is selected.
             * */
            private void selectItemFromDrawer(int position) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragmentInMain = new MainFragment();
                switch (position) {
                    case 0:
                        fragmentInMain.onStop();
                        fragmentInMain = new MainFragment();

                        mDrawerList.setItemChecked(position, true);
                        setTitle(mNavItems.get(position).mTitle);

                        // Close the drawer
                        mDrawerLayout.closeDrawer(mDrawerPane);
                        break;
                    case 1:
                        fragmentInMain.onPause();
                        fragmentInMain = new LibraryFragment();

                        mDrawerList.setItemChecked(position, true);
                        setTitle(mNavItems.get(position).mTitle);

                        // Close the drawer
                        mDrawerLayout.closeDrawer(mDrawerPane);
                        break;
                    case 2:
                        fragmentInMain.onPause();
                        Intent settingsIntent = new Intent(me, SettingsActivity.class);
                        startActivity(settingsIntent);
                        mDrawerList.setItemChecked(position, true);

                        // Close the drawer
                        mDrawerLayout.closeDrawer(mDrawerPane);
                        break;
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, fragmentInMain)
                        .commit();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        // Observe Open/Close Events of the Drawer begin
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.d("Drawer", "onDrawerOpened");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d("Drawer", "onDrawerClosed: " + getTitle());
                invalidateOptionsMenu();
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // Pass the event to ActionBarDrawerToggle
                // If it returns true, then it has handled
                // the nav drawer indicator touch event
                Log.d("Drawer", "onOptionsItemSelected: " + item);
                if (mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
                // Handle your other action bar items...
                return super.onOptionsItemSelected(item);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // end

        // More info: http://codetheory.in/difference-between-setdisplayhomeasupenabled-sethomebuttonenabled-and-setdisplayshowhomeenabled/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void createCompletedChallenge() {
        LocalChallenge challenge = new LocalChallenge("Completed challenge", "This challenge should be completed", 1);
        challenge.forceCompleted();
        challenge.save();
    }

    public void createTestEnvironment() {
        //LocalConnector.dropDatabase();
        //createCompletedChallenge();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getSupportActionBar().setElevation(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // toggle the side menu
        if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerPane)) {
                mDrawerLayout.closeDrawer(mDrawerPane);
            } else {
                mDrawerLayout.openDrawer(mDrawerPane);
            }
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_example) {
            Intent newChallengeIntent;
            newChallengeIntent = new Intent(this, AddChallenge.class);
            startActivity(newChallengeIntent);

            return true;
        }
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.action_mock_details) {
            Intent DetailsIntent = new Intent(this, DetailsActivity.class);
            //DetailsIntent.putExtra("challenge", new LocalChallenge("4","3",4));
            startActivity(DetailsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class NavItem {
        String mTitle;
        String mSubtitle;
        int mIcon;

        public NavItem(String title, String subtitle, int icon) {
            mTitle = title;
            mSubtitle = subtitle;
            mIcon = icon;
        }
    }

    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            } else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText(mNavItems.get(position).mTitle);
            subtitleView.setText(mNavItems.get(position).mSubtitle);
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }
}
