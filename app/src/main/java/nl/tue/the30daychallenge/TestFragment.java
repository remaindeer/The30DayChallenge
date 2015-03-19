package nl.tue.the30daychallenge;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import nl.tue.the30daychallenge.data.RemoteChallenge;
import nl.tue.the30daychallenge.data.RemoteConnector;
import nl.tue.the30daychallenge.exception.NoServerConnectionException;

public class TestFragment extends Fragment {

    public TestFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test, container, false);
        Button bu = (Button) rootView.findViewById(R.id.ChallengeDetailsTestButton);
        bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetChallengesFromRemote test = new GetChallengesFromRemote();
                test.execute();
            }
        });
        return rootView;
    }

    public class GetChallengesFromRemote extends AsyncTask<Void, Void, List<RemoteChallenge>> {

        @Override
        protected List<RemoteChallenge> doInBackground(Void... params) {
            try {
                List<RemoteChallenge> testChallenges = RemoteConnector.getChallenges();
                return testChallenges;
            } catch (NoServerConnectionException e) {
                Log.d("getChallenges", "NoServerConnectionException");
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<RemoteChallenge> challenges) {
            for (RemoteChallenge test : challenges) {
                Log.d("challenges", test.description);
            }
        }
    }
}
