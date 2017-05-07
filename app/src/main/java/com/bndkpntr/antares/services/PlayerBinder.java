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

    public void setOnPreparedListener(OnPreparedListener listener) {
        playerService.setOnPreparedListener(listener);
    }

    public interface OnPreparedListener {
        void onPrepared();
    }
}
