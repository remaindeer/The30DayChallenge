package nl.tue.the30daychallenge.details;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.Challenge;
import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.data.LocalConnector;
import nl.tue.the30daychallenge.data.RemoteChallenge;
import nl.tue.the30daychallenge.data.RemoteConnector;
import nl.tue.the30daychallenge.exception.NoServerConnectionException;
import nl.tue.the30daychallenge.exception.RemoteChallengeNotFoundException;

public class DetailsActivity extends ActionBarActivity {

    private static boolean challengeIsLocal = false;
    private static Challenge challenge;

    /**
     * To use this activity, create an intent, with as data:
     * - an id, which uses key challengeId,
     * - a bool, which uses key isLocal;
     */
    public DetailsActivity() {
    }

    /*public DetailsActivity(Challenge challenge) {
        setChallenge(challenge);
    }

    public static void setChallenge(Challenge inputChallenge) {
        challenge = inputChallenge;
        if (challenge instanceof RemoteChallenge) {
            challengeIsLocal = false;
        } else {
            challengeIsLocal = true;
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent starterIntent = getIntent();
        Bundle data = starterIntent.getExtras();
        challengeIsLocal = data.getBoolean("isLocal");
        if (challengeIsLocal) {
            getLocalChallenge(data.getInt("id"));
        } else {
            getRemoteChallenge(data.getInt("id"));
        }
        SetButtonContent();
        SetRunningTime();
        SetTitleAndDescription();
    }

    private void SetTitleAndDescription() {
        TextView TitleView = (TextView) findViewById(R.id.details_challengeTitleBar);
        TitleView.setText(challenge.title);

        TextView DescriptionView = (TextView) findViewById(R.id.details_challengeDescription);
        DescriptionView.setText(challenge.description);
    }

    private void getLocalChallenge(int id) {
        List<LocalChallenge> challenges = LocalConnector.getChallenges();
        for (LocalChallenge challengeToCheck : challenges) {
            if (challengeToCheck.localID == id) {
                challenge = challengeToCheck;
                return;
            }
        }
    }

    private void getRemoteChallenge(int id) {
        final int identifier = id;
        AsyncTask test = new AsyncTask() {

            @Override
            protected RemoteChallenge doInBackground(Object[] params) {
                RemoteChallenge challengeToReturn;
                try {
                    challengeToReturn = RemoteConnector.getChallenge(identifier);
                } catch (Throwable e) {
                    challengeToReturn = new RemoteChallenge();
                    ShowMessageBox("Something went wrong ;(", "There is no internet connection");
                }
                return challengeToReturn;
            }
        };
        try {
            RemoteChallenge test2 = (RemoteChallenge) test.execute().get();
            challenge = (Challenge) test2;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_challenge_details, menu);
        return true;
    }

    private void SetButtonContent() {
        ButtonState buttonState;
        Button button = (Button) findViewById(R.id.likeSlashUploadSlashDownloadButton);
        if (challengeIsLocal) {
            // If liking is available
            LocalChallenge localChallenge = (LocalChallenge) challenge;
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
        button.setOnClickListener(new ButtonClickListener(buttonState, challenge, this));
    }

    private void SetRunningTime() {
        if (challengeIsLocal) {
            LocalChallenge local = (LocalChallenge) challenge;
            String startedAt = String.format("Challenge started at %s", local.startDate.toString());
            ((TextView) findViewById(R.id.details_StartedAt)).setText(startedAt);
        } else {
            RemoteChallenge remote = (RemoteChallenge) challenge;
            String startedAt = String.format("Challenge downloaded %d times", remote.downloads);
            Log.d("DetailsActivity", startedAt);
            ((TextView) findViewById(R.id.details_StartedAt)).setText(startedAt);
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
        Activity parent;

        public ButtonClickListener(ButtonState state, Challenge challenge, Activity parent) {
            this.state = state;
            this.challenge = challenge;
            this.parent = parent;
        }

        @Override
        public void onClick(View v) {
            switch (state) {
                case Like:
                    if (challenge instanceof LocalChallenge) {
                        new Liker(true, (LocalChallenge) challenge).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        ShowMessageBox(
                                "Challenge can't be  liked",
                                "This is not a bug, this is a feature"
                        );
                    }
                    break;
                case Unlike:
                    if (challenge instanceof LocalChallenge) {
                        new Liker(false, (LocalChallenge) challenge).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        ShowMessageBox(
                                "Challenge can't be unliked",
                                "This is not a bug, this is a feature"
                        );
                    }
                    break;
                case Download:
                    if (challenge instanceof RemoteChallenge) {
                        UpDownloader downloader = new UpDownloader(challenge, parent);
                        downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        finish();
                    } else {
                        ShowMessageBox(
                                "Challenge can't be downloaded",
                                "This is not a bug, this is a feature"
                        );
                    }
                    break;
                case Upload:
                    if (challenge instanceof LocalChallenge) {
                        UpDownloader upLoader = new UpDownloader(challenge, parent);
                        upLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
    Activity parent;

    public UpDownloader(Challenge challenge, Activity parent) {
        this.challenge = challenge;
        this.parent = parent;
        if (challenge instanceof LocalChallenge) {
            isLocal = true;
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            if (isLocal) {
                ((LocalChallenge) challenge).upload();
                nl.tue.the30daychallenge.Globals.MessageBoxes.ShowOkMessageBox(
                        "Upload succeeded",
                        "This app is uploaded to the server",
                        parent);
            } else {
                RemoteConnector.downloadRemoteChallenge(((RemoteChallenge) challenge));
                nl.tue.the30daychallenge.Globals.MessageBoxes.ShowOkMessageBox(
                        "Download succeeded",
                        "This app is downloaded from the server",
                        parent);
            }
        } catch (NoServerConnectionException e) {

        } catch (RemoteChallengeNotFoundException e) {

        }
        return null;
    }
}
