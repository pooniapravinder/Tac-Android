package com.wookes.tac.exo;

public class MediaObject {
    private int userId;
    private String username;
    private String coverImage;
    private String mediaUrl;
    private String likes;
    private String comments;
    private String shares;
    private String postId;

    public MediaObject(int userId, String username, String coverImage, String mediaUrl, String likes, String comments, String shares, String postId) {
        this.userId = userId;
        this.username = username;
        this.coverImage = coverImage;
        this.mediaUrl = mediaUrl;
        this.likes = likes;
        this.comments = comments;
        this.shares = shares;
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}