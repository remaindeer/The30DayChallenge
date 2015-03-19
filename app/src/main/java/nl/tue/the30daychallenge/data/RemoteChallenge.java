package nl.tue.the30daychallenge.data;

/**
 * Created by kevin on 3/10/15.
 */
public class RemoteChallenge extends Challenge {

    public int challengeID;
    public int likes;
    public int completions;
    public int downloads;
    public String created_at;
    public String updated_at;

    @Override
    public String toString() {
        return "Challenge{" +
                "challengeID=" + challengeID +
                ", categoryID='" + categoryID + '\'' +
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
