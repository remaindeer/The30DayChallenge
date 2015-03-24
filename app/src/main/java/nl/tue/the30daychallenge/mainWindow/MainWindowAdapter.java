package nl.tue.the30daychallenge.mainWindow;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.Challenge;

/**
 * Created by tane on 3/23/15.
 */
public class MainWindowAdapter extends RecyclerView.Adapter<MainWindowAdapter.MainChallengeCard> {
    List<Challenge> challenges;

    public MainWindowAdapter(List<Challenge> data){
        this.challenges = data;
    }

    @Override
    public MainChallengeCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewToReturn = LayoutInflater.from(parent.getContext()).inflate(R.layout.challenge_main_list_item, parent, false);
        MainChallengeCard vh = new MainChallengeCard(viewToReturn);
        return vh;
    }

    @Override
    public void onBindViewHolder(MainChallengeCard holder, int position) {
        MainChallengeCard newHolder = (MainChallengeCard) holder;
        newHolder.titleText.setText(challenges.get(position).title);
        // TODO: add more info
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public static class MainChallengeCard extends RecyclerView.ViewHolder {

        protected TextView titleText;
        protected TextView descriptionText;
        protected TextView downloadsText;
        protected TextView completedAmountText;
        protected CardView card;

        public MainChallengeCard(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.titleTextView);
            descriptionText = (TextView) itemView.findViewById(R.id.descriptionTextView);
            downloadsText = (TextView) itemView.findViewById(R.id.downloadsTextView);
            completedAmountText = (TextView) itemView.findViewById(R.id.completionsTextView);
            card = (CardView) itemView;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return challenges.size();
    }
}