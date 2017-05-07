package com.bndkpntr.antares.db.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.bndkpntr.antares.db.DbContentProvider;
import com.bndkpntr.antares.db.constants.FavoritesTable;
import com.bndkpntr.antares.db.constants.TracksTable;
import com.bndkpntr.antares.model.Track;

public class FavoritesContract {
    public static final String PATH = "favorites";

    public static final Uri URI = Uri.withAppendedPath(DbContentProvider.BASE_URI, PATH);

    public static final String ID = TracksTable.NAME + "." + TracksTable.ID;
    public static final String TITLE = TracksTable.NAME + "." + TracksTable.TITLE;
    public static final String STREAM_URL = TracksTable.NAME + "." + TracksTable.STREAM_URL;
    public static final String ARTWORK_URL = TracksTable.NAME + "." + TracksTable.ARTWORK_URL;
    public static final String DURATION = TracksTable.NAME + "." + TracksTable.DURATION;
    public static final String ORDER_NO = FavoritesTable.NAME + "." + FavoritesTable.ORDER_ID;

    public static final String[] ALL_COLUMNS = {
            ID,
            TITLE,
            STREAM_URL,
            ARTWORK_URL,
            DURATION,
            ORDER_NO
    };

    public static Track getTrackByCursor(Cursor cursor) {
        return TracksTable.getTrackByCursor(cursor);
    }

    public static ContentValues createContentValues(Track track, int orderNo) {
        ContentValues values = new ContentValues();
        values.put(ID, track.id);
        values.put(TITLE, track.title);
        values.put(STREAM_URL, track.streamUrl);
        values.put(ARTWORK_URL, track.artworkUrl);
        values.put(DURATION, track.duration);
        values.put(ORDER_NO, orderNo);

        return values;
    }

    public static ContentValues createTracksTableContentValues(ContentValues values) {
        ContentValues tracksValues = new ContentValues();
        tracksValues.put(TracksTable.ID, values.getAsInteger(FavoritesContract.ID));
        tracksValues.put(TracksTable.TITLE, values.getAsString(FavoritesContract.TITLE));
        tracksValues.put(TracksTable.STREAM_URL, values.getAsString(FavoritesContract.STREAM_URL));
        tracksValues.put(TracksTable.ARTWORK_URL, values.getAsString(FavoritesContract.ARTWORK_URL));
        tracksValues.put(TracksTable.DURATION, values.getAsInteger(FavoritesContract.DURATION));
        return tracksValues;
    }

    public static ContentValues createFavoritesTableContentValues(ContentValues values) {
        ContentValues favoritesValues = new ContentValues();
        favoritesValues.put(FavoritesTable.ID, values.getAsInteger(FavoritesContract.ID));
        favoritesValues.put(FavoritesTable.ORDER_ID, values.getAsInteger(FavoritesContract.ORDER_NO));
        return favoritesValues;
    }
}
