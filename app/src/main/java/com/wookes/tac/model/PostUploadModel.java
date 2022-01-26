package com.wookes.tac.model;

public class PostUploadModel {
    private String caption;
    private String videoUri;
    private String audioUri;
    private int viewPrivacy;
    private boolean allowComments;
    private boolean allowDownloads;
    private long duration;
    private long size;
    private int width;
    private int height;
    private String thumbnail;
    private String configPath;
    private boolean composed;
    private boolean extracted;
    private boolean compressed;
    private String auth;
    private String authKey;
    private String musicId;

    public PostUploadModel(String caption, String videoUri, String audioUri, int viewPrivacy, boolean allowComments, boolean allowDownloads, long duration, long size, int width, int height, String thumbnail, String configPath, boolean composed, boolean extracted, boolean compressed, String auth, String authKey, String musicId) {
        this.caption = caption;
        this.videoUri = videoUri;
        this.audioUri = audioUri;
        this.viewPrivacy = viewPrivacy;
        this.allowComments = allowComments;
        this.allowDownloads = allowDownloads;
        this.duration = duration;
        this.size = size;
        this.width = width;
        this.height = height;
        this.thumbnail = thumbnail;
        this.configPath = configPath;
        this.composed = composed;
        this.extracted = extracted;
        this.compressed = compressed;
        this.auth = auth;
        this.authKey = authKey;
        this.musicId = musicId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public String getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }

    public int getViewPrivacy() {
        return viewPrivacy;
    }

    public void setViewPrivacy(int viewPrivacy) {
        this.viewPrivacy = viewPrivacy;
    }

    public boolean isAllowComments() {
        return allowComments;
    }

    public void setAllowComments(boolean allowComments) {
        this.allowComments = allowComments;
    }

    public boolean isAllowDownloads() {
        return allowDownloads;
    }

    public void setAllowDownloads(boolean allowDownloads) {
        this.allowDownloads = allowDownloads;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public boolean isComposed() {
        return composed;
    }

    public void setComposed(boolean composed) {
        this.composed = composed;
    }

    public boolean isExtracted() {
        return extracted;
    }

    public void setExtracted(boolean extracted) {
        this.extracted = extracted;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }
}
