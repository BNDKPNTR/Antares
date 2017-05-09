package com.bndkpntr.antares.services;

import android.os.Binder;

import com.bndkpntr.antares.model.Track;

import java.util.ArrayList;

public class PlayerBinder extends Binder {
    private final PlayerService playerService;

    public PlayerBinder(PlayerService playerService) {
        this.playerService = playerService;
    }

    public boolean isPlaying() {
        return playerService.isPlaying();
    }

    public void play() {
        playerService.play();
    }

    public void pause() {
        playerService.pause();
    }

    public void seek(int seconds) {
        playerService.seek(seconds * 1000);
    }

    public void playNext() {
        playerService.playNext();
    }

    public void playPrevious() {
        playerService.playPrevious();
    }

    public int getElapsed() {
        return playerService.getElapsed() / 1000;
    }

    public int getDuration() {
        return playerService.getDuration() / 1000;
    }

    public ArrayList<Track> getPlaylist() {
        return playerService.getPlaylist();
    }

    public int getCurrentTrackIndex() {
        return playerService.getCurrentTrackIndex();
    }

    public boolean getStreamLoaded() {
        return playerService.getStreamLoaded();
    }

    public void setOnPlayerStateChangedListener(OnPlayerStateChangedListener listener) {
        playerService.setOnPlayerStateChangedListener(listener);
    }

    public interface OnPlayerStateChangedListener {
        void onTrackChanged();
        void onBufferingStarted();
        void onBufferingFinished();
    }
}
