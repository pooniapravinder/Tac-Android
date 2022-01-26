package com.wookes.tac.ui;


import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.wookes.tac.R;

public class VideoPlayer extends AppCompatActivity {
    private VideoView videoView;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_video_player);
        setVideo();
        videoView.setOnClickListener(view -> finish());
    }

    public void setVideo() {
        videoView = findViewById(R.id.videoview);
        videoUri = Uri.parse(getIntent().getStringExtra("videoUri"));
        videoView.setOnCompletionListener(mp -> playVideo(videoUri));
        playVideo(videoUri);
    }

    public void playVideo(Uri uri) {
        try {
            videoView.setVideoURI(uri);
            videoView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}