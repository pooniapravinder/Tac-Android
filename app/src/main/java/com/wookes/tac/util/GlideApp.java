package com.wookes.tac.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.wookes.tac.R;
import com.bumptech.glide.Glide;

import java.lang.annotation.Annotation;

public class GlideApp {

    public static void load(Context context, String uri, ImageView imageView) {
        Glide.with(context).load(uri).skipMemoryCache(true).placeholder(ContextCompat.getDrawable(context, R.color.colorGrey)).error(android.R.drawable.stat_notify_error).into(imageView);
    }

    public static void loadThumbnailGallery(Context context, String uri, ImageView imageView) {
        Glide.with(context).load(uri).override(1000, 1000).skipMemoryCache(true).placeholder(ContextCompat.getDrawable(context, R.color.colorGrey)).error(android.R.drawable.stat_notify_error).into(imageView);
    }

    public static void loadProfilePic(Context context, String uri, ImageView imageView) {
        Glide.with(context).load(uri != null ? uri : (ContextCompat.getDrawable(context, R.drawable.user_profile_avatar)))
                .apply(new RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .skipMemoryCache(true)
                        .dontAnimate())
                .into(imageView);
    }

    public static void loadSimple(Context context, String uri, ImageView imageView) {
        Glide.with(context).load(uri)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .skipMemoryCache(true)
                        .dontAnimate())
                .into(imageView);
    }

    public static void loadDownloadThumbnail(Context context, String uri, ImageView imageView) {
        Glide.with(context).load(uri)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.alivc_common_dialog_circle_progress_background)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .skipMemoryCache(true)
                        .dontAnimate())
                .into(imageView);
    }

    public static void loadCoverImage(Context context, String uri, ImageView imageView, Drawable placeholder) {
        Glide.with(context).load(uri)
                .apply(new RequestOptions()
                        .placeholder(placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .skipMemoryCache(true)
                        .dontAnimate())
                .into(imageView);
    }
}
