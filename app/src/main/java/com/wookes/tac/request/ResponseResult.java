package com.wookes.tac.request;

import android.content.Context;

import com.wookes.tac.R;
import com.wookes.tac.util.ToastDisplay;

public interface ResponseResult {
    void onTaskDone(String output);

    default void onTaskFailed(Context context) {
        ToastDisplay.a(context, context.getResources().getString(R.string.invalid_data_from_server), 0);
    }
}
