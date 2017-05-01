package com.bndkpntr.antares.db.constants;

import android.database.sqlite.SQLiteDatabase;

public class TracksTable {
    public static final String NAME = "tracks";
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String STREAM_URL = "streamUrl";
    public static final String ARTWORK_URL = "artworkUrl";

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + NAME + "("
                    + ID + " INTEGER PRIMARY KEY, "
                    + TITLE + " TEXT NOT NULL, "
                    + STREAM_URL + " TEXT NOT NULL, "
                    + ARTWORK_URL + " TEXT NOT NULL"
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
}
