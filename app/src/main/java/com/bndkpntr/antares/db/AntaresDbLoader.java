package com.bndkpntr.antares.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bndkpntr.antares.db.constants.TracksTable;
import com.bndkpntr.antares.model.Track;

public class AntaresDbLoader {
    private final Context context;
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public AntaresDbLoader(Context context) {
        this.context = context;
    }

    public void open() {
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);
    }

    public void close() {
        dbHelper.close();
    }

    public long createTrack(Track track) {
        ContentValues values = new ContentValues();
        values.put(TracksTable.ID, track.id);
        values.put(TracksTable.TITLE, track.title);
        values.put(TracksTable.STREAM_URL, track.streamUrl);
        values.put(TracksTable.ARTWORK_URL, track.streamUrl);

        return db.insert(TracksTable.NAME, null, values);
    }

    public long insertTracks(Track... tracks) {
        long result = 0;
        for (Track track : tracks) {
            result += createTrack(track);
        }

        return result;
    }

    public void deleteAllTracks() {
        db.delete(TracksTable.NAME, null, null);
    }

    public Cursor fetchAll() {
        return db.query(
                TracksTable.NAME,
                new String[]{
                        TracksTable.ID,
                        TracksTable.TITLE,
                        TracksTable.STREAM_URL,
                        TracksTable.ARTWORK_URL
                }, null, null, null, null, TracksTable.ID);
    }

    public Track fetchTrack(long rowId) {
        Cursor cursor = db.query(
                TracksTable.NAME,
                new String[]{
                        TracksTable.ID,
                        TracksTable.TITLE,
                        TracksTable.STREAM_URL,
                        TracksTable.ARTWORK_URL
                }, TracksTable.ID + " = ?", new String[]{String.valueOf(rowId)}, null, null, TracksTable.ID);

        if (cursor.moveToFirst()) {
            return getTrackByCursor(cursor);
        }

        return null;
    }

    public static Track getTrackByCursor(Cursor cursor) {
        return new Track(
                String.valueOf(cursor.getLong(cursor.getColumnIndex(TracksTable.ID))),
                cursor.getString(cursor.getColumnIndex(TracksTable.TITLE)),
                cursor.getString(cursor.getColumnIndex(TracksTable.STREAM_URL)),
                cursor.getString(cursor.getColumnIndex(TracksTable.ARTWORK_URL))
        );
    }
}
