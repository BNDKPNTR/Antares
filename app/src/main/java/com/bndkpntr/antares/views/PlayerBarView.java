package com.bndkpntr.antares.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.bndkpntr.antares.R;

public class PlayerBarView extends RelativeLayout {
    public PlayerBarView(Context context) {
        super(context);
        init(context);
    }

    public PlayerBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayerBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_player_bar, this, true);
    }
}
