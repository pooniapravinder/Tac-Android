package com.wookes.tac.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aliyun.apsara.alivclittlevideo.activity.AlivcLittlePreviewActivity;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;
import com.aliyun.svideo.editor.publish.PublishActivity;
import com.aliyun.svideo.sdk.external.struct.common.AliyunVideoParam;
import com.wookes.tac.R;
import com.wookes.tac.adapter.MentionTagAdapter;
import com.wookes.tac.fragments.VideoPrivacyDialog;
import com.wookes.tac.model.PostUploadModel;
import com.wookes.tac.util.PostDataGenerate;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.ToastDisplay;
import com.wookes.tac.util.c;

import java.io.File;

public class Post extends AppCompatActivity {
    private String mConfigPath;
    /**
     * 封面图片地址
     */
    private String mThumbnailPath;
    private float videoRatio;
    private AliyunVideoParam mVideoPram;
    private String musicId;
    private AutoCompleteTextView caption;
    private TextView videoViewPrivacyText;
    private TextView atTheRateButton;
    private TextView hashtagButton;
    private ImageView videoViewPrivacyIcon, thumbnail;
    private View videoViewPrivacy;
    private Toolbar toolbar;
    private Button post, cancel;
    private Context context;
    private TypedArray icons;
    private int viewPrivacy = 1;
    private Switch commentPrivacy, downloadPrivacy;
    private c c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        setContentView(R.layout.activity_layout_post);
        context = this;
        c = new c(context);
        initView();
        initData();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        icons = getResources().obtainTypedArray(R.array.video_privacy_icons);
        thumbnail.setOnClickListener(view -> startPreview());
        videoViewPrivacy.setOnClickListener(view -> openVideoPrivacyDialog());
        post.setOnClickListener(view -> {
            long timestamp = System.currentTimeMillis();
            if (!PostDataGenerate.createPostDirectory(context)) {
                ToastDisplay.a(context, R.string.post_path_creation_failed, 0);
                return;
            }
            String videoPath = new File(context.getCacheDir(), "post_data" + "/" + timestamp + ".mp4").getPath();
            String audioPath = new File(context.getCacheDir(), "post_data" + "/" + timestamp + ".mp3").getPath();
            c.storePostUploadModel(new PostUploadModel(caption.getText().toString(), videoPath, audioPath, viewPrivacy, commentPrivacy.isChecked(), downloadPrivacy.isChecked(), 0, 0, mVideoPram.getOutputWidth(), mVideoPram.getOutputHeight(), mThumbnailPath, mConfigPath, false, false, false,null, null, musicId));
            Intent intent = new Intent(context, c.isLoggedIn() ? LandingUi.class : Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        cancel.setOnClickListener(view -> {
            Intent intent = new Intent(context, LandingUi.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        atTheRateButton.setOnClickListener(view -> {
            int start = caption.getSelectionStart();
            caption.getText().insert(start, "@");
        });
        hashtagButton.setOnClickListener(view -> {
            int start = caption.getSelectionStart();
            caption.getText().insert(start, "#");
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        post = findViewById(R.id.btn_post);
        cancel = findViewById(R.id.btn_cancel);
        thumbnail = findViewById(R.id.thumbnail);
        caption = findViewById(R.id.caption);
        TextView captionCounter = findViewById(R.id.caption_counter);
        atTheRateButton = findViewById(R.id.at_the_rate_button);
        hashtagButton = findViewById(R.id.hashtag_button);
        caption.setAdapter(new MentionTagAdapter(context, caption, captionCounter));
        videoViewPrivacy = findViewById(R.id.video_view_privacy);
        videoViewPrivacyText = findViewById(R.id.video_view_privacy_text);
        videoViewPrivacyIcon = findViewById(R.id.video_view_privacy_icon);
        commentPrivacy = findViewById(R.id.comment_privacy);
        downloadPrivacy = findViewById(R.id.download_privacy);
    }


    private void initData() {
        Intent intent = getIntent();
        mConfigPath = intent.getStringExtra(PublishActivity.KEY_PARAM_CONFIG);
        mThumbnailPath = intent.getStringExtra(PublishActivity.KEY_PARAM_THUMBNAIL);
        musicId = intent.getStringExtra(PublishActivity.KEY_PARAM_MUSIC_ID);
        videoRatio = intent.getFloatExtra(PublishActivity.KEY_PARAM_VIDEO_RATIO, 0f);
        mVideoPram = (AliyunVideoParam) intent.getSerializableExtra(PublishActivity.KEY_PARAM_VIDEO_PARAM);
        new ImageLoaderImpl().loadImage(this, mThumbnailPath,
                new ImageLoaderOptions.Builder().
                        skipMemoryCache().
                        skipDiskCacheCache().
                        build()).into(thumbnail);
    }

    private void openVideoPrivacyDialog() {
        VideoPrivacyDialog videoPrivacyDialog = VideoPrivacyDialog.newInstance();
        Bundle args = new Bundle();
        args.putInt("position", viewPrivacy);
        videoPrivacyDialog.setArguments(args);
        videoPrivacyDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), VideoPrivacyDialog.TAG);
    }

    public void setResultFromFragment(int position) {
        viewPrivacy = position + 1;
        videoViewPrivacyText.setText(context.getResources().getStringArray(R.array.video_privacy)[position]);
        videoViewPrivacyIcon.setImageDrawable(icons.getDrawable(position));
    }

    private void startPreview() {
        Intent intent = new Intent(this, AlivcLittlePreviewActivity.class);
        intent.putExtra(PublishActivity.KEY_PARAM_CONFIG, mConfigPath);
        intent.putExtra(PublishActivity.KEY_PARAM_VIDEO_PARAM, mVideoPram);
        //传入视频比列
        intent.putExtra(PublishActivity.KEY_PARAM_VIDEO_RATIO, videoRatio);
        startActivity(intent);
    }
}