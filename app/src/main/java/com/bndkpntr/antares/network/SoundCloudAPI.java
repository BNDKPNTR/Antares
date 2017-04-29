package com.bndkpntr.antares.network;

import com.bndkpntr.antares.model.ActivitiesResponse;
import com.bndkpntr.antares.model.OAuthToken;
import com.bndkpntr.antares.model.OAuthTokenRequestWithCode;
import com.bndkpntr.antares.model.OAuthTokenRequestWithRefreshToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SoundCloudAPI {
    String CLIENT_ID = "***REMOVED***";
    String CLIENT_SECRET = "***REMOVED***";
    String REDIRECT_URI = "antares://auth";
    String BASE_URL = "https://api.soundcloud.com/";
    String LOGIN_URL = BASE_URL + "connect?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code&display=popup";


    @GET("me/activities?client_id=" + CLIENT_ID)
    Call<ActivitiesResponse> getRecommended(@Query(QueryParams.OAUTH_TOKEN) String token, @Query(QueryParams.LIMIT) int limit);

    @GET("me/activities?client_id=" + CLIENT_ID)
    Call<ActivitiesResponse> getRecommended(@Query(QueryParams.OAUTH_TOKEN) String token, @Query(QueryParams.LIMIT) int limit, @Query(QueryParams.CURSOR) String cursor);

    @POST("oauth2/token?client_id=" + CLIENT_ID)
    Call<OAuthToken> getOAuthToken(@Body OAuthTokenRequestWithCode message);

    @POST("oauth2/token?client_id=" + CLIENT_ID)
    Call<OAuthToken> getOAuthToken(@Body OAuthTokenRequestWithRefreshToken message);

    class QueryParams {
        static final String OAUTH_TOKEN = "oauth_token";
        static final String LIMIT = "limit";
        static final String CURSOR = "cursor";
    }
}
