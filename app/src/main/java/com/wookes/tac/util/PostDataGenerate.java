package com.wookes.tac.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class PostDataGenerate {

    public static boolean createPostDirectory(Context context) {
        File myDir = new File(context.getCacheDir() + "/post_data");
        if (!myDir.exists()) {
            if (!myDir.mkdirs()) {
                Log.i("PostData", "Failed to create directory for post");
                return false;
            }
        }
        return true;
    }

    public static boolean deletePostDirectory(Context context) {
        File myDir = new File(context.getCacheDir() + "/post_data");
        if (myDir.exists()) {
            return myDir.delete();
        }
        return true;
    }
}
