package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

public class FollowingFollowersModel {

    @SerializedName("userId")
    private long userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("profilePic")
    private String profilePic;
    @SerializedName("following")
    private boolean following;

    public FollowingFollowersModel(long userId, String userName, String fullName, String profilePic, boolean following) {
        this.userId = userId;
        this.userName = userName;
        this.fullName = fullName;
        this.profilePic = profilePic;
        this.following = following;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }
}
