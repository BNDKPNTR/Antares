package com.bndkpntr.antares.db.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;

import com.bndkpntr.antares.db.DbContentProvider;
import com.bndkpntr.antares.db.constants.RecommendedTable;
import com.bndkpntr.antares.db.constants.TracksTable;
import com.bndkpntr.antares.model.ActivitiesContent;
import com.bndkpntr.antares.model.Track;

import java.text.ParseException;
import java.util.Calendar;

public class RecommendedContract {
    public static final String PATH = "recommended";

    public static final Uri URI = Uri.withAppendedPath(DbContentProvider.BASE_URI, PATH);

    public static final String ID = TracksTable.NAME + "." + TracksTable.ID;
    public static final String TITLE = TracksTable.NAME + "." + TracksTable.TITLE;
    public static final String STREAM_URL = TracksTable.NAME + "." + TracksTable.STREAM_URL;
    public static final String ARTWORK_URL = TracksTable.NAME + "." + TracksTable.ARTWORK_URL;
    public static final String DURATION = TracksTable.NAME + "." + TracksTable.DURATION;
    public static final String CREATED_AT = RecommendedTable.NAME + "." + RecommendedTable.CREATED_AT;

    public static final String[] ALL_COLUMNS = {
            ID,
            TITLE,
            STREAM_URL,
            ARTWORK_URL,
            DURATION,
            CREATED_AT
    };

    public static ContentValues createContentValues(ActivitiesContent content) {
        ContentValues values = new ContentValues();
        values.put(ID, content.track.id);
        values.put(TITLE, content.track.title);
        values.put(STREAM_URL, content.track.streamUrl);
        values.put(ARTWORK_URL, content.track.artworkUrl);
        values.put(DURATION, content.track.duration);

        long time;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
            time = format.parse(content.createdAt).getTime();
        } catch (ParseException e) {
            Calendar calendar = Calendar.getInstance();
            time = calendar.getTime().getTime();
        }
        values.put(CREATED_AT, time);

        return values;
    }

    public static Track getTrackByCursor(Cursor cursor) {
        return TracksTable.getTrackByCursor(cursor);
    }

    public static ContentValues createTracksTableContentValues(ContentValues values) {
        ContentValues tracksValues = new ContentValues();
        tracksValues.put(TracksTable.ID, values.getAsInteger(RecommendedContract.ID));
        tracksValues.put(TracksTable.TITLE, values.getAsString(RecommendedContract.TITLE));
        tracksValues.put(TracksTable.STREAM_URL, values.getAsString(RecommendedContract.STREAM_URL));
        tracksValues.put(TracksTable.ARTWORK_URL, values.getAsString(RecommendedContract.ARTWORK_URL));
        tracksValues.put(TracksTable.DURATION, values.getAsInteger(RecommendedContract.DURATION));
        return tracksValues;
    }

    public static ContentValues createRecommendedTableContentValues(ContentValues values) {
        ContentValues recommendedValues = new ContentValues();
        recommendedValues.put(RecommendedTable.ID, values.getAsInteger(RecommendedContract.ID));
        recommendedValues.put(RecommendedTable.CREATED_AT, values.getAsLong(RecommendedContract.CREATED_AT));
        return recommendedValues;
    }
}
