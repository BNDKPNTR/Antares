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
import android.text.TextUtils;

import com.bndkpntr.antares.db.constants.RecommendedTable;
import com.bndkpntr.antares.db.constants.TracksTable;
import com.bndkpntr.antares.db.contracts.RecommendedContract;

public class DbContentProvider extends ContentProvider {

    private static final int RECOMMENDED_ALL = 1;
    private static final int RECOMMENDED_ID = 2;

    private static final String AUTHORITY = "com.bndkpntr.antares.db";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private static final UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URIMatcher.addURI(AUTHORITY, RecommendedContract.PATH, RECOMMENDED_ALL);
        URIMatcher.addURI(AUTHORITY, RecommendedContract.PATH + "/#", RECOMMENDED_ID);
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
                queryBuilder.appendWhere(RecommendedTable.ID + "=" + uri.getLastPathSegment());
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
                ContentValues tracksValues = new ContentValues();
                tracksValues.put(TracksTable.ID, values.getAsInteger(RecommendedContract.ID));
                tracksValues.put(TracksTable.TITLE, values.getAsString(RecommendedContract.TITLE));
                tracksValues.put(TracksTable.STREAM_URL, values.getAsString(RecommendedContract.STREAM_URL));
                tracksValues.put(TracksTable.ARTWORK_URL, values.getAsString(RecommendedContract.ARTWORK_URL));

                ContentValues recommendedValues = new ContentValues();
                recommendedValues.put(RecommendedTable.ID, values.getAsInteger(RecommendedContract.ID));
                recommendedValues.put(RecommendedTable.CREATED_AT, values.getAsLong(RecommendedContract.CREATED_AT));

                db.insert(TracksTable.NAME, null, tracksValues);
                id = db.insert(RecommendedTable.NAME, null, recommendedValues);
                baseUri = RecommendedContract.URI;
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
//                Cursor cursor = db.query(RecommendedTable.NAME, new String[]{RecommendedTable.ID}, null, null, null, null, null);
//                while (cursor.moveToNext()) {
//                    db.delete(TracksTable.NAME, TracksTable.ID + "= ?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(RecommendedTable.ID)))});
//                }

                rowsDeleted = db.delete(RecommendedTable.NAME, selection, selectionArgs);
                break;
            case RECOMMENDED_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(TracksTable.NAME, TracksTable.ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(TracksTable.NAME, TracksTable.ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public synchronized int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
//        int uriType = URIMatcher.match(uri);
//        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
//        int rowsUpdated = 0;
//        switch (uriType) {
//            case TODOS:
//                rowsUpdated = sqlDB.update(TracksTable.NAME,
//                        values,
//                        selection,
//                        selectionArgs);
//                break;
//            case TODO_ID:
//                String id = uri.getLastPathSegment();
//                if (TextUtils.isEmpty(selection)) {
//                    rowsUpdated = sqlDB.update(TracksTable.NAME,
//                            values,
//                            TracksTable.ID + "=" + id,
//                            null);
//                } else {
//                    rowsUpdated = sqlDB.update(TracksTable.NAME,
//                            values,
//                            TracksTable.ID + "=" + id
//                                    + " and "
//                                    + selection,
//                            selectionArgs);
//                }
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown URI: " + uri);
//        }
//        getContext().getContentResolver().notifyChange(uri, null);
//        return rowsUpdated;
        return 0;
    }
}
