package nl.tue.the30daychallenge.mainWindow;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.Challenge;
import nl.tue.the30daychallenge.data.LocalChallenge;

/**
 * Created by tane on 3/23/15.
 */
public class MainWindowAdapter extends RecyclerView.Adapter<MainWindowAdapter.MainChallengeCard> {
    List<LocalChallenge> challenges;
    String oldDate;
    String currentDate;
    long difference;

    public MainWindowAdapter(List<LocalChallenge> data){
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
        Date now = Calendar.getInstance().getTime();
        currentDate = now.toString();
        oldDate = challenges.get(position).startDate.toString();
        //difference = currentDate - oldDate;


        MainChallengeCard newHolder = (MainChallengeCard) holder;
        newHolder.titleText.setText(challenges.get(position).title);
        newHolder.descriptionText.setText(challenges.get(position).description);
        newHolder.startDateText.setText("Started " + currentDate + " days ago.");

        // TODO: add more info
    }

    public void setChallenges(List<LocalChallenge> challenges) {
        this.challenges = challenges;
    }

    public static class MainChallengeCard extends RecyclerView.ViewHolder {

        protected TextView titleText;
        protected TextView descriptionText;
        protected TextView downloadsText;
        protected TextView completedAmountText;
        protected TextView startDateText;
        protected CardView card;

        public MainChallengeCard(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.titleTextView);
            descriptionText = (TextView) itemView.findViewById(R.id.descriptionTextView);
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