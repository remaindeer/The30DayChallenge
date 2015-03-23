package nl.tue.the30daychallenge.Globals;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.Category;
import nl.tue.the30daychallenge.exception.NoServerConnectionException;

/**
 * Created by tane on 3/23/15.
 */
public class Categories {
    private static List<Category> categoryList;
    public static HashMap<String, Integer> icons = new HashMap<String, Integer>();

    public static List<Category> getList() {
        if (categoryList == null || categoryList.size() == 0) {
            initList();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return categoryList;
    }

    private static void initList() {
        categoryList = new ArrayList<Category>();
        icons.put("Fitness", R.mipmap.category_icon_fitness);
        icons.put("Diets", R.mipmap.category_icon_fitness);
        icons.put("Mental health", R.mipmap.category_icon_mental_health);
        icons.put("Chores", R.mipmap.category_icon_chores);
        icons.put("Intellectual", R.mipmap.category_icon_intellectual);
        icons.put("Relationships", R.mipmap.category_icon_relationship);
        icons.put("Career", R.mipmap.category_icon_relationship);
        icons.put("Party", R.mipmap.category_icon_relationship);
        icons.put("Finance", R.mipmap.category_icon_finance);
        icons.put("Funny", R.mipmap.category_icon_funny);
        icons.put("Other", R.mipmap.category_icon_social);

        //not yet present on server:
        icons.put("Creativity", R.mipmap.category_icon_creativity);
        icons.put("Outgoing", R.mipmap.category_icon_outgoing);
        icons.put("Social", R.mipmap.category_icon_social);
        // TODO: if anyone knows how to do this, please repair it for me :( greetings Tane
        //getCategoriesFromOnline();
        categoryList.add(new Category() {{
            categoryID = 1;
            title = "Fitness";
        }});
        categoryList.add(new Category() {{
            categoryID = 2;
            title = "Diets";
        }});
        categoryList.add(new Category() {{
            categoryID = 3;
            title = "Mental health";
        }});
        categoryList.add(new Category() {{
            categoryID = 4;
            title = "Chores";
        }});
        categoryList.add(new Category() {{
            categoryID = 5;
            title = "Intellectual";
        }});
        categoryList.add(new Category() {{
            categoryID = 6;
            title = "Relationships";
        }});
        categoryList.add(new Category() {{
            categoryID = 7;
            title = "Career";
        }});
        categoryList.add(new Category() {{
            categoryID = 8;
            title = "Party";
        }});
        categoryList.add(new Category() {{
            categoryID = 9;
            title = "Finance";
        }});
        categoryList.add(new Category() {{
            categoryID = 10;
            title = "Funny";
        }});
        categoryList.add(new Category() {{
            categoryID = 11;
            title = "Other";
        }});
        categoryList.add(new Category() {{
            categoryID = 0;
            title = "Creativity";
        }});
        categoryList.add(new Category() {{
            categoryID = 0;
            title = "Outgoing";
        }});
        categoryList.add(new Category() {{
            categoryID = 0;
            title = "Social";
        }});
    }

    private static void getCategoriesFromOnline() {
        GetCategoriesFromRemote test = new GetCategoriesFromRemote(categoryList);
        test.execute();
        try {
            test.get(500, TimeUnit.MILLISECONDS);
        } catch (Exception e){
             categoryList.add(new Category() {{
                    categoryID = 1;
                    title = "Fitness";
                }});
             categoryList.add(new Category() {{
                categoryID = 2;
                title = "Diets";
             }});
             categoryList.add(new Category() {{
                categoryID = 3;
                title = "Mental health";
            }});
             categoryList.add(new Category() {{
                categoryID = 4;
                title = "Chores";
            }});
             categoryList.add(new Category() {{
                categoryID = 5;
                title = "Intellectual";
            }});
             categoryList.add(new Category() {{
                categoryID = 6;
                title = "Relationships";
            }});
             categoryList.add(new Category() {{
                categoryID = 7;
                title = "Career";
            }});
             categoryList.add(new Category() {{
                categoryID = 8;
                title = "Party";
            }});
             categoryList.add(new Category() {{
                categoryID = 9;
                title = "Finance";
            }});
             categoryList.add(new Category() {{
                categoryID = 10;
                title = "Funny";
            }});
             categoryList.add(new Category() {{
                categoryID = 11;
                title = "Other";
            }});
             categoryList.add(new Category() {{
                categoryID = 0;
                title = "Creativity";
            }});
             categoryList.add(new Category() {{
                categoryID = 0;
                title = "Outgoing";
            }});
             categoryList.add(new Category() {{
                    categoryID = 0;
                    title = "Social";
                }});
        }
    }


    private static class GetCategoriesFromRemote extends AsyncTask<Void, Void, Void> {

        public GetCategoriesFromRemote(List<Category> list) {
            this.list = list;
        }

        private List<Category> list;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.d("Categories", "Checking for categories");
                list = nl.tue.the30daychallenge.data.RemoteConnector.getCategories();
            } catch (NoServerConnectionException ex) {
                Log.d("Categories", "NoServerException, using defaults");
            }
            return null;
        }
    }
}

