package com.wookes.tac.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.svideo.common.utils.PermissionUtils;
import com.wookes.tac.R;
import com.wookes.tac.adapter.PostOptionsAdapter;
import com.wookes.tac.adapter.ShareDialogAdapter;
import com.wookes.tac.cz.ConstantsShare;
import com.wookes.tac.interfaces.dd;
import com.wookes.tac.interfaces.g;
import com.wookes.tac.interfaces.h;
import com.wookes.tac.model.FeedModel;
import com.wookes.tac.model.ShareModel;
import com.wookes.tac.request.AsyncTaskDownload;
import com.wookes.tac.util.GenerateFileName;
import com.wookes.tac.util.GlideApp;
import com.wookes.tac.util.SelfUser;
import com.wookes.tac.util.ToastDisplay;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class ShareDialog extends BottomSheetDialogFragment implements h {
    public static final String TAG = "ShareDialog";
    private Context context;
    private RecyclerView shareMedia;
    private RecyclerView postOptions;
    private final ArrayList<ShareModel> shareModels = new ArrayList<>();
    private final ArrayList<ShareModel> postOptionsModels = new ArrayList<>();
    private FeedModel feedModel;
    private Dialog mDialog;
    private ImageView thumbnail;
    private TextView progressDisplay;
    private int progress;
    private dd dd;
    private int id;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    AlertDialog openAppDetDialog;
    private AsyncTaskDownload asyncTaskDownload;
    private ShareDialogListener shareDialogListener;
    String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public static ShareDialog newInstance() {
        return new ShareDialog();
    }

    public void setReference(dd dd) {
        this.dd = dd;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dismiss();
        if (id != 5001 && id != 5003) shareDialogListener.startPlayNext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_layout_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view1 = layoutInflater.inflate(R.layout.include_layout_post_upload, null);
        SelfUser selfUser = new SelfUser(context);
        mDialog = new Dialog(context);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(view1);
        thumbnail = view1.findViewById(R.id.thumbnail);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 180);
        thumbnail.setLayoutParams(layoutParams);
        progressDisplay = view1.findViewById(R.id.progress);
        Bundle bundle = this.getArguments();
        if (bundle == null) return;
        feedModel = (FeedModel) bundle.getSerializable("feedModel");
        if (feedModel == null) return;
        String[] mediaTitles = context.getResources().getStringArray(R.array.share_media_title);
        int[] mediaIds = context.getResources().getIntArray(R.array.share_media_id);
        TypedArray mediaIcons = context.getResources().obtainTypedArray(R.array.share_media_icon);
        for (int i = 0; i < mediaIds.length; i++) {
            shareModels.add(new ShareModel(mediaIds[i], mediaTitles[i], mediaIcons.getDrawable(i)));
        }
        mediaIcons.recycle();
        ShareDialogAdapter shareDialogAdapter = new ShareDialogAdapter(context, shareModels, ShareDialog.this);
        shareMedia.setAdapter(shareDialogAdapter);
        String[] postOptionsTitles = context.getResources().getStringArray(R.array.post_options_title);
        int[] postOptionsIds = context.getResources().getIntArray(R.array.post_options_id);
        TypedArray postOptionsIcons = context.getResources().obtainTypedArray(R.array.post_options_icon);
        String title;
        for (int i = 0; i < postOptionsIds.length; i++) {
            title = postOptionsTitles[i];
            switch (postOptionsIds[i]) {
                case 5001:
                    if (!feedModel.getPost().isAllowDownloads()) continue;
                    break;
                case 5002:
                    title = feedModel.getPost().isSaved() ? getString(R.string.remove_from_favorites) : title;
                    break;
                case 5004:
                    if (selfUser.getSelfUser() == null || selfUser.getSelfUser().getUserId() != feedModel.getUser().getId())
                        continue;
                    break;
                case 5005:
                    if (selfUser.getSelfUser() == null || selfUser.getSelfUser().getUserId() == feedModel.getUser().getId())
                        continue;
                case 5006:
                    title = feedModel.getPost().isAllowComments() ? getString(R.string.turn_off_comments) : title;
                    if (selfUser.getSelfUser() == null || selfUser.getSelfUser().getUserId() != feedModel.getUser().getId())
                        continue;
                    break;
                case 5007:
                    title = feedModel.getPost().isAllowDownloads() ? getString(R.string.turn_off_downloads) : title;
                    if (selfUser.getSelfUser() == null || selfUser.getSelfUser().getUserId() != feedModel.getUser().getId())
                        continue;
                    break;
            }
            postOptionsModels.add(new ShareModel(postOptionsIds[i], title, postOptionsIcons.getDrawable(i)));
        }
        postOptionsIcons.recycle();
        PostOptionsAdapter postOptionsAdapter = new PostOptionsAdapter(context, postOptionsModels, ShareDialog.this);
        postOptions.setAdapter(postOptionsAdapter);
    }

    private void initView(View view) {
        context = getActivity();
        shareMedia = view.findViewById(R.id.share_media);
        postOptions = view.findViewById(R.id.post_options);
        shareMedia.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        postOptions.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    }

    @Override
    public void sharePlatform(int id) {
        switch (id) {
            case 4001:
                share(ConstantsShare.WHATSAPP_SHARE);
                break;
            case 4002:
                share(ConstantsShare.MESSENGER_SHARE);
                break;
            case 4003:
                share(ConstantsShare.TWITTER_SHARE);
                break;
            case 4004:
                copyLink();
                break;
            case 4005:
                share(Settings.Secure.getString(context.getContentResolver(), "sms_default_application"));
                break;
            case 4006:
                share(ConstantsShare.SNAPCHAT_SHARE);
                break;
            case 4007:
                share(ConstantsShare.INSTAGRAM_SHARE);
                break;
            default:
                showAll();
                break;
        }
        dismiss();
    }

    @Override
    public void postOptions(int id) {
        switch (id) {
            case 5001:
            case 5003:
                this.id = id;
                if (checkPermission()) {
                    asyncTaskDownload = new AsyncTaskDownload(context, id == 5001 ? feedModel.getPost().getUrl() : feedModel.getPost().getThumbnail(), id == 5001 ? AsyncTaskDownload.DownloadType.VIDEO : AsyncTaskDownload.DownloadType.IMAGE, GenerateFileName.getFileName(String.valueOf(feedModel.getPost().getId())));
                    asyncTaskDownload.setmCallBack(g);
                    asyncTaskDownload.setUsername(feedModel.getUser().getUserName());
                    asyncTaskDownload.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;
            case 5002:
                dd.onSave();
                break;
            case 5004:
                dd.onDelete();
                break;
            case 5005:
                dd.onReport();
                break;
            case 5006:
                dd.onTurnComments();
                break;
            case 5007:
                dd.onTurnDownloads();
                break;
        }
        dismiss();
    }

    private void share(String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            ToastDisplay.a(context, R.string.app_not_installed, 0);
            return;
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, feedModel.getPost().getUrl());
        sendIntent.setType("text/plain");
        sendIntent.setPackage(packageName);
        startActivity(sendIntent);
    }

    private void copyLink() {
        ClipboardManager clipMan = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipMan.setPrimaryClip(ClipData.newPlainText("label", feedModel.getPost().getUrl()));
        ToastDisplay.a(context, context.getResources().getString((R.string.link_copied)), 0);
    }

    private void showAll() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, feedModel.getPost().getUrl());
        startActivity(Intent.createChooser(intent, getString(R.string.share_to)));
    }

    private final g g = new g() {

        @Override
        public void onDownloadProgress(long downloaded, long total) {
            progress = (int) ((1.0 * downloaded / total) * 100);
            ((Activity) context).runOnUiThread(() -> {
                thumbnail.setAlpha(1.0f - (progress / 100f));
                progressDisplay.setText(String.format(context.getResources().getString(R.string.upload_progress), progress));
            });
        }

        @Override
        public void onDownloadStart() {
            GlideApp.loadDownloadThumbnail(context, feedModel.getPost().getThumbnail(), thumbnail);
            mDialog.show();
        }

        @Override
        public void onDownloadSuccess(String path) {
            ToastDisplay.a(context, R.string.saved, 0);
            mDialog.dismiss();
            shareDialogListener.startPlayNext();
        }

        @Override
        public void onDownloadFailed() {
            ToastDisplay.a(context, R.string.failed, 0);
            mDialog.dismiss();
        }
    };

    private boolean checkPermission() {
        boolean checkResult = PermissionUtils.checkPermissionsGroup(getContext(), permission);
        if (!checkResult) {
            requestPermissions(permission, PERMISSION_REQUEST_CODE);
        }
        return checkResult;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                asyncTaskDownload = new AsyncTaskDownload(context, id == 5001 ? feedModel.getPost().getUrl() : feedModel.getPost().getThumbnail(), id == 5001 ? AsyncTaskDownload.DownloadType.VIDEO : AsyncTaskDownload.DownloadType.IMAGE, GenerateFileName.getFileName(String.valueOf(feedModel.getPost().getId())));
                asyncTaskDownload.setmCallBack(g);
                asyncTaskDownload.setUsername(feedModel.getUser().getUserName());
                asyncTaskDownload.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                boolean goToSettings = false;
                for (String permission : permissions) {
                    if (!shouldShowRequestPermissionRationale(permission)) {
                        goToSettings = true;
                        break;
                    }
                }
                showPermissionDialog(goToSettings);
            }
        }
    }

    private void showPermissionDialog(boolean isGoToSettings) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.alivc_little_dialog_permission_tips_download));
        builder.setPositiveButton(getResources().getString(R.string.alivc_little_main_dialog_setting), (dialog, which) -> {
            if (!isGoToSettings) {
                checkPermission();
                return;
            }
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.setNegativeButton(getResources().getString(R.string.alivc_little_main_dialog_not_setting), (dialog, which) -> {
            //finish();
        });
        openAppDetDialog = builder.create();
        if (null != openAppDetDialog && !openAppDetDialog.isShowing()) {
            openAppDetDialog.show();
        }
    }

    public interface ShareDialogListener {
        void startPlayNext();
    }

    public void setListener(ShareDialogListener shareDialogListener) {
        this.shareDialogListener = shareDialogListener;
    }
}
