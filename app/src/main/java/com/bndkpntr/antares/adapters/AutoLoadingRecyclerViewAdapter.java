package com.bndkpntr.antares.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class AutoLoadingRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends CursorRecyclerViewAdapter<VH> {
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading = false;

    public AutoLoadingRecyclerViewAdapter(Context context, Cursor cursor, final RecyclerView recyclerView) {
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

    public void reset() {
        swapCursor(null);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }

    public interface OnLoadMoreListener {
        void loadMore();
    }
}
