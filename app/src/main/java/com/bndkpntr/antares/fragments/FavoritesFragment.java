package com.bndkpntr.antares.fragments;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bndkpntr.antares.Antares;
import com.bndkpntr.antares.R;
import com.bndkpntr.antares.adapters.AutoLoadingRecyclerViewAdapter;
import com.bndkpntr.antares.adapters.TracksRecyclerViewAdapter;
import com.bndkpntr.antares.db.contracts.FavoritesContract;
import com.bndkpntr.antares.events.GetFavoritesFailedEvent;
import com.bndkpntr.antares.events.GetFavoritesSuccessfulEvent;
import com.bndkpntr.antares.model.Track;
import com.bndkpntr.antares.services.PlayerService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    Unbinder unbinder;
    TracksRecyclerViewAdapter adapter;

    public FavoritesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getContentResolver().delete(FavoritesContract.URI, null, null);
                Antares.getSharedPreferencesManager().setFavoritesOffset(0);
            }
        });

        setUpAdapter();
        recyclerView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    private void setUpAdapter() {
        adapter = new TracksRecyclerViewAdapter(getContext(), null, recyclerView);

        adapter.setOnLoadMoreListener(new AutoLoadingRecyclerViewAdapter.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                swipeRefreshLayout.setRefreshing(true);
                Antares.getSoundCloudInteractor().getFavorites();
            }
        });

        adapter.setOnTrackClickListener(new TracksRecyclerViewAdapter.OnTrackClickListener() {
            @Override
            public void onClick(View view, Track track) {
                ArrayList<Track> playlist = getFavoritesPlaylist();
                int selectedTrackIndex = getSelectedTrackIndex(playlist, track);

                Intent intent = new Intent(getContext(), PlayerService.class);
                intent.putParcelableArrayListExtra(PlayerService.PLAYLIST_EXTRA, playlist);
                intent.putExtra(PlayerService.SELECTED_TRACK_INDEX_EXTRA, selectedTrackIndex);
                getActivity().startService(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetFavoritesSuccessfulEvent(GetFavoritesSuccessfulEvent event) {
        List<Track> tracks = event.getTracks();
        ContentValues[] values = new ContentValues[tracks.size()];
        for (int i = 0; i < values.length; ++i) {
            values[i] = FavoritesContract.createContentValues(tracks.get(i), event.getOffset() + i);
        }

        getActivity().getContentResolver().bulkInsert(FavoritesContract.URI, values);
        swipeRefreshLayout.setRefreshing(false);
        adapter.setLoaded();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetFavoritesFailedEvent(GetFavoritesFailedEvent event) {
        Toast.makeText(getContext(), R.string.error_load_favorites, Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
        adapter.setLoaded();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), FavoritesContract.URI, FavoritesContract.ALL_COLUMNS, null, null, FavoritesContract.ORDER_NUM);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.changeCursor(cursor);

        if (cursor.getCount() == 0) {
            swipeRefreshLayout.setRefreshing(true);
            Antares.getSoundCloudInteractor().getFavorites();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    private ArrayList<Track> getFavoritesPlaylist() {
        ArrayList<Track> favorites = new ArrayList<>();
        Cursor cursor = null;

        try {
            String[] projection = new String[] {
                    FavoritesContract.ID,
                    FavoritesContract.TITLE,
                    FavoritesContract.STREAM_URL,
                    FavoritesContract.DURATION,
                    FavoritesContract.ARTWORK_URL
            };

            cursor = getContext().getContentResolver().query(
                    FavoritesContract.URI,
                    projection,
                    null, null, FavoritesContract.ORDER_NUM);

            while (cursor.moveToNext()) {
                favorites.add(FavoritesContract.getTrackByCursor(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return favorites;
    }

    private int getSelectedTrackIndex(ArrayList<Track> playlist, Track track) {
        for (int i = 0; i < playlist.size(); ++i) {
            if (playlist.get(i).id.equals(track.id)) {
                return i;
            }
        }

        return 0;
    }
}
