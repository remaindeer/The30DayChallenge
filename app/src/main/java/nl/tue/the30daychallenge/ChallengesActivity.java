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
            Fragment newFragment = new ChallengeItemFragment(category);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.challengesContent, newFragment).commit();

    }
}
