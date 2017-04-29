package com.bndkpntr.antares.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bndkpntr.antares.R;
import com.bndkpntr.antares.model.Track;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.TrackViewHolder> {

    private final List<Track> data;

    public RecyclerViewAdapter(List<Track> tracks) {
        this.data = tracks;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        holder.title.setText(data.get(position).title);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItems(List<Track> items) {
        data.addAll(items);
        notifyDataSetChanged();
    }

    public void reset() {
        data.clear();
        notifyDataSetChanged();
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        TrackViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
