package com.bndkpntr.antares.events;

import com.bndkpntr.antares.model.OAuthToken;

public class GetOAuthTokenSuccessfulEvent {
    private OAuthToken token;

    public GetOAuthTokenSuccessfulEvent(OAuthToken token) {
        this.token = token;
    }

    public OAuthToken getToken() {
        return token;
    }
}
