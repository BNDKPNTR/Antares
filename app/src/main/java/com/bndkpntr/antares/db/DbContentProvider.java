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
import com.bndkpntr.antares.db.constants.RecommendedTable;
import com.bndkpntr.antares.db.constants.TracksTable;
import com.bndkpntr.antares.db.contracts.FavoritesContract;
import com.bndkpntr.antares.db.contracts.RecommendedContract;

public class DbContentProvider extends ContentProvider {

    private static final int RECOMMENDED_ALL = 1;
    private static final int RECOMMENDED_ID = 2;
    private static final int FAVORITES_ALL = 3;
    private static final int FAVORITES_ID = 4;

    private static final String AUTHORITY = "com.bndkpntr.antares.db";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private static final UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URIMatcher.addURI(AUTHORITY, RecommendedContract.PATH, RECOMMENDED_ALL);
        URIMatcher.addURI(AUTHORITY, RecommendedContract.PATH + "/#", RECOMMENDED_ID);
        URIMatcher.addURI(AUTHORITY, FavoritesContract.PATH, FAVORITES_ALL);
        URIMatcher.addURI(AUTHORITY, FavoritesContract.PATH + "/#", FAVORITES_ID);
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
        switch (URIMatcher.match(uri)) {
            case RECOMMENDED_ALL:
                Cursor recommendedTableCursor = db.query(RecommendedTable.NAME, new String[]{RecommendedTable.ID}, null, null, null, null, null);
                while (recommendedTableCursor.moveToNext()) {
                    db.delete(TracksTable.NAME, TracksTable.ID + "= ?", new String[]{String.valueOf(recommendedTableCursor.getInt(recommendedTableCursor.getColumnIndex(RecommendedTable.ID)))});
                }

                rowsDeleted = db.delete(RecommendedTable.NAME, selection, selectionArgs);
                break;
            case RECOMMENDED_ID:
                String recommendedId = uri.getLastPathSegment();
                rowsDeleted = db.delete(RecommendedTable.NAME, RecommendedTable.ID + " = ?", new String[]{String.valueOf(recommendedId)});
                db.delete(TracksTable.NAME, TracksTable.ID + " = ?", new String[]{String.valueOf(recommendedId)});
                break;
            case FAVORITES_ALL:
                Cursor favoritesTableCursor = db.query(FavoritesTable.NAME, new String[]{FavoritesTable.ID}, null, null, null, null, null);
                while (favoritesTableCursor.moveToNext()) {
                    db.delete(TracksTable.NAME, TracksTable.ID + " = ?", new String[]{String.valueOf(favoritesTableCursor.getInt(favoritesTableCursor.getColumnIndex(FavoritesTable.ID)))});
                }

                rowsDeleted = db.delete(FavoritesTable.NAME, selection, selectionArgs);
                break;
            case FAVORITES_ID:
                String favoritesId = uri.getLastPathSegment();
                rowsDeleted = db.delete(FavoritesTable.NAME, FavoritesTable.ID + " = ?", new String[]{String.valueOf(favoritesId)});
                db.delete(TracksTable.NAME, TracksTable.ID + " = ?", new String[]{String.valueOf(favoritesId)});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public synchronized int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
