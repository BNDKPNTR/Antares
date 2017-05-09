package com.bndkpntr.antares.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bndkpntr.antares.R;
import com.bndkpntr.antares.model.Track;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    public static final String PLAYLIST_EXTRA = "PlaylistExtra";
    public static final String SELECTED_TRACK_INDEX_EXTRA = "SelectedTrackIndexExtra";

    private final PlayerBinder playerBinder = new PlayerBinder(this);
    private MediaPlayer mediaPlayer;
    private final ArrayList<Track> playlist = new ArrayList<>();
    private int currentTrackIndex;
    private boolean streamLoaded;
    private WifiManager.WifiLock wifiLock;
    private PlayerBinder.OnPlayerStateChangedListener onPlayerStateChangedListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        wifiLock = ((WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "myLock");
        wifiLock.acquire();

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.player_notification_title))
                .setContentText(getString(R.string.player_notification_text))
                .getNotification();

        startForeground(0, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setUpPlaylist(intent);

        if (playlist.size() != 0) {
            currentTrackIndex = intent.getIntExtra(SELECTED_TRACK_INDEX_EXTRA, 0);
            prepareMediaPlayer();
        }

        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return playerBinder;
    }

    public ArrayList<Track> getPlaylist() {
        return playlist;
    }

    public int getCurrentTrackIndex() {
        return currentTrackIndex;
    }

    public boolean getStreamLoaded() {
        return streamLoaded;
    }

    public void setOnPlayerStateChangedListener(PlayerBinder.OnPlayerStateChangedListener listener) {
        onPlayerStateChangedListener = listener;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        stopForeground(true);
        wifiLock.release();
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        streamLoaded = true;
        if (onPlayerStateChangedListener != null) {
            onPlayerStateChangedListener.onBufferingFinished();
        }
    }

    private void setUpPlaylist(Intent intent) {
        ArrayList<Track> playlist = intent.getParcelableArrayListExtra(PLAYLIST_EXTRA);
        if (playlist != null) {
            this.playlist.clear();
            this.playlist.addAll(playlist);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (currentTrackIndex == playlist.size() - 1) {
            stopSelf();
        } else {
            ++currentTrackIndex;
            prepareMediaPlayer();
        }
    }

    private void prepareMediaPlayer() {
        try {
            streamLoaded = false;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(playlist.get(currentTrackIndex).streamUrl);
            mediaPlayer.prepareAsync();

            if (onPlayerStateChangedListener != null) {
                onPlayerStateChangedListener.onTrackChanged();
                onPlayerStateChangedListener.onBufferingStarted();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void seek(int millisecond) {
        mediaPlayer.seekTo(millisecond);
    }

    public void playNext() {
        if (currentTrackIndex != playlist.size() - 1) {
            ++currentTrackIndex;
            prepareMediaPlayer();
        }
    }

    public void playPrevious() {
        if (currentTrackIndex != 0) {
            --currentTrackIndex;
            prepareMediaPlayer();
        }
    }

    public int getElapsed() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getApplicationContext(), R.string.error_player_service, Toast.LENGTH_SHORT).show();
        return false;
    }
}
