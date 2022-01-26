package com.wookes.tac.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aliyun.apsara.alivclittlevideo.constants.LittleVideoParamConfig;
import com.aliyun.svideo.base.widget.ProgressDialog;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;
import com.aliyun.svideo.recorder.activity.AlivcSvideoRecordActivity;
import com.aliyun.svideo.recorder.bean.AlivcRecordInputParam;
import com.aliyun.svideo.recorder.bean.RenderingMode;
import com.wookes.tac.R;
import com.wookes.tac.interfaces.g;
import com.wookes.tac.model.Music;
import com.wookes.tac.request.AsyncTaskDownload;
import com.wookes.tac.ui.a.PostsData;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.GenerateFileName;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.ToastDisplay;

import java.io.IOException;
import java.util.HashMap;

public class MusicUi extends AppCompatActivity {
    private TextView musicName, user, useSound;
    private ProgressBar buffering;
    private ImageView musicCover, musicState;
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private MediaPlayerStatus mediaPlayerStatus = MediaPlayerStatus.NONE;
    private boolean isPrepared;
    private Toolbar toolbar;
    private Music music;
    private int duration;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        setContentView(R.layout.activity_layout_music);
        Intent intent = getIntent();
        if (intent == null || intent.getExtras() == null) return;
        music = (Music) intent.getSerializableExtra("data");
        duration = Integer.parseInt(String.valueOf(intent.getLongExtra("duration", 0)));
        initViews();
        attachFragment();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        musicName.setText(getString(R.string.original_sound_name, music.getUsername()));
        user.setText(music.getFullName());
        user.setOnClickListener(view -> {
            Intent intent1 = new Intent(MusicUi.this, UserProfile.class);
            intent1.putExtra("user", String.valueOf(music.getUsername()));
            intent1.putExtra("user_type", 1);
            startActivity(intent1);
        });
        Drawable drawable = getDrawable(R.drawable.icon_use_sound);
        drawable.setBounds(0, 0, 50, 50);
        useSound.setCompoundDrawables(drawable, null, null, null);
        useSound.setTypeface(Typeface.create(ResourcesCompat.getFont(this, R.font.proxima_nova_bold), Typeface.NORMAL));
        useSound.setOnClickListener(view -> loadMusic());
        new ImageLoaderImpl().loadImage(this, music.getProfilePic(),
                new ImageLoaderOptions.Builder()
                        .skipMemoryCache()
                        .roundCorner()
                        .placeholder(new ColorDrawable(Color.GRAY))
                        .build()
        ).into(musicCover);
        mediaPlayer.setLooping(true);
        musicState.setOnClickListener(view -> {
            switch (mediaPlayerStatus) {
                case NONE:
                    toggle();
                    mediaPlayerStatus = MediaPlayerStatus.PLAYING;
                    AsyncTask.execute(() -> {
                        try {
                            mediaPlayer.setDataSource(music.getUrl());
                            mediaPlayer.prepare();
                            isPrepared = true;
                            if (mediaPlayerStatus != MediaPlayerStatus.PLAYING) return;
                            mediaPlayer.start();
                            musicState.setImageResource(R.drawable.icon_music_pause);
                            toggle();
                        } catch (IOException e) {
                            Log.d("onPrepareError", String.valueOf(e));
                        }
                    });
                    break;
                case PAUSED:
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                    musicState.setImageResource(R.drawable.icon_music_pause);
                    mediaPlayerStatus = MediaPlayerStatus.PLAYING;
                    break;
                case PLAYING:
                    pausePlayer();
                    break;
                case BUFFERING:
                    break;
            }
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        useSound = findViewById(R.id.use_sound);
        musicName = findViewById(R.id.music_name);
        user = findViewById(R.id.user);
        musicCover = findViewById(R.id.music_cover);
        musicState = findViewById(R.id.music_state);
        buffering = findViewById(R.id.buffering);
    }

    enum MediaPlayerStatus {
        NONE,
        PAUSED,
        PLAYING,
        BUFFERING
    }

    private void pausePlayer() {
        musicState.setImageResource(R.drawable.icon_music_play);
        mediaPlayerStatus = MediaPlayerStatus.PAUSED;
        if (isPrepared) mediaPlayer.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pausePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    private void toggle() {
        runOnUiThread(() -> {
            if (buffering.getVisibility() == View.VISIBLE) {
                buffering.setVisibility(View.GONE);
                musicState.setVisibility(View.VISIBLE);
            } else {
                buffering.setVisibility(View.VISIBLE);
                musicState.setVisibility(View.GONE);
            }
        });
    }

    private void attachFragment() {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(music.getId()));
        Bundle bundle = new Bundle();
        bundle.putSerializable("map", map);
        bundle.putInt("bottomInset", 0);
        bundle.putString("url", URLConfig.RETRIEVE_POST_BY_MUSIC);
        Fragment fragment = PostsData.newInstance();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void loadMusic() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = ProgressDialog.show(this, null, getString(R.string.loading_sound));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        AsyncTaskDownload asyncTaskDownload = new AsyncTaskDownload(this, music.getUrl(), AsyncTaskDownload.DownloadType.AUDIO, GenerateFileName.getFileName(String.valueOf(music.getId())));
        asyncTaskDownload.setmCallBack(g);
        asyncTaskDownload.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    g g = new g() {

        @Override
        public void onDownloadProgress(long downloaded, long total) {
            int progress = (int) ((1.0 * downloaded / total) * 100);
            if (progressDialog != null) {
                progressDialog.setProgress(progress);
            }
        }

        @Override
        public void onDownloadStart() {

        }

        @Override
        public void onDownloadSuccess(String path) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            jumpToRecorder(path, String.valueOf(music.getId()));
        }

        @Override
        public void onDownloadFailed() {
            ToastDisplay.a(MusicUi.this, R.string.failed, 0);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    };

    private void jumpToRecorder(String musicPath, String musicId) {
        final AlivcRecordInputParam recordInputParam = new AlivcRecordInputParam.Builder()
                .setResolutionMode(LittleVideoParamConfig.Recorder.RESOLUTION_MODE)
                .setRatioMode(LittleVideoParamConfig.Recorder.RATIO_MODE)
                .setMaxDuration(duration)
                .setMinDuration(LittleVideoParamConfig.Recorder.MIN_DURATION)
                .setVideoQuality(LittleVideoParamConfig.Recorder.VIDEO_QUALITY)
                .setGop(LittleVideoParamConfig.Recorder.GOP)
                .setVideoCodec(LittleVideoParamConfig.Recorder.VIDEO_CODEC)
                .setVideoRenderingMode(RenderingMode.Race)
                .setMusicPath(musicPath)
                .setMusicId(musicId)
                .build();
        AlivcSvideoRecordActivity.startRecord(this, recordInputParam);
    }
}
