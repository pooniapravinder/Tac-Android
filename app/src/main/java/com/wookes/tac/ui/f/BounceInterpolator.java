package com.wookes.tac.ui.f;

import android.view.animation.Interpolator;

public class BounceInterpolator implements Interpolator {
    private final double mAmplitude;
    private final double mFrequency;

    public BounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}