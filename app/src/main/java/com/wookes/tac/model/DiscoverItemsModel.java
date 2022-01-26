package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DiscoverItemsModel {

    @SerializedName("hashtagId")
    private long hashtagId;
    @SerializedName("hashtagName")
    private String hashtagName;
    @SerializedName("hashtagTotal")
    private long hashtagTotal;
    @SerializedName("feedModels")
    private ArrayList<FeedModel> feedModels;

    public DiscoverItemsModel(long hashtagId, String hashtagName, long hashtagTotal, ArrayList<FeedModel> feedModels) {
        this.hashtagId = hashtagId;
        this.hashtagName = hashtagName;
        this.hashtagTotal = hashtagTotal;
        this.feedModels = feedModels;
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

    public ArrayList<FeedModel> getFeedModels() {
        return feedModels;
    }

    public void setFeedModels(ArrayList<FeedModel> feedModels) {
        this.feedModels = feedModels;
    }
}
