package com.bndkpntr.antares.model;

import com.google.gson.annotations.SerializedName;

public class OAuthTokenRequestWithCode extends OAuthTokenRequest {
    @SerializedName("code")
    public String code;

    public OAuthTokenRequestWithCode(String code) {
        super("authorization_code");
        this.code = code;
    }
}

