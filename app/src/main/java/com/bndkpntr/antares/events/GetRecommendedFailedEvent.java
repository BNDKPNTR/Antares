package com.bndkpntr.antares.events;

public class GetRecommendedFailedEvent {
    private Exception e;

    public GetRecommendedFailedEvent(Exception e) {
        this.e = e;
    }

    public Exception getE() {
        return e;
    }
}
