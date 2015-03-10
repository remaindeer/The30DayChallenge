package nl.tue.the30daychallenge;

/**
 * Created by kevin on 3/10/15.
 */
public class Challenge {

    public int challengeID;
    public String title;
    public String description;
    public int likes;
    public int completions;
    public int downloads;
    public String created_at;
    public String updated_at;

    @Override
    public String toString() {
        return "Challenge{" +
                "challengeID=" + challengeID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", likes=" + likes +
                ", completions=" + completions +
                ", downloads=" + downloads +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }

}
