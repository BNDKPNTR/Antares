package com.bndkpntr.antares.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.bndkpntr.antares.adapters.PlaylistsRecyclerViewAdapter;
import com.bndkpntr.antares.db.contracts.PlaylistTracksContract;
import com.bndkpntr.antares.db.contracts.PlaylistsContract;
import com.bndkpntr.antares.events.GetPlaylistsFailedEvent;
import com.bndkpntr.antares.events.GetPlaylistsSuccessfulEvent;
import com.bndkpntr.antares.model.Playlist;
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

public class PlaylistsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private Unbinder unbinder;
    private PlaylistsRecyclerViewAdapter adapter;

    public PlaylistsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getContentResolver().delete(PlaylistsContract.URI, null, null);
                Antares.getSharedPreferencesManager().setPlaylistsOffset(0);
            }
        });

        setUpAdapter();
        recyclerView.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
        return view;
    }

    private void setUpAdapter() {
        adapter = new PlaylistsRecyclerViewAdapter(getContext(), null, recyclerView);

        adapter.setOnLoadMoreListener(new AutoLoadingRecyclerViewAdapter.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                swipeRefreshLayout.setRefreshing(true);
                Antares.getSoundCloudInteractor().getPlaylists();
            }
        });

        adapter.setOnPlaylistClickListener(new PlaylistsRecyclerViewAdapter.OnPlaylistClickListener() {
            @Override
            public void onClick(View view, Playlist playlist) {
                Intent intent = new Intent(getContext(), PlayerService.class);
                intent.putParcelableArrayListExtra(PlayerService.PLAYLIST_EXTRA, getPlaylistTracks(playlist.id));
                intent.putExtra(PlayerService.SELECTED_TRACK_INDEX_EXTRA, 0);
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
    public void onGetPlaylistsSuccessfulEvent(GetPlaylistsSuccessfulEvent event) {
        List<Playlist> playlists = event.getPlaylists();
        for(int i = 0; i < playlists.size(); ++i) {
            Playlist playlist = playlists.get(i);
            getContext().getContentResolver().insert(PlaylistsContract.URI, PlaylistsContract.createContentValues(playlist, event.getOffset() + i));

            ContentValues[] values = new ContentValues[playlist.tracks.size()];
            for (int j = 0; j < values.length; ++j) {
                values[j] = PlaylistTracksContract.createContentValues(playlist.tracks.get(j), j + 1);
            }

            getContext().getContentResolver().bulkInsert(Uri.withAppendedPath(PlaylistTracksContract.URI, playlist.id), values);
        }

        adapter.setLoaded();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetPlaylistsFailedEvent(GetPlaylistsFailedEvent event) {
        adapter.setLoaded();
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), R.string.error_load_playlists, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), PlaylistsContract.URI, PlaylistsContract.ALL_COLUMNS, null, null, PlaylistsContract.ORDER_NUM);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.changeCursor(cursor);

        if (cursor.getCount() == 0) {
            swipeRefreshLayout.setRefreshing(true);
            Antares.getSoundCloudInteractor().getPlaylists();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    private ArrayList<Track> getPlaylistTracks(String playlistId) {
        ArrayList<Track> tracks = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] projection = new String[] {
                    PlaylistTracksContract.ID,
                    PlaylistTracksContract.TITLE,
                    PlaylistTracksContract.STREAM_URL,
                    PlaylistTracksContract.ARTWORK_URL,
                    PlaylistTracksContract.DURATION
            };

            cursor = getContext().getContentResolver().query(
                    Uri.withAppendedPath(PlaylistTracksContract.URI, playlistId),
                    projection,
                    null, null, PlaylistTracksContract.TRACK_NUMBER);

            while (cursor.moveToNext()) {
                tracks.add(PlaylistTracksContract.getTrackByCursor(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tracks;
    }
}
