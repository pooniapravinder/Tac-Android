package com.wookes.tac.exo;

import android.content.Context;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;
import java.io.IOException;

public class ExoCache {
    private static SimpleCache sDownloadCache;
    static LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor;
    static ExoDatabaseProvider exoDatabaseProvider;
    static long exoPlayerCacheSize = 100 * 1024 * 1024;

    void cacheVideo(SimpleCache simpleCache, DataSpec dataSpec, CacheKeyFactory defaultCacheKeyFactory, DataSource dataSource, CacheUtil.ProgressListener progressListener) {
        try {
            CacheUtil.cache(dataSpec, simpleCache, defaultCacheKeyFactory, dataSource, progressListener, null);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static SimpleCache getInstance(Context context) {
        exoDatabaseProvider = new ExoDatabaseProvider(context);
        leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize);
        if (sDownloadCache == null)
            sDownloadCache = new SimpleCache(new File(context.getCacheDir(), "media"), leastRecentlyUsedCacheEvictor, exoDatabaseProvider);
        return sDownloadCache;
    }
}
