package nl.tue.the30daychallenge;

/**
 * Created by s122552 on 21-3-2015.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nl.tue.the30daychallenge.data.RemoteChallenge;

public class ChallengeListAdapter extends ArrayAdapter {

    private Context context;
    private boolean useList = true;

    public ChallengeListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        TextView titleText;
        TextView descriptionText;
        TextView downloadsText;
        TextView completionsText;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RemoteChallenge item = (RemoteChallenge)getItem(position);
        View viewToUse = null;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            if(useList){
                viewToUse = mInflater.inflate(R.layout.challenge_list_item, null);
            } else {
                viewToUse = mInflater.inflate(R.layout.challenge_grid_item, null);
            }

            holder = new ViewHolder();
            holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
            holder.descriptionText = (TextView)viewToUse.findViewById(R.id.descriptionTextView);
            holder.downloadsText = (TextView)viewToUse.findViewById(R.id.downloadsTextView);
            holder.completionsText = (TextView)viewToUse.findViewById(R.id.completionsTextView);

            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        holder.titleText.setText(item.title);
        holder.descriptionText.setText(item.description);
        holder.downloadsText.setText("Starts: "+item.downloads);
        holder.completionsText.setText("Completions: "+item.completions);


        return viewToUse;
    }
}
