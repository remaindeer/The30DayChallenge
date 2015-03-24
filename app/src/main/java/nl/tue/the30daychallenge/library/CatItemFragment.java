package nl.tue.the30daychallenge.library;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import nl.tue.the30daychallenge.Globals.Categories;
import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.Category;


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

        List<Category> test = Categories.getList();
        Category category1 = new Category();

        for (Category category : Categories.getList()) {
            category1 = category;
            CategoryListItem Item = new CategoryListItem(
                    category.title,
                    Categories.getList().indexOf(category) + 1,
                    Categories.icons.get(category.title)
            );
            categoryListItemList.add(Item);
        }

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
        Intent intent = new Intent(getActivity(), ChallengesActivity.class);
        Bundle b = new Bundle();
        b.putInt("Category", item.getCategoryID());
        intent.putExtras(b);
        startActivity(intent);

    }

}
