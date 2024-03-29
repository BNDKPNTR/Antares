package com.bndkpntr.antares.db;

import android.content.SharedPreferences;

import com.bndkpntr.antares.model.OAuthToken;

import java.util.Calendar;
import java.util.Date;

public class SharedPreferencesManager {
    private static final String ACCESS_TOKEN = "AccessCode";
    private static final String REFRESH_TOKEN = "RefreshToken";
    private static final String TOKEN_EXPIRES_AT = "TokenExpiresAt";
    private static final String RECOMMENDED_CURSOR = "RecommendedCursor";
    private static final String FAVORITES_OFFSET = "FavoritesOffset";
    private static final String PLAYLISTS_OFFSET = "PlaylistsOffset";

    private final SharedPreferences preferences;
    private final Calendar calendar;

    public SharedPreferencesManager(SharedPreferences sharedPreferences) {
        this.preferences = sharedPreferences;
        this.calendar = Calendar.getInstance();
    }

    public String getAccessToken() {
        return preferences.getString(ACCESS_TOKEN, "");
    }

    public String getRefreshToken() {
        return preferences.getString(REFRESH_TOKEN, "");
    }

    public boolean containsToken() {
        return preferences.contains(ACCESS_TOKEN) && preferences.contains(REFRESH_TOKEN);
    }

    public Date getTokenExpirationDate() {
        Date expirationDate = calendar.getTime();
        expirationDate.setTime(preferences.getLong(TOKEN_EXPIRES_AT, 0));
        return expirationDate;
    }

    public void saveToken(OAuthToken token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ACCESS_TOKEN, token.accessToken);
        editor.putString(REFRESH_TOKEN, token.refreshToken);
        Date expirationDate = calendar.getTime();
        expirationDate.setTime(expirationDate.getTime() + (token.expiresIn - 10) * 1000);
        editor.putLong(TOKEN_EXPIRES_AT, expirationDate.getTime());
        editor.commit();
    }

    public String getRecommendedCursor() {
        return preferences.getString(RECOMMENDED_CURSOR, "");
    }

    public void setRecommendedCursor(String cursor) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(RECOMMENDED_CURSOR, cursor);
        editor.commit();
    }

    public int getFavoritesOffset() {
        return preferences.getInt(FAVORITES_OFFSET, 0);
    }

    public void setFavoritesOffset(int offset) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(FAVORITES_OFFSET, offset);
        editor.commit();
    }

    public int getPlaylistsOffset() {
        return preferences.getInt(PLAYLISTS_OFFSET, 0);
    }

    public void setPlaylistsOffset(int offset) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PLAYLISTS_OFFSET, offset);
        editor.commit();
    }
}
