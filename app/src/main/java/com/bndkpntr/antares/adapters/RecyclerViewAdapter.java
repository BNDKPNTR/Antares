package com.bndkpntr.antares.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bndkpntr.antares.Antares;
import com.bndkpntr.antares.R;
import com.bndkpntr.antares.db.AntaresDbLoader;
import com.bndkpntr.antares.model.Track;

public class RecyclerViewAdapter extends CursorRecyclerViewAdapter<RecyclerViewAdapter.ViewHolder> {

    public RecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Track track = AntaresDbLoader.getTrackByCursor(cursor);
        viewHolder.title.setText(track.title);

        if (cursor.isLast()) {
            Antares.getSoundCloudInteractor().getRecommended();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
