package nl.tue.the30daychallenge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class CatItemFragment extends Fragment implements AbsListView.OnItemClickListener {

    private List categoryListItemList; // at the top of your fragment list

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
    public CatItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryListItemList = new ArrayList();
        categoryListItemList.add(new CategoryListItem("Chores", R.mipmap.category_icon_chores));
        categoryListItemList.add(new CategoryListItem("Creativity", R.mipmap.category_icon_creativity));
        categoryListItemList.add(new CategoryListItem("Finance", R.mipmap.category_icon_finance));
        categoryListItemList.add(new CategoryListItem("Fitness", R.mipmap.category_icon_fitness));
        categoryListItemList.add(new CategoryListItem("Funny", R.mipmap.category_icon_funny));
        categoryListItemList.add(new CategoryListItem("Intellectual", R.mipmap.category_icon_intellectual));
        categoryListItemList.add(new CategoryListItem("Mental health", R.mipmap.category_icon_mental_health));
        categoryListItemList.add(new CategoryListItem("Outgoing", R.mipmap.category_icon_outgoing));
        categoryListItemList.add(new CategoryListItem("Relationship", R.mipmap.category_icon_relationship));
        categoryListItemList.add(new CategoryListItem("Social", R.mipmap.category_icon_social));

        mAdapter = new CategoryListAdapter(getActivity(), categoryListItemList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catitem, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CategoryListItem item = (CategoryListItem) this.categoryListItemList.get(position);
        Toast.makeText(getActivity(), item.getItemTitle() + " Clicked!"
                , Toast.LENGTH_SHORT).show();
    }

}
