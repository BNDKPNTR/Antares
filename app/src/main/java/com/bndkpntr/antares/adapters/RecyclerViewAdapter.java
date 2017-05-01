package com.bndkpntr.antares.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
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

public class RecyclerViewAdapter extends CursorRecyclerViewAdapter<RecyclerViewAdapter.ViewHolder> {
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading = false;

    public RecyclerViewAdapter(Context context, Cursor cursor, final RecyclerView recyclerView) {
        super(context, cursor);

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                private final int visibleThreshold = 5;
                private final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!loading && layoutManager.getItemCount() <= (layoutManager.findLastVisibleItemPosition() + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.loadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
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

    public void reset() {
        swapCursor(null);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
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

    public interface OnLoadMoreListener {
        void loadMore();
    }
}
