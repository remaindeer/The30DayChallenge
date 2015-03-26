package nl.tue.the30daychallenge.details;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import nl.tue.the30daychallenge.Globals.MessageBoxes;
import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.Challenge;
import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.data.LocalConnector;
import nl.tue.the30daychallenge.data.RemoteChallenge;
import nl.tue.the30daychallenge.data.RemoteConnector;
import nl.tue.the30daychallenge.exception.NoServerConnectionException;
import nl.tue.the30daychallenge.exception.RemoteChallengeNotFoundException;

public class DetailsActivity extends ActionBarActivity {

    private boolean challengeIsLocal = false;
    private Challenge challenge;

    /**
     * To use this activity, create an intent, with as data:
     * - an id, which uses key challengeId,
     * - a bool, which uses key isLocal;
     */
    public DetailsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent starterIntent = getIntent();
        Bundle data = starterIntent.getExtras();
        challengeIsLocal = data.getBoolean("isLocal");
        if(challengeIsLocal){
            getLocalChallenge(data);
        } else {
            getRemoteChallenge(data);
        }
        SetButtonContent();
        SetRunningTime();
    }

    private void getLocalChallenge(Bundle data) {
        int id = data.getInt("challengeId");
        List<LocalChallenge> challenges = LocalConnector.getChallenges();
        for (LocalChallenge challengeToCheck : challenges) {
            if (challengeToCheck.localID == id) {
                this.challenge = challengeToCheck;
                break;
            }
        }
        throw new RuntimeException("Local challenge not found");
    }

    private void getRemoteChallenge(Bundle data) {
        final int id = data.getInt("challengeId");
        final Activity parent = (Activity) this;
        try {
            AsyncTask run = new AsyncTask<Integer, Void, RemoteChallenge>() {
                @Override
                protected RemoteChallenge doInBackground(Integer... q) {
                    try {
                        return RemoteConnector.getChallenge(q[0]);
                    } catch (NoServerConnectionException e) {
                        MessageBoxes.ShowOkMessageBox("Error", "We can't reach the network", parent);
                    } catch (RemoteChallengeNotFoundException e) {
                        MessageBoxes.ShowOkMessageBox("Error", "Remote Challenge not found", parent);
                    }
                    return null;
                }
            };
            run.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id).get();
        } catch (Exception e) {
            MessageBoxes.ShowOkMessageBox("Error", "No idea what went wrong here. I think it's your fault (the end user)", this);
        }
    }

    private boolean challengeIsLocal = false;
    static private Challenge challenge;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_challenge_details, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        SetButtonContent();
        SetChallengeTimeRunning();
    }

    private void SetButtonContent() {
        Button button = (Button)findViewById(R.id.likeSlashUploadSlashDownloadButton);
        LocalChallenge localChallenge = (LocalChallenge)challenge;
        if(challengeIsLocal){
            // If liking is available
            if (localChallenge.isUploaded) {
                if (localChallenge.isLiked()) {
                    buttonState = ButtonState.Unlike;
                    button.setText("Unlike");
                } else {
                    buttonState = ButtonState.Like;
                    button.setText("Like");
                }
            } else {
                buttonState = ButtonState.Upload;
                button.setText("Upload");
            }
        } else {
            buttonState = ButtonState.Download;
            button.setText("Download");
        }
        button.setOnClickListener(new ButtonClickListener(buttonState, challenge));
    }

    private void SetRunningTime() {
        if (challengeIsLocal) {
            LocalChallenge local = (LocalChallenge) challenge;
            String startedAt = String.format("Challenge started at %d", local.startDate);
            ((TextView) findViewById(R.id.details_StartedAt)).setText(startedAt);
        } else{
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

    public void ShowMessageBox(String title, String message) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.create().show();
    }

    enum ButtonState {
        Like, Unlike, Download, Upload
    }

    class ButtonClickListener implements View.OnClickListener {

        ButtonState state;
        Challenge challenge;

        public ButtonClickListener(ButtonState state, Challenge challenge) {
            this.state = state;
            this.challenge = challenge;
        }

        public void SetState(ButtonState state) {
            this.state = state;
        }

        @Override
        public void onClick(View v) {
            switch (state) {
                case Like:
                    if (challenge instanceof LocalChallenge) {
                        new Liker(true, (LocalChallenge) challenge).executeOnExecutor(null);
                    } else {
                        ShowMessageBox(
                                "Challenge can't be  liked",
                                "This is not a bug, this is a feature"
                        );
                    }
                    break;
                case Unlike:
                    if (challenge instanceof LocalChallenge) {
                        new Liker(false, (LocalChallenge) challenge).executeOnExecutor(null);
                    } else {
                        ShowMessageBox(
                                "Challenge can't be unliked",
                                "This is not a bug, this is a feature"
                        );
                    }
                    break;
                case Download:
                    if (challenge instanceof RemoteChallenge) {
                        new UpDownloader(challenge).executeOnExecutor(null);
                    } else {
                        ShowMessageBox(
                                "Challenge can't be downloaded",
                                "This is not a bug, this is a feature"
                        );
                    }
                    break;
                case Upload:
                    if (challenge instanceof LocalChallenge) {
                        new UpDownloader(challenge).executeOnExecutor(null);
                    } else {
                        ShowMessageBox(
                                "Challenge can't be uploaded",
                                "This is not a bug, this is a feature"
                        );
                    }
                    break;
                default:
                    break;
            }
        }

    }
}

class Liker extends AsyncTask {

    boolean like;
    LocalChallenge challenge;

    public Liker(boolean like, LocalChallenge challenge) {
        this.like = like;
        this.challenge = challenge;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            challenge.setLike(like);
        } catch (NoServerConnectionException e) {

        } catch (RemoteChallengeNotFoundException e) {

        }
        return null;
    }
}

class UpDownloader extends AsyncTask {

    Challenge challenge;
    boolean isLocal = false;

    public UpDownloader(Challenge challenge) {
        this.challenge = challenge;
        if (challenge instanceof LocalChallenge) {
            isLocal = true;
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            if (isLocal) {
                ((LocalChallenge) challenge).upload();
            } else {
                RemoteConnector.downloadRemoteChallenge(((RemoteChallenge) challenge));
            }
        } catch (NoServerConnectionException e) {

        } catch (RemoteChallengeNotFoundException e) {

        }
        return null;
    }
}
