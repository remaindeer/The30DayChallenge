package nl.tue.the30daychallenge;

/**
 * Created by kevin on 3/10/15.
 */
public class Category {

    public int categoryID;
    public String title;
    public String description;
    public String created_at;
    public String updated_at;

    @Override
    public String toString() {
        return "Category{" +
                "categoryID=" + categoryID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
