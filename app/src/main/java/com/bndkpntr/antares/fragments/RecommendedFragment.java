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
import com.bndkpntr.antares.activities.PlayerActivity;
import com.bndkpntr.antares.adapters.AutoLoadingRecyclerViewAdapter;
import com.bndkpntr.antares.adapters.TracksRecyclerViewAdapter;
import com.bndkpntr.antares.db.contracts.RecommendedContract;
import com.bndkpntr.antares.events.GetRecommendedFailedEvent;
import com.bndkpntr.antares.events.GetRecommendedSuccessfulEvent;
import com.bndkpntr.antares.model.ActivitiesContent;
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

public class RecommendedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    Unbinder unbinder;
    TracksRecyclerViewAdapter adapter;

    public RecommendedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommended, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getContentResolver().delete(RecommendedContract.URI, null, null);
                Antares.getSharedPreferencesManager().setRecommendedCursor("");
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
                Antares.getSoundCloudInteractor().getRecommended();
            }
        });

        adapter.setOnTrackClickListener(new TracksRecyclerViewAdapter.OnTrackClickListener() {
            @Override
            public void onClick(View view, Track track) {
                ArrayList<Track> playlist = getRecommendedPlaylist();
                int selectedTrackIndex = getSelectedTrackIndex(playlist, track);

                Intent intent = new Intent(getContext(), PlayerService.class);
                intent.putParcelableArrayListExtra(PlayerService.PLAYLIST_EXTRA, playlist);
                intent.putExtra(PlayerService.SELECTED_TRACK_INDEX_EXTRA, selectedTrackIndex);
                getActivity().startService(intent);

                intent = new Intent(getContext(), PlayerActivity.class);
                startActivity(intent);
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
    public void onGetRecommendedSuccessfulEvent(GetRecommendedSuccessfulEvent event) {
        List<ActivitiesContent> contents = event.getContents();
        ContentValues[] values = new ContentValues[contents.size()];
        for (int i = 0; i < values.length; ++i) {
            values[i] = RecommendedContract.createContentValues(contents.get(i));
        }

        getActivity().getContentResolver().bulkInsert(RecommendedContract.URI, values);
        adapter.setLoaded();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRecommendedFailedEvent(GetRecommendedFailedEvent event) {
        adapter.setLoaded();
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), R.string.error_load_recommended, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), RecommendedContract.URI, RecommendedContract.ALL_COLUMNS, null, null, RecommendedContract.CREATED_AT + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.changeCursor(cursor);

        if (cursor.getCount() == 0) {
            swipeRefreshLayout.setRefreshing(true);
            Antares.getSoundCloudInteractor().getRecommended();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    private ArrayList<Track> getRecommendedPlaylist() {
        ArrayList<Track> playlist = new ArrayList<>();
        Cursor cursor = null;

        try {
            String[] projection = new String[] {
                    RecommendedContract.ID,
                    RecommendedContract.TITLE,
                    RecommendedContract.STREAM_URL,
                    RecommendedContract.ARTWORK_URL,
                    RecommendedContract.DURATION
            };

            cursor = getContext().getContentResolver().query(
                    RecommendedContract.URI,
                    projection,
                    null, null, RecommendedContract.CREATED_AT + " DESC"
            );

            while (cursor.moveToNext()) {
                playlist.add(RecommendedContract.getTrackByCursor(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return playlist;
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
