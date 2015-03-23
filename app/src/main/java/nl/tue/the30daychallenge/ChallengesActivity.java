package nl.tue.the30daychallenge;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;

/**
 * Created by s129778 on 22-3-2015.
 */
public class ChallengesActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenges_activity);
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
}
