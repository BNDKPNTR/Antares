package com.bndkpntr.antares.network;

import com.bndkpntr.antares.model.ActivitiesResponse;
import com.bndkpntr.antares.model.OAuthToken;
import com.bndkpntr.antares.model.OAuthTokenRequestWithCode;
import com.bndkpntr.antares.model.OAuthTokenRequestWithRefreshToken;
import com.bndkpntr.antares.model.Playlist;
import com.bndkpntr.antares.model.Track;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SoundCloudAPI {
    String CLIENT_ID = "";
    String CLIENT_SECRET = "";
    String REDIRECT_URI = "antares://auth";
    String BASE_URL = "https://api.soundcloud.com/";
    String LOGIN_URL = BASE_URL + "connect?" + QueryParams.CLIENT_ID + "=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code&display=popup";

    @POST("oauth2/token?" + QueryParams.CLIENT_ID + "=" + CLIENT_ID)
    Call<OAuthToken> getOAuthToken(@Body OAuthTokenRequestWithCode message);

    @POST("oauth2/token?" + QueryParams.CLIENT_ID + "=" + CLIENT_ID)
    Call<OAuthToken> getOAuthToken(@Body OAuthTokenRequestWithRefreshToken message);

    @GET("me/activities?" + QueryParams.CLIENT_ID + "=" + CLIENT_ID)
    Call<ActivitiesResponse> getRecommended(@Query(QueryParams.OAUTH_TOKEN) String token, @Query(QueryParams.LIMIT) int limit);

    @GET("me/activities?" + QueryParams.CLIENT_ID + "=" + CLIENT_ID)
    Call<ActivitiesResponse> getRecommended(@Query(QueryParams.OAUTH_TOKEN) String token, @Query(QueryParams.LIMIT) int limit, @Query(QueryParams.CURSOR) String cursor);

    @GET("me/favorites?" + QueryParams.CLIENT_ID + "=" + CLIENT_ID)
    Call<List<Track>> getFavorites(@Query(QueryParams.OAUTH_TOKEN) String token, @Query(QueryParams.LIMIT) int limit, @Query(QueryParams.OFFSET) int offset);

    @GET("me/playlists?" + QueryParams.CLIENT_ID + "=" + CLIENT_ID)
    Call<List<Playlist>> getPlaylists(@Query(QueryParams.OAUTH_TOKEN) String token, @Query(QueryParams.LIMIT) int limit, @Query(QueryParams.OFFSET) int offset);

    public class QueryParams {
        public static final String OAUTH_TOKEN = "oauth_token";
        public static final String CLIENT_ID = "client_id";
        public static final String LIMIT = "limit";
        public static final String OFFSET = "offset";
        public static final String CURSOR = "cursor";
    }
}
