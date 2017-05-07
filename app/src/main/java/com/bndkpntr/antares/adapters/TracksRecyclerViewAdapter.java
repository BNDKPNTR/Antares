package com.bndkpntr.antares.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bndkpntr.antares.R;
import com.bndkpntr.antares.db.constants.TracksTable;
import com.bndkpntr.antares.model.Track;
import com.bumptech.glide.Glide;

public class TracksRecyclerViewAdapter extends AutoLoadingRecyclerViewAdapter<TracksRecyclerViewAdapter.ViewHolder> {
    private OnTrackClickListener onTrackClickListener;

    public TracksRecyclerViewAdapter(Context context, Cursor cursor, RecyclerView recyclerView) {
        super(context, cursor, recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final Track track = TracksTable.getTrackByCursor(cursor);
        viewHolder.title.setText(track.title);
        Glide.with(viewHolder.imageView.getContext()).load(Uri.parse(track.artworkUrl)).into(viewHolder.imageView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTrackClickListener != null) {
                    onTrackClickListener.onClick(v, track);
                }
            }
        });
    }

    public void setOnTrackClickListener(OnTrackClickListener listener) {
        this.onTrackClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public interface OnTrackClickListener {
        void onClick(View view, Track track);
    }
}
