package com.wookes.tac.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class UpdateMediaStore {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void saveVideoToMediaStore(Context context, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.IS_PENDING, 1);
        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri item = resolver.insert(collection, values);
        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(item, "w", null)) {
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
            ParcelFileDescriptor.AutoCloseOutputStream outputStream = new ParcelFileDescriptor.AutoCloseOutputStream(pfd);
            BufferedOutputStream bot = new BufferedOutputStream(outputStream);
            byte[] bt = new byte[2048];
            int len;
            while ((len = bin.read(bt)) >= 0) {
                bot.write(bt, 0, len);
                bot.flush();
            }
            bin.close();
            bot.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        values.clear();
        values.put(MediaStore.Video.Media.IS_PENDING, 0);
        resolver.update(item, values, null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void saveImageToMediaStore(Context context, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);
        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri item = resolver.insert(collection, values);
        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(item, "w", null)) {
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
            ParcelFileDescriptor.AutoCloseOutputStream outputStream = new ParcelFileDescriptor.AutoCloseOutputStream(pfd);
            BufferedOutputStream bot = new BufferedOutputStream(outputStream);
            byte[] bt = new byte[2048];
            int len;
            while ((len = bin.read(bt)) >= 0) {
                bot.write(bt, 0, len);
                bot.flush();
            }
            bin.close();
            bot.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        values.clear();
        values.put(MediaStore.Images.Media.IS_PENDING, 0);
        resolver.update(item, values, null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void saveAudioToMediaStore(Context context, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.IS_PENDING, 1);
        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri item = resolver.insert(collection, values);
        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(item, "w", null)) {
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
            ParcelFileDescriptor.AutoCloseOutputStream outputStream = new ParcelFileDescriptor.AutoCloseOutputStream(pfd);
            BufferedOutputStream bot = new BufferedOutputStream(outputStream);
            byte[] bt = new byte[2048];
            int len;
            while ((len = bin.read(bt)) >= 0) {
                bot.write(bt, 0, len);
                bot.flush();
            }
            bin.close();
            bot.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        values.clear();
        values.put(MediaStore.Audio.Media.IS_PENDING, 0);
        resolver.update(item, values, null, null);
    }
}
