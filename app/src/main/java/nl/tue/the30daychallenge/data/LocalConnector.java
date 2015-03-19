package nl.tue.the30daychallenge.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import nl.tue.the30daychallenge.exception.ChallengeAlreadyCheckedException;
import nl.tue.the30daychallenge.exception.ChallengeFailedException;

/**
 * An object which is a gateway to the back-end.
 *
 * Created by Daan and Kevin on 3/10/15.
 */
public class LocalConnector extends SQLiteOpenHelper {

    public static SQLiteDatabase db;

    public LocalConnector(Context context) throws ChallengeAlreadyCheckedException, ChallengeFailedException {
        super(context, "30DayChallenge", null, 1);
        LocalConnector.db = this.getWritableDatabase();
        LocalConnector.db.execSQL("DROP TABLE IF EXISTS LocalChallenge");

        // create databases
        LocalChallenge.create();

        LocalChallenge challenge = new LocalChallenge("title", "description");
        LocalChallenge challenge2 = new LocalChallenge("title2", "description2");
        challenge2.description = "updated description";
        challenge2.save();
        //new LocalChallenge(1).delete();
        //Log.d("Connector", new LocalChallenge(2).toString());
        //Cursor cursor = LocalConnector.db.query("LocalChallenge", new String[]{"*"}, null, null, null, null, "created_at");
        //cursor.moveToFirst();
        //Log.d("Connector", cursor.getString(cursor.getColumnIndexOrThrow("title")));

        challenge2.lastChecked = new Timestamp(0);
        challenge2.save();
        challenge2.check();

        for (Challenge c: LocalConnector.getChallenges()) {
            Log.d("Connector", c.toString());
        }
    }

    /**
     * Retrieve a list of all local challenges.
     *
     * @return a list of all local challenges
     */
    public static List<LocalChallenge> getChallenges() {
        List<LocalChallenge> challenges = new ArrayList<>();
        SQLiteDatabase db = LocalConnector.db;
        Cursor cursor = db.query("LocalChallenge", new String[]{"*"}, null, null, null, null, null);
        cursor.moveToFirst();
        do {
            int localID = cursor.getInt(cursor.getColumnIndexOrThrow("localID"));
            challenges.add(new LocalChallenge(localID));
        } while (cursor.moveToNext());
        return challenges;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
