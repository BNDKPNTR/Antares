package com.bndkpntr.antares.events;

public class GetOAuthTokenFailedEvent {
    Exception e;

    public GetOAuthTokenFailedEvent(Exception e) {
        this.e = e;
    }

    public Exception getE() {
        return e;
    }
}
