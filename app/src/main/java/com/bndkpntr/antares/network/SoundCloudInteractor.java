package com.bndkpntr.antares.network;

import android.net.Uri;

import com.bndkpntr.antares.db.SharedPreferencesManager;
import com.bndkpntr.antares.events.GetFavoritesFailedEvent;
import com.bndkpntr.antares.events.GetFavoritesSuccessfulEvent;
import com.bndkpntr.antares.events.GetOAuthTokenFailedEvent;
import com.bndkpntr.antares.events.GetOAuthTokenSuccessfulEvent;
import com.bndkpntr.antares.events.GetPlaylistsFailedEvent;
import com.bndkpntr.antares.events.GetPlaylistsSuccessfulEvent;
import com.bndkpntr.antares.events.GetRecommendedFailedEvent;
import com.bndkpntr.antares.events.GetRecommendedSuccessfulEvent;
import com.bndkpntr.antares.model.ActivitiesResponse;
import com.bndkpntr.antares.model.OAuthToken;
import com.bndkpntr.antares.model.OAuthTokenRequestWithCode;
import com.bndkpntr.antares.model.OAuthTokenRequestWithRefreshToken;
import com.bndkpntr.antares.model.Playlist;
import com.bndkpntr.antares.model.Track;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bndkpntr.antares.network.SoundCloudAPI.QueryParams.CURSOR;

public class SoundCloudInteractor {
    private static final int LIMIT = 10;

    private final SoundCloudAPI api;
    private final SharedPreferencesManager preferencesManager;
    private final Calendar calendar;
    private final ExecutorService executorService;

