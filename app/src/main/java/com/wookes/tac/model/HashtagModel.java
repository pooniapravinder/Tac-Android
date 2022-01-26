package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

public class HashtagModel {
    @SerializedName("hashtagId")
    private long hashtagId;
    @SerializedName("hashtagName")
    private String hashtagName;
    @SerializedName("hashtagTotal")
    private long hashtagTotal;
    @SerializedName("thumbnail")
    private String thumbnail;

    public HashtagModel(long hashtagId, String hashtagName, long hashtagTotal, String thumbnail) {
        this.hashtagId = hashtagId;
        this.hashtagName = hashtagName;
        this.hashtagTotal = hashtagTotal;
        this.thumbnail = thumbnail;
    }

    public long getHashtagId() {
        return hashtagId;
    }

    public void setHashtagId(long hashtagId) {
        this.hashtagId = hashtagId;
    }

    public String getHashtagName() {
        return hashtagName;
    }

    public void setHashtagName(String hashtagName) {
        this.hashtagName = hashtagName;
    }

    public long getHashtagTotal() {
        return hashtagTotal;
    }

    public void setHashtagTotal(long hashtagTotal) {
        this.hashtagTotal = hashtagTotal;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
