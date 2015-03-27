package nl.tue.the30daychallenge.mainWindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.Challenge;
import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.details.DetailsActivity;
import nl.tue.the30daychallenge.data.LocalChallenge;

/**
 * Created by tane on 3/23/15.
 */
public class MainWindowAdapter extends RecyclerView.Adapter<MainWindowAdapter.MainChallengeCard>  {
    List<LocalChallenge> challenges;
    public Activity mainactivity;
    public MainWindowAdapter(List<LocalChallenge> data,Activity activity){
        this.challenges = data;
        this.mainactivity = activity;
    }

    @Override
    public MainChallengeCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewToReturn = LayoutInflater.from(parent.getContext()).inflate(R.layout.challenge_main_list_item, parent, false);
        MainChallengeCard vh = new MainChallengeCard(viewToReturn, mainactivity);
        return vh;
    }

    @Override
    public void onBindViewHolder(MainChallengeCard holder, int position) {
        MainChallengeCard newHolder = (MainChallengeCard) holder;
        newHolder.titleText.setText(challenges.get(position).title);
        newHolder.descriptionText.setText(challenges.get(position).description);
        newHolder.challenge = challenges.get(position);

        // TODO: add more info
    }

    public void setChallenges(List<LocalChallenge> challenges) {
        this.challenges = challenges;
    }



    public class MainChallengeCard extends RecyclerView.ViewHolder {

        protected PopupMenu optionsButton;
        protected TextView titleText;
        protected TextView descriptionText;
        protected TextView downloadsText;
        protected TextView completedAmountText;
        protected TextView startDateText;
        protected CardView card;
        public LocalChallenge challenge;

        public MainChallengeCard(View itemView, final Activity activity) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.titleTextView);
            descriptionText = (TextView) itemView.findViewById(R.id.descriptionTextView);
            downloadsText = (TextView) itemView.findViewById(R.id.downloadsTextView);
            completedAmountText = (TextView) itemView.findViewById(R.id.completionsTextView);
            ((ImageButton)itemView.findViewById(R.id.optionsbutton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(activity, v);

                    // This activity implements OnMenuItemClickListener
                    optionsButton = popup;
                    popup.inflate(R.menu.menu_main_challenge);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Log.d("Options", "options clicked"+((LocalChallenge)challenge).toString());
                            switch(menuItem.getItemId()){
                                case R.id.action_openDetails:
                                    Intent TestIntent = new Intent(activity, DetailsActivity.class);
                                    TestIntent.putExtra("id", ((LocalChallenge) challenge).localID);
                                    TestIntent.putExtra("isLocal", true);
                                    activity.startActivity(TestIntent);
                                    break;
                                case R.id.action_remove:
                                    ((LocalChallenge)challenge).delete();
                                    break;
                                case R.id.action_share:
                                    // @todo SHARE
                                    break;
                            }

                            return true;
                        }
                    });
                    popup.show();
                }
            });

            startDateText = (TextView) itemView.findViewById(R.id.startDateTextView);
            card = (CardView) itemView;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return challenges.size();
    }
}