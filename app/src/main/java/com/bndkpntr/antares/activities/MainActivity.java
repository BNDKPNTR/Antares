package com.bndkpntr.antares.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bndkpntr.antares.Antares;
import com.bndkpntr.antares.R;
import com.bndkpntr.antares.adapters.ViewPagerAdapter;
import com.bndkpntr.antares.fragments.FavoritesFragment;
import com.bndkpntr.antares.fragments.PlaylistsFragment;
import com.bndkpntr.antares.fragments.RecommendedFragment;
import com.bndkpntr.antares.model.Track;
import com.bndkpntr.antares.services.PlayerBinder;
import com.bndkpntr.antares.services.PlayerService;
import com.bndkpntr.antares.views.PlayerBarView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.playerBar)
    PlayerBarView playerBar;

    private PlayerBinder playerBinder;

    private ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playerBinder = (PlayerBinder) service;

            if (playerBinder.getPlaylist().size() != 0) {
                Track currentTrack = playerBinder.getPlaylist().get(playerBinder.getCurrentTrackIndex());
                playerBar.setTrackData(currentTrack.title, currentTrack.artworkUrl);

                if (!playerBar.getIsVisible()) {
                    playerBar.showPlayerBar();
                }
            }

            playerBinder.setOnTrackChangedListener(new PlayerBinder.OnTrackChangedListener() {
                @Override
                public void onTrackChanged() {
                    Track currentTrack = playerBinder.getPlaylist().get(playerBinder.getCurrentTrackIndex());
                    playerBar.setTrackData(currentTrack.title, currentTrack.artworkUrl);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (playerBar.getIsVisible()) {
                playerBar.hidePlayerBar();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        playerBar.setDependentView(viewPager);

        if (userLoggedIn()) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.add(new RecommendedFragment(), getString(R.string.title_recommended_fragment));
            adapter.add(new FavoritesFragment(), getString(R.string.title_favorites_fragment));
            adapter.add(new PlaylistsFragment(), getString(R.string.title_playlists_fragment));
            viewPager.setAdapter(adapter);

            tabLayout.setupWithViewPager(viewPager);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        playerBar.setPlayerBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PlayerActivity.class));
            }
        });

        playerBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PlayerActivity.class));
            }
        });

        playerBar.setFABOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerBinder != null) {
                    if (playerBinder.isPlaying()) {
                        playerBar.setFABDrawable(R.drawable.ic_play);
                        playerBinder.pause();
                    } else {
                        playerBar.setFABDrawable(R.drawable.ic_pause);
                        playerBinder.play();
                    }
                }
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

    private boolean userLoggedIn() {
        return Antares.getSharedPreferencesManager().containsToken();
    }
}
