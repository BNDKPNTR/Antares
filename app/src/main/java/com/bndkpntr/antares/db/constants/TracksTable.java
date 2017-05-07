package com.bndkpntr.antares.db.constants;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bndkpntr.antares.model.Track;

public class TracksTable {
    public static final String NAME = "tracks";
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String STREAM_URL = "streamUrl";
    public static final String ARTWORK_URL = "artworkUrl";
    public static final String DURATION = "duration";

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + NAME + "("
                    + ID + " INTEGER PRIMARY KEY, "
                    + TITLE + " TEXT NOT NULL, "
                    + STREAM_URL + " TEXT NOT NULL, "
                    + ARTWORK_URL + " TEXT NOT NULL, "
                    + DURATION + " INTEGER NOT NULL"
                    + ");";

    private static final String DROP_TABLE =
            "DROP TABLE IF EXISTS " + NAME + ";";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public static Track getTrackByCursor(Cursor cursor) {
        return new Track(
                String.valueOf(cursor.getLong(cursor.getColumnIndex(TracksTable.ID))),
                cursor.getString(cursor.getColumnIndex(TracksTable.TITLE)),
                cursor.getString(cursor.getColumnIndex(TracksTable.STREAM_URL)),
                cursor.getString(cursor.getColumnIndex(TracksTable.ARTWORK_URL)),
                cursor.getInt(cursor.getColumnIndex(TracksTable.DURATION))
        );
    }
}
