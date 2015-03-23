package nl.tue.the30daychallenge;

import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.tue.the30daychallenge.data.RemoteChallenge;
import nl.tue.the30daychallenge.data.RemoteConnector;
import nl.tue.the30daychallenge.data.RemoteConnector.Filter;
import nl.tue.the30daychallenge.exception.NoServerConnectionException;


public class ChallengeItemFragment extends Fragment implements AbsListView.OnItemClickListener {

    private List challengeListItemList = new ArrayList(); // at the top of your fragment list
    private Filter categoryFilter = null;
    private boolean editorspickes = false;
    private String query = null;
    private int page = 0;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    public ChallengeItemFragment(int category){
        categoryFilter = new RemoteConnector.CategoryFilter(category);
        getChallenges();
    }

    public ChallengeItemFragment(boolean editorspickes){
        this.editorspickes = editorspickes;
        getChallenges();
    }
    public ChallengeItemFragment(String query){
        this.query = query;
        getChallenges();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChallengeItemFragment() {
        getChallenges();
    }

    public void getChallenges(){
        GetChallengesFromRemote get = new GetChallengesFromRemote();
        get.execute();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        challengeListItemList = new ArrayList();
        challengeListItemList.add(new ChallengeListItem("Challenge", R.mipmap.category_icon_chores));
        challengeListItemList.add(new ChallengeListItem("Creativity", R.mipmap.category_icon_creativity));
        challengeListItemList.add(new ChallengeListItem("Finance", R.mipmap.category_icon_finance));
        challengeListItemList.add(new ChallengeListItem("Fitness", R.mipmap.category_icon_fitness));
        challengeListItemList.add(new ChallengeListItem("Funny", R.mipmap.category_icon_funny));
        challengeListItemList.add(new ChallengeListItem("Intellectual", R.mipmap.category_icon_intellectual));
        challengeListItemList.add(new ChallengeListItem("Mental health", R.mipmap.category_icon_mental_health));
        challengeListItemList.add(new ChallengeListItem("Outgoing", R.mipmap.category_icon_outgoing));
        challengeListItemList.add(new ChallengeListItem("Relationship", R.mipmap.category_icon_relationship));
        challengeListItemList.add(new ChallengeListItem("Social", R.mipmap.category_icon_social));
        */
        mAdapter = new ChallengeListAdapter(getActivity(), challengeListItemList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catitem, container, false);
        Log.d("Store","load view");
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RemoteChallenge item = (RemoteChallenge) this.challengeListItemList.get(position);
        Toast.makeText(getActivity(), item.title + " Clicked!"
                , Toast.LENGTH_SHORT).show();
    }
    public class GetChallengesFromRemote extends AsyncTask<Void, Void, List<RemoteChallenge>> {

        @Override
        protected List<RemoteChallenge> doInBackground(Void... params) {
            try {
                ArrayList filters = new ArrayList();
                if(categoryFilter!=null){filters.add(categoryFilter);}
                if(query!=null){filters.add(new RemoteConnector.SearchFilter(query));}
                if(editorspickes){filters.add(new RemoteConnector.EditorsPicksFilter());}
                return RemoteConnector.getChallenges((Filter[])filters.toArray(new Filter[filters.size()]));
            } catch (NoServerConnectionException e) {
                Log.d("getChallenges", "NoServerConnectionException");
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<RemoteChallenge> challenges) {
            if(challenges!=null) {
                for (RemoteChallenge challenge : challenges) {
                    Log.d("challenges", challenge.description);
                    challengeListItemList.add(challenge);
                }
            }
            ((ChallengeListAdapter)mAdapter).notifyDataSetChanged();
        }
    }

}
