package com.bndkpntr.antares.db.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.bndkpntr.antares.db.DbContentProvider;
import com.bndkpntr.antares.db.constants.PlaylistsTracksTable;
import com.bndkpntr.antares.db.constants.TracksTable;
import com.bndkpntr.antares.model.Track;

public class PlaylistTracksContract {
    public static final String PATH = "playlistTracks";

    public static final Uri URI = Uri.withAppendedPath(DbContentProvider.BASE_URI, PATH);

    public static final String ID = TracksTable.NAME + "." + TracksTable.ID;
    public static final String TITLE = TracksTable.NAME + "." + TracksTable.TITLE;
    public static final String STREAM_URL = TracksTable.NAME + "." + TracksTable.STREAM_URL;
    public static final String ARTWORK_URL = TracksTable.NAME + "." + TracksTable.ARTWORK_URL;
    public static final String DURATION = TracksTable.NAME + "." + TracksTable.DURATION;
    public static final String TRACK_NUMBER = PlaylistsTracksTable.NAME + "." + PlaylistsTracksTable.TRACK_NUMBER;

    public static final String[] ALL_COLUMNS = {
            ID,
            TITLE,
            STREAM_URL,
            ARTWORK_URL,
            DURATION,
            TRACK_NUMBER
    };

    public static ContentValues createContentValues(Track track, int trackNumber) {
        ContentValues values = new ContentValues();
        values.put(ID, track.id);
        values.put(TITLE, track.title);
        values.put(STREAM_URL, track.streamUrl);
        values.put(ARTWORK_URL, track.artworkUrl);
        values.put(DURATION, track.duration);
        values.put(TRACK_NUMBER, trackNumber);

        return values;
    }

    public static Track getTrackByCursor(Cursor cursor) {
        return TracksTable.getTrackByCursor(cursor);
    }

    public static ContentValues createTracksTableContentValues(ContentValues values) {
        ContentValues tracksValues = new ContentValues();
        tracksValues.put(TracksTable.ID, values.getAsInteger(PlaylistTracksContract.ID));
        tracksValues.put(TracksTable.TITLE, values.getAsString(PlaylistTracksContract.TITLE));
        tracksValues.put(TracksTable.STREAM_URL, values.getAsString(PlaylistTracksContract.STREAM_URL));
        tracksValues.put(TracksTable.ARTWORK_URL, values.getAsString(PlaylistTracksContract.ARTWORK_URL));
        tracksValues.put(TracksTable.DURATION, values.getAsInteger(PlaylistTracksContract.DURATION));
        return tracksValues;
    }

    public static ContentValues createPlaylistsTracksTableContentValues(ContentValues values, int playlistId) {
        ContentValues playlistsTracksValues = new ContentValues();
        playlistsTracksValues.put(PlaylistsTracksTable.PLAYLIST_ID, playlistId);
        playlistsTracksValues.put(PlaylistsTracksTable.TRACK_ID, values.getAsInteger(PlaylistTracksContract.ID));
        playlistsTracksValues.put(PlaylistsTracksTable.TRACK_NUMBER, values.getAsInteger(PlaylistTracksContract.TRACK_NUMBER));
        return playlistsTracksValues;
    }
}
