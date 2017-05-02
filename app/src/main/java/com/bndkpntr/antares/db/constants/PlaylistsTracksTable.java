package com.bndkpntr.antares.db.constants;

import android.database.sqlite.SQLiteDatabase;

public class PlaylistsTracksTable {
    public static final String NAME = "playlistsTracks";
    public static final String ID = "_id";
    public static final String PLAYLIST_ID = "playlistId";
    public static final String TRACK_ID = "trackId";
    public static final String TRACK_NUMBER = "trackNumber";

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PLAYLIST_ID + " INTEGER NOT NULL, "
            + TRACK_ID + " INTEGER NOT NULL, "
            + TRACK_NUMBER + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + PLAYLIST_ID + ") REFERENCES " + PlaylistsTable.NAME + "(" + PlaylistsTable.ID + "), "
            + "FOREIGN KEY(" + TRACK_ID + ") REFERENCES " + TracksTable.NAME + "(" + TracksTable.ID + ")"
            + ");";

    private static final String DROP_TABLE =
            "DROP TABLE IF EXISTS " + NAME + ");";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
