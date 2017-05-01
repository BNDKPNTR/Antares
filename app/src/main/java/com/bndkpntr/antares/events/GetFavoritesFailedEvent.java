package com.bndkpntr.antares.events;


public class GetFavoritesFailedEvent {
    private Exception e;

    public GetFavoritesFailedEvent(Exception e) {
        this.e = e;
    }

    public Exception getE() {
        return e;
    }
}
