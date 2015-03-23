package nl.tue.the30daychallenge.details;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.Challenge;
import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.data.RemoteChallenge;

public class DetailsFragment extends Fragment {

    public DetailsFragment(Challenge challengeToShow) {
        challenge = challengeToShow;
        if(challengeToShow instanceof LocalChallenge){
            challengeIsLocal = true;
        }
    }

    public DetailsFragment(){

    }

    private boolean challengeIsLocal = false;
    private Challenge challenge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        SetChallengeTimeRunning(rootView);
        SetButtonContent(rootView);
        ((TextView)rootView.findViewById(R.id.details_challengeTitleBar)).setText(challenge.title);
        ((TextView)rootView.findViewById(R.id.details_challengeDescription)).setText(challenge.description);
        return rootView;
    }

    private void SetButtonContent(View rootView) {
        Button button = (Button)rootView.findViewById(R.id.likeSlashUploadSlashDownloadButton);
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

    private void SetChallengeTimeRunning(View rootView) {
        if(challengeIsLocal){
            LocalChallenge local = (LocalChallenge)challenge;
            String startedAt = String.format("Challenge started at %d", local.startDate);
            ((TextView)rootView.findViewById(R.id.details_StartedAt)).setText(startedAt);
        }
        else{
            RemoteChallenge remote = (RemoteChallenge)challenge;
            String startedAt = String.format("Challenge downloaded %d times", remote.downloads);
            ((TextView)rootView.findViewById(R.id.details_StartedAt)).setText(startedAt);
        }
    }
}
