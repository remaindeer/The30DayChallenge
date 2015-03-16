package nl.tue.the30daychallenge;

/**
 * Created by kevin on 3/12/15.
 */
public class Challenge {

    public String title;
    public String description;
    public int categoryID;

    @Override
    public String toString() {
        return "Challenge{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", categoryID=" + categoryID +
                '}';
    }

}
