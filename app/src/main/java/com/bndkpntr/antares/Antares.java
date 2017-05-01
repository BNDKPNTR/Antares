package com.bndkpntr.antares;

import android.app.Application;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.bndkpntr.antares.db.SharedPreferencesManager;
import com.bndkpntr.antares.events.GetOAuthTokenFailedEvent;
import com.bndkpntr.antares.network.SoundCloudInteractor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class Antares extends Application {
    private static final String SHARED_PREFERENCES_NAME = "Antares_SP";

    private static SharedPreferencesManager sharedPreferencesManager;
    private static SoundCloudInteractor soundCloudInteractor;

    public static SharedPreferencesManager getSharedPreferencesManager() {
        return sharedPreferencesManager;
    }

    public static SoundCloudInteractor getSoundCloudInteractor() {
        return soundCloudInteractor;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        sharedPreferencesManager = new SharedPreferencesManager(sharedPreferences);
        soundCloudInteractor = new SoundCloudInteractor(sharedPreferencesManager);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onTerminate() {
        EventBus.getDefault().unregister(this);
        super.onTerminate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetOAuthTokenFailedEvent(GetOAuthTokenFailedEvent event) {
        Toast.makeText(this, "Error while refreshing your access token.", Toast.LENGTH_SHORT).show();
    }
}
