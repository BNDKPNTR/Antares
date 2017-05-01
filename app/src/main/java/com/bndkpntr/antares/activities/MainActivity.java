package com.bndkpntr.antares.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

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
            adapter.add(new RecommendedFragment(), "Recommended");
            adapter.add(new FavoritesFragment(), "Favorites");
            adapter.add(new PlaylistsFragment(), "Playlists");
            viewPager.setAdapter(adapter);

            tabLayout.setupWithViewPager(viewPager);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private boolean userLoggedIn() {
        return Antares.getSharedPreferencesManager().containsToken();
    }
}
