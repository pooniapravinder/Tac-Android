package com.wookes.tac.ui.h;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.aliyun.common.utils.DensityUtil;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;
import com.aliyun.svideo.common.widget.AlivcCustomAlertDialog;
import com.wookes.tac.R;
import com.wookes.tac.interfaces.OnUploadCompleteListener;
import com.wookes.tac.model.PostUploadModel;
import com.wookes.tac.util.c;


public class PublishView extends FrameLayout {
    private OnUploadCompleteListener mOutOnUploadCompleteListener;
    private PostUploadModel postUploadModel;
    private c c;
    private ProgressBar mProgressBar;
    private ImageView mIvThumbnail;
    private PublishManager mPublishManager;
    /**
     * 封面图在屏幕中的宽度
     */
    private final float mIvWidth = 67.5f;
    /**
     * 封面图在屏幕中的高度
     */
    private final float mIvHeight = 100f;

    /**
     * 合成视频输出文件路径
     */

    public PublishView(@NonNull Context context) {
        super(context);
        initView();
    }

    public PublishView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PublishView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setBackground(getResources().getDrawable(R.drawable.rounded_video_background));
        setClipToOutline(true);
        c = new c(getContext());
        mProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.vertical_progress_bar));
        mProgressBar.setProgress(100);
        mIvThumbnail = new ImageView(getContext());
        mIvThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mIvThumbnail.setBackgroundColor(getResources().getColor(R.color.alivc_common_bg_black_alpha_80));
    }

    public void showSuccess() {
        post(() -> {
            clearUploadProgress();
            TextView textView = new TextView(getContext());
            textView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.alivc_common_bg_black_alpha_40));
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            int padding = DensityUtil.dip2px(getContext(), 10);
            textView.setPadding(padding, padding, padding, padding);
            textView.setText(R.string.alivc_little_main_tip_upload_success);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            Toast toast = new Toast(getContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 100);
            toast.setView(textView);
            toast.show();
        });
    }

    public void showFailed(final String message) {

        post(() -> {
            clearUploadProgress();
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    public void updateProgress(final int progress) {
        post(() -> mProgressBar.setProgress(100 - progress));
    }

    public void showProgress(String filePath) {
        new ImageLoaderImpl().loadImage(getContext(), filePath, new ImageLoaderOptions.Builder().skipDiskCacheCache().skipMemoryCache().build()).into(mIvThumbnail);
        LayoutParams layoutParams = new LayoutParams(DensityUtil.dip2px(getContext(), mIvWidth), DensityUtil.dip2px(getContext(), mIvHeight));
        if (mIvThumbnail.getParent() == null) {
            addView(mIvThumbnail, layoutParams);
        }
        if (mProgressBar.getParent() == null) {
            addView(mProgressBar, layoutParams);
        }
    }

    public void clearUploadProgress() {
        removeView(mProgressBar);
        removeView(mIvThumbnail);
        mProgressBar.setProgress(100);
    }

    /**
     * 合成、上传管理类
     */

    /**
     * 开始上传
     */
    public void startUpload(PostUploadModel postUploadModel) {
        this.postUploadModel = postUploadModel;
        if (mPublishManager != null) {
            mPublishManager.releaseCompose();
        }
        mPublishManager = new PublishManager(getContext(), postUploadModel, mComposeCallback);
        showProgress(mPublishManager.getThumbnail());
        if (postUploadModel.isComposed()) {
            if (postUploadModel.isCompressed()) {
                if (postUploadModel.isExtracted()) {
                    mPublishManager.getAuth();
                } else {
                    mPublishManager.startExtracting();
                }
            } else {
                mPublishManager.startCompress();
            }
        } else {
            mPublishManager.startCompose();
        }
    }

    private final PublishManager.MyComposeListener mComposeCallback = new PublishManager.MyComposeListener() {
        @Override
        public void onComposeStart() {
        }

        @Override
        public void onComposeProgress(int i) {
            updateProgress(i / 4);
        }

        @Override
        public void onComposeCompleted() {
            c.storePostUploadModel(postUploadModel);
        }

        @Override
        public void onComposeError(int i) {
            if (mOutOnUploadCompleteListener != null) {
                mOutOnUploadCompleteListener.onFailure(i + " : " + getResources().getString(R.string.alivc_little_main_creation_failed));
            }
        }

        @Override
        public void onCompressProgress(int i) {
            updateProgress((i / 4) + 25);
        }

        @Override
        public void onCompressCompleted() {
            c.storePostUploadModel(postUploadModel);
        }

        @Override
        public void onCompressError(int i) {

        }

        @Override
        public void onExtractCompleted() {
            postUploadModel.setExtracted(true);
            c.storePostUploadModel(postUploadModel);
        }

        @Override
        public void onExtractError(int i) {
            if (mOutOnUploadCompleteListener != null) {
                mOutOnUploadCompleteListener.onFailure(i + " : " + getResources().getString(R.string.alivc_little_main_creation_failed));
            }
        }

        @Override
        public void onAuthorizeSuccess(String key, String auth) {
            postUploadModel.setAuthKey(key);
            postUploadModel.setAuth(auth);
            c.storePostUploadModel(postUploadModel);
        }

        @Override
        public void onUploadStart() {

        }

        @Override
        public void onUploadProgress(int progress) {
            updateProgress((progress / 2) + 50);
        }

        @Override
        public void onUploadFailed(String message) {
            showRetryDialog(message);
        }

        @Override
        public void onUploadSuccess(String response) {
            if (mOutOnUploadCompleteListener != null) {
                mOutOnUploadCompleteListener.onSuccess(response);
            }
            if (mPublishManager != null) {
                mPublishManager.releaseCompose();
                mPublishManager = null;
            }
        }
    };

    public void showRetryDialog(final String message) {
        post(() -> {
            AlivcCustomAlertDialog dialog = new AlivcCustomAlertDialog.Builder(getContext())
                    .setMessage(message + "," + getResources().getString(R.string.alivc_little_main_dialog_upload_failed))
                    .setDialogClickListener(getResources().getString(R.string.alivc_little_main_dialog_retry), getResources().getString(R.string.alivc_little_main_dialog_close), new AlivcCustomAlertDialog.OnDialogClickListener() {
                        @Override
                        public void onConfirm() {
                            mPublishManager.retryTask();
                        }

                        @Override
                        public void onCancel() {
                            clearUploadProgress();
                            if (mOutOnUploadCompleteListener != null) {
                                mOutOnUploadCompleteListener.onFailure(message);
                            }
                        }
                    })
                    .create();
            dialog.setCancelable(false);
            dialog.show();

        });
    }

    public void setOnUploadCompleteListener(OnUploadCompleteListener listener) {
        this.mOutOnUploadCompleteListener = listener;
    }
}
