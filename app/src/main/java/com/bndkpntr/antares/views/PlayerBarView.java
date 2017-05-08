package com.bndkpntr.antares.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bndkpntr.antares.R;
import com.bumptech.glide.Glide;

public class PlayerBarView extends LinearLayout {

    private LinearLayout playerBar;
    private ImageView playerBarImageView;
    private TextView playerBarTextView;
    private FloatingActionButton playerBarFAB;
    private View dependentView;

    private int animationDurationInMs = 500;
    private boolean isVisible = false;

    public PlayerBarView(Context context) {
        super(context);
        init(context, null);
    }

    public PlayerBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PlayerBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PlayerBarView);
            try {
                int dependentId = array.getResourceId(R.styleable.PlayerBarView_dependent, -1);
                dependentView = findViewById(dependentId);
            } finally {
                array.recycle();
            }
        }

        LayoutInflater.from(context).inflate(R.layout.view_player_bar, this, true);
        playerBar = (LinearLayout) findViewById(R.id.playerBar);
        playerBarImageView = (ImageView) findViewById(R.id.playerBarImageView);
        playerBarTextView = (TextView) findViewById(R.id.playerBarTextView);
        playerBarFAB = (FloatingActionButton) findViewById(R.id.playerBarFAB);
    }

    public void setPlayerBarClickListener(OnClickListener listener) {
        playerBarImageView.setOnClickListener(listener);
        playerBarTextView.setOnClickListener(listener);
    }

    public void setDependentView(View view) {
        dependentView = view;
    }

    public void setTrackData(String title, String artworkUrl) {
        Glide.with(this.getContext()).load(artworkUrl).into(playerBarImageView);
        playerBarTextView.setText(title);
    }

    public boolean getIsVisible() {
        return isVisible;
    }

    public void setFABOnClickListener(OnClickListener listener) {
        playerBarFAB.setOnClickListener(listener);
    }

    public void setFABDrawable(int drawableId) {
        playerBarFAB.setImageDrawable(ContextCompat.getDrawable(this.getContext(), drawableId));
    }

    public void showPlayerBar() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 0 - playerBar.getMeasuredHeight());
        animation.setFillAfter(true);
        animation.setDuration(animationDurationInMs);

        int from = dependentView.getMeasuredHeight();
        int to = from - playerBar.getMeasuredHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = dependentView.getLayoutParams();
                params.height = (Integer) animation.getAnimatedValue();
                dependentView.setLayoutParams(params);
            }
        });

        valueAnimator.setDuration(animationDurationInMs);
        valueAnimator.start();
        playerBar.startAnimation(animation);
        isVisible = true;
    }

    public void hidePlayerBar() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, -playerBar.getMeasuredHeight(), 0);
        animation.setFillAfter(true);
        animation.setDuration(animationDurationInMs);

        int from = dependentView.getMeasuredHeight();
        int to = from + playerBar.getMeasuredHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = dependentView.getLayoutParams();
                params.height = (Integer) animation.getAnimatedValue();
                dependentView.setLayoutParams(params);
            }
        });

        valueAnimator.setDuration(animationDurationInMs);
        valueAnimator.start();
        playerBar.startAnimation(animation);
        isVisible = false;
    }
}
