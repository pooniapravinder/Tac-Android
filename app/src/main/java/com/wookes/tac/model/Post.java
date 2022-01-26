package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Post implements Serializable {

    @SerializedName("id")
    private long id;
    @SerializedName("caption")
    private String caption;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("url")
    private String url;
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;
    @SerializedName("duration")
    private long duration;
    @SerializedName("size")
    private long size;
    @SerializedName("liked")
    private boolean liked;
    @SerializedName("saved")
    private boolean saved;
    @SerializedName("likes")
    private long likes;
    @SerializedName("comments")
    private long comments;
    @SerializedName("allowComments")
    private boolean allowComments;
    @SerializedName("allowDownloads")
    private boolean allowDownloads;

    public Post(long id, String caption, String thumbnail, String url, int width, int height, long duration, long size, boolean liked, boolean saved, long likes, long comments, boolean allowComments, boolean allowDownloads) {
        this.id = id;
        this.caption = caption;
        this.thumbnail = thumbnail;
        this.url = url;
        this.liked = liked;
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.size = size;
        this.saved = saved;
        this.likes = likes;
        this.comments = comments;
        this.allowComments = allowComments;
        this.allowDownloads = allowDownloads;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public boolean isLiked() {
        return liked;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public void setAllowComments(boolean allowComments) {
        this.allowComments = allowComments;
    }

    public boolean isAllowComments() {
        return this.allowComments;
    }

    public void setAllowDownloads(boolean allowDownloads) {
        this.allowDownloads = allowDownloads;
    }

    public boolean isAllowDownloads() {
        return this.allowDownloads;
    }
}
