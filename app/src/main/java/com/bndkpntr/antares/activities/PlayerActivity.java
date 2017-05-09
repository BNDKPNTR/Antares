package com.bndkpntr.antares.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bndkpntr.antares.R;
import com.bndkpntr.antares.model.Track;
import com.bndkpntr.antares.services.PlayerBinder;
import com.bndkpntr.antares.services.PlayerService;
import com.bumptech.glide.Glide;
import com.github.jorgecastilloprz.FABProgressCircle;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayerActivity extends AppCompatActivity {

    @BindView(R.id.playPauseFAB)
    FloatingActionButton playPauseFAB;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.durationTV)
    TextView durationTV;
    @BindView(R.id.previousFAB)
    FloatingActionButton previousFAB;
    @BindView(R.id.nextFAB)
    FloatingActionButton nextFAB;
    @BindView(R.id.titleTV)
    TextView titleTV;
    @BindView(R.id.playPauseFABProgressCircle)
    FABProgressCircle playPauseFABProgressCircle;

    private Handler handler;
    private PlayerBinder playerBinder;
    private ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playerBinder = (PlayerBinder) service;
            updateView();
            playerBinder.setOnPlayerStateChangedListener(new PlayerBinder.OnPlayerStateChangedListener() {
                @Override
                public void onTrackChanged() {
                    updateView();
                }

                @Override
                public void onBufferingStarted() {
                    playPauseFAB.setEnabled(false);
                    playPauseFABProgressCircle.show();
                }

                @Override
                public void onBufferingFinished() {
                    playPauseFAB.setEnabled(true);
                    playPauseFABProgressCircle.hide();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playerBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);

        playPauseFAB.setEnabled(false);
        previousFAB.setEnabled(false);
        nextFAB.setEnabled(false);
        seekBar.setMax(0);

        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (playerBinder != null && playerBinder.getStreamLoaded()) {
                    int duration = playerBinder.getDuration();
                    int elapsed = playerBinder.getElapsed();
                    seekBar.setProgress(elapsed);
                    updateDurationTV(duration, elapsed);
                }

                handler.postDelayed(this, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && playerBinder != null && playerBinder.getStreamLoaded()) {
                    playerBinder.seek(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, PlayerService.class), playerServiceConnection, 0);
    }

    @Override
    protected void onPause() {
        unbindService(playerServiceConnection);
        super.onPause();
    }

    @OnClick(R.id.playPauseFAB)
    public void onPlayPauseClicked() {
        if (playerBinder != null) {
            if (playerBinder.isPlaying()) {
                playPauseFAB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play));
                playerBinder.pause();
            } else {
                playPauseFAB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause));
                playerBinder.play();
            }
        }
    }

    @OnClick(R.id.previousFAB)
    public void onPreviousClicked() {
        if (playerBinder != null && playerBinder.getCurrentTrackIndex() != 0) {
            playerBinder.playPrevious();
            updateView();
        }
    }

    @OnClick(R.id.nextFAB)
    public void onNextClicked() {
        if (playerBinder != null && playerBinder.getCurrentTrackIndex() != playerBinder.getPlaylist().size() - 1) {
            playerBinder.playNext();
            updateView();
        }
    }

    private void updateView() {
        Track selectedTrack = playerBinder.getPlaylist().get(playerBinder.getCurrentTrackIndex());

        titleTV.setText(selectedTrack.title);
        Glide.with(this).load(Uri.parse(selectedTrack.artworkUrl)).into(imageView);

        if (playerBinder.getStreamLoaded()) {
            playPauseFAB.setEnabled(true);
            playPauseFABProgressCircle.hide();
        } else {
            playPauseFAB.setEnabled(false);
            playPauseFABProgressCircle.show();
        }

        if (playerBinder.isPlaying()) {
            playPauseFAB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause));
        } else {
            playPauseFAB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play));
        }

        if (playerBinder.getCurrentTrackIndex() != 0) {
            previousFAB.setEnabled(true);
        }

        if (playerBinder.getCurrentTrackIndex() != playerBinder.getPlaylist().size() - 1) {
            nextFAB.setEnabled(true);
        }

        seekBar.setMax(selectedTrack.duration / 1000);
        seekBar.setProgress(0);
        updateDurationTV(selectedTrack.duration / 1000, 0);
    }

    private void updateDurationTV(int durationInSeconds, int elapsedInSeconds) {
        int remainingInSeconds = durationInSeconds - elapsedInSeconds;
        int minutes = remainingInSeconds / 60;
        int seconds = remainingInSeconds % 60;
        durationTV.setText(String.format("-%1$01d:%2$01d", minutes, seconds));
    }
}
