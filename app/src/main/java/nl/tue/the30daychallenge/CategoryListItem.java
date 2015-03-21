package nl.tue.the30daychallenge;

/**
 * Created by s122552 on 21-3-2015.
 */
public class CategoryListItem {

    private String itemTitle;
    private int itemCategory;

    public String getItemTitle() {
        return itemTitle;
    }
    public int getItemCategory() {
        return itemCategory;
    }

    public void setItemTitle(String itemTitle){
        this.itemTitle = itemTitle;
    }

    public CategoryListItem(String title, int category){
        this.itemTitle = title;
        this.itemCategory = category;
    }
}
