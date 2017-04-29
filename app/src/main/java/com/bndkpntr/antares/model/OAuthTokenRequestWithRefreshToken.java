package com.bndkpntr.antares.model;

import com.google.gson.annotations.SerializedName;

public class OAuthTokenRequestWithRefreshToken extends OAuthTokenRequest {
    @SerializedName("refresh_token")
    public String refreshToken;

    public OAuthTokenRequestWithRefreshToken(String refreshToken) {
        super("refresh_token");
        this.refreshToken = refreshToken;
    }
}
