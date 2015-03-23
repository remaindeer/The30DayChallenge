package nl.tue.the30daychallenge;

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

import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.data.LocalConnector;


public class MainFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.OnScrollListener {

    private List challengeListItemList = new ArrayList(); // at the top of your fragment list
    private int page = 0;
    private int itemspage= 15;

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
    public MainFragment() {


    }

    public void getChallenges(){
        challengeListItemList = LocalConnector.getChallenges();
        ((ChallengeListAdapterLocal)mAdapter).notifyDataSetChanged();
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
        mAdapter = new ChallengeListAdapterLocal(getActivity(), challengeListItemList);

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
        mListView.setOnScrollListener(this);

        return view;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LocalChallenge item = (LocalChallenge) this.challengeListItemList.get(position);
        Toast.makeText(getActivity(), item.title + " Clicked!"
                , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem+visibleItemCount==totalItemCount &&firstVisibleItem > 0 && totalItemCount == (page+1)*itemspage){
            page++;
            getChallenges();
        }
    }
}
