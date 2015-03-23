package nl.tue.the30daychallenge.addChallenge;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import nl.tue.the30daychallenge.Globals.Categories;
import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.Category;
import nl.tue.the30daychallenge.data.LocalChallenge;

public class AddChallenge extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_challenge);
        CategorySpinnerItem[] categories = getCategories();
        Spinner categorySpinner = (Spinner)findViewById(R.id.create_categorySpinner);
        categorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories));
    }

    private CategorySpinnerItem[] getCategories(){
        List<Category> categories = Categories.getList();
        CategorySpinnerItem[] toReturn = new CategorySpinnerItem[categories.size()];
        for(int index = 0; index<categories.size(); index++){
            toReturn[index] = new CategorySpinnerItem(categories.get(index));
        }
        return toReturn;
    }

    private class CategorySpinnerItem extends Category{
        public CategorySpinnerItem(Category castFrom){
            this.title = castFrom.title;
            this.categoryID = castFrom.categoryID;
            this.created_at = castFrom.created_at;
            this.updated_at = castFrom.updated_at;
            this.description = castFrom.description;
        }
       @Override
        public String toString(){
           return this.title;
       }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_challenge, menu);
        // Remove the action bar's shadow
        getSupportActionBar().setElevation(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.create_addChallenge){
            try{
                String title = getChallengeTitle();
                String description = getDescription();
                int categoryID = getCategoryId();
                Boolean toUpload = getUploadState();
                LocalChallenge challengeToAdd = new LocalChallenge(title, description, categoryID);
                if(toUpload){
                    try {
                        challengeToAdd.upload();
                    } catch (Throwable e){
                        e.printStackTrace();
                    }
                }
                Log.d("AddChallenge", "Adding challenge");
            } catch (IllegalArgumentException e){
                final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                dlgAlert.setMessage(e.getMessage());
                dlgAlert.setTitle("YOU SUCK!");
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private Boolean getUploadState() {
        CheckBox checkBox = (CheckBox)findViewById(R.id.create_uploadToServerCheckBox);
        return checkBox.isChecked();
    }

    private String getChallengeTitle() {
        EditText titleField = (EditText)findViewById(R.id.create_titleField);
        String text = titleField.getText().toString();
        if(text.equals("")){
            throw new IllegalArgumentException("Title can't be empty");
        }
        return text;
    }

    private int getCategoryId() {
        Spinner categorySpinner = (Spinner)findViewById(R.id.create_categorySpinner);
        CategorySpinnerItem test = (CategorySpinnerItem)categorySpinner.getSelectedItem();
        return test.categoryID;
    }

    private String getDescription() {
        EditText descriptionField = (EditText)findViewById(R.id.create_descriptionField);
        String description = descriptionField.getText().toString();
        if(description.equals("")){
            throw new IllegalArgumentException("Description can't be empty");
        }
        return description;
    }
}
