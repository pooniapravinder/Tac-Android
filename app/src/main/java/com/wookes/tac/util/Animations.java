package com.wookes.tac.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class Animations {
    public static ScaleAnimation bubblePopupShow() {
        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_PARENT, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scale.setDuration(1000);
        scale.setInterpolator(new OvershootInterpolator());
        return scale;
    }

    public static ScaleAnimation bubblePopupHide() {
        ScaleAnimation scale = new ScaleAnimation(1, 0, 1, 0, ScaleAnimation.RELATIVE_TO_PARENT, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scale.setDuration(1000);
        scale.setInterpolator(new OvershootInterpolator());
        return scale;
    }

    public static void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, view.getHeight(), 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
