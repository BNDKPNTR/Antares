package com.bndkpntr.antares.events;

import com.bndkpntr.antares.model.Playlist;

import java.util.List;

public class GetPlaylistsSuccessfulEvent {
    private List<Playlist> playlists;
    private int offset;

    public GetPlaylistsSuccessfulEvent(List<Playlist> playlists, int offset) {
        this.playlists = playlists;
        this.offset = offset;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public int getOffset() {
        return offset;
    }
}
