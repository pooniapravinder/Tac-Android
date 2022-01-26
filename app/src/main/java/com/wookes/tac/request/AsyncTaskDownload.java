package com.wookes.tac.request;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.aliyun.svideo.common.utils.ThreadUtils;
import com.wookes.tac.R;
import com.wookes.tac.interfaces.g;
import com.wookes.tac.libffmpeg.ExecuteBinaryResponseHandler;
import com.wookes.tac.libffmpeg.FFmpeg;
import com.wookes.tac.libffmpeg.LoadBinaryResponseHandler;
import com.wookes.tac.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.wookes.tac.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.wookes.tac.util.UpdateMediaStore;
import com.wookes.tac.util.e;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AsyncTaskDownload extends AsyncTask<Void, Long, Boolean> {
    private final WeakReference<Context> context;
    private g mCallBack;
    private final WeakReference<String> url;
    private String path = Environment.getExternalStorageDirectory() + "/Tac";
    private String cachePath;
    private String fileName;
    private final DownloadType downloadType;
    private final FFmpeg ffmpeg;
    private String username;

    public AsyncTaskDownload(Context context, String url, DownloadType downloadType, String fileName) {
        this.context = new WeakReference<>(context);
        this.url = new WeakReference<>(url);
        this.downloadType = downloadType;
        this.fileName = fileName;
        this.ffmpeg = FFmpeg.getInstance(context);
        this.cachePath = context.getFilesDir() + "/temp/";
    }

    public void setmCallBack(g g) {
        this.mCallBack = g;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    protected void onPreExecute() {
        mCallBack.onDownloadStart();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (makeDirs() || e.a() || !createDir()) return false;
        fileName += getExtension();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(new Request.Builder().url(url.get()).get().build());
        try {
            Response response = call.execute();
            if (response.code() == 200 || response.code() == 201) {
                path += getPath();
                if (makeDirs()) return false;
                File file = new File(path, fileName);
                if (file.exists()) return null;
                try (InputStream inputStream = Objects.requireNonNull(response.body()).byteStream()) {
                    byte[] buff = new byte[1024 * 4];
                    long downloaded = 0;
                    long target = Objects.requireNonNull(response.body()).contentLength();
                    OutputStream output = new FileOutputStream(new File(downloadType == DownloadType.VIDEO ? cachePath : path, fileName));
                    publishProgress(0L, target);
                    while (true) {
                        int readed = inputStream.read(buff);
                        if (readed == -1) {
                            break;
                        }
                        output.write(buff, 0, readed);
                        downloaded += readed;
                        publishProgress(downloaded, target);
                        if (isCancelled()) {
                            return false;
                        }
                    }
                    output.flush();
                    output.close();
                    return downloaded == target;
                } catch (IOException ignore) {
                    return false;
                }
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        mCallBack.onDownloadProgress(values[0].intValue(), values[1].intValue());
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        String filePath = new File(downloadType == DownloadType.VIDEO ? cachePath : path, fileName).getPath();
        if (aBoolean == null) {
            mCallBack.onDownloadSuccess(filePath);
            return;
        }
        if (aBoolean) {
            if (downloadType == DownloadType.VIDEO) {
                loadBinaryFile();
                mCallBack.onDownloadSuccess(filePath);
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //android Q
                ThreadUtils.runOnSubThread(() -> {
                    if (downloadType == DownloadType.VIDEO) {
                        UpdateMediaStore.saveVideoToMediaStore(context.get(), filePath);
                    } else if (downloadType == DownloadType.IMAGE) {
                        UpdateMediaStore.saveImageToMediaStore(context.get(), filePath);
                    } else {
                        UpdateMediaStore.saveAudioToMediaStore(context.get(), filePath);
                    }
                });
            } else {
                String mime = "";
                switch (downloadType) {
                    case VIDEO:
                        mime = "video/mp4";
                        break;
                    case IMAGE:
                        mime = "image/jpeg";
                        break;
                    case AUDIO:
                        mime = "audio/mp3";
                        break;
                }
                MediaScannerConnection.scanFile(context.get(), new String[]{filePath}, new String[]{mime}, null);
                mCallBack.onDownloadSuccess(filePath);
            }
        } else {
            mCallBack.onDownloadFailed();
        }
    }

    boolean makeDirs() {
        File mFolder = new File(path);
        if (!mFolder.exists()) {
            return !mFolder.mkdir();
        }
        return false;
    }

    public enum DownloadType {
        AUDIO,
        VIDEO,
        IMAGE
    }

    private String getPath() {
        switch (downloadType) {
            case AUDIO:
                return "/Audios";
            case VIDEO:
                return "/Videos";
            case IMAGE:
                return "/Images";
            default:
                return "";
        }
    }

    private String getExtension() {
        switch (downloadType) {
            case AUDIO:
                return ".mp3";
            case VIDEO:
                return ".mp4";
            case IMAGE:
                return ".jpg";
            default:
                return "";
        }
    }


    private void loadBinaryFile() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                    deleteTempFile();
                }

                @Override
                public void onSuccess() {
                    executeCMD();
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }

    private void executeCMD() {
        String input = new File(cachePath, fileName).getPath();
        String output = new File(path, fileName).getPath();
        String task = "-y -i " + input + " -i " + copyLogo() + " -filter_complex [1]scale=iw*0.1:-1[wm];[0][wm]overlay=W-w-10:H-h-40,drawtext=fontfile=" + copyFonts("font.ttf") + ":text=@" + username + ":fontcolor=white@1.0:fontsize=20:x=W-tw-10:y=H-th-10 -codec:a copy -preset ultrafast -async 1 " + output;
        String[] strings = task.split(" ");
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(strings, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                }

                @Override
                public void onFailure(String message) {
                    deleteTempFile();
                }

                @Override
                public void onSuccess(String message) {
                    deleteTempFile();
                }

                @Override
                public void onFinish() {
                    deleteTempFile();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    private boolean createDir() {
        File folder = new File(cachePath);
        if (folder.exists()) return true;
        return folder.mkdirs();
    }

    private String copyLogo() {
        Bitmap bm = BitmapFactory.decodeResource(context.get().getResources(), R.drawable.wookes_logo);
        File file = new File(context.get().getFilesDir() + "/temp/", "tac.png");
        if (file.exists()) file.delete();
        try {
            FileOutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    private String copyFonts(String path) {
        AssetManager assetManager = context.get().getAssets();
        String[] assets;
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                return copyFile(path);
            } else {
                File dir = new File(cachePath + path);
                if (!dir.exists())
                    dir.mkdir();
                for (String asset : assets) {
                    copyFonts(path + "/" + asset);
                }
            }
        } catch (IOException ex) {
            Log.e("Exception", "I/O Exception", ex);
        }
        return null;
    }

    private String copyFile(String filename) {
        AssetManager assetManager = context.get().getAssets();
        InputStream in;
        OutputStream out;
        String newFileName = null;
        try {
            in = assetManager.open(filename);
            newFileName = cachePath + filename;
            out = new FileOutputStream(newFileName);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        return newFileName;
    }

    private void deleteTempFile() {
        File file = new File(cachePath, fileName);
        if (file.exists()) file.delete();
    }
}