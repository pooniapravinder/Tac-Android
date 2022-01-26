package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("id")
    private long id;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("userName")
    private String userName;
    @SerializedName("profilePic")
    private String profilePic;
    @SerializedName("verified")
    private boolean verified;

    public User(long id, String fullName, String userName, String profilePic, boolean verified) {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
        this.profilePic = profilePic;
        this.verified = verified;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
