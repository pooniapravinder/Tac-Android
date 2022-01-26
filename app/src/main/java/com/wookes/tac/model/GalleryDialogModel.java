package com.wookes.tac.model;

import android.net.Uri;

public class GalleryDialogModel {

    private String duration;
    private Uri videoUri;
    private String mimeType;

    public GalleryDialogModel(String duration, Uri videoUri, String mimeType) {
        this.duration = duration;
        this.videoUri = videoUri;
        this.mimeType = mimeType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}