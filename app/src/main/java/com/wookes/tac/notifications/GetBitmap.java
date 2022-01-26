package com.wookes.tac.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class GetBitmap {
    private Context context;
    private a a;

    public GetBitmap(Context context, a a) {
        this.context = context;
        this.a = a;
    }

    void retrieveBitmap(String url, boolean isProfile) {
        Glide.with(context).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (isProfile) {
                    a.onLoadProfileBitmap(resource);
                } else {
                    a.onLoadContentBitmap(resource);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }
}
