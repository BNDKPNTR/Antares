package com.bndkpntr.antares.db.constants;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bndkpntr.antares.model.Playlist;

public class PlaylistsTable {
    public static final String NAME = "playlists";
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String ARTWORK_URL = "artworkUrl";
    public static final String ORDER_NUM = "orderNum";

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + NAME + "("
                    + ID + " INTEGER PRIMARY KEY, "
                    + TITLE + " TEXT NOT NULL, "
                    + ARTWORK_URL + " TEXT NOT NULL, "
                    + ORDER_NUM + " INTEGER NOT NULL"
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

    public static Playlist getPlaylistByCursor(Cursor cursor) {
        return new Playlist(
                String.valueOf(cursor.getLong(cursor.getColumnIndex(PlaylistsTable.ID))),
                cursor.getString(cursor.getColumnIndex(PlaylistsTable.TITLE)),
                cursor.getString(cursor.getColumnIndex(TracksTable.ARTWORK_URL)),
                null
        );
    }
}
