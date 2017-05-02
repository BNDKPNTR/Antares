package com.bndkpntr.antares.db.constants;

import android.database.sqlite.SQLiteDatabase;

public class FavoritesTable {
    public static final String NAME = "favorites";
    public static final String ID = "_id";
    public static final String ORDER_ID = "orderId";

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + NAME + "("
                    + ID + " INTEGER PRIMARY KEY, "
                    + ORDER_ID + " INTEGER NOT NULL, "
                    + "FOREIGN KEY(" + ID + ") REFERENCES " + TracksTable.NAME + "(" + TracksTable.ID + ")"
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
