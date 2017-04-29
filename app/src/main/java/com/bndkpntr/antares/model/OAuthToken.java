package com.bndkpntr.antares.model;

import com.google.gson.annotations.SerializedName;

public class OAuthToken {
    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("expires_in")
    public int expiresIn;

    @SerializedName("refresh_token")
    public String refreshToken;
}
