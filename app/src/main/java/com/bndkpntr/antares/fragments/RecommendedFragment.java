package com.bndkpntr.antares.fragments;

import android.content.ContentValues;
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
import com.bndkpntr.antares.adapters.RecyclerViewAdapter;
import com.bndkpntr.antares.db.contracts.RecommendedContract;
import com.bndkpntr.antares.events.GetRecommendedFailedEvent;
import com.bndkpntr.antares.events.GetRecommendedSuccessfulEvent;
import com.bndkpntr.antares.model.ActivitiesContent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    RecyclerViewAdapter adapter;

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
                Antares.getSoundCloudInteractor().getRecommended();
            }
        });

        adapter = new RecyclerViewAdapter(getContext(), null, recyclerView);
        adapter.setOnLoadMoreListener(new RecyclerViewAdapter.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                swipeRefreshLayout.setRefreshing(true);
                Antares.getSoundCloudInteractor().getRecommended();
            }
        });
        recyclerView.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
        return view;
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
        Toast.makeText(getContext(), "Error while loading recommended.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), RecommendedContract.URI, RecommendedContract.ALL_COLUMNS, null, null, RecommendedContract.CREATED_AT + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }
}
