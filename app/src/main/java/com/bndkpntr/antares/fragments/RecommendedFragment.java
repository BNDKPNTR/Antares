package com.bndkpntr.antares.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bndkpntr.antares.Antares;
import com.bndkpntr.antares.R;
import com.bndkpntr.antares.activities.EndlessRecyclerOnScrollListener;
import com.bndkpntr.antares.adapters.RecyclerViewAdapter;
import com.bndkpntr.antares.events.GetRecommendedTracksFailedEvent;
import com.bndkpntr.antares.events.GetRecommendedTracksSuccessfulEvent;
import com.bndkpntr.antares.model.Track;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecommendedFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    Unbinder unbinder;
    private RecyclerViewAdapter adapter;
    private EndlessRecyclerOnScrollListener scrollListener;

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
                scrollListener.reset();
                adapter.reset();
                loadRecommendedTracks();
            }
        });

        adapter = new RecyclerViewAdapter(new ArrayList<Track>());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Antares.getSoundCloudInteractor().getRecommended(current_page * 10);
            }
        };
        recyclerView.setOnScrollListener(scrollListener);
        //loadRecommendedTracks();

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
    public void onGetRecommendedTracksSuccessfulEvent(GetRecommendedTracksSuccessfulEvent event) {
        adapter.addItems(event.getTracks());
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRecommendedTracksFailedEvent(GetRecommendedTracksFailedEvent event) {
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadRecommendedTracks() {
        Antares.getSoundCloudInteractor().getRecommended(0);
    }
}
