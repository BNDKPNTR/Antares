package com.bndkpntr.antares.model;

import android.os.Handler;

import com.bndkpntr.antares.events.GetOAuthTokenFailedEvent;
import com.bndkpntr.antares.events.GetOAuthTokenSuccessfulEvent;
import com.bndkpntr.antares.events.GetRecommendedTracksFailedEvent;
import com.bndkpntr.antares.events.GetRecommendedTracksSuccessfulEvent;
import com.bndkpntr.antares.network.SoundCloudAPI;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SoundCloudInteractor {
    private final SoundCloudAPI api;
    private final SharedPreferencesManager preferencesManager;
    private final Calendar calendar;

    public SoundCloudInteractor(SharedPreferencesManager preferencesManager) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SoundCloudAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(SoundCloudAPI.class);
        this.preferencesManager = preferencesManager;
        this.calendar = Calendar.getInstance();
    }

    private static <T> void runCallOnBackgroundThread(final Call<T> call, final ResponseListener<T> listener) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<T> response = call.execute();
                    if (response.isSuccessful()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onResponse(response.body());
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(new Exception(String.valueOf(response.code()) + " " + response.message()));
                            }
                        });
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e);
                        }
                    });
                }
            }
        }).start();
    }

    public void getTokenFromCode(String code) {
        Call<OAuthToken> tokenRequest = api.getOAuthToken(new OAuthTokenRequestWithCode(code));
        runCallOnBackgroundThread(tokenRequest, new ResponseListener<OAuthToken>() {
            @Override
            public void onResponse(OAuthToken data) {
                preferencesManager.saveToken(data);
                EventBus.getDefault().post(new GetOAuthTokenSuccessfulEvent(data));
            }

            @Override
            public void onError(Exception e) {
                EventBus.getDefault().post(new GetOAuthTokenFailedEvent(e));
            }
        });
    }

    public void getRecommended(final int offset) {
        refreshTokenIfNeededThenCall(new Runnable() {
            @Override
            public void run() {
                Call<ActivitiesResponse> getRecommendedRequest = api.getRecommended(preferencesManager.getAccessToken(), 10, offset);
                runCallOnBackgroundThread(getRecommendedRequest, new ResponseListener<ActivitiesResponse>() {
                    @Override
                    public void onResponse(ActivitiesResponse data) {
                        EventBus.getDefault().post(new GetRecommendedTracksSuccessfulEvent(data.getTracks()));
                    }

                    @Override
                    public void onError(Exception e) {
                        EventBus.getDefault().post(new GetRecommendedTracksFailedEvent(e));
                    }
                });
            }
        });
    }

    private void refreshTokenIfNeededThenCall(Runnable func) {
        Date now = calendar.getTime();
        if (now.before(preferencesManager.getTokenExpirationDate())) {
            func.run();
        } else {
            refreshTokenFunc(func).run();
        }
    }

    private Runnable refreshTokenFunc(final Runnable func) {
        return new Runnable() {
            @Override
            public void run() {
                OAuthTokenRequestWithRefreshToken token = new OAuthTokenRequestWithRefreshToken(preferencesManager.getRefreshToken());
                Call<OAuthToken> tokenRequest = api.getOAuthToken(token);
                runCallOnBackgroundThread(tokenRequest, new ResponseListener<OAuthToken>() {
                    @Override
                    public void onResponse(OAuthToken data) {
                        preferencesManager.saveToken(data);
                        EventBus.getDefault().post(new GetOAuthTokenSuccessfulEvent(data));
                        func.run();
                    }

                    @Override
                    public void onError(Exception e) {
                        EventBus.getDefault().post(new GetOAuthTokenFailedEvent(e));
                    }
                });
            }
        };
    }

    interface ResponseListener<T> {
        void onResponse(T data);

        void onError(Exception e);
    }
}