    public SoundCloudInteractor(SharedPreferencesManager preferencesManager) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SoundCloudAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(SoundCloudAPI.class);
        this.preferencesManager = preferencesManager;
        this.calendar = Calendar.getInstance();
        this.executorService = Executors.newCachedThreadPool();
    }

    public void getTokenFromCode(String code) {
        Call<OAuthToken> tokenRequest = api.getOAuthToken(new OAuthTokenRequestWithCode(code));
        runCallOnBackgroundThread(tokenRequest, new ResponseListener<OAuthToken>() {
            @Override
            public void onResponse(OAuthToken data) {
                if (data != null) {
                    preferencesManager.saveToken(data);
                    EventBus.getDefault().post(new GetOAuthTokenSuccessfulEvent(data));
                } else {
                    EventBus.getDefault().post(new GetOAuthTokenFailedEvent(new NullPointerException("data")));
                }
            }

            @Override
            public void onError(Exception e) {
                EventBus.getDefault().post(new GetOAuthTokenFailedEvent(e));
            }
        });
    }

    public void getRecommended() {
        refreshTokenIfNeededThenCall(new ResponseListener<OAuthToken>() {
            @Override
            public void onResponse(OAuthToken data) {
                String cursor = preferencesManager.getRecommendedCursor();
                Call<ActivitiesResponse> getRecommendedRequest = cursor.isEmpty() ?
                        api.getRecommended(preferencesManager.getAccessToken(), LIMIT)
                        : api.getRecommended(preferencesManager.getAccessToken(), LIMIT, cursor);

                runCallOnBackgroundThread(getRecommendedRequest, new ResponseListener<ActivitiesResponse>() {
                    @Override
                    public void onResponse(ActivitiesResponse data) {
                        if (data != null) {
                            preferencesManager.setRecommendedCursor(Uri.parse(data.nextHref).getQueryParameter(CURSOR));
                            EventBus.getDefault().post(new GetRecommendedSuccessfulEvent(data.getContents()));
                        } else {
                            EventBus.getDefault().post(new GetRecommendedFailedEvent(new NullPointerException("data")));
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        EventBus.getDefault().post(new GetRecommendedFailedEvent(e));
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                EventBus.getDefault().post(new GetRecommendedFailedEvent(e));
            }
        });
    }

    public void getFavorites() {
        refreshTokenIfNeededThenCall(new ResponseListener<OAuthToken>() {
            @Override
            public void onResponse(OAuthToken data) {
                final int offset = preferencesManager.getFavoritesOffset();
                Call<List<Track>> getFavoritesRequest = api.getFavorites(preferencesManager.getAccessToken(), LIMIT, offset);
                runCallOnBackgroundThread(getFavoritesRequest, new ResponseListener<List<Track>>() {
                    @Override
                    public void onResponse(List<Track> data) {
                        if (data != null) {
                            List<Track> normalizedTracks = new ArrayList<>();
                            for (Track track : data) {
                                Track normalizedTrack = Track.tryGetNormalizedTrack(track);
                                if (normalizedTrack != null) {
                                    normalizedTracks.add(normalizedTrack);
                                }
                            }

                            preferencesManager.setFavoritesOffset(offset + LIMIT);
                            EventBus.getDefault().post(new GetFavoritesSuccessfulEvent(normalizedTracks, offset));
                        } else {
                            EventBus.getDefault().post(new GetFavoritesFailedEvent(new NullPointerException("data")));
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        EventBus.getDefault().post(new GetFavoritesFailedEvent(e));
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                EventBus.getDefault().post(new GetFavoritesFailedEvent(e));
            }
        });
    }

    public void getPlaylists() {
        refreshTokenIfNeededThenCall(new ResponseListener<OAuthToken>() {
            @Override
            public void onResponse(OAuthToken data) {
                final int offset = preferencesManager.getPlaylistsOffset();
                Call<List<Playlist>> getPlaylistsRequest = api.getPlaylists(preferencesManager.getAccessToken(), LIMIT, offset);
                runCallOnBackgroundThread(getPlaylistsRequest, new ResponseListener<List<Playlist>>() {
                    @Override
                    public void onResponse(List<Playlist> data) {
                        if (data != null) {
                            List<Playlist> normalizedPlaylists = new ArrayList<>();
                            for (Playlist playlist : data) {
                                Playlist normalizedPlaylist = Playlist.tryGetNormalizedPlaylistAndTracks(playlist);
                                if (normalizedPlaylist != null) {
                                    normalizedPlaylists.add(normalizedPlaylist);
                                }
                            }

                            preferencesManager.setPlaylistsOffset(offset + LIMIT);
                            EventBus.getDefault().post(new GetPlaylistsSuccessfulEvent(normalizedPlaylists, offset));
                        } else {
                            EventBus.getDefault().post(new GetPlaylistsFailedEvent(new NullPointerException("data")));
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        EventBus.getDefault().post(new GetPlaylistsFailedEvent(e));
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                EventBus.getDefault().post(new GetPlaylistsFailedEvent(e));
            }
        });
    }

    private void refreshTokenIfNeededThenCall(ResponseListener<OAuthToken> responseListener) {
        Date now = calendar.getTime();
        if (now.before(preferencesManager.getTokenExpirationDate())) {
            responseListener.onResponse(null);
        } else {
            refreshTokenFunc(responseListener).run();
        }
    }

    private Runnable refreshTokenFunc(final ResponseListener<OAuthToken> responseListener) {
        return new Runnable() {
            @Override
            public void run() {
                OAuthTokenRequestWithRefreshToken token = new OAuthTokenRequestWithRefreshToken(preferencesManager.getRefreshToken());
                Call<OAuthToken> tokenRequest = api.getOAuthToken(token);
                runCallOnBackgroundThread(tokenRequest, new ResponseListener<OAuthToken>() {
                    @Override
                    public void onResponse(OAuthToken data) {
                        if (data != null) {
                            preferencesManager.saveToken(data);
                            EventBus.getDefault().post(new GetOAuthTokenSuccessfulEvent(data));
                            responseListener.onResponse(data);
                        } else {
                            NullPointerException e = new NullPointerException("data");
                            EventBus.getDefault().post(new GetOAuthTokenFailedEvent(e));
                            responseListener.onError(e);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        EventBus.getDefault().post(new GetOAuthTokenFailedEvent(e));
                        responseListener.onError(e);
                    }
                });
            }
        };
    }

    private <T> void runCallOnBackgroundThread(final Call<T> call, final ResponseListener<T> listener) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<T> response = call.execute();
                    if (response.isSuccessful()) {
                        listener.onResponse(response.body());
                    } else {
                        listener.onError(new Exception(String.valueOf(response.code()) + " " + response.message()));
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    listener.onError(e);
                }
            }
        });
    }

    interface ResponseListener<T> {
        void onResponse(T data);

        void onError(Exception e);
    }
}
