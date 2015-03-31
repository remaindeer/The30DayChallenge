package nl.tue.the30daychallenge.addChallenge;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.List;

import nl.tue.the30daychallenge.Globals.Categories;
import nl.tue.the30daychallenge.Globals.MessageBoxes;
import nl.tue.the30daychallenge.R;
import nl.tue.the30daychallenge.data.Category;
import nl.tue.the30daychallenge.data.LocalChallenge;
import nl.tue.the30daychallenge.exception.NoServerConnectionException;
import nl.tue.the30daychallenge.exception.RemoteChallengeNotFoundException;

public class AddChallenge extends ActionBarActivity implements View.OnClickListener {
    private final Activity me = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_challenge);
        CategorySpinnerItem[] categories = getCategories();
        Spinner categorySpinner = (Spinner)findViewById(R.id.create_categorySpinner);
        categorySpinner.setAdapter(new ArrayAdapter(this, R.layout.activity_add_challenge_spinner_item, categories));
        ImageButton test = (ImageButton) findViewById(R.id.doneCheck);
        test.setOnClickListener(this);
    }

    private CategorySpinnerItem[] getCategories() {
        List<Category> categories = Categories.getList();
        CategorySpinnerItem[] toReturn = new CategorySpinnerItem[categories.size()];
        for (int index = 0; index < categories.size(); index++) {
            toReturn[index] = new CategorySpinnerItem(categories.get(index));
        }
        return toReturn;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_challenge, menu);
        // Remove the action bar's shadow
        getSupportActionBar().setElevation(0);
        return true;
    }

    private void addChallenge() {
        try {
            Log.d("AddChallenge", "Adding challenge");
            String title = getChallengeTitle();
            String description = getDescription();
            int categoryID = getCategoryId();
            Boolean toUpload = getUploadState();
            final LocalChallenge challengeToAdd;
            challengeToAdd = new LocalChallenge(title, description, categoryID);
            if (toUpload) {
                try {
                    new AsyncTask() {

                        @Override
                        protected Object doInBackground(Object[] params) {
                            try {
                                challengeToAdd.upload();
                            } catch (NoServerConnectionException e) {
                                MessageBoxes.ShowNetworkError(me);
                            } catch (RemoteChallengeNotFoundException e) {
                                MessageBoxes.ShowOkMessageBox(
                                        "Unexpected error",
                                        "There was a problem in the backend, we have no idea" +
                                                "why this happened :(",
                                        me);
                            }
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (Throwable e) {
                    MessageBoxes.ShowNetworkError(this);
                    Log.d("LocalChallenge", e.toString());
                }
            }
            finish();
        } catch (IllegalArgumentException e) {
            MessageBoxes.ShowOkMessageBox("Empty fields", "All fields have to be filled in.", this);
        }
    }

    private Boolean getUploadState() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.create_uploadToServerCheckBox);
        return checkBox.isChecked();
    }

    private String getChallengeTitle() {
        EditText titleField = (EditText) findViewById(R.id.create_titleField);
        String text = titleField.getText().toString();
        if (text.equals("")) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        return text;
    }

    private int getCategoryId() {
        Spinner categorySpinner = (Spinner) findViewById(R.id.create_categorySpinner);
        CategorySpinnerItem test = (CategorySpinnerItem) categorySpinner.getSelectedItem();
        return test.categoryID;
    }

    private String getDescription() {
        EditText descriptionField = (EditText) findViewById(R.id.create_descriptionField);
        String description = descriptionField.getText().toString();
        if (description.equals("")) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        return description;
    }

    @Override
    public void onClick(View v) {
        addChallenge();
    }

    private class CategorySpinnerItem extends Category {
        public CategorySpinnerItem(Category castFrom) {
            this.title = castFrom.title;
            this.categoryID = castFrom.categoryID;
            this.created_at = castFrom.created_at;
            this.updated_at = castFrom.updated_at;
            this.description = castFrom.description;
        }

        @Override
        public String toString() {
            return this.title;
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.create_addChallenge) {
            addChallenge();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
