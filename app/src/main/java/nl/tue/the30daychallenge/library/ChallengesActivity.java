package nl.tue.the30daychallenge.library;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.RemoteChallenge;
import nl.tue.the30daychallenge.data.RemoteConnector;

public class ChallengesActivity extends ActionBarActivity implements ActionBar.OnNavigationListener{

    // Title navigation Spinner data
    private ArrayList<SpinnerNavItem> navSpinner;
    private String sort ="";
    private Fragment fragment;
    // Navigation adapter
    private TitleNavigationAdapter adapter;

    private ActionBar actionBar;
    private int category;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenges_activity);
        Bundle b = getIntent().getExtras();
        category = b.getInt("Category");
        query = b.getString("query");
        if(query==null) {
            // Hide the action bar title
            actionBar = getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(false);

            // Enabling Spinner dropdown navigation
            actionBar.setNavigationMode(getActionBar().NAVIGATION_MODE_LIST);

            // Spinner title navigation data
            navSpinner = new ArrayList<SpinnerNavItem>();
            for (RemoteConnector.SortField s : RemoteConnector.SortField.values()) {
                navSpinner.add(new SpinnerNavItem(s.name()));
            }
            sort = navSpinner.get(0).getTitle();
            // title drop down adapter
            adapter = new TitleNavigationAdapter(getApplicationContext(), navSpinner);

            // assigning the spinner navigation
            actionBar.setListNavigationCallbacks(adapter, this);
        }

        loadFragment();
    }

    public void loadFragment(){
        fragment = null;
        if(category>0){
            fragment = new ChallengeItemFragment(category,sort);
        }
        if(query!=null){
            fragment = new ChallengeItemFragment(query);
        }
        if(fragment==null){
            fragment = new ChallengeItemFragment();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.challengesContent, fragment).commit();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_challenge_list_library, menu);
        // Remove the action bar's shadow
        getSupportActionBar().setElevation(0);
        return true;
    }

    /**
     * Actionbar navigation item select listener
     * */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        // Action to be taken after selecting a spinner item
        sort=navSpinner.get(itemPosition).getTitle();
        loadFragment();
        return true;
    }
}