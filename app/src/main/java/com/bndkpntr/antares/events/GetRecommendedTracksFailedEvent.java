package com.bndkpntr.antares.events;

public class GetRecommendedTracksFailedEvent {
    private Exception e;

    public GetRecommendedTracksFailedEvent(Exception e) {
        this.e = e;
    }

    public Exception getE() {
        return e;
    }
}
