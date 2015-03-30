package nl.tue.the30daychallenge.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
    public int highscore = 0;
    public int checkCount = 0;
    public int amountOfTimesFailed = 0;
    public boolean isUploaded = false;
    public Timestamp startDate;
    public Timestamp lastChecked;
    public boolean shouldBeUploaded = false;
    public boolean inSync = false;
    private RemoteChallenge remoteChallenge = null;
    public boolean isCompleted = false;
    private boolean hasLiked = false;

    public LocalChallenge(String title, String description, int categoryID) {
        createChallenge(title, description, categoryID);
    }

    public LocalChallenge(int localID) {
        this.load(localID);
    }

    public LocalChallenge(RemoteChallenge remoteChallenge) throws NoServerConnectionException, RemoteChallengeNotFoundException {
        createChallenge(remoteChallenge.title, remoteChallenge.description, remoteChallenge.categoryID);
        remoteChallengeID = remoteChallenge.challengeID;
        this.remoteChallenge = remoteChallenge;
        this.isUploaded = true;
        save();
        setDownloaded();
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
                + "checkCount INTEGER, "
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
        if (cursor.getCount() == 0) return;
        cursor.moveToFirst();
        do {
            int localID = cursor.getInt(cursor.getColumnIndexOrThrow("localID"));
            new LocalChallenge(localID).sync();
        } while (cursor.moveToNext());
        cursor.close();
    }

    public void save() {
        SQLiteDatabase db = LocalConnector.db;
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("categoryID",categoryID);
        values.put("isCompleted", isCompleted);
        values.put("hasLiked", hasLiked);
        values.put("highscore", highscore);
        values.put("checkCount", checkCount);
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
        cursor.close();
        return true;
    }

    public RemoteChallenge getRemoteChallenge() throws NoServerConnectionException, RemoteChallengeNotFoundException {
        loadRemoteChallenge(this.remoteChallengeID);
        return this.remoteChallenge;
    }

    public void delete() {
        SQLiteDatabase db = LocalConnector.db;
        db.execSQL("DELETE FROM LocalChallenge WHERE localID = ?", new String[]{"" + this.localID});
    }

    public void createChallenge(String title, String description, int categoryID) {
        this.title = title;
        this.description = description;
        this.categoryID = categoryID;
        Date now = Calendar.getInstance().getTime();
        this.startDate = new Timestamp(now.getTime());
        this.lastChecked = new Timestamp(getMidnight().getLastMidnight().getTime() - 1000);
        Log.d("LocalChallenge", "New local challenge: " + toString());
        save();
    }

    public void sync() throws NoServerConnectionException, RemoteChallengeNotFoundException {
        if (!inSync) {
            if ((this.remoteChallengeID == -1 || this.remoteChallengeID == 0 ) && this.shouldBeUploaded) {
                // not in sync
                this.remoteChallenge = RemoteConnector.addChallenge(categoryID, title, description);
                if (this.remoteChallenge != null) {
                    this.remoteChallengeID = this.remoteChallenge.challengeID;
                    this.isUploaded = true;
                    this.save();
                }
            }

            loadRemoteChallenge(this.remoteChallengeID);

            RemoteConnector.createAttempt(this.remoteChallengeID);
            this.save();

            RemoteConnector.likeChallenge(this.remoteChallengeID, this.hasLiked);
            this.save();

            if (this.isCompleted) {
                RemoteConnector.completeChallenge(this.remoteChallengeID);
                this.save();
            }

            this.inSync = true;
            this.save();
        }
    }

    public void loadRemoteChallenge(int remoteChallengeID) throws NoServerConnectionException, RemoteChallengeNotFoundException {
        if (this.remoteChallengeID != -1 && this.remoteChallenge == null) {
            this.remoteChallengeID = remoteChallengeID;
            this.remoteChallenge = RemoteConnector.getChallenge(remoteChallengeID);
        }
    }

    public boolean isLiked() {
        return this.hasLiked;
    }
    public boolean isCompleted() {return this.isCompleted;}

    public void setLike(boolean hasLiked) throws NoServerConnectionException, RemoteChallengeNotFoundException {
        this.hasLiked = hasLiked;
        this.inSync = false;
        this.save();
        sync();
    }

    public void forceCompleted() {
        this.isCompleted = true;
        this.save();
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

    public Midnight getMidnight() {
        return new Midnight();
    }

    public long calculateDeltaCheckTime() {
        return getMidnight().getLastMidnight().getTime() - lastChecked.getTime();
    }

    public boolean isAlreadyCheckedToday() {
        if (calculateDeltaCheckTime() <= 0) return true;
        return false;
    }

    public boolean isFailedYesterday() {
        Log.d("Connector", "" + calculateDeltaCheckTime());
        if (isFailed() && calculateDeltaCheckTime() <= (24 + 24) * 60 * 60 * 1000) return true;
        return false;
    }

    public boolean isFailed() {
        if (calculateDeltaCheckTime() > 24 * 60 * 60 * 1000) return true;
        return false;
    }

    public boolean canCheck() {
        long deltaMillis = calculateDeltaCheckTime();
        if (deltaMillis < 24 * 60 * 60 * 1000 && deltaMillis > 0) return true;
        return false;
    }

    public void check() throws ChallengeFailedException, ChallengeAlreadyCheckedException {
        Date nowDate = Calendar.getInstance().getTime();
        Timestamp now = new Timestamp(nowDate.getTime());
        if (canCheck()) {
            this.lastChecked = now;
            checkCount++;
            highscore = Math.max(highscore,checkCount);
            if(checkCount == 30){
                try {
                    setCompleted();
                } catch (NoServerConnectionException e) {
                    Log.d("Check",e.toString());
                } catch (RemoteChallengeNotFoundException e) {
                    Log.d("Check",e.toString());
                }
            }
            this.save();
        } else {
            if (isFailed()) throw new ChallengeFailedException();
            if (isAlreadyCheckedToday()) throw new ChallengeAlreadyCheckedException();
            throw new IllegalStateException();
        }
    }

    public void reset(){
        checkCount = 0;
        startDate = new Timestamp(Calendar.getInstance().getTime().getTime());
        lastChecked = new Timestamp(getMidnight().getLastMidnight().getTime() - 1000);
        save();
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
