package com.bndkpntr.antares.events;

public class GetPlaylistsFailedEvent {
    Exception e;

    public GetPlaylistsFailedEvent(Exception e) {
        this.e = e;
    }

    public Exception getE() {
        return e;
    }
}
