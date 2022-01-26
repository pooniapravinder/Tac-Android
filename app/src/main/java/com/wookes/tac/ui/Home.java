package com.wookes.tac.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aliyun.apsara.alivclittlevideo.constants.LittleVideoParamConfig;
import com.aliyun.svideo.common.utils.PermissionUtils;
import com.aliyun.svideo.recorder.activity.AlivcSvideoRecordActivity;
import com.aliyun.svideo.recorder.bean.AlivcRecordInputParam;
import com.aliyun.svideo.recorder.bean.RenderingMode;
import com.google.android.material.appbar.AppBarLayout;
import com.wookes.tac.R;
import com.wookes.tac.interfaces.OnUploadCompleteListener;
import com.wookes.tac.model.PostUploadModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.ui.f.BaseHome;
import com.wookes.tac.ui.f.BounceInterpolator;
import com.wookes.tac.ui.f.FollowingHome;
import com.wookes.tac.ui.f.PopularHome;
import com.wookes.tac.ui.h.PublishUtil;
import com.wookes.tac.ui.h.PublishView;
import com.wookes.tac.ui.i.IntroDialog;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.ToastDisplay;
import com.wookes.tac.util.c;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class Home extends Fragment {
    private boolean isFirstTime;
    private AppBarLayout appBarLayout;
    private LinearLayout uploadLayout;
    private Context context;
    private ImageView createPost;
    private c c;
    private Button following, popular;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    AlertDialog openAppDetDialog;
    String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
    private PublishView publishView;
    public boolean isPublishing;
    private PopularHome popularFragment;
    private FollowingHome followingFragment;
    private Fragment currentFragment;

    public static Home newInstance() {
        return new Home();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popularFragment = PopularHome.newInstance();
        followingFragment = FollowingHome.newInstance();
        context = getActivity();
        c = new c(context);
        if (c.isFirstTime()) {
            isFirstTime = true;
            IntroDialog introDialog = new IntroDialog(getActivity());
            introDialog.setListener(listener);
            introDialog.show();
            c.putFirstTime(false);
        }
        if (c.isLoggedIn()) c.refreshProfile(getActivity());
        Bundle bundle = new Bundle();
        bundle.putBoolean("pausePlayer", isFirstTime);
        popularFragment.setArguments(bundle);
        loadFragment(popularFragment);
        currentFragment = popularFragment;
        View rootView = inflater.inflate(R.layout.activity_layout_home, container, false);
        initView(rootView);
        createPost.setOnClickListener((View v) -> {
            if (isPublishing) {
                ToastDisplay.a(context, R.string.already_publishing_post, 0);
                return;
            }
            if (checkPermission()) {
                jumpToRecorder();
            }
        });
        StatusBar.getInset(appBarLayout, (topInset, bottomInset) -> appBarLayout.setPadding(0, topInset, 0, 0));
        appBarLayout.post(() -> uploadLayout.setPadding(uploadLayout.getLeft(), appBarLayout.getHeight(), uploadLayout.getPaddingRight(), uploadLayout.getPaddingBottom()));
        following.setOnClickListener(view -> {
            if (followingFragment.equals(currentFragment)) return;
            toggle(true);
            loadFragment(followingFragment);
            currentFragment = followingFragment;
        });
        popular.setOnClickListener(view -> {
            if (popularFragment.equals(currentFragment)) return;
            toggle(false);
            loadFragment(popularFragment);
            currentFragment = popularFragment;
        });
        return rootView;
    }

    private void initView(View view) {
        createPost = view.findViewById(R.id.create_post);
        appBarLayout = view.findViewById(R.id.appBar);
        uploadLayout = view.findViewById(R.id.upload_layout);
        following = view.findViewById(R.id.following);
        popular = view.findViewById(R.id.popular);
    }

    private void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragment.isAdded()) {
            ((BaseHome) fragment).resumeFragment();
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.home_fragment_container, fragment, backStateName);
        }
        if (currentFragment != null) {
            currentFragment.onPause();
            fragmentTransaction.hide(currentFragment);
        }
        fragmentTransaction.commit();
    }

    private void toggle(boolean isFollowing) {
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 10);
        animation.setInterpolator(interpolator);
        if (isFollowing) {
            following.setTextSize(18);
            popular.setTextSize(16);
            following.startAnimation(animation);
            following.setTextColor(Color.WHITE);
            popular.setTextColor(getResources().getColor(R.color.home_top_option_inactive));
        } else {
            following.setTextSize(16);
            popular.setTextSize(18);
            popular.startAnimation(animation);
            following.setTextColor(getResources().getColor(R.color.home_top_option_inactive));
            popular.setTextColor(Color.WHITE);
        }
    }

    private void jumpToRecorder() {
        final AlivcRecordInputParam recordInputParam = new AlivcRecordInputParam.Builder()
                .setResolutionMode(LittleVideoParamConfig.Recorder.RESOLUTION_MODE)
                .setRatioMode(LittleVideoParamConfig.Recorder.RATIO_MODE)
                .setMaxDuration(LittleVideoParamConfig.Recorder.MAX_DURATION)
                .setMinDuration(LittleVideoParamConfig.Recorder.MIN_DURATION)
                .setVideoQuality(LittleVideoParamConfig.Recorder.VIDEO_QUALITY)
                .setGop(LittleVideoParamConfig.Recorder.GOP)
                .setVideoCodec(LittleVideoParamConfig.Recorder.VIDEO_CODEC)
                .setVideoRenderingMode(RenderingMode.Race)
                .build();
        AlivcSvideoRecordActivity.startRecord(getContext(), recordInputParam);
    }

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
                jumpToRecorder();
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
        builder.setMessage(getResources().getString(R.string.alivc_little_dialog_permission_tips));
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

    @Override
    public void onStart() {
        super.onStart();
        PostUploadModel postUploadModel = c.getStoredPostUploadModels();
        if (postUploadModel == null) return;
        if (!TextUtils.isEmpty(postUploadModel.getThumbnail())) {
            if (publishView == null) {
                isPublishing = true;
                initUploadView();
                publishView.startUpload(postUploadModel);
            }
        }
    }

    private void initUploadView() {
        publishView = new PublishView(context);
        publishView.setOnUploadCompleteListener(new MyUploadCompleteListener(Home.this));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        uploadLayout.addView(publishView, params);
    }

    private class MyUploadCompleteListener implements OnUploadCompleteListener {
        WeakReference<Home> weakReference;

        MyUploadCompleteListener(Home activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(final String response) {
            if (weakReference == null) {
                return;
            }
            PostUploadModel postUploadModel = weakReference.get().c.getStoredPostUploadModels();
            Map<String, String> map = new HashMap<>();
            map.put("caption", postUploadModel.getCaption());
            map.put("thumbnail", PublishUtil.parseObject(101, postUploadModel).getKey());
            map.put("audio", PublishUtil.parseObject(102, postUploadModel).getKey());
            map.put("video", PublishUtil.parseObject(103, postUploadModel).getKey());
            map.put("size", String.valueOf(postUploadModel.getSize()));
            map.put("width", String.valueOf(postUploadModel.getWidth()));
            map.put("height", String.valueOf(postUploadModel.getHeight()));
            map.put("duration", String.valueOf(postUploadModel.getDuration()));
            map.put("viewPrivacy", String.valueOf(postUploadModel.getViewPrivacy()));
            map.put("allowComments", String.valueOf(postUploadModel.isAllowComments()));
            map.put("allowDownloads", String.valueOf(postUploadModel.isAllowDownloads()));
            if (postUploadModel.getMusicId() != null)
                map.put("musicId", String.valueOf(postUploadModel.getMusicId()));
            new AsyncRequest(weakReference.get().context, responseResult, null, new RequestData(URLConfig.PUBLISH_POST_URL, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        public void onFailure(String msg) {
            if (weakReference == null) {
                return;
            }
            Home activity = weakReference.get();
            activity.publishView.showFailed(activity.getResources().getString(R.string.alivc_little_main_tip_publish_error) + msg);
        }
    }

    private final ResponseResult responseResult = new ResponseResult() {

        @Override
        public void onTaskDone(String output) {
            isPublishing = false;
            try {
                JSONObject root = new JSONObject(output);
                if (!root.has("success")) {
                    return;
                }
                c.removePostUploadModel();
                publishView.showSuccess();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTaskFailed(Context context) {
            publishView.showFailed(getResources().getString(R.string.post_upload_failed));
        }
    };

    private final IntroDialog.Listener listener = new IntroDialog.Listener() {
        @Override
        public void closeDialog() {
            popularFragment.startPlayer();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        currentFragment.onPause();
    }

    public void resumeFragment() {
        ((BaseHome) currentFragment).resumeFragment();
    }
}