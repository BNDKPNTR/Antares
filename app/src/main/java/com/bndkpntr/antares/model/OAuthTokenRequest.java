package com.bndkpntr.antares.model;

import com.bndkpntr.antares.network.SoundCloudAPI;
import com.google.gson.annotations.SerializedName;

public class OAuthTokenRequest {
    @SerializedName("client_id")
    public String clientId;

    @SerializedName("client_secret")
    public String clientSecret;

    @SerializedName("redirect_uri")
    public String redirectUri;

    @SerializedName("grant_type")
    public String grantType;

    OAuthTokenRequest(String grantType) {
        clientId = SoundCloudAPI.CLIENT_ID;
        clientSecret = SoundCloudAPI.CLIENT_SECRET;
        redirectUri = SoundCloudAPI.REDIRECT_URI;
        this.grantType = grantType;
    }
}
