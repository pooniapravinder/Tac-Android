package com.wookes.tac.exo;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

public abstract class DoubleClickListener implements View.OnClickListener {

    private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds
    private Handler handler;
    private long lastClickTime = 0;

    @Override
    public void onClick(final View v) {
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            handler.removeCallbacksAndMessages(null);
            onDoubleClick(v);
            lastClickTime = 0;
        } else {
            handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onSingleClick(v);
                }
            }, DOUBLE_CLICK_TIME_DELTA);
        }
        lastClickTime = clickTime;
    }

    public abstract void onSingleClick(View v);

    public abstract void onDoubleClick(View v);
}