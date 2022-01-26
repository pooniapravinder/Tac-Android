package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeedModel implements Serializable {

    @SerializedName("user")
    private User user;
    @SerializedName("post")
    private Post post;
    @SerializedName("music")
    private Music music;
    @SerializedName("following")
    private boolean following;

    public FeedModel() {
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Music getMusic() {
        return this.music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public boolean isFollowing() {
        return this.following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }
}
