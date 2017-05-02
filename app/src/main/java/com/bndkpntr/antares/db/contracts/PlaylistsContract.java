package com.bndkpntr.antares.db.contracts;

import android.content.ContentValues;
import android.net.Uri;

import com.bndkpntr.antares.db.DbContentProvider;
import com.bndkpntr.antares.db.constants.PlaylistsTable;
import com.bndkpntr.antares.model.Playlist;

public class PlaylistsContract {
    public static final String PATH = "playlists";

    public static final Uri URI = Uri.withAppendedPath(DbContentProvider.BASE_URI, PATH);

    public static final String ID = PlaylistsTable.NAME + "." + PlaylistsTable.ID;
    public static final String TITLE = PlaylistsTable.NAME + "." + PlaylistsTable.TITLE;
    public static final String ARTWORK_URL = PlaylistsTable.NAME + "." + PlaylistsTable.ARTWORK_URL;
    public static final String ORDER_NO = PlaylistsTable.NAME + "." + PlaylistsTable.ORDER_NO;

    public static final String[] ALL_COLUMNS = {
            ID,
            TITLE,
            ARTWORK_URL,
            ORDER_NO
    };

    public static ContentValues createContentValues(Playlist playlist, int orderNo) {
        ContentValues values = new ContentValues();
        values.put(ID, playlist.id);
        values.put(TITLE, playlist.title);
        values.put(ARTWORK_URL, playlist.artworkUrl);
        values.put(ORDER_NO, orderNo);
        return values;
    }

    public static ContentValues createPlaylistsTableContentValues(ContentValues values) {
        ContentValues playlistsValues = new ContentValues();
        playlistsValues.put(PlaylistsTable.ID, values.getAsInteger(PlaylistsContract.ID));
        playlistsValues.put(PlaylistsTable.TITLE, values.getAsString(PlaylistsContract.TITLE));
        playlistsValues.put(PlaylistsTable.ARTWORK_URL, values.getAsString(PlaylistsContract.ARTWORK_URL));
        playlistsValues.put(PlaylistsTable.ORDER_NO, values.getAsInteger(PlaylistsContract.ORDER_NO));
        return playlistsValues;
    }
}
