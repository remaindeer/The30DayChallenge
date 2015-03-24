package nl.tue.the30daychallenge.library;

/**
 * Created by s122552 on 21-3-2015.
 */
public class CategoryListItem {

    private String itemTitle;
    private int itemCategory;
    private int categoryID;

    public String getItemTitle() {
        return itemTitle;
    }
    public int getItemCategory() {
        return itemCategory;
    }
    public int getCategoryID() {return categoryID;}

    public void setItemTitle(String itemTitle){
        this.itemTitle = itemTitle;
    }

    public CategoryListItem(String title,int id, int category){
        this.itemTitle = title;
        this.itemCategory = category;
        this.categoryID = id;
    }
}
