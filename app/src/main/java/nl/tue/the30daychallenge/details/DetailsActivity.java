package nl.tue.the30daychallenge.details;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.Challenge;
import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.data.RemoteChallenge;

public class DetailsActivity extends ActionBarActivity {

    public DetailsActivity(Challenge challengeToShow){
        challenge = challengeToShow;
        if(challengeToShow instanceof LocalChallenge){
            challengeIsLocal = true;
        }
    }

    public DetailsActivity(){}


    private boolean challengeIsLocal = false;
    static private Challenge challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        SetButtonContent();
        SetChallengeTimeRunning();
    }

    private void SetButtonContent() {
        Button button = (Button)findViewById(R.id.likeSlashUploadSlashDownloadButton);
        if(challengeIsLocal){
            // If liking is available
            if(((LocalChallenge)challenge).isUploaded){
                button.setText("Like");
            } else {
                button.setText("Upload");
            }
        } else {
            button.setText("Download");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DetailsView", "Button clicked");
            }
        });
    }

    private void SetChallengeTimeRunning() {
        if(challengeIsLocal){
            LocalChallenge local = (LocalChallenge)challenge;
            String startedAt = String.format("Challenge started at %d", local.startDate);
            ((TextView)findViewById(R.id.details_StartedAt)).setText(startedAt);
        }
        else{
            RemoteChallenge remote = (RemoteChallenge)challenge;
            String startedAt = String.format("Challenge downloaded %d times", remote.downloads);
            ((TextView)findViewById(R.id.details_StartedAt)).setText(startedAt);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void setChallenge(Challenge challenge) {
        DetailsActivity.challenge = challenge;
    }
}