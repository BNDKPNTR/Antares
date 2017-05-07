package com.bndkpntr.antares.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.bndkpntr.antares.Antares;
import com.bndkpntr.antares.R;
import com.bndkpntr.antares.adapters.ViewPagerAdapter;
import com.bndkpntr.antares.fragments.FavoritesFragment;
import com.bndkpntr.antares.fragments.PlaylistsFragment;
import com.bndkpntr.antares.fragments.RecommendedFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

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

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PlayerActivity.class));
            }
        });
    }

    private boolean userLoggedIn() {
        return Antares.getSharedPreferencesManager().containsToken();
    }
}
