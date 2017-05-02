package com.bndkpntr.antares.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bndkpntr.antares.db.constants.FavoritesTable;
import com.bndkpntr.antares.db.constants.PlaylistsTable;
import com.bndkpntr.antares.db.constants.PlaylistsTracksTable;
import com.bndkpntr.antares.db.constants.RecommendedTable;
import com.bndkpntr.antares.db.constants.TracksTable;
import com.bndkpntr.antares.db.contracts.FavoritesContract;
import com.bndkpntr.antares.db.contracts.PlaylistTracksContract;
import com.bndkpntr.antares.db.contracts.PlaylistsContract;
import com.bndkpntr.antares.db.contracts.RecommendedContract;

public class DbContentProvider extends ContentProvider {

    private static final int RECOMMENDED_ALL = 1;
    private static final int RECOMMENDED_ID = 2;
    private static final int FAVORITES_ALL = 3;
    private static final int FAVORITES_ID = 4;
    private static final int PLAYLISTS_ALL = 5;
    private static final int PLAYLISTS_ID = 6;
    private static final int PLAYLIST_TRACKS_ID = 7;

    private static final String AUTHORITY = "com.bndkpntr.antares.db";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private static final UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URIMatcher.addURI(AUTHORITY, RecommendedContract.PATH, RECOMMENDED_ALL);
        URIMatcher.addURI(AUTHORITY, RecommendedContract.PATH + "/#", RECOMMENDED_ID);
        URIMatcher.addURI(AUTHORITY, FavoritesContract.PATH, FAVORITES_ALL);
        URIMatcher.addURI(AUTHORITY, FavoritesContract.PATH + "/#", FAVORITES_ID);
        URIMatcher.addURI(AUTHORITY, PlaylistsContract.PATH, PLAYLISTS_ALL);
        URIMatcher.addURI(AUTHORITY, PlaylistsContract.PATH + "/#", PLAYLISTS_ID);
        URIMatcher.addURI(AUTHORITY, PlaylistTracksContract.PATH + "/#", PLAYLIST_TRACKS_ID);
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (URIMatcher.match(uri)) {
            case RECOMMENDED_ALL:
                queryBuilder.setTables(TracksTable.NAME + " INNER JOIN " + RecommendedTable.NAME + " ON " + TracksTable.NAME + "." + TracksTable.ID + "=" + RecommendedTable.NAME + "." + RecommendedTable.ID);
                break;
            case RECOMMENDED_ID:
                queryBuilder.setTables(TracksTable.NAME + " INNER JOIN " + RecommendedTable.NAME + " ON " + TracksTable.NAME + "." + TracksTable.ID + "=" + RecommendedTable.NAME + "." + RecommendedTable.ID);
                queryBuilder.appendWhere(RecommendedTable.NAME + "." + RecommendedTable.ID + "=" + uri.getLastPathSegment());
                break;
            case FAVORITES_ALL:
                queryBuilder.setTables(TracksTable.NAME + " INNER JOIN " + FavoritesTable.NAME + " ON " + TracksTable.NAME + "." + TracksTable.ID + "=" + FavoritesTable.NAME + "." + FavoritesTable.ID);
                break;
            case FAVORITES_ID:
                queryBuilder.setTables(TracksTable.NAME + " INNER JOIN " + FavoritesTable.NAME + " ON " + TracksTable.NAME + "." + TracksTable.ID + "=" + FavoritesTable.NAME + "." + FavoritesTable.ID);
                queryBuilder.appendWhere(FavoritesTable.NAME + "." + FavoritesTable.ID + "=" + uri.getLastPathSegment());
                break;
            case PLAYLISTS_ALL:
                queryBuilder.setTables(PlaylistsTable.NAME);
                break;
            case PLAYLISTS_ID:
                queryBuilder.setTables(PlaylistsTable.NAME);
                queryBuilder.appendWhere(PlaylistsTable.ID + "=" + uri.getLastPathSegment());
                break;
            case PLAYLIST_TRACKS_ID:
                queryBuilder.setTables(PlaylistsTracksTable.NAME + " INNER JOIN " + TracksTable.NAME + " ON " + PlaylistsTracksTable.NAME + "." + PlaylistsTracksTable.TRACK_ID + "=" + TracksTable.NAME + "." + TracksTable.ID);
                queryBuilder.appendWhere(PlaylistsTracksTable.NAME + "." + PlaylistsTracksTable.PLAYLIST_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalStateException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public synchronized Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = 0;
        Uri baseUri;
        switch (URIMatcher.match(uri)) {
            case RECOMMENDED_ALL:
                db.insert(TracksTable.NAME, null, RecommendedContract.createTracksTableContentValues(values));
                id = db.insert(RecommendedTable.NAME, null, RecommendedContract.createRecommendedTableContentValues(values));
                baseUri = RecommendedContract.URI;
                break;
            case FAVORITES_ALL:
                db.insert(TracksTable.NAME, null, FavoritesContract.createTracksTableContentValues(values));
                id = db.insert(FavoritesTable.NAME, null, FavoritesContract.createFavoritesTableContentValues(values));
                baseUri = FavoritesContract.URI;
                break;
            case PLAYLISTS_ALL:
                id = db.insert(PlaylistsTable.NAME, null, PlaylistsContract.createPlaylistsTableContentValues(values));
                baseUri = PlaylistsContract.URI;
                break;
            case PLAYLIST_TRACKS_ID:
                db.insert(TracksTable.NAME, null, PlaylistTracksContract.createTracksTableContentValues(values));
                id = db.insert(PlaylistsTracksTable.NAME, null, PlaylistTracksContract.createPlaylistsTracksTableContentValues(values, Integer.parseInt(uri.getLastPathSegment())));
                baseUri = PlaylistTracksContract.URI;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(baseUri, String.valueOf(id));
    }

    @Override
    public synchronized int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        Cursor cursor = null;
        switch (URIMatcher.match(uri)) {
            case RECOMMENDED_ALL:
                cursor = db.query(RecommendedTable.NAME, new String[]{RecommendedTable.ID}, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    db.delete(TracksTable.NAME, TracksTable.ID + "= ?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(RecommendedTable.ID)))});
                }

                rowsDeleted = db.delete(RecommendedTable.NAME, selection, selectionArgs);
                break;
            case RECOMMENDED_ID:
                rowsDeleted = db.delete(RecommendedTable.NAME, RecommendedTable.ID + " = ?", new String[]{uri.getLastPathSegment()});
                db.delete(TracksTable.NAME, TracksTable.ID + " = ?", new String[]{uri.getLastPathSegment()});
                break;
            case FAVORITES_ALL:
                cursor = db.query(FavoritesTable.NAME, new String[]{FavoritesTable.ID}, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    db.delete(TracksTable.NAME, TracksTable.ID + " = ?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(FavoritesTable.ID)))});
                }

                rowsDeleted = db.delete(FavoritesTable.NAME, selection, selectionArgs);
                break;
            case FAVORITES_ID:
                rowsDeleted = db.delete(FavoritesTable.NAME, FavoritesTable.ID + " = ?", new String[]{uri.getLastPathSegment()});
                db.delete(TracksTable.NAME, TracksTable.ID + " = ?", new String[]{uri.getLastPathSegment()});
                break;
            case PLAYLISTS_ALL:
                cursor = db.query(PlaylistsTracksTable.NAME, new String[]{PlaylistsTracksTable.PLAYLIST_ID, PlaylistsTracksTable.TRACK_ID}, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    db.delete(PlaylistsTable.NAME, PlaylistsTable.ID + " = ?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(PlaylistsTracksTable.PLAYLIST_ID)))});
                    db.delete(TracksTable.NAME, TracksTable.ID + " = ?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(PlaylistsTracksTable.TRACK_ID)))});
                }

                rowsDeleted = db.delete(PlaylistsTracksTable.NAME, selection, selectionArgs);
                break;
            case PLAYLISTS_ID:
                cursor = db.query(PlaylistsTracksTable.NAME, new String[]{PlaylistsTracksTable.PLAYLIST_ID, PlaylistsTracksTable.TRACK_ID}, PlaylistsTracksTable.PLAYLIST_ID + " = ?", new String[]{uri.getLastPathSegment()}, null, null, null);
                while (cursor.moveToNext()) {
                    db.delete(TracksTable.NAME, TracksTable.ID + " = ?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(PlaylistsTracksTable.TRACK_ID)))});
                }
                db.delete(PlaylistsTable.NAME, PlaylistsTable.ID + " = ?", new String[]{uri.getLastPathSegment()});
                rowsDeleted = db.delete(PlaylistsTracksTable.NAME, PlaylistsTracksTable.PLAYLIST_ID + " = ?", new String[]{uri.getLastPathSegment()});
                break;
            case PLAYLIST_TRACKS_ID:
                cursor = db.query(PlaylistsTracksTable.NAME, new String[]{PlaylistsTracksTable.PLAYLIST_ID, PlaylistsTracksTable.TRACK_ID}, PlaylistsTracksTable.PLAYLIST_ID + " = ?", new String[]{uri.getLastPathSegment()}, null, null, null);
                while (cursor.moveToNext()) {
                    db.delete(TracksTable.NAME, TracksTable.ID + " = ?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(PlaylistsTracksTable.TRACK_ID)))});
                }
                db.delete(PlaylistsTable.NAME, PlaylistsTable.ID + " = ?", new String[]{uri.getLastPathSegment()});
                rowsDeleted = db.delete(PlaylistsTracksTable.NAME, PlaylistsTracksTable.PLAYLIST_ID + " = ?", new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (cursor != null) {
            cursor.close();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public synchronized int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
