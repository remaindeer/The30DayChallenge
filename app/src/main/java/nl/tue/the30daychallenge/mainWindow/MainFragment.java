package nl.tue.the30daychallenge.mainWindow;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.addChallenge.AddChallenge;
import nl.tue.the30daychallenge.data.LocalConnector;

public class MainFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.OnScrollListener {
    public static Timer timer;
    /**
     * updates the view when testUpdates sends a message
     */
    public Handler _handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            getChallenges();
        }
    };
    /**
     * Running challenges we want to show.
     */
    private List challengeListItemList = new ArrayList(); // at the top of your fragment list
    /**
     * The fragment's ListView/GridView.
     */
    private RecyclerView cardView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainFragment() {
    }

    /**
     * Method that is supposed to fill the view with cards
     */
    public void getChallenges() {
        challengeListItemList = LocalConnector.getChallenges();
        if (adapter != null) {
            MainWindowAdapter mainAdapter = (MainWindowAdapter) adapter;
            mainAdapter.setChallenges(challengeListItemList);
            mainAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Method that starts up the fragment
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mAdapter = new ChallengeListAdapterLocal(getActivity(), challengeListItemList);
        LocalConnector.load(getActivity());

        if (timer == null) {
            MainFragment.timer = new Timer();
            MainFragment.timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    //LocalConnector.dropDatabase();
                    Log.d("LocalChallenge", "updating view");
                    _handler.sendMessage(new Message());
                }
            }, 0, 500);
        }

        getChallenges();
    }

    /**
     * Method that stops the timer updating the view
     * TODO: maybe this should be implemented on onPause()...
     */
    @Override
    public void onPause() {
        timer.cancel();
        super.onPause();
    }

    @Override
    public void onResume() {
        timer = new Timer();
        MainFragment.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("LocalChallenge", "updating view");
                _handler.sendMessage(new Message());
            }
        }, 0, 3000);
        super.onResume();
    }


    /**
     * Creates and returns the view hierarchy associated with the fragment.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Store", "load view");
        //View view = inflater.inflate(R.layout.fragment_catitem, container, false);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        cardView = (RecyclerView) view.findViewById(R.id.main_cardView);
        layoutManager = new LinearLayoutManager(getActivity());
        cardView.setLayoutManager(layoutManager);
        adapter = new MainWindowAdapter(challengeListItemList, getActivity());
        cardView.setAdapter(adapter);
        ImageButton test = (ImageButton) view.findViewById(R.id.addChallengeFab);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newChallengeIntent;
                newChallengeIntent = new Intent(getActivity(), AddChallenge.class);
                startActivity(newChallengeIntent);
            }
        });
        return view;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

}