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
import com.bndkpntr.antares.db.constants.PlaylistsTable;
import com.bndkpntr.antares.model.Playlist;
import com.bumptech.glide.Glide;

public class PlaylistsRecyclerViewAdapter extends AutoLoadingRecyclerViewAdapter<PlaylistsRecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private OnPlaylistClickListener onPlaylistClickListener;

    public PlaylistsRecyclerViewAdapter(Context context, Cursor cursor, RecyclerView recyclerView) {
        super(context, cursor, recyclerView);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final Playlist playlist = PlaylistsTable.getPlaylistByCursor(cursor);
        viewHolder.title.setText(playlist.title);
        Glide.with(viewHolder.imageView.getContext()).load(Uri.parse(playlist.artworkUrl)).into(viewHolder.imageView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlaylistClickListener != null) {
                    onPlaylistClickListener.onClick(v, playlist);
                }
            }
        });
    }

    public void setOnPlaylistClickListener(OnPlaylistClickListener listener) {
        this.onPlaylistClickListener = listener;
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

    public interface OnPlaylistClickListener {
        void onClick(View view, Playlist playlist);
    }
}
