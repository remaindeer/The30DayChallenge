package nl.tue.the30daychallenge.mainWindow;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import nl.tue.the30daychallenge.MainActivity;
import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.Share;
import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.details.DetailsActivity;
import nl.tue.the30daychallenge.exception.ChallengeAlreadyCheckedException;
import nl.tue.the30daychallenge.exception.ChallengeCompletedException;
import nl.tue.the30daychallenge.exception.ChallengeFailedException;

public class MainWindowAdapter extends RecyclerView.Adapter<MainWindowAdapter.MainChallengeCard> {
    public Activity mainactivity;
    List<LocalChallenge> challenges;

    public MainWindowAdapter(List<LocalChallenge> data, Activity activity) {
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
        LocalChallenge local = challenges.get(position);
        MainChallengeCard newHolder = (MainChallengeCard) holder;
        newHolder.titleText.setText(local.title);
        newHolder.descriptionText.setText(local.description);
        newHolder.challenge = local;


        Long startDate = local.startDate.getTime();
        Long today = new Timestamp(Calendar.getInstance().getTime().getTime()).getTime();

        Long delta = today - startDate;
        Long amountOfMillisecondsInADay = Long.valueOf(1000 * 3600 * 24);
        Long amountOfDays = (long) ((float) delta / amountOfMillisecondsInADay);
        //  Long amountOfDays = delta / amountOfMillisecondsInADay;

        newHolder.startDateText.setText(
                String.format("Challenge started %d days ago",
                        (int) Math.floor((double) amountOfDays))
        );
        newHolder.checkAndSetColor();
        // TODO: add more info
    }

    public void setChallenges(List<LocalChallenge> challenges) {
        this.challenges = challenges;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return challenges.size();
    }

    public class MainChallengeCard extends RecyclerView.ViewHolder {

        public LocalChallenge challenge;
        protected ImageButton optionsButton;
        protected TextView titleText;
        protected TextView descriptionText;
        protected TextView downloadsText;
        protected TextView completedAmountText;
        protected TextView startDateText;
        protected CardView card;
        protected ImageButton checkedButton;
        protected LinearLayout cardColor;

        public MainChallengeCard(View itemView, final Activity activity) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.titleTextView);
            descriptionText = (TextView) itemView.findViewById(R.id.descriptionTextView);
            downloadsText = (TextView) itemView.findViewById(R.id.downloadsTextView);
            completedAmountText = (TextView) itemView.findViewById(R.id.completionsTextView);
            checkedButton = (ImageButton) itemView.findViewById(R.id.checkbutton);
            cardColor = (LinearLayout) itemView.findViewById(R.id.cardColor);
            optionsButton = (ImageButton) itemView.findViewById(R.id.optionsbutton);

            checkedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        challenge.check();
                    } catch (ChallengeCompletedException e) {
                        ChallengeCompletedFragment completedDialog = new ChallengeCompletedFragment();
                        completedDialog.challengeFragment = challenge;
                        completedDialog.show(((Activity) v.getContext()).getFragmentManager(), "");
                    } catch (ChallengeFailedException e) {
                        FailedChallengeResetFragment resetDialog = new FailedChallengeResetFragment();
                        resetDialog.challengeFragment = challenge;
                        resetDialog.show(((Activity) v.getContext()).getFragmentManager(), "");
                    } catch (ChallengeAlreadyCheckedException e) {
                        ChallengeAlreadyCheckedFragment checkedDialog = new ChallengeAlreadyCheckedFragment();
                        checkedDialog.show(((Activity) v.getContext()).getFragmentManager(), "");
                    }
                    checkAndSetColor();
                }
            });

            optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(activity, v);

                    // This activity implements OnMenuItemClickListener
                    popup.inflate(R.menu.menu_main_challenge);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Log.d("Options", "options clicked" + ((LocalChallenge) challenge).toString());
                            switch (menuItem.getItemId()) {
                                case R.id.action_openDetails:
                                    Intent TestIntent = new Intent(activity, DetailsActivity.class);
                                    TestIntent.putExtra("id", ((LocalChallenge) challenge).localID);
                                    TestIntent.putExtra("isLocal", true);
                                    activity.startActivity(TestIntent);
                                    break;
                                case R.id.action_remove:
                                    ((LocalChallenge) challenge).delete();
                                    break;
                                case R.id.action_share:
                                    boolean isFailed = ((LocalChallenge) challenge).isFailed();
                                    boolean isCompleted = ((LocalChallenge) challenge).isCompleted();
                                    Share.ACTION action = Share.ACTION.COMPLETE;
                                    if (isCompleted) {
                                        action = Share.ACTION.COMPLETE;
                                    } else {
                                        if (isFailed) {
                                            action = Share.ACTION.FAIL;
                                        } else {
                                            action = Share.ACTION.DO;
                                        }
                                    }
                                    Log.d("Share", "Action: " + action);
                                    Log.d("Share", "Title: " + challenge.title);
                                    MainActivity.share.share(activity, action, challenge.title);
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

        // checks the status of the challenge and colors the background appropriately
        public void checkAndSetColor() {
            Resources res = itemView.getResources();
            final ImageButton image = (ImageButton) itemView.findViewById(R.id.checkbutton);

            if (challenge.isCompleted()) {
                int newColor = res.getColor(R.color.gold);
                cardColor.setBackgroundColor(newColor);
                image.setImageResource(R.drawable.ic_action_star_rate_grey);
                image.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
            } else if (challenge.isAlreadyCheckedToday()) {
                int newColor = res.getColor(R.color.green);
                cardColor.setBackgroundColor(newColor);
                image.setImageResource(R.drawable.ic_action_done_grey);
                image.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
            } else if (challenge.canCheck()) {
                int newColor = res.getColor(R.color.orange);
                cardColor.setBackgroundColor(newColor);
                image.setImageResource(R.drawable.ic_action_done_grey);
                image.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
            } else if (challenge.isFailed()) {
                int newColor = res.getColor(R.color.red);
                cardColor.setBackgroundColor(newColor);
                image.setImageResource(R.drawable.ic_action_close_grey);
                image.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }
}