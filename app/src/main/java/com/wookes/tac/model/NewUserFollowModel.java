package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

public class NewUserFollowModel {

    @SerializedName("userId")
    private long userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("verified")
    private boolean verified;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("profilePic")
    private String profilePic;

    public NewUserFollowModel(long userId, String userName, boolean verified, String fullName, String profilePic) {
        this.userId = userId;
        this.userName = userName;
        this.verified = verified;
        this.fullName = fullName;
        this.profilePic = profilePic;
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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
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
}
