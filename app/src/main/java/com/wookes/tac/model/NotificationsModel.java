package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

public class NotificationsModel {

    @SerializedName("activityId")
    private long activityId;
    @SerializedName("type")
    private byte type;
    @SerializedName("typeName")
    private String typeName;
    @SerializedName("isFollowing")
    private boolean isFollowing;
    @SerializedName("timestamp")
    private long timestamp;
    @SerializedName("user")
    private User user;
    @SerializedName("media")
    private Media media;

    public NotificationsModel(long activityId, byte type, String typeName, boolean isFollowing, long timestamp, User user, Media media) {
        this.activityId = activityId;
        this.type = type;
        this.typeName = typeName;
        this.isFollowing = isFollowing;
        this.timestamp = timestamp;
        this.user = user;
        this.media = media;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }
}
