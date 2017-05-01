package com.bndkpntr.antares.events;

import com.bndkpntr.antares.model.Track;

import java.util.List;

public class GetFavoritesSuccessfulEvent {
    private List<Track> tracks;
    private int offset;

    public GetFavoritesSuccessfulEvent(List<Track> tracks, int offset) {
        this.tracks = tracks;
        this.offset = offset;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public int getOffset() {
        return offset;
    }
}
