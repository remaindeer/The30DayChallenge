package nl.tue.the30daychallenge;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kevin on 3/12/15.
 */
public class LocalChallenge extends Challenge {

    public int localID = -1;
    public int remoteChallengeID = -1;
    public RemoteChallenge remoteChallenge = null;
    public boolean isCompleted = false;
    public boolean hasLiked = false;
    public int highscore = 0;
    public int amountOfTimesFailed = 0;
    public boolean isUploaded = false;
    public Timestamp startDate;
    public Timestamp lastChecked;

    public void save() {
        int i = 0;
        SQLiteDatabase db = LocalConnector.db;
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("isCompleted", isCompleted);
        values.put("hasLiked", hasLiked);
        values.put("amountOfTimesFailed", amountOfTimesFailed);
        values.put("isUploaded", isUploaded);
        values.put("lastChecked", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastChecked));
        values.put("startDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate));
        if (localID == -1) {
            // new record
            this.localID = (int) db.insert("LocalChallenge", null, values);
            Log.d("Connector", "" + this.localID);
            check();
        } else {
            // update: existing record
        }
    }

    public void check() {

    }

    public boolean load(int localID) {
        SQLiteDatabase db = LocalConnector.db;
        Cursor cursor = db.query("LocalChallenge", new String[]{"*"}, "localID = ?", new String[]{"" + localID}, null, null, null);
        if (cursor.getCount() != 1) return false;
        cursor.moveToFirst();
        this.localID = cursor.getInt(cursor.getColumnIndexOrThrow("localID"));
        this.remoteChallengeID = cursor.getInt(cursor.getColumnIndexOrThrow("remoteChallengeID"));
        this.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        this.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        this.categoryID = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
        this.isCompleted = (cursor.getInt(cursor.getColumnIndexOrThrow("isCompleted")) == 1);
        this.hasLiked = (cursor.getInt(cursor.getColumnIndexOrThrow("hasLiked")) == 1);
        this.isUploaded = (cursor.getInt(cursor.getColumnIndexOrThrow("isUploaded")) == 1);
        this.amountOfTimesFailed = cursor.getInt(cursor.getColumnIndexOrThrow("amountOfTimesFailed"));
        this.startDate = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("startDate")));
        this.lastChecked = Timestamp.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("lastChecked")));
        return true;
    }

    public void delete() {
        SQLiteDatabase db = LocalConnector.db;
        db.execSQL("DELETE FROM LocalChallenge WHERE localID = ?", new String[]{"" + this.localID});
    }

    public LocalChallenge(String title, String description) {
        this.title = title;
        this.description = description;
        Date now = Calendar.getInstance().getTime();
        this.startDate = new Timestamp(now.getTime());
        this.lastChecked = this.startDate;
        Log.d("Connector", "Coole constructor");
        save();
    }

    public LocalChallenge(int localID) {
        this.load(localID);
    }

    public static void create() {
        Log.d("Connector", "createDatabases");
        SQLiteDatabase db = LocalConnector.db;
        db.execSQL("CREATE TABLE IF NOT EXISTS LocalChallenge ("
                + "localID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT, "
                + "description TEXT, "
                + "categoryID TEXT, "
                + "created_at TEXT, "
                + "isCompleted TEXT, "
                + "hasLiked TEXT, "
                + "highscore INTEGER, "
                + "amountOfTimesFailed INTEGER, "
                + "isUploaded TEXT, "
                + "startDate TEXT, "
                + "lastChecked TEXT, "
                + "remoteChallengeID INTEGER)");
    }

    public void loadRemoteChallenge(int remoteChallengeID) throws NoServerConnectionException, RemoteChallengeNotFoundException {
        RemoteChallenge result = RemoteConnector.getChallenge(remoteChallengeID);
        Log.d("Connector", result.toString());
    }

    public void setLike(boolean hasLiked) {
        this.hasLiked = hasLiked;
        this.save();
    }

    @Override
    public String toString() {
        return "LocalChallenge{" +
                "localID=" + localID +
                ", remoteChallengeID=" + remoteChallengeID +
                ", isCompleted=" + isCompleted +
                ", hasLiked=" + hasLiked +
                ", highscore=" + highscore +
                ", amountOfTimesFailed=" + amountOfTimesFailed +
                ", isUploaded=" + isUploaded +
                ", startDate=" + startDate +
                ", lastChecked=" + lastChecked +
                ", superClass=" + super.toString() +
                '}';
    }

}
