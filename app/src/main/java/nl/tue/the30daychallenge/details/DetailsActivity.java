package nl.tue.the30daychallenge.details;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import java.util.concurrent.ExecutionException;

import nl.tue.the30daychallenge.Globals.Categories;
import nl.tue.the30daychallenge.MainActivity;
import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.Category;
import nl.tue.the30daychallenge.data.Challenge;
import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.data.LocalConnector;
import nl.tue.the30daychallenge.data.RemoteChallenge;
import nl.tue.the30daychallenge.data.RemoteConnector;
import nl.tue.the30daychallenge.exception.NoServerConnectionException;
import nl.tue.the30daychallenge.exception.RemoteChallengeNotFoundException;

/**
 * To use this activity, create an intent, with as data:
 * - an id, which uses key challengeId,
 * - a bool, which uses key isLocal;
 */
public class DetailsActivity extends ActionBarActivity {

    private static boolean challengeIsLocal = false;
    private static Challenge challenge;
    public Handler clickHandler = new Handler() {
        public void handleMessage(Message msg) {
            setButtonContent();
        }
    };
    public Handler messageHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            String title = data.getString("title");
            String description = data.getString("description");
            nl.tue.the30daychallenge.Globals.MessageBoxes.ShowOkMessageBox(title, description, MainActivity.me);
        }
    };
    private DetailsActivity me = this;

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
        setButtonContent();
        SetRunningTime();
        SetTitleAndDescription();
    }

    private void SetTitleAndDescription() {
        TextView titleView = (TextView) findViewById(R.id.details_challengeTitleBar);
        titleView.setText(challenge.title);

        TextView descriptionView = (TextView) findViewById(R.id.details_challengeDescription);
        descriptionView.setText(challenge.description);

        ImageView imageView = (ImageView) findViewById(R.id.details_challengeLogo);

        List<Category> categories = Categories.getList();
        for (Category category : categories) {
            if (category.categoryID == challenge.categoryID) {
                imageView.setImageResource(Categories.icons.get(category.title));
                break;
            }
        }

        TextView toGo = (TextView) findViewById(R.id.details_RunningToGo);
        if (challengeIsLocal) {
            LocalChallenge challenge2 = (LocalChallenge) challenge;
            if (challenge2.checkCount <= 30) {
                toGo.setText("Amount of days to go: " + (30 - challenge2.checkCount));
            } else {
                toGo.setEnabled(false);
            }
        } else {
            int likes = ((RemoteChallenge) challenge).likes;
            toGo.setText("Amount of likes: " + likes);
        }

        TextView amountOfTimesFailed = (TextView) findViewById(R.id.details_AmountOfTimesFailed);
        if (challengeIsLocal) {
            LocalChallenge challenge2 = (LocalChallenge) challenge;
            if (challenge2.amountOfTimesFailed > 1) {
                amountOfTimesFailed.setText("Amount of attempts: " + challenge2.amountOfTimesFailed);
            } else {
                amountOfTimesFailed.setEnabled(false);
            }
        }

        TextView highScore = (TextView) findViewById(R.id.details_HighScore);
        if (challengeIsLocal) {
            highScore.setText("Highscore: " + ((LocalChallenge) challenge).highscore + " days");
        } else {
            highScore.setEnabled(false);
        }
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
        Log.d("Details", "Searching for remote ID: " + id);
        for (LocalChallenge challenge: LocalConnector.getChallenges()) {
            Log.d("Details", "Challenge found: " + challenge.remoteChallengeID);
            if (id == challenge.remoteChallengeID) {
                challengeIsLocal = true;
                getLocalChallenge(challenge.localID);
                return;
            }
        }
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

    public void setButtonContent() {
        ButtonState buttonState;
        ImageButton button = (ImageButton) findViewById(R.id.likeSlashUploadSlashDownloadButton);
        if (challengeIsLocal) {
            // If liking is available
            LocalChallenge localChallenge = (LocalChallenge) challenge;
            if (localChallenge.isUploaded) {
                if (localChallenge.isLiked()) {
                    buttonState = ButtonState.Unlike;
                    button.setImageResource(R.drawable.ic_action_thumb_down_fab);
                } else {
                    buttonState = ButtonState.Like;
                    button.setImageResource(R.drawable.ic_action_thumb_up_fab);
                }
            } else {
                buttonState = ButtonState.Upload;
                button.setImageResource(R.drawable.ic_action_share_fab);
            }
        } else {
            buttonState = ButtonState.Download;
            button.setImageResource(R.drawable.ic_action_add_fab);
        }
        button.setOnClickListener(new ButtonClickListener(buttonState, challenge, this));
    }

    private void SetRunningTime() {
        if (challengeIsLocal) {
            LocalChallenge local = (LocalChallenge) challenge;
            String startedAt = String.format("Challenge started at %s", local.startDate.toString().substring(0, 10));
            ((TextView) findViewById(R.id.details_StartedAt)).setText(startedAt);

            String runningFor = String.format("Challenge running for %d days", local.checkCount);
            ((TextView) findViewById(R.id.details_RunningFor)).setText(runningFor);
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
        DetailsActivity parent;

        public ButtonClickListener(ButtonState state, Challenge challenge, DetailsActivity parent) {
            this.state = state;
            this.challenge = challenge;
            this.parent = parent;
        }

        @Override
        public void onClick(View v) {
            switch (state) {
                case Like:
                    if (challenge instanceof LocalChallenge) {
                        new Liker(true, (LocalChallenge) challenge, this.parent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        parent.setButtonContent();
                    } else {
                        ShowMessageBox(
                                "Challenge can't be  liked",
                                "This is not a bug, this is a feature"
                        );
                    }
                    break;
                case Unlike:
                    if (challenge instanceof LocalChallenge) {
                        new Liker(false, (LocalChallenge) challenge, this.parent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        parent.setButtonContent();
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
    DetailsActivity parent;

    public Liker(boolean like, LocalChallenge challenge, DetailsActivity parent) {
        this.like = like;
        this.challenge = challenge;
        this.parent = parent;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            challenge.setLike(like);
            parent.clickHandler.sendEmptyMessage(1);
        } catch (NoServerConnectionException e) {

        } catch (RemoteChallengeNotFoundException e) {

        }
        return null;
    }
}

class UpDownloader extends AsyncTask {

    Challenge challenge;
    boolean isLocal = false;
    DetailsActivity parent;

    public UpDownloader(Challenge challenge, DetailsActivity parent) {
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
                /*Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("title", "Upload succeeded");
                bundle.putString("description", "This challenge is uploaded to the server");
                msg.setData(bundle);
                parent.messageHandler.sendMessage(msg);*/
            } else {
                RemoteConnector.downloadRemoteChallenge(((RemoteChallenge) challenge));
                /*Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("title", "Download succeeded");
                bundle.putString("description", "This challenge is download from server");
                msg.setData(bundle);
                parent.messageHandler.sendMessage(msg);*/
            }
        } catch (NoServerConnectionException e) {

        } catch (RemoteChallengeNotFoundException e) {

        }
        return null;
    }
}
