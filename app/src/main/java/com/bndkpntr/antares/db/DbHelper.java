package com.bndkpntr.antares.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bndkpntr.antares.db.constants.AntaresDb;
import com.bndkpntr.antares.db.constants.FavoritesTable;
import com.bndkpntr.antares.db.constants.PlaylistsTable;
import com.bndkpntr.antares.db.constants.PlaylistsTracksTable;
import com.bndkpntr.antares.db.constants.RecommendedTable;
import com.bndkpntr.antares.db.constants.TracksTable;

class DbHelper extends SQLiteOpenHelper {

    DbHelper(Context context) {
        super(context, AntaresDb.NAME, null, AntaresDb.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TracksTable.onCreate(db);
        RecommendedTable.onCreate(db);
        FavoritesTable.onCreate(db);
        PlaylistsTable.onCreate(db);
        PlaylistsTracksTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TracksTable.onUpgrade(db, oldVersion, newVersion);
        RecommendedTable.onUpgrade(db, oldVersion, newVersion);
        FavoritesTable.onUpgrade(db, oldVersion, newVersion);
        PlaylistsTable.onUpgrade(db, oldVersion, newVersion);
        PlaylistsTracksTable.onUpgrade(db, oldVersion, newVersion);
    }
}
