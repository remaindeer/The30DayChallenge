package nl.tue.the30daychallenge.library;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import nl.tue.the30daychallenge.R;

public class ChallengesActivity extends ActionBarActivity implements ActionBar.OnNavigationListener{

    // Title navigation Spinner data
    private ArrayList<SpinnerNavItem> navSpinner;

    // Navigation adapter
    private TitleNavigationAdapter adapter;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenges_activity);
        // Hide the action bar title
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        // Enabling Spinner dropdown navigation
        actionBar.setNavigationMode(getActionBar().NAVIGATION_MODE_LIST);

        // Spinner title navigation data
        navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem("Most started"));
        navSpinner.add(new SpinnerNavItem("Most completed"));
        navSpinner.add(new SpinnerNavItem("Most liked"));

        // title drop down adapter
        adapter = new TitleNavigationAdapter(getApplicationContext(), navSpinner);

        // assigning the spinner navigation
        actionBar.setListNavigationCallbacks(adapter, this);

        Bundle b = getIntent().getExtras();
        int category = b.getInt("Category");
        String query = b.getString("query");
        Fragment newFragment = null;
        if(category>0){
            newFragment = new ChallengeItemFragment(category);
        }
        if(query!=null){
            newFragment = new ChallengeItemFragment(query);
        }
        if(newFragment==null){
            newFragment = new ChallengeItemFragment();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.challengesContent, newFragment).commit();
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
        return false;
    }
}