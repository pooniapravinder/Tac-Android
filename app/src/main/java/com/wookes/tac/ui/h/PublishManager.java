package com.wookes.tac.ui.h;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import com.aliyun.qupai.editor.AliyunIComposeCallBack;
import com.aliyun.qupai.editor.AliyunIVodCompose;
import com.aliyun.qupai.editor.impl.AliyunComposeFactory;
import com.google.gson.Gson;
import com.wookes.tac.R;
import com.wookes.tac.libffmpeg.ExecuteBinaryResponseHandler;
import com.wookes.tac.libffmpeg.FFmpeg;
import com.wookes.tac.libffmpeg.LoadBinaryResponseHandler;
import com.wookes.tac.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.wookes.tac.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.wookes.tac.model.PAuthInputModel;
import com.wookes.tac.model.PostUploadModel;
import com.wookes.tac.request.PAuth;
import com.wookes.tac.request.UploadPost;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.AssD;
import com.wookes.tac.util.MD5;
import com.wookes.tac.util.ToastDisplay;
import com.wookes.tac.util.c;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class PublishManager {

    private final AliyunIVodCompose mCompose;
    private final Context context;
    private final c c;
    private final PostUploadModel postUploadModel;
    private ComposeStatus mComposeStatus = ComposeStatus.NONE;
    private final MyComposeListener mComposeListener;
    private final UploadPost uploadPost;
    private final AudioExtractor audioExtractor;
    private final PAuth pAuth;
    private long totalSize;
    private File thumbnail, audio, video;
    private final FFmpeg ffmpeg;

    public PublishManager(Context context, PostUploadModel postUploadModel, MyComposeListener composeListener) {
        this.mCompose = AliyunComposeFactory.createAliyunVodCompose();
        this.postUploadModel = postUploadModel;
        this.mComposeListener = composeListener;
        this.c = new c(context);
        this.ffmpeg = FFmpeg.getInstance(context);
        this.uploadPost = new UploadPost(mUploadCallback);
        this.audioExtractor = new AudioExtractor(mAudioExtractCallback);
        pAuth = new PAuth(pAuthListener, context);
        this.context = context;
        mCompose.init(context);
    }

    public void cancel() {
        if (mComposeStatus == ComposeStatus.COMPOSE) {
            mCompose.cancelCompose();
        }
        mComposeStatus = ComposeStatus.NONE;
    }

    public void startCompose() {
        cancel();
        mComposeStatus = ComposeStatus.COMPOSE;
        if (mComposeListener != null) {
            mComposeListener.onComposeStart();
        }
        mCompose.compose(postUploadModel.getConfigPath(), postUploadModel.getVideoUri(), new AliyunIComposeCallBack() {
            @Override
            public void onComposeError(int i) {
                //合成失败
                mComposeStatus = ComposeStatus.COMPOSE_ERROR;
                if (mComposeListener != null) {
                    mComposeListener.onComposeError(i);
                }
            }

            @Override
            public void onComposeProgress(int i) {
                if (mComposeListener != null) {
                    mComposeListener.onComposeProgress(i);
                }
            }

            @Override
            public void onComposeCompleted() {
                postUploadModel.setComposed(true);
                if (mComposeListener != null) {
                    mComposeListener.onComposeCompleted();
                    startCompress();
                }
            }
        });
    }

    public void startCompress() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
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
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, Uri.parse(postUploadModel.getVideoUri()));
        long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        int width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        retriever.release();
        String outputPath = new File(context.getCacheDir(), "post_data" + "/" + System.currentTimeMillis() + ".mp4").getPath();
        String task = "-y -i " + postUploadModel.getVideoUri() + " -c:v libx265 -q:v 5 -c:a aac -ab 32072 -ar 44100 -acodec libfdk_aac -profile:a aac_he -tag:v hvc1 -bsf:v hevc_mp4toannexb -preset ultrafast -crf 28 " + outputPath;
        postUploadModel.setVideoUri(outputPath);
        String[] strings = task.split(" ");
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(strings, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                    Pattern timePattern = Pattern.compile("(?<=time=)[\\d:.]*");
                    Scanner sc = new Scanner(message);
                    String match = sc.findWithinHorizon(timePattern, 0);
                    if (match != null) {
                        String[] matchSplit = match.split(":");
                        if (duration != 0) {
                            float progress = (Integer.parseInt(matchSplit[0]) * 3600 + Integer.parseInt(matchSplit[1]) * 60 + Float.parseFloat(matchSplit[2])) / (duration / 1000);
                            float showProgress = (progress * 100);
                            mComposeListener.onCompressProgress((int) showProgress);
                        }
                    }
                }

                @Override
                public void onFailure(String message) {
                    mComposeListener.onCompressError(3001);
                }

                @Override
                public void onSuccess(String message) {
                    postUploadModel.setCompressed(true);
                    postUploadModel.setWidth(width);
                    postUploadModel.setHeight(height);
                    postUploadModel.setDuration(duration);
                    postUploadModel.setSize(new File(postUploadModel.getVideoUri()).length());
                    mComposeListener.onCompressCompleted();
                    startExtracting();
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    public void startExtracting() {
        mComposeStatus = ComposeStatus.EXTRACTING;
        audioExtractor.genVideoUsingMuxer(postUploadModel.getVideoUri(), postUploadModel.getAudioUri(), -1, -1, true, false);
    }

    public void getAuth() {
        if (!c.isLoggedIn()) {
            ToastDisplay.a(context, context.getResources().getString(R.string.not_logged_in), 0);
            return;
        }
        thumbnail = new File(postUploadModel.getThumbnail());
        audio = new File(postUploadModel.getAudioUri());
        video = new File(postUploadModel.getVideoUri());
        totalSize = thumbnail.length() + audio.length() + video.length();
        if (postUploadModel.getAuth() != null) {
            startVideoUploading();
            return;
        }
        mComposeStatus = ComposeStatus.FETCH_AUTHORIZATION;
        ArrayList<PAuthInputModel> pAuthInputModels = new ArrayList<>();
        pAuthInputModels.add(new PAuthInputModel(101, "image/jpg", thumbnail.length(), MD5.getMD5(thumbnail)));
        pAuthInputModels.add(new PAuthInputModel(102, "audio/mp3", audio.length(), MD5.getMD5(audio)));
        pAuthInputModels.add(new PAuthInputModel(103, "video/mp4", video.length(), MD5.getMD5(video)));
        pAuth.getAuth(new Gson().toJson(pAuthInputModels), URLConfig.PUBLISH_POST_AUTH_URL);
    }

    public void startThumbnailUploading() {
        mComposeStatus = ComposeStatus.UPLOADING_THUMBNAIL;
        performUpload(postUploadModel.getThumbnail(), PublishUtil.parseUrl(101, postUploadModel), "image/jpg", thumbnail.length(), MD5.getMD5(thumbnail));
    }

    public void startMusicUploading() {
        mComposeStatus = ComposeStatus.UPLOADING_MUSIC;
        performUpload(postUploadModel.getAudioUri(), PublishUtil.parseUrl(102, postUploadModel), "audio/mp3", audio.length(), MD5.getMD5(audio));
    }

    public void startVideoUploading() {
        mComposeStatus = ComposeStatus.UPLOADING_VIDEO;
        performUpload(postUploadModel.getVideoUri(), PublishUtil.parseUrl(103, postUploadModel), "video/mp4", video.length(), MD5.getMD5(video));
    }

    public void performUpload(String filePath, String url, String contentType, long contentLength, String contentMD5) {
        if (!c.isLoggedIn()) {
            ToastDisplay.a(context, context.getResources().getString(R.string.not_logged_in), 0);
            return;
        }
        uploadPost.uploadPost(filePath, url, contentType, contentLength, contentMD5);
    }

    public void releaseCompose() {
        cancel();
        mCompose.release();
    }

    private final AudioExtractor.MyAudioExtractListener mAudioExtractCallback = new AudioExtractor.MyAudioExtractListener() {
        @Override
        public void onExtractCompleted() {
            if (mComposeListener != null) {
                mComposeListener.onExtractCompleted();
            }
            getAuth();
        }

        @Override
        public void onExtractError(int i) {
            mComposeStatus = ComposeStatus.EXTRACTING_ERROR;
            if (mComposeListener != null) {
                mComposeListener.onExtractError(i);
            }
        }
    };

    private final PAuth.PAuthListener pAuthListener = new PAuth.PAuthListener() {
        @Override
        public void onAuthResponse(String response, String key) {
            if (isAuthorizationSuccess(key, response)) {
                mComposeListener.onAuthorizeSuccess(key, response);
                startVideoUploading();
            } else {
                mComposeStatus = ComposeStatus.FETCH_AUTHORIZATION_FAILED;
                if (mComposeListener != null) {
                    mComposeListener.onUploadFailed(context.getResources().getString(R.string.authorization_failed));
                }
            }
        }

        @Override
        public void onAuthFailed() {
            mComposeStatus = ComposeStatus.FETCH_AUTHORIZATION_FAILED;
            if (mComposeListener != null) {
                mComposeListener.onUploadFailed(context.getResources().getString(R.string.authorization_failed));
            }
        }
    };
    private final UploadPost.UploadPostListener mUploadCallback = new UploadPost.UploadPostListener() {
        @Override
        public void onUploadStart() {
            if (mComposeListener != null) {
                mComposeListener.onUploadStart();
            }
        }

        @Override
        public void onUploadProgress(long bytesRead, long contentLength) {
            if (mComposeListener != null) {
                if (mComposeStatus == ComposeStatus.UPLOADING_MUSIC) {
                    bytesRead = bytesRead + video.length();
                } else if (mComposeStatus == ComposeStatus.UPLOADING_THUMBNAIL) {
                    bytesRead = bytesRead + video.length() + audio.length();
                }
                final int progress = (int) (((double) bytesRead / totalSize) * 100);
                mComposeListener.onUploadProgress(progress);
            }
        }

        @Override
        public void onUploadFailed() {
            if (mComposeListener != null) {
                mComposeListener.onUploadFailed(context.getResources().getString(R.string.alivc_little_main_net_error));
            }
        }

        @Override
        public void onUploadSuccess(String response) {
            if (mComposeStatus == ComposeStatus.UPLOADING_VIDEO) {
                if (postUploadModel.getMusicId() != null) {
                    startThumbnailUploading();
                } else {
                    startMusicUploading();
                }
            } else if (mComposeStatus == ComposeStatus.UPLOADING_MUSIC) {
                startThumbnailUploading();
            } else if (mComposeStatus == ComposeStatus.UPLOADING_THUMBNAIL) {
                if (mComposeListener != null) {
                    mComposeListener.onUploadSuccess(response);
                }
            }
        }
    };

    public void retryTask() {
        switch (mComposeStatus) {
            case NONE:
            case COMPOSE_ERROR:
                startCompose();
                break;
            case EXTRACTING_ERROR:
                startExtracting();
                break;
            case FETCH_AUTHORIZATION_FAILED:
                getAuth();
                break;
            case UPLOADING_THUMBNAIL_FAILED:
                startThumbnailUploading();
                break;
            case UPLOADING_MUSIC_FAILED:
                startMusicUploading();
                break;
            case UPLOADING_VIDEO_FAILED:
                startVideoUploading();
                break;
            default:
                break;
        }
    }

    public interface MyComposeListener {

        void onComposeStart();

        void onComposeProgress(int i);

        void onComposeCompleted();

        void onComposeError(int i);

        void onCompressProgress(int i);

        void onCompressCompleted();

        void onCompressError(int i);

        void onExtractCompleted();

        void onExtractError(int i);

        void onAuthorizeSuccess(String key, String auth);

        void onUploadStart();

        void onUploadProgress(int progress);

        void onUploadFailed(String error);

        void onUploadSuccess(String response);
    }

    enum ComposeStatus {
        NONE,
        COMPOSE,
        COMPOSE_ERROR,
        EXTRACTING,
        EXTRACTING_ERROR,
        FETCH_AUTHORIZATION,
        FETCH_AUTHORIZATION_FAILED,
        UPLOADING_THUMBNAIL,
        UPLOADING_THUMBNAIL_FAILED,
        UPLOADING_MUSIC,
        UPLOADING_MUSIC_FAILED,
        UPLOADING_VIDEO,
        UPLOADING_VIDEO_FAILED
    }

    public String getThumbnail() {
        return postUploadModel.getThumbnail();
    }

    /**
     * 是否正在发布过程中
     *
     * @return
     */
    public boolean isPublishing() {
        return mComposeStatus == ComposeStatus.COMPOSE || mComposeStatus == ComposeStatus.EXTRACTING || mComposeStatus == ComposeStatus.FETCH_AUTHORIZATION || mComposeStatus == ComposeStatus.UPLOADING_THUMBNAIL || mComposeStatus == ComposeStatus.UPLOADING_MUSIC || mComposeStatus == ComposeStatus.UPLOADING_VIDEO;
    }

    private boolean isAuthorizationSuccess(String key, String data) {
        try {
            JSONObject jsonObject = new JSONObject(AssD.a(key, data));
            if (jsonObject.has("success")) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
