package com.bndkpntr.antares.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bndkpntr.antares.R;
import com.bndkpntr.antares.model.Track;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    public static final String PLAYLIST_EXTRA = "PlaylistExtra";
    public static final String SELECTED_TRACK_INDEX_EXTRA = "SelectedTrackIndexExtra";

    private final PlayerBinder playerBinder = new PlayerBinder(this);
    private MediaPlayer mediaPlayer;
    private final ArrayList<Track> playlist = new ArrayList<>();
    private int currentTrackIndex;
    private PlayerBinder.OnPreparedListener onPreparedListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

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

    public void setOnPreparedListener(PlayerBinder.OnPreparedListener listener) {
        this.onPreparedListener = listener;
    }

    public ArrayList<Track> getPlaylist() {
        return playlist;
    }

    public int getCurrentTrackIndex() {
        return currentTrackIndex;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        if (onPreparedListener != null) {
            onPreparedListener.onPrepared();
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
            mediaPlayer.reset();
            mediaPlayer.setDataSource(playlist.get(currentTrackIndex).streamUrl);
            mediaPlayer.prepareAsync();
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
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (IllegalStateException e) {
            return 0;
        }
    }

    public int getDuration() {
        try {
            return mediaPlayer.getDuration();
        } catch (IllegalStateException e) {
            return 0;
        }
    }
}
