package com.bndkpntr.antares.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bndkpntr.antares.model.Track;

public class AntaresDbLoader {
    private final Context context;
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public AntaresDbLoader(Context context) {
        this.context = context;
    }

    public void open() {
        dbHelper = new DbHelper(context, DbConstants.DATABASE_NAME);
        db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);
    }

    public void close() {
        dbHelper.close();
    }

    public long createTrack(Track track) {
        ContentValues values = new ContentValues();
        values.put(DbConstants.Tracks.KEY_ROWID, track.id);
        values.put(DbConstants.Tracks.KEY_TITLE, track.title);
        values.put(DbConstants.Tracks.KEY_STREAM_URL, track.streamUrl);
        values.put(DbConstants.Tracks.KEY_ARTWORK_URL, track.streamUrl);

        return db.insert(DbConstants.Tracks.DATABASE_TABLE, null, values);
    }

    public void deleteAllTracks() {
        db.delete(DbConstants.Tracks.DATABASE_TABLE, null, null);
    }

    public Cursor fetchAll() {
        return db.query(
                DbConstants.Tracks.DATABASE_TABLE,
                new String[]{
                        DbConstants.Tracks.KEY_ROWID,
                        DbConstants.Tracks.KEY_TITLE,
                        DbConstants.Tracks.KEY_STREAM_URL,
                        DbConstants.Tracks.KEY_ARTWORK_URL
                }, null, null, null, null, DbConstants.Tracks.KEY_ROWID);
    }

    public Track fetchTrack(long rowId) {
        Cursor cursor = db.query(
                DbConstants.Tracks.DATABASE_TABLE,
                new String[]{
                        DbConstants.Tracks.KEY_ROWID,
                        DbConstants.Tracks.KEY_TITLE,
                        DbConstants.Tracks.KEY_STREAM_URL,
                        DbConstants.Tracks.KEY_ARTWORK_URL
                }, DbConstants.Tracks.KEY_ROWID + " = ?", new String[]{String.valueOf(rowId)}, null, null, DbConstants.Tracks.KEY_ROWID);

        if (cursor.moveToFirst()) {
            return getTrackByCursor(cursor);
        }

        return null;
    }

    public static Track getTrackByCursor(Cursor cursor) {
        return new Track(
                String.valueOf(cursor.getLong(cursor.getColumnIndex(DbConstants.Tracks.KEY_ROWID))),
                cursor.getString(cursor.getColumnIndex(DbConstants.Tracks.KEY_TITLE)),
                cursor.getString(cursor.getColumnIndex(DbConstants.Tracks.KEY_STREAM_URL)),
                cursor.getString(cursor.getColumnIndex(DbConstants.Tracks.KEY_ARTWORK_URL))
        );
    }
}
