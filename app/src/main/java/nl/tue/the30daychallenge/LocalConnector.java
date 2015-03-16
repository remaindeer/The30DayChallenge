package nl.tue.the30daychallenge;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * An object which is a gateway to the back-end.
 *
 * Created by Daan and Kevin on 3/10/15.
 */
public class LocalConnector extends SQLiteOpenHelper {

    public static SQLiteDatabase db;

    public LocalConnector(Context context) {
        super(context, "30DayChallenge", null, 1);
        LocalConnector.db = this.getWritableDatabase();
        LocalConnector.db.execSQL("DROP TABLE LocalChallenge");

        // create databases
        LocalChallenge.create();

        LocalChallenge challenge = new LocalChallenge("title", "description");
        LocalChallenge challenge2 = new LocalChallenge("title2", "description2");
        challenge2.description = "updated description";
        challenge2.save();
        //new LocalChallenge(1).delete();
        Log.d("Connector", new LocalChallenge(1).toString());
        //Cursor cursor = LocalConnector.db.query("LocalChallenge", new String[]{"*"}, null, null, null, null, "created_at");
        //cursor.moveToFirst();
        //Log.d("Connector", cursor.getString(cursor.getColumnIndexOrThrow("title")));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
