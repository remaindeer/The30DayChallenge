package nl.tue.the30daychallenge.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nl.tue.the30daychallenge.exception.ChallengeAlreadyCheckedException;
import nl.tue.the30daychallenge.exception.ChallengeFailedException;
import nl.tue.the30daychallenge.exception.NoServerConnectionException;
import nl.tue.the30daychallenge.exception.RemoteChallengeNotFoundException;

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
    public boolean shouldBeUploaded = false;
    public boolean inSync = false;

    public void save() {
        SQLiteDatabase db = LocalConnector.db;
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("isCompleted", isCompleted);
        values.put("hasLiked", hasLiked);
        values.put("amountOfTimesFailed", amountOfTimesFailed);
        values.put("isUploaded", isUploaded);
        values.put("inSync", inSync);
        values.put("shouldBeUploaded", shouldBeUploaded);
        values.put("lastChecked", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastChecked));
        values.put("startDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate));
        if (localID == -1) {
            // new record
            this.localID = (int) db.insert("LocalChallenge", null, values);
        } else {
            // update: existing record
            db.update("LocalChallenge", values, "localID = ?", new String[]{"" + localID});
        }
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
        this.inSync = (cursor.getInt(cursor.getColumnIndexOrThrow("inSync")) == 1);
        this.shouldBeUploaded = (cursor.getInt(cursor.getColumnIndexOrThrow("shouldBeUploaded")) == 1);
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
        save();
    }

    public LocalChallenge(int localID) {
        this.load(localID);
    }

    public static void create() {
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
                + "inSync INTEGER, "
                + "shouldBeUploaded INTEGER, "
                + "remoteChallengeID INTEGER)");
    }

    public static void syncAll() throws NoServerConnectionException, RemoteChallengeNotFoundException {
        SQLiteDatabase db = LocalConnector.db;
        Cursor cursor = db.query("LocalChallenge", new String[]{"*"}, "inSync = 0", null, null, null, null);
        cursor.moveToFirst();
        do {
            int localID = cursor.getInt(cursor.getColumnIndexOrThrow("localID"));
            new LocalChallenge(localID).sync();
        } while (cursor.moveToNext());
    }

    public void sync() throws NoServerConnectionException, RemoteChallengeNotFoundException {
        if (!inSync) {
            if (this.remoteChallengeID == -1 && this.shouldBeUploaded) {
                // not in sync
                this.remoteChallenge = RemoteConnector.addChallenge(categoryID, title, description);
                if (this.remoteChallenge != null) {
                    this.remoteChallengeID = this.remoteChallenge.challengeID;
                    this.save();
                }
            }

            loadRemoteChallenge(this.remoteChallengeID);

            RemoteConnector.likeChallenge(this.remoteChallengeID, this.hasLiked);
            this.save();

            if (this.isCompleted) {
                RemoteConnector.completeChallenge(this.remoteChallengeID);
                this.save();
            }

            RemoteConnector.downloadChallenge(this.remoteChallengeID);
            this.save();

            this.inSync = true;
            this.save();
        }
    }

    public void loadRemoteChallenge(int remoteChallengeID) throws NoServerConnectionException, RemoteChallengeNotFoundException {
        RemoteChallenge result = RemoteConnector.getChallenge(remoteChallengeID);
    }

    public void setLike(boolean hasLiked) throws NoServerConnectionException, RemoteChallengeNotFoundException {
        this.hasLiked = hasLiked;
        this.inSync = false;
        this.save();
        sync();
    }

    public void setCompleted() throws NoServerConnectionException, RemoteChallengeNotFoundException {
        this.isCompleted = true;
        this.inSync = false;
        this.save();
        sync();
    }

    public void setDownloaded() throws NoServerConnectionException, RemoteChallengeNotFoundException {
        this.inSync = false;
        this.save();
        sync();
    }

    public void upload() throws NoServerConnectionException, RemoteChallengeNotFoundException {
        this.shouldBeUploaded = true;
        this.inSync = false;
        this.save();
        sync();
    }

    public boolean isAlreadyCheckedToday() {
        Midnight m = new Midnight(12, 30);
        long deltaMillis = m.getLastMidnight().getTime() - lastChecked.getTime();
        if (deltaMillis < 24 * 60 * 60 * 1000 && deltaMillis > 0) {
            // the valid case :)
        } else {
            if (deltaMillis <= 0) {
                // already checked
                return true;
            }
        }
        return false;
    }

    public void check() throws ChallengeFailedException, ChallengeAlreadyCheckedException {
        Date nowDate = Calendar.getInstance().getTime();
        Timestamp now = new Timestamp(nowDate.getTime());
        Midnight m = new Midnight(12, 30);
        long deltaMillis = m.getLastMidnight().getTime() - lastChecked.getTime();
        if (deltaMillis < 24 * 60 * 60 * 1000 && deltaMillis > 0) {
            // last checked was within 24 hours of now (valid)
            this.lastChecked = now;
            this.save();
        } else {
            if (deltaMillis > 0) {
                // too late
                throw new ChallengeFailedException();
            } else {
                // already checked
                throw new ChallengeAlreadyCheckedException();
            }
        }
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
