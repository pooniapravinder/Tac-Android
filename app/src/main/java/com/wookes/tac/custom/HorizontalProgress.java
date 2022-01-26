package com.wookes.tac.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;

import com.wookes.tac.R;

public class HorizontalProgress extends FrameLayout {

    private static final int DEFAULT_ANIMATION_DURATION = 2000;
    private static final int DEFAULT_START_COLOR = Color.RED;
    private static final int DEFAULT_END_COLOR = Color.BLUE;

    private final View one;
    private final View two;

    private int animationDuration;
    private int startColor;
    private int endColor;

    private int laidOutWidth;

    public HorizontalProgress(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.progress_bar_horizontal, this);
        readAttributes(attrs);

        one = findViewById(R.id.one);
        two = findViewById(R.id.two);

        ViewCompat.setBackground(one, new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor}));
        ViewCompat.setBackground(two, new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{endColor, startColor}));

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                laidOutWidth = HorizontalProgress.this.getWidth();

                ValueAnimator animator = ValueAnimator.ofInt(0, 2 * laidOutWidth);
                animator.setInterpolator(new LinearInterpolator());
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setDuration(animationDuration);
                animator.addUpdateListener(updateListener);
                animator.start();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void readAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgress);
        animationDuration = a.getInt(R.styleable.HorizontalProgress_animationDuration, DEFAULT_ANIMATION_DURATION);
        startColor = a.getColor(R.styleable.HorizontalProgress_gradientStartColor, DEFAULT_START_COLOR);
        endColor = a.getColor(R.styleable.HorizontalProgress_gradientEndColor, DEFAULT_END_COLOR);
        a.recycle();
    }

    private ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int offset = (int) valueAnimator.getAnimatedValue();
            one.setTranslationX(calculateOneTranslationX(laidOutWidth, offset));
            two.setTranslationX(calculateTwoTranslationX(laidOutWidth, offset));
        }
    };

    private int calculateOneTranslationX(int width, int offset) {
        return (-1 * width) + offset;
    }

    private int calculateTwoTranslationX(int width, int offset) {
        if (offset <= width) {
            return offset;
        } else {
            return (-2 * width) + offset;
        }
    }
}