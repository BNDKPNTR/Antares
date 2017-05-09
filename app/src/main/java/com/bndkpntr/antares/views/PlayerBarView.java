package com.bndkpntr.antares.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bndkpntr.antares.R;
import com.bumptech.glide.Glide;
import com.github.jorgecastilloprz.FABProgressCircle;

public class PlayerBarView extends RelativeLayout {

    private RelativeLayout playerBar;
    private ImageView playerBarImageView;
    private TextView playerBarTextView;
    private FloatingActionButton playerBarFAB;
    private FABProgressCircle playerBarFABProgressCircle;
    private View dependentView;
    private float height;

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

        playerBar = (RelativeLayout) inflate(context, R.layout.view_player_bar, this);
        playerBarImageView = (ImageView) findViewById(R.id.playerBarImageView);
        playerBarTextView = (TextView) findViewById(R.id.playerBarTextView);
        playerBarFAB = (FloatingActionButton) findViewById(R.id.playerBarFAB);
        playerBarFABProgressCircle = (FABProgressCircle)findViewById(R.id.playPauseFABProgressCircle);

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.layout_height});
            try {
                height = array.getDimensionPixelSize(0, -1);
                playerBar.setTranslationY(height);
            } finally {
                array.recycle();
            }
        }

        playerBar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }


    public void setOnTitleClickListener(OnClickListener listener) {
        playerBarTextView.setOnClickListener(listener);
    }

    public void setOnImageClickListener(OnClickListener listener) {
        playerBarImageView.setOnClickListener(listener);
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

    public void setOnFABClickListener(OnClickListener listener) {
        playerBarFAB.setOnClickListener(listener);
    }

    public void setFABDrawable(int drawableId) {
        playerBarFAB.setImageDrawable(ContextCompat.getDrawable(this.getContext(), drawableId));
    }

    public void showLoading() {
        playerBarFAB.setEnabled(false);
        playerBarFABProgressCircle.show();
    }

    public void endLoading() {
        playerBarFAB.setEnabled(true);
        playerBarFABProgressCircle.hide();
    }

    public void showPlayerBar() {
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
        valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        valueAnimator.start();

        playerBar.animate()
                .translationYBy(-height)
                .setDuration(animationDurationInMs)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();

        playerBarFABProgressCircle.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(animationDurationInMs / 2)
                .setStartDelay(animationDurationInMs / 2)
                .setInterpolator(new FastOutLinearInInterpolator())
                .start();


        isVisible = true;
    }

    public void hidePlayerBar() {
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
        valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        valueAnimator.start();

        playerBar.animate()
                .translationYBy(height)
                .setDuration(animationDurationInMs)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();

        playerBarFABProgressCircle.animate()
                .scaleX(0)
                .scaleY(0)
                .setDuration(animationDurationInMs / 2)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();

        isVisible = false;
    }
}
