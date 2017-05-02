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
        Track track = TracksTable.getTrackByCursor(cursor);
        viewHolder.title.setText(track.title);
        Glide.with(viewHolder.imageView.getContext()).load(Uri.parse(track.artworkUrl)).into(viewHolder.imageView);
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
}
