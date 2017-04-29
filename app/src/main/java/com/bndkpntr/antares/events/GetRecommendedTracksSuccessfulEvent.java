package com.bndkpntr.antares.events;

import com.bndkpntr.antares.model.Track;

import java.util.List;

public class GetRecommendedTracksSuccessfulEvent {
    private List<Track> tracks;

    public GetRecommendedTracksSuccessfulEvent(List<Track> tracks) {
        this.tracks = tracks;
    }

    public List<Track> getTracks() {
        return tracks;
    }
}
