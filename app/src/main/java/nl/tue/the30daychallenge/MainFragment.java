package nl.tue.the30daychallenge;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import nl.tue.the30daychallenge.data.LocalConnector;

public class MainFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.OnScrollListener {
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
     * Updates the view after 30 seconds.
     */
    updater testUpdater;
    /**
     * Running challenges we want to show.
     */
    private List challengeListItemList = new ArrayList(); // at the top of your fragment list
    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainFragment() {}

    /**
     * Method that is supposed to fill the view with cards
     */
    public void getChallenges() {
        challengeListItemList = LocalConnector.getChallenges();
        for (Object challenge : challengeListItemList) {
            Log.d("MainFragment", challenge.toString());
        }
        //((ChallengeListAdapterLocal)mAdapter).notifyDataSetChanged();
        mAdapter = new ChallengeListAdapterLocal(getActivity(), challengeListItemList);
    }

    /**
     * Method that starts up the fragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ChallengeListAdapterLocal(getActivity(), challengeListItemList);
        testUpdater = new updater(_handler);
        testUpdater.execute();
    }

    /**
     * Method that stops the timer updating the view
     * TODO: maybe this should be implemented on onPause()...
     */
    @Override
    public void onStop() {
        testUpdater.cancel(true);
        super.onStop();
    }


    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Store", "load view");
        View view = inflater.inflate(R.layout.fragment_catitem, container, false);
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);

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


    /*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LocalChallenge item = (LocalChallenge) this.challengeListItemList.get(position);
        Toast.makeText(getActivity(), item.title + " Clicked!"
                , Toast.LENGTH_SHORT).show();
    }*/

    class updater extends AsyncTask<Void, Void, Void> {

        private final Handler parent;

        public updater(Handler parent) {
            this.parent = parent;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
                Log.d("MainFragment", "updating view");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                parent.sendMessage(new Message());
            }
        }
    }
}
